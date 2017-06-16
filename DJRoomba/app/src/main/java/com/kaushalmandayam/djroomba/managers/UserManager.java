package com.kaushalmandayam.djroomba.managers;

import com.kaushalmandayam.djroomba.Utils.PreferenceUtils;

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
}
