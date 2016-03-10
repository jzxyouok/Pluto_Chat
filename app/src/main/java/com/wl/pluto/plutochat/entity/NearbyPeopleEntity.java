package com.wl.pluto.plutochat.entity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

import android.annotation.SuppressLint;

/**
 * Entity mapped to table "NEARBY_PEOPLE_ENTITY".
 */
@SuppressLint("ParcelCreator")
public class NearbyPeopleEntity extends UserEntity {

    private Long id;

    private String userDistance;

    public NearbyPeopleEntity() {
    }

    public NearbyPeopleEntity(Long id) {
        this.id = id;
    }

    public NearbyPeopleEntity(Long id, String userName, String userNickName,
                              String userHeadImageUrl, String userGender,
                              String userAddress, String userPersonalitySignature,
                              String userDistance) {
        this.id = id;
        this.username = userName;
        this.userNickName = userNickName;
        this.userHeadImageUrl = userHeadImageUrl;
        this.userGender = userGender;
        this.userAddress = userAddress;
        this.userPersonalitySignature = userPersonalitySignature;
        this.userDistance = userDistance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getUserDistance() {
        return userDistance;
    }

    public void setUserDistance(String userDistance) {
        this.userDistance = userDistance;
    }

}
