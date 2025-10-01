package com.example.mastercardsend.model;

import io.swagger.v3.oas.annotations.media.Schema;

public class Participant extends Extensible {

    @Schema(example = "John Doe")
    private String name;

    @Schema(example = "+14155550123")
    private String phone;

    @Schema(example = "john@example.com")
    private String email;

    @Schema(example = "urn:payment:card:pan:411111******1111")
    private String accountUri;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAccountUri() { return accountUri; }
    public void setAccountUri(String accountUri) { this.accountUri = accountUri; }
}

