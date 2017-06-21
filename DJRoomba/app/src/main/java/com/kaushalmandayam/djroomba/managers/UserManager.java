package com.kaushalmandayam.djroomba.managers;

import com.google.firebase.database.DataSnapshot;
import com.kaushalmandayam.djroomba.Utils.PreferenceUtils;
import com.kaushalmandayam.djroomba.models.Party;
import com.kaushalmandayam.djroomba.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kaushal on 6/5/2017.
 */

public enum UserManager
{

    //==============================================================================================
    // Class Properties
    //==============================================================================================

    INSTANCE;

    private String userToken;
    private String userCode;
    private String userId;
    private String userImageUrl;
    private User user;
    public List<String> hostedParties = new ArrayList<>();
    private String fireBaseUserId;

    //==============================================================================================
    // Class Instance Methods
    //==============================================================================================

    public String getUserToken()
    {
        return userToken;
    }

    public void setUserAccessToken(String userToken)
    {
        this.userToken = userToken;
    }

    // Spotify user ID
    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserImageUrl(String userImageUrl)
    {
        this.userImageUrl = userImageUrl;
    }

    public String getUserImageUrl()
    {
        return userImageUrl;
    }

    public void setUserCode(String userCode)
    {
        this.userCode = userCode;
        PreferenceUtils.setAccessCode(userCode);
    }

    public String getUserCode()
    {
        return userCode;
    }

    public void setFirebaseUserNodeId(String firebaseUserId)
    {
        this.fireBaseUserId = firebaseUserId;
    }

    public void saveUserMataData(DataSnapshot dataSnapshot)
    {
        user = dataSnapshot.getValue(User.class);
    }
}
