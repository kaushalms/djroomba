package com.kaushalmandayam.djroomba.managers;

import com.kaushalmandayam.djroomba.models.TrackViewModel;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Singleton to manage Spotify Audio Player
 * <p>
 * Created by kaushalmandayam on 4/29/17.
 */

public enum AudioPlayerManager
{
    //==============================================================================================
    // Class Properties
    //==============================================================================================

    INSTANCE;

    private Player player;
    private List<Track> tracks;
    private TrackViewModel currentTrackViewModel;
    private List<TrackViewModel> trackViewModels;
    private int progress;

    //==============================================================================================
    // Class Instance Methods
    //==============================================================================================

    public List<TrackViewModel> getTrackViewModels()
    {
        return trackViewModels;
    }

    public void setTracks(List<Track> tracks)
    {
        this.tracks = tracks;
    }

    public Player getPlayer()
    {
        return player;
    }

    public void setPlayer(Player player)
    {
        this.player = player;
    }

    public void setTrackViewModels(List<TrackViewModel> trackViewModels)
    {
        this.trackViewModels = trackViewModels;
    }

    public List<Track> getTracks()
    {
        return tracks;
    }

    public void clearTracks()
    {
        trackViewModels = null;
        tracks = null;
    }

    public TrackViewModel getCurrentTrackViewModel()
    {
        return currentTrackViewModel;
    }

    public void setCurrentTrackViewModel(TrackViewModel currentTrackViewModel)
    {
        this.currentTrackViewModel = currentTrackViewModel;
    }

    public void playTrack(int lastClickedPosition)
    {
        player.playUri(new Player.OperationCallback()
        {
            @Override
            public void onSuccess()
            {
                // nothing
            }

            @Override
            public void onError(Error error)
            {
                // nothing
            }
        }, tracks.get(lastClickedPosition).uri, 0, 0);
    }

    public int getProgess()
    {
        return progress;
    }

    public void setProgress(int pregress)
    {
        this.progress = pregress;
    }
}
