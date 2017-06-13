package com.kaushalmandayam.djroomba.managers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaushalmandayam on 5/4/17.
 */

public enum PartyManager
{
    INSTANCE;

    private String partyName;
    private String partyDescription;
    private boolean isPasswordProtected;
    private List<Integer> partyPlayListSongs = new ArrayList<>();

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

    public List<Integer> getPartyPlayListSongs()
    {
        return partyPlayListSongs;
    }

    public void setPartyPlayListSongs(List<Integer> partyPlayListSongs)
    {
        this.partyPlayListSongs = partyPlayListSongs;
    }
}
