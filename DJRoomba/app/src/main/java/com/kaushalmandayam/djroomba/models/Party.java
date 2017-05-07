package com.kaushalmandayam.djroomba.models;

import java.util.ArrayList;
import java.util.List;

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
    public boolean name;
    public List<track> tracks= new ArrayList<>();

}
