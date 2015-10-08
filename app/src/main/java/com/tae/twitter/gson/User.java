package com.tae.twitter.gson;

/**
 * User bean for JSON processing
 *
 * @author David Castillo Fuentes <dcaf82@gmail.com>
 *
 */
public class User {
    private String name;
    private String screen_name;
    private String profile_image_url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScreenName() {
        return screen_name;
    }

    public void setScreenName(String screen_name) {
        this.screen_name = screen_name;
    }

    public String getProfileImageUrl() {
        return profile_image_url;
    }

    public void setProfileImageUrl(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }
}
