FROM eclipse-temurin:17-jre as runtime

ARG JAR_FILE=target/*.jar
WORKDIR /app
COPY ${JAR_FILE} app.jar

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseContainerSupport"

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

