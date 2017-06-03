package com.kaushalmandayam.djroomba.models;

import com.spotify.sdk.android.player.Metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kaushalmandayam on 5/5/17.
 */

public class Party
{
    public boolean active;
    public String desc;
    public String hostId;
    public String hostImg;
    public String hostName;
    public String id;
    public String password;
    public boolean locked;
    public String name;
    public Map<String, track> tracks= new HashMap<>();

}
