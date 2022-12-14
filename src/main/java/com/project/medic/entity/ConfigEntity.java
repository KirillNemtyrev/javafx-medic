package com.project.medic.entity;

public class ConfigEntity {
    private boolean start;
    private String pathToDriver;
    private String accessToken;
    private String tokenType;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getPathToDriver() {
        return pathToDriver;
    }

    public void setPathToDriver(String pathToDriver) {
        this.pathToDriver = pathToDriver;
    }
}
