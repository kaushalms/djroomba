package com.kaushalmandayam.djroomba.managers;

import com.google.firebase.database.DataSnapshot;
import com.kaushalmandayam.djroomba.models.Party;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by kaushalmandayam on 5/4/17.
 */

public enum PartyManager
{
    INSTANCE;

    private Party party;

    public Map<String, Party> getPartyMap()
    {
        return partyMap;
    }

    Map<String, Party> partyMap = new HashMap<>();

    public Party getParty()
    {
        return party;
    }

    public void setParty(Party party)
    {
        this.party = party;
    }

    public void savePartyMataData(DataSnapshot dataSnapshot)
    {
        // Get Post object and use the values to update the UI
        for (DataSnapshot partyDataSnapShot : dataSnapshot.getChildren())
        {
            addToPartyList(partyDataSnapShot);
        }
    }

    private void addToPartyList(DataSnapshot partyDataSnapShot)
    {
        String key = partyDataSnapShot.getKey();
        Party party = partyDataSnapShot.getValue(Party.class);
        partyMap.put(key, party);
    }
}
