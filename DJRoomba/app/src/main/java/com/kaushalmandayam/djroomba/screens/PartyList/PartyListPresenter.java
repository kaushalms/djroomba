package com.kaushalmandayam.djroomba.screens.PartyList;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kaushalmandayam.djroomba.managers.PartyManager;
import com.kaushalmandayam.djroomba.managers.UserManager;
import com.kaushalmandayam.djroomba.models.Party;
import com.kaushalmandayam.djroomba.screens.base.BasePresenter;
import com.kaushalmandayam.djroomba.screens.base.BaseView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Presenter for PartyListActivity
 * <p>
 * Created on 6/13/17.
 *
 * @author Kaushal
 */

public class PartyListPresenter extends BasePresenter<PartyListPresenter.PartyListView>
{
    //==============================================================================================
    // Class properties
    //==============================================================================================

    private DatabaseReference partyDatabaseReference;

    //==============================================================================================
    // Class Instance Methods
    //==============================================================================================

    public void onSubmitButtonClicked(String partyName, String partyDesctiption, boolean isPasswordProtected)
    {
        partyDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("parties");
        String partyId = partyDatabaseReference.push().getKey();

        Party party = new Party();
        party.setPartyDescription(partyDesctiption);
        party.setPartyName(partyName);
        party.setPasswordProtected(isPasswordProtected);
        party.setPartyHostId(UserManager.INSTANCE.getUserId());
        party.setPartyId(partyId);
        party.setImageUrl(UserManager.INSTANCE.getUserImageUrl());

        // Convert party model to map and add to firebase
        Map<String, Object> partyValues = party.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(partyId, partyValues);
        partyDatabaseReference.updateChildren(childUpdates);

        view.showPartyAdded();
    }

    public void onAdapterViewSet()
    {
        if (PartyManager.INSTANCE.getPartyMap() != null)
        {
            List<Party> parties = new ArrayList<>(PartyManager.INSTANCE.getPartyMap().values());
            view.showPartyList(parties);
        }
    }

    public void savePartyMatadata(DataSnapshot dataSnapshot)
    {
        PartyManager.INSTANCE.savePartyMataData(dataSnapshot);
    }

    //==============================================================================================
    // View Interface
    //==============================================================================================

    public interface PartyListView extends BaseView
    {
        void showPartyAdded();

        void showPartyList(List<Party> parties);
    }
}
