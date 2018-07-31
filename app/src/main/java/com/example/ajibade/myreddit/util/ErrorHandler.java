package com.example.ajibade.myreddit.util;

import org.json.JSONObject;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.functions.Consumer;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

public class ErrorHandler implements Consumer<Throwable> {

    private final String errorMessage;
    private final Consumer<String> errorConsumer;
    private final Map<String, String> messageMap;

    private ErrorHandler(String errorMessage,
                         Consumer<String> errorConsumer,
                         Map<String, String> messageMap) {
        this.errorMessage = errorMessage;
        this.errorConsumer = errorConsumer;
        this.messageMap = messageMap;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static ErrorHandler EMPTY = new ErrorHandler(null, null, null) {
        @Override
        public void accept(Throwable throwable) throws Exception {
            throwable.printStackTrace();
        }
    };

    @Override
    public void accept(Throwable throwable) throws Exception {
        String key = throwable.getClass().getName();
        String apiError;

        if (messageMap.containsKey(key)) apiError = messageMap.get(key);
        else if (throwable instanceof HttpException)
            apiError = getMessage((HttpException) throwable, errorMessage);
        else if (throwable instanceof ConnectException)
            apiError = "Failed to connect to server";
        else if (throwable instanceof SocketTimeoutException)
            apiError = "Server took too long to reply";
        else
            apiError = errorMessage;

        errorConsumer.accept(apiError);

        throwable.printStackTrace();
    }

    private String getMessage(HttpException throwable, String defaultMessage) {
        try {
            ResponseBody errorBody = throwable.response().errorBody();
            if (errorBody != null) {
                String json = errorBody.string();
                JSONObject jsonObject = new JSONObject(json);

                return jsonObject.getString("errors");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultMessage;
    }

    public static class Builder {
        String defaultMessage;
        Consumer<String> errorConsumer;
        Map<String, String> messageMap = new HashMap<>();

        public Builder defaultMessage(String errorMessage) {
            defaultMessage = errorMessage;
            return this;
        }

        public Builder add(String message, Class<? extends Throwable> exceptionClass) {
            messageMap.put(exceptionClass.getName(), message);
            return this;
        }

        public Builder add(Consumer<String> errorConsumer) {
            this.errorConsumer = errorConsumer;
            return this;
        }

        public ErrorHandler build() {
            if (defaultMessage == null) {
                throw new IllegalArgumentException("No default message provided");
            }
            if (errorConsumer == null) {
                throw new IllegalArgumentException("No Consumer provided for error message");
            }
            return new ErrorHandler(defaultMessage, errorConsumer, messageMap);
        }
    }
}