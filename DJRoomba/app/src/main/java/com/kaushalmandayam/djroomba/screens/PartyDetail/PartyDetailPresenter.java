package com.kaushalmandayam.djroomba.screens.PartyDetail;

import android.os.AsyncTask;
import android.util.Log;

import com.kaushalmandayam.djroomba.managers.UserManager;
import com.kaushalmandayam.djroomba.models.Party;
import com.kaushalmandayam.djroomba.screens.base.BasePresenter;
import com.kaushalmandayam.djroomba.screens.base.BaseView;
import com.spotify.sdk.android.player.Spotify;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.RetrofitError;

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
    // Class Instance Methods
    //==============================================================================================

    public void getTracks(Party party)
    {
        // todo move spotify api and service to a manager

        final SpotifyApi api = new SpotifyApi();
        final SpotifyService spotify = api.getService();
        final List<String> playListSongs = party.getPartyPlayListSongs();
        final List<Track> tracks = new ArrayList<>();

        api.setAccessToken(UserManager.INSTANCE.getUserToken());

        try
        {
            AsyncTask.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    for (String songId : playListSongs)
                    {
                        tracks.add(spotify.getTrack(songId));
                    }

                    view.showTracks(tracks);
                }
            });
        }
        catch (Error error)
        {
            Log.d("Spotify retrofit error", "getTracks: " + error.getMessage());
        }
    }

    //==============================================================================================
    // View Interface
    //==============================================================================================

    public interface PartyDetailView extends BaseView
    {
        void showTracks(List<Track> tracks);
    }
}
