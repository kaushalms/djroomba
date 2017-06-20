package com.kaushalmandayam.djroomba.screens.PartyList;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kaushalmandayam.djroomba.Utils.PreferenceUtils;
import com.kaushalmandayam.djroomba.managers.LoginManager;
import com.kaushalmandayam.djroomba.managers.PartyManager;
import com.kaushalmandayam.djroomba.managers.UserManager;
import com.kaushalmandayam.djroomba.models.Party;
import com.kaushalmandayam.djroomba.screens.base.BasePresenter;
import com.kaushalmandayam.djroomba.screens.base.BaseView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Image;

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
    private Party party;
    private String partyId;

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

        party.setPartyId(partyId);

        if (UserManager.INSTANCE.getUserImageUrl()!= null && UserManager.INSTANCE.getUserId() != null)
        {
            party.setPartyHostId(UserManager.INSTANCE.getUserId());
            party.setImageUrl(UserManager.INSTANCE.getUserImageUrl());
            submitInformation(partyId, party);
        }
        else
        {
            this.party = party;
            this.partyId = partyId;
            fetchAccessToken();
        }
    }

    private void fetchAccessToken()
    {
        LoginManager.INSTANCE.fetchAccessToken(PreferenceUtils.getRefreshToken());
    }

    public void onAccessTokenReceived(final String accessToken)
    {
        AsyncTask.execute(new Runnable()
        {
            @Override
            public void run()
            {

                SpotifyApi api = new SpotifyApi();
                api.setAccessToken(accessToken);
                SpotifyService spotify = api.getService();
                String id = spotify.getMe().id;
                List<Image> userImages = spotify.getMe().images;
                if (id != null)
                {
                    UserManager.INSTANCE.setUserId(id);
                }
                if (userImages.size() > 0)
                {
                    UserManager.INSTANCE.setUserImageUrl(userImages.get(0).url);
                }
                party.setPartyHostId(UserManager.INSTANCE.getUserId());
                party.setImageUrl(UserManager.INSTANCE.getUserImageUrl());
                submitInformation(partyId, party);
                Log.d("Login Presenter", "onLoggedIn: userid" + id);
            }
        });
    }

    private void submitInformation(String partyId, Party party)
    {
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
