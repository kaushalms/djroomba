package com.kaushalmandayam.djroomba.screens.PartyList;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kaushalmandayam.djroomba.managers.UserManager;
import com.kaushalmandayam.djroomba.models.Party;
import com.kaushalmandayam.djroomba.screens.base.BasePresenter;
import com.kaushalmandayam.djroomba.screens.base.BaseView;

/**
 * Presenter for PartyListActivity
 * <p>
 * Created on 6/13/17.
 *
 * @author Kaushal
 */

public class PartyListPresenter extends BasePresenter<PartyListPresenter.PartyListView>
{
    private DatabaseReference partyDatabaseReference;

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

        partyDatabaseReference.setValue(party);
        view.showPartyAdded();
    }

    public interface PartyListView extends BaseView
    {
        void showPartyAdded();
    }
}
