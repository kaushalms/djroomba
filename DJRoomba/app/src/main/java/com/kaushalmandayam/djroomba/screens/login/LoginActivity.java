package com.kaushalmandayam.djroomba.screens.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.kaushalmandayam.djroomba.R;
import com.kaushalmandayam.djroomba.screens.base.BaseActivity;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import static com.kaushalmandayam.djroomba.Constants.CLIENT_ID;

/**
 * Created by kaushalmandayam on 4/29/17.
 */

public class LoginActivity extends BaseActivity<LoginPresenter> implements LoginPresenter.LoginView,
        ConnectionStateCallback, SpotifyPlayer.NotificationCallback
{

    // Request code that will be used to verify if the result comes from correct activity
    // Can be any integer
    private static final int REQUEST_CODE = 1337;
    private Player mPlayer;

    //==============================================================================================
    // Life-cycle Methods
    //==============================================================================================

    public static void start(Context context)
    {
        Intent starter = new Intent(context, LoginActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        attachPresenter(new LoginPresenter(), this);

        presenter.onCreate();



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE)
        {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN)
            {
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver()
                {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer)
                    {

                        mPlayer.addConnectionStateCallback(LoginActivity.this);
                        mPlayer.addNotificationCallback(LoginActivity.this);
                    }

                    @Override
                    public void onError(Throwable throwable)
                    {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }

    @Override
    public void onLoggedIn()
    {

    }

    @Override
    public void onLoggedOut()
    {

    }

    @Override
    public void onLoginFailed(Error error)
    {

    }

    @Override
    public void onTemporaryError()
    {

    }

    @Override
    public void onConnectionMessage(String s)
    {

    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent)
    {

    }

    @Override
    public void onPlaybackError(Error error)
    {

    }


    //==============================================================================================
    // Presenter Method Implementations
    //==============================================================================================

    @Override
    public void ShowLoginActivity(AuthenticationRequest request)
    {
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }
}
