package com.kaushalmandayam.djroomba.managers;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kaushalmandayam.djroomba.models.Party;
import com.kaushalmandayam.djroomba.models.PartyTrack;
import com.kaushalmandayam.djroomba.models.TrackViewModel;

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
    private Map<String, Party> partyMap = new HashMap<>();
    private Map<String, PartyTrack> trackMap = new HashMap<>();

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

    public void savePartyMetaData(DataSnapshot dataSnapshot)
    {
        // Get Post object and use the values to update the UI
        for (DataSnapshot partyDataSnapShot : dataSnapshot.getChildren())
        {
            addToPartyList(partyDataSnapShot);
        }
    }

    public void saveTracksMetaData(DataSnapshot dataSnapshot)
    {
        // Get Post object and use the values to update the UI
        for (DataSnapshot partyDataSnapShot : dataSnapshot.getChildren())
        {
            addToTracksList(partyDataSnapShot);
        }
    }

    public Map<String, PartyTrack> getTrackMap()
    {
        return trackMap;
    }

    private void addToTracksList(DataSnapshot partyDataSnapShot)
    {
        String key = partyDataSnapShot.getKey();
        PartyTrack track = partyDataSnapShot.getValue(PartyTrack.class);
        trackMap.put(key, track);
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
        DatabaseReference partyNodeReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("parties/" + party.getPartyId());
        partyNodeReference.setValue(party);
    }

    public void updatePartyTrack(PartyTrack track)
    {
        DatabaseReference partyNodeReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("parties/" + party.getPartyId()+"/tracks");

        Map<String, Object> TrackValues = track.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(track.trackId, TrackValues);
        partyNodeReference.updateChildren(childUpdates);
    }

    public void updateParty(DataSnapshot dataSnapshot)
    {
        this.party = dataSnapshot.getValue(Party.class);
    }

    public void updateVotes(TrackViewModel trackViewModel)
    {
        DatabaseReference trackNodeReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("parties/" + party.getPartyId() + "/tracks");
        PartyTrack partyTrack = new PartyTrack();
        partyTrack.trackId = trackViewModel.getTrack().id;
        partyTrack.votes = trackViewModel.getVotes();

        Map<String, Object> trackValues = partyTrack.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(trackViewModel.getTrack().id, trackValues);

        trackNodeReference.updateChildren(childUpdates);
    }
}
