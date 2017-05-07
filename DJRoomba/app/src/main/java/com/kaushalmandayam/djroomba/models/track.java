package com.kaushalmandayam.djroomba.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaushalmandayam on 5/5/17.
 */

public class track
{
    public List<Artist> artists = new ArrayList<>();
    public int duration;
    public String id;
    public String name;
    public String popularity;
    public String url;
    public List<User> users = new ArrayList<>();
    public int votes;
}
