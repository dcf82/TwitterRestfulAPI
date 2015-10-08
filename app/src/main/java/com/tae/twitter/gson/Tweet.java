package com.tae.twitter.gson;

/**
 * Tweet bean for JSON processing
 *
 * @author David Castillo Fuentes <dcaf82@gmail.com>
 *
 */
public class Tweet {
    private String text;
    private String created_at;
    private User user;

    public String getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(String created_at) {
        this.created_at = created_at;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
