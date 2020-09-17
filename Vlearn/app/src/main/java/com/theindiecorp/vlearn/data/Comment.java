package com.theindiecorp.vlearn.data;

public class Comment {

    private String commentId, message, userId;
    private long date;

    public Comment(){}

    public Comment(String commentId, String message, String userId, long date) {
        this.commentId = commentId;
        this.message = message;
        this.userId = userId;
        this.date = date;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
