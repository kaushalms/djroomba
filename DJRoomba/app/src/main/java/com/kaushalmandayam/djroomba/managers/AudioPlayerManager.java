package com.kaushalmandayam.djroomba.managers;

import com.kaushalmandayam.djroomba.models.TrackViewModel;
import com.spotify.sdk.android.player.Player;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Singleton to manage Spotify Audio Player
 *
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

    public List<TrackViewModel> getTrackViewModels()
    {
        return trackViewModels;
    }

    private List<TrackViewModel> trackViewModels;

    public void setTracks(List<Track> tracks)
    {
        this.tracks = tracks;
    }

    //==============================================================================================
    // Class Instance Methods
    //==============================================================================================

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
}
