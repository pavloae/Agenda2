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
    public boolean share;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String comment, String url_image, boolean share) {
        this.name = name;
        this.comment = comment;
        this.url_image = url_image;
        this.share = share;
    }

    public User(Contact contactValues){
        this.contactValues = contactValues;
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
