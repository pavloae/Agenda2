package com.nablanet.agenda2.pojos;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Contact {

    public String getUid() {
        return uid;
    }

    public String uid;
    public String phone;
    public String name;

    public Contact() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Contact(String phone, String name) {
        this.phone = phone;
        this.name = name;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("phone", phone);
        result.put("name", name);

        return result;
    }

}
