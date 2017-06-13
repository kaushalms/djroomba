package com.kaushalmandayam.djroomba.managers;

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
    private String userId;

    //==============================================================================================
    // Class Instance Methods
    //==============================================================================================

    public String getUserToken()
    {
        return userToken;
    }

    public void setUserToken(String userToken)
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
}
