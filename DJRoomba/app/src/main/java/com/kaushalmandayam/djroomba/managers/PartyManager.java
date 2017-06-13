package com.kaushalmandayam.djroomba.managers;

import com.kaushalmandayam.djroomba.models.Party;

/**
 * Created by kaushalmandayam on 5/4/17.
 */

public enum PartyManager
{
    INSTANCE;

    private Party party;

    public Party getParty()
    {
        return party;
    }

    public void setParty(Party party)
    {
        this.party = party;
    }
}
