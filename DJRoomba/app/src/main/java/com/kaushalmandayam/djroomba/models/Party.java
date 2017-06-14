package com.kaushalmandayam.djroomba.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kaushalmandayam on 5/5/17.
 */

public class Party
{
    public String partyName;
    public String partyDescription;
    public boolean isPasswordProtected;
    public String partyHostId;
    public String partyId;
    public Map<String, String> partyPlayListSongs = new HashMap<>();


    public String getPartyName()
    {
        return partyName;
    }

    public void setPartyName(String partyName)
    {
        this.partyName = partyName;
    }

    public String getPartyDescription()
    {
        return partyDescription;
    }

    public void setPartyDescription(String partyDescription)
    {
        this.partyDescription = partyDescription;
    }

    public boolean isPasswordProtected()
    {
        return isPasswordProtected;
    }

    public void setPasswordProtected(boolean passwordProtected)
    {
        isPasswordProtected = passwordProtected;
    }

    public Map<String, String>  getPartyPlayListSongs()
    {
        return partyPlayListSongs;
    }

    public void setPartyPlayListSongs(Map<String, String> partyPlayListSongs)
    {
        this.partyPlayListSongs = partyPlayListSongs;
    }

    public String getPartyId()
    {
        return partyId;
    }

    public void setPartyId(String partyId)
    {
        this.partyId = partyId;
    }

    public String getPartyHostId()
    {
        return partyHostId;
    }

    public void setPartyHostId(String partyHostId)
    {
        this.partyHostId = partyHostId;
    }


    public Map<String,Object> toMap()
    {
        HashMap<String, Object> partyValue = new HashMap<>();
        partyValue.put("partyName", partyName);
        partyValue.put("partyDescription", partyDescription);
        partyValue.put("isPassword_Protected", isPasswordProtected);
        partyValue.put("partyHostId", partyHostId);
        partyValue.put("partyId", partyId);
        partyValue.put("partyplaylist", partyPlayListSongs);

        return partyValue;
    }
}
