package com.kaushalmandayam.djroomba.screens.PartyDetail;


import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kaushalmandayam.djroomba.DjRoombaApplication;
import com.kaushalmandayam.djroomba.managers.AudioPlayerManager;
import com.kaushalmandayam.djroomba.managers.LoginManager;
import com.kaushalmandayam.djroomba.managers.PartyManager;
import com.kaushalmandayam.djroomba.models.Party;
import com.kaushalmandayam.djroomba.models.PartyTrack;
import com.kaushalmandayam.djroomba.models.TrackViewModel;
import com.kaushalmandayam.djroomba.screens.base.BasePresenter;
import com.kaushalmandayam.djroomba.screens.base.BaseView;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.RetrofitError;

import static com.kaushalmandayam.djroomba.Constants.CLIENT_ID;

/**
 * Presenter for PartyDetailActivity
 * <p>
 * Created on 6/14/17.
 *
 * @author Kaushal
 */

public class PartyDetailPresenter extends BasePresenter<PartyDetailPresenter.PartyDetailView>
{

    //==============================================================================================
    // Class properties
    //==============================================================================================

    private SpotifyService spotifyService;
    private static final String TAG = "PartyDetailPresenter";

    //==============================================================================================
    // Class Instance Methods
    //==============================================================================================


    public void getPartyTracks(final Party party)
    {
        DatabaseReference tracksNodeReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("parties/" + party.getPartyId()+"/tracks");

        tracksNodeReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                saveTracks(dataSnapshot);
                getTracks();
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    private void saveTracks(DataSnapshot tracksDataSnapshot)
    {
        PartyManager.INSTANCE.saveTracksMetaData(tracksDataSnapshot);
    }

    public void getTracks()
    {
        final List<PartyTrack> partyTracks;
        final List<TrackViewModel> trackViewModels = new ArrayList<>();
        spotifyService = LoginManager.INSTANCE.getService();

        if (PartyManager.INSTANCE.getTrackMap() != null)
        {
            partyTracks = new ArrayList<>(PartyManager.INSTANCE.getTrackMap().values());
            try
            {
                AsyncTask.execute(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (PartyTrack partyTrack : partyTracks)
                        {
                            Track track = spotifyService.getTrack(partyTrack.trackId);
                            TrackViewModel trackViewModel = new TrackViewModel();
                            trackViewModel.setPlaying(false);
                            trackViewModel.setTrack(track);
                            trackViewModel.setVotes(partyTrack.votes);

                            trackViewModels.add(trackViewModel);
                        }
                        AudioPlayerManager.INSTANCE.setTrackViewModels(trackViewModels);

                        if (view != null)
                        {
                            view.showTracks(trackViewModels);
                        }
                    }
                });
            }
            catch (RetrofitError error)
            {
                Log.d("Spotify retrofit error", "getTracks: " + error.getMessage());
            }

        }
    }


    public void onPlayClicked(Track track)
    {
        String songUri = track.uri;
        AudioPlayerManager.INSTANCE.getPlayer().playUri(operationCallback, songUri, 0, 0);
    }

    private final Player.OperationCallback operationCallback = new Player.OperationCallback()
    {
        @Override
        public void onSuccess()
        {
            Log.d("Spotify player", "onSuccess: OK!");
        }

        @Override
        public void onError(Error error)
        {
            Log.d("Spotify player", "Failed");
        }
    };

    public void onPauseClicked()
    {
        AudioPlayerManager.INSTANCE.getPlayer().pause(operationCallback);
    }

    public void onPlayerResumed()
    {
        AudioPlayerManager.INSTANCE.getPlayer().resume(operationCallback);
    }

    public void onAccessTokenReceived(String userToken)
    {
        Log.d("login", "onActivityResult: success");
        Config playerConfig = new Config(DjRoombaApplication.getContext(), userToken, CLIENT_ID);
        Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver()
        {
            @Override
            public void onInitialized(SpotifyPlayer spotifyPlayer)
            {
                AudioPlayerManager.INSTANCE.setPlayer(spotifyPlayer);
            }

            @Override
            public void onError(Throwable throwable)
            {
                Log.e("LoginActivity", "Could not initialize player: " + throwable.getMessage());
            }
        });
    }


    //==============================================================================================
    // View Interface
    //==============================================================================================

    public interface PartyDetailView extends BaseView
    {
        void showTracks(List<TrackViewModel> tracks);
    }
}
