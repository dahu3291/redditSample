package com.example.ajibade.myreddit.api;

public class NetworkState {

    public enum Status {
        RUNNING,
        SUCCESS,
        FAILED
    }

    private Status status;
    private String msg;

    private NetworkState(Status status) {
        this(status, null);
    }

    private NetworkState(Status status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public Status getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public static NetworkState success() {
        return new NetworkState(Status.SUCCESS);
    }
    public static NetworkState loading() {
        return new NetworkState(Status.RUNNING);
    }
    public static NetworkState error(String msg) {
        return new NetworkState(Status.FAILED, msg);
    }
}
