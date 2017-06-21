package com.kaushalmandayam.djroomba.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model to hold user details
 * <p>
 * Created on 6/21/17.
 *
 * @author Kaushal
 */

public class User
{
    public String userId;
    public List<String> hostedParties = new ArrayList<>();
    public List<String> attendingParties = new ArrayList<>();

    public List<String> getHostedParties()
    {
        return hostedParties;
    }

    public void setHostedParties(List<String> hostedParties)
    {
        this.hostedParties = hostedParties;
    }

    public List<String> getAttendingParties()
    {
        return attendingParties;
    }

    public void setAttendingParties(List<String> attendingParties)
    {
        this.attendingParties = attendingParties;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public Map<String, Object> toMap()
    {
        HashMap<String, Object> userValue = new HashMap<>();
        userValue.put("userId", userId);
        userValue.put("hostedParties", hostedParties);
        userValue.put("attendingParties", attendingParties);

        return userValue;
    }

    public void addTohostedParties(String partyId)
    {
        if (!hostedParties.contains(partyId))
        {
            hostedParties.add(partyId);
        }
    }

    public void addToattendingParties(String partyId)
    {
        if (!attendingParties.contains(partyId))
        {
            attendingParties.add(partyId);
        }
    }

}
