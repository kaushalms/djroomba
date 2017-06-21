package com.kaushalmandayam.djroomba.managers;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kaushalmandayam.djroomba.models.Party;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kaushalmandayam on 5/4/17.
 */

public enum PartyManager
{
    //==============================================================================================
    // Class properties
    //==============================================================================================

    INSTANCE;

    private Party party;
    private DatabaseReference partyNodeReference;
    private Map<String, Party> partyMap = new HashMap<>();

    //==============================================================================================
    // Class Instance Methods
    //==============================================================================================


    public Map<String, Party> getPartyMap()
    {
        return partyMap;
    }

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

    public void updateParty(Party party)
    {
        this.party = party;
        partyNodeReference = FirebaseDatabase.getInstance()
                                                .getReference()
                                                .child("parties/" + party.getPartyId());
        partyNodeReference.setValue(party);
    }

    public void updateParty(DataSnapshot dataSnapshot)
    {
        Party party = dataSnapshot.getValue(Party.class);
        this.party = party;
    }
}
