package com.kaushalmandayam.djroomba.models;

/**
 * Model for refresh token body parameters
 * <p>
 * Created on 6/16/17.
 *
 * @author Kaushal
 */

public class RefreshTokenModel
{
    public String access_token;
    public String token_type;
    public int expires_in;
    public String refresh_token;
    public String scope;
}
