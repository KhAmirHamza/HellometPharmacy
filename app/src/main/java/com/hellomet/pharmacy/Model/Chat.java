package com.hellomet.pharmacy.Model;

public class Chat {
    String ownerId;
    String message;
    long timestamp;

    public Chat() {
    }

    public Chat(String ownerId, String message, long timestamp) {
        this.ownerId = ownerId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

