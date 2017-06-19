package com.kaushalmandayam.djroomba.screens.PartyDetail;


import android.os.AsyncTask;
import android.util.Log;

import com.kaushalmandayam.djroomba.managers.AudioPlayerManager;
import com.kaushalmandayam.djroomba.managers.LoginManager;
import com.kaushalmandayam.djroomba.models.Party;
import com.kaushalmandayam.djroomba.screens.base.BasePresenter;
import com.kaushalmandayam.djroomba.screens.base.BaseView;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
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
        final List<String> playListSongs = party.getPartyPlayListSongs();
        final List<Track> tracks = new ArrayList<>();
        final SpotifyService spotifyService = LoginManager.INSTANCE.getService();

        try
        {
            AsyncTask.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    for (String songId : playListSongs)
                    {
                        tracks.add(spotifyService.getTrack(songId));
                    }

                    view.showTracks(tracks);
                }
            });
        }
        catch (RetrofitError error)
        {
            Log.d("Spotify retrofit error", "getTracks: " + error.getMessage());
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

    //==============================================================================================
    // View Interface
    //==============================================================================================

    public interface PartyDetailView extends BaseView
    {
        void showTracks(List<Track> tracks);
    }
}
