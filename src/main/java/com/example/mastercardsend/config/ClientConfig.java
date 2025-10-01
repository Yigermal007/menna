package com.example.mastercardsend.config;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.micrometer.core.instrument.MeterRegistry;
import com.example.mastercardsend.util.OkHttpMetricsEventListener;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.time.Duration;
import java.util.Arrays;

@Configuration
@EnableConfigurationProperties(MastercardSendProperties.class)
public class ClientConfig {

	private static final Logger log = LoggerFactory.getLogger(ClientConfig.class);

	@Bean
	public OkHttpClient okHttpClient(MastercardSendProperties properties, MeterRegistry meterRegistry) {
		OkHttpClient.Builder builder = new OkHttpClient.Builder()
			.callTimeout(Duration.ofSeconds(30))
			.connectTimeout(Duration.ofSeconds(10))
			.readTimeout(Duration.ofSeconds(30))
			.writeTimeout(Duration.ofSeconds(30))
			.connectionSpecs(Arrays.asList(new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
				.tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_3)
				.build()));

		builder.eventListener(new OkHttpMetricsEventListener(meterRegistry));

		HttpLoggingInterceptor logging = new HttpLoggingInterceptor(msg ->
			log.debug(maskSensitive(msg))
		);
		logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
		builder.addInterceptor(logging);

		if (!properties.isSigningDisabled()) {
			try {
				Class<?> utilsClass = Class.forName("com.mastercard.developer.utils.AuthenticationUtils");
				Class<?> interceptorClass = Class.forName("com.mastercard.developer.interceptors.OkHttpOAuth1Interceptor");
				PrivateKey signingKey = (PrivateKey) utilsClass
					.getMethod("loadSigningKey", String.class, String.class, char[].class)
					.invoke(null, properties.getKeyFile(), properties.getKeyAlias(), properties.getKeyPassword().toCharArray());
				Object interceptor = interceptorClass
					.getConstructor(String.class, PrivateKey.class)
					.newInstance(properties.getConsumerKey(), signingKey);
				builder.addInterceptor((okhttp3.Interceptor) interceptor);
			} catch (ClassNotFoundException e) {
				log.warn("Mastercard OAuth1 signer not on classpath; requests will not be signed");
			} catch (Exception e) {
				throw new IllegalStateException("Failed to initialize OAuth1 signer", e);
			}
		} else {
			log.warn("OAuth1 signing disabled by configuration; use only for tests");
		}

		if (properties.getMtls().isEnabled()) {
			try {
				SslContextBundle ssl = buildSslContextBundle(properties);
				builder.sslSocketFactory(ssl.sslContext().getSocketFactory(), ssl.trustManager());
			} catch (Exception e) {
				throw new IllegalStateException("Failed to configure mTLS", e);
			}
		}

		return builder.build();
	}

	private static SslContextBundle buildSslContextBundle(MastercardSendProperties properties) throws Exception {
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		try (FileInputStream fis = new FileInputStream(properties.getMtls().getKeyStore())) {
			keyStore.load(fis, properties.getMtls().getKeyStorePassword().toCharArray());
		}
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(keyStore, properties.getMtls().getKeyStorePassword().toCharArray());

		KeyStore trustStore = KeyStore.getInstance("JKS");
		try (FileInputStream fis = new FileInputStream(properties.getMtls().getTrustStore())) {
			trustStore.load(fis, properties.getMtls().getTrustStorePassword().toCharArray());
		}
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(trustStore);

		SSLContext sslContext = SSLContext.getInstance("TLS");
		TrustManager[] tms = tmf.getTrustManagers();
		X509TrustManager x509Tm = null;
		for (TrustManager tm : tms) {
			if (tm instanceof X509TrustManager) {
				x509Tm = (X509TrustManager) tm;
				break;
			}
		}
		if (x509Tm == null) {
			throw new IllegalStateException("No X509TrustManager found in TrustManagerFactory");
		}
		sslContext.init(kmf.getKeyManagers(), new TrustManager[]{x509Tm}, null);
		return new SslContextBundle(sslContext, x509Tm);
	}

	private record SslContextBundle(SSLContext sslContext, X509TrustManager trustManager) { }

	private static String maskSensitive(String msg) {
		return msg.replaceAll("(?i)(Authorization: ).+", "$1***masked***");
	}
}

