package com.kaushalmandayam.djroomba.screens.TrackList;

import android.os.AsyncTask;
import android.util.Log;

import com.kaushalmandayam.djroomba.managers.UserManager;
import com.kaushalmandayam.djroomba.screens.base.BasePresenter;
import com.kaushalmandayam.djroomba.screens.base.BaseView;

import java.util.ArrayList;
import java.util.List;

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

    List<Track> tracks = new ArrayList<>();

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

    //==============================================================================================
    // View Interface
    //==============================================================================================

    public interface TrackListView extends BaseView
    {
        void populateTrack(List<Track> tracks);
    }
}
