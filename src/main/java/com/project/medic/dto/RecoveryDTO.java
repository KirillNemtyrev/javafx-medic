package com.project.medic.dto;

public class RecoveryDTO {
    private String usernameOrEmail;
    private String newPassword;
    private String code;

    public RecoveryDTO(String usernameOrEmail){
        this.usernameOrEmail = usernameOrEmail;
    }

    public RecoveryDTO(String usernameOrEmail, String newPassword, String code){
        this.usernameOrEmail = usernameOrEmail;
        this.newPassword = newPassword;
        this.code = code;
    }

    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
