package com.kaushalmandayam.djroomba.models;

import java.util.HashMap;
import java.util.Map;

/**
 * PartyTrack model to store PartyTrack id and votes
 * <p>
 * Created on 6/26/17.
 *
 * @author Kaushal
 */

public class PartyTrack
{
    public String trackId;
    public int votes;

    public Map<String, Object> toMap()
    {
        HashMap<String, Object> trackValue = new HashMap<>();
        trackValue.put("trackId", trackId);
        trackValue.put("votes", votes);

        return trackValue;
    }
}
