package com.nablanet.agenda2.pojos;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Phone {

    public String uid;
    public boolean share;

    public Phone() {
    }

    public Phone(String uid, boolean share) {
        this.uid = uid;
        this.share = share;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("share", share);
        return result;
    }
}
