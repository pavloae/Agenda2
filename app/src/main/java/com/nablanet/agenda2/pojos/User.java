package com.nablanet.agenda2.pojos;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User {

    public String uid;

    public Contact contactValues;

    public String name;
    public String comment;
    public String url_image;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public User setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public User setContactValues(Contact contactValues) {
        this.contactValues = contactValues;
        return this;
    }

    public User(String name, String comment, String url_image) {
        this.name = name;
        this.comment = comment;
        this.url_image = url_image;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("comment", comment);
        //result.put("url_image", url_image);
        return result;
    }


}
