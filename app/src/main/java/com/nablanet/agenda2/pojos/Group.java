package com.nablanet.agenda2.pojos;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Group {

    String gid, name, comment, imageUrl, ownerUid;
    boolean publicGroup;

    public Group() {
    }

    public Group(String name, String comment, String imageUrl, String ownerUid, boolean publicGroup) {
        this.name = name;
        this.comment = comment;
        this.imageUrl = imageUrl;
        this.ownerUid = ownerUid;
        this.publicGroup = publicGroup;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getGid() {
        return gid;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getOwnerUid() {
        return ownerUid;
    }

    public boolean isPublicGroup() {
        return publicGroup;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("comment", comment);
        result.put("image", imageUrl);
        result.put("owner", ownerUid);
        result.put("public", publicGroup);

        return result;
    }


}
