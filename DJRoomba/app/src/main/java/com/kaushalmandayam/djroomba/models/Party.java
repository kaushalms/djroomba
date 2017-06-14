package com.kaushalmandayam.djroomba.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public String imageUrl;
    public List<String> partyPlayListSongs = new ArrayList<>();

    public void setPartyPlayListSongs(List<String> partyPlayListSongs)
    {
        this.partyPlayListSongs = partyPlayListSongs;
    }


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

    public List<String> getPartyPlayListSongs()
    {
        return partyPlayListSongs;
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


    public Map<String, Object> toMap()
    {
        HashMap<String, Object> partyValue = new HashMap<>();
        partyValue.put("partyName", partyName);
        partyValue.put("partyDescription", partyDescription);
        partyValue.put("isPassword_Protected", isPasswordProtected);
        partyValue.put("partyHostId", partyHostId);
        partyValue.put("partyId", partyId);
        partyValue.put("partyplaylist", partyPlayListSongs);
        partyValue.put("imageUrl", imageUrl);

        return partyValue;
    }

    public void addTracktoPlaylist(String trackId)
    {
        if (!partyPlayListSongs.contains(trackId))
        {
            partyPlayListSongs.add(trackId);
        }
    }

    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl()
    {
        return imageUrl;
    }
}
