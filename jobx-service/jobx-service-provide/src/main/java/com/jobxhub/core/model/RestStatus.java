package com.jobxhub.core.model;

public enum RestStatus {

    Ok(200),
    NOT_FOUNT(404);

    private int status;

    RestStatus(int status){
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
