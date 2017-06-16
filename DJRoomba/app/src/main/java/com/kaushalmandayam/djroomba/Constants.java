package com.kaushalmandayam.djroomba;

/**
 * Created by kaushalmandayam on 4/27/17.
 */

public class Constants
{
    public static String TAG = "DJRoomba";
    public static final String CLIENT_ID = "7235a270255b421e98957199414a8e43";
    public static final String CLIENT_SECRET = "4da28a5db0f645e483cbb4aa6c090069";
    public static final String ACCOUNTS_BASE_URL = "https://accounts.spotify.com";
    public static final String REDIRECT_URI = "http://localhost:8888/callback";

    // Request code that will be used to verify if the result comes from correct activity
    // Can be any integer
    public static final int REQUEST_CODE = 1337;
}
