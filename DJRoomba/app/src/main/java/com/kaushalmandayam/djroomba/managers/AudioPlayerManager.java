package com.kaushalmandayam.djroomba.managers;

import com.spotify.sdk.android.player.Player;

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




}
