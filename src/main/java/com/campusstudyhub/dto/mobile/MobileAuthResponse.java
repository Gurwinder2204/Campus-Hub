package com.campusstudyhub.dto.mobile;

public class MobileAuthResponse {

    private String message;
    private MobileUserDto user;

    public MobileAuthResponse() {
    }

    public MobileAuthResponse(String message, MobileUserDto user) {
        this.message = message;
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MobileUserDto getUser() {
        return user;
    }

    public void setUser(MobileUserDto user) {
        this.user = user;
    }
}
