package com.kaushalmandayam.djroomba.screens.TrackList;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kaushalmandayam.djroomba.managers.AudioPlayerManager;
import com.kaushalmandayam.djroomba.managers.LoginManager;
import com.kaushalmandayam.djroomba.managers.PartyManager;
import com.kaushalmandayam.djroomba.managers.UserManager;
import com.kaushalmandayam.djroomba.models.Party;
import com.kaushalmandayam.djroomba.models.PartyTrack;
import com.kaushalmandayam.djroomba.models.TrackViewModel;
import com.kaushalmandayam.djroomba.screens.base.BasePresenter;
import com.kaushalmandayam.djroomba.screens.base.BaseView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;

import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.RetrofitError;

/**
 * Created by kaushalmandayam on 5/7/17.
 */

class TrackListPresenter extends BasePresenter<TrackListPresenter.TrackListView>
{
    //==============================================================================================
    // Activity/public Methods
    //==============================================================================================

    private SpotifyService spotifyService;
    List<PartyTrack> partyTracks = new ArrayList<>();
    List<Track> tracks = new ArrayList<>();
    private DatabaseReference tracksNodeReference;

    void onTrackSearched(final String searchParameter)
    {
        final SpotifyApi api = new SpotifyApi();
        api.setAccessToken(UserManager.INSTANCE.getUserToken());
        if (!searchParameter.isEmpty())
        {
            try
            {
                AsyncTask.execute(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        SpotifyService spotifyService = api.getService();
                        TracksPager tracksPager = spotifyService.searchTracks(searchParameter);
                        int tracks = tracksPager.tracks.items.size();
                        Log.d("TRACKS", "afterTextChanged: " + tracks);
                        addToTracks(tracksPager);
                    }
                });
            }
            catch (RetrofitError error)
            {
                SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
                // handle error
            }
        }
    }

    //==============================================================================================
    // Class Instance Methods
    //==============================================================================================

    private void addToTracks(TracksPager tracksPager)
    {
        tracks.clear();
        for (Track track : tracksPager.tracks.items)
        {

            tracks.add(track);
        }

        view.populateTrack(tracks);
    }

    public void updatePlaylist(final Track track)
    {
        PartyTrack partyTrack = new PartyTrack();
        partyTrack.trackId = track.id;
        PartyManager.INSTANCE.updatePartyTrack(partyTrack);

        DatabaseReference tracksNodeReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("parties/" + PartyManager.INSTANCE.getParty().getPartyId() +"/tracks");

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
                // do nothing
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
                            view.startPartyDetailActivity();
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

    //==============================================================================================
    // View Interface
    //==============================================================================================

    public interface TrackListView extends BaseView
    {
        void populateTrack(List<Track> tracks);

        void startPartyDetailActivity();
    }
}
