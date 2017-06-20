package com.kaushalmandayam.djroomba.screens.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.kaushalmandayam.djroomba.R;
import com.kaushalmandayam.djroomba.DjRoombaApplication;
import com.kaushalmandayam.djroomba.Utils.PreferenceUtils;
import com.kaushalmandayam.djroomba.managers.AudioPlayerManager;
import com.kaushalmandayam.djroomba.managers.LoginManager;
import com.kaushalmandayam.djroomba.screens.PartyList.PartyListActivity;
import com.kaushalmandayam.djroomba.screens.base.BaseActivity;
import com.kaushalmandayam.djroomba.screens.login.LoginPresenter.LoginView;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import butterknife.OnClick;

import static com.kaushalmandayam.djroomba.Constants.CLIENT_ID;


/**
 * Created by kaushalmandayam on 4/29/17.
 */

public class LoginActivity extends BaseActivity<LoginPresenter> implements LoginView,
        LoginManager.RefreshTokenListener,
        ConnectionStateCallback,
        SpotifyPlayer.NotificationCallback
{
    // Request code that will be used to verify if the result comes from correct activity
    // Can be any integer
    private static final int REQUEST_CODE = 1337;
    private static final String TAG = "LoginActivity";
    private Player spotifyPlayer;

    //==============================================================================================
    // Static Methods
    //==============================================================================================

    public static void start(Context context)
    {
        Intent starter = new Intent(context, LoginActivity.class);
        context.startActivity(starter);
    }

    //==============================================================================================
    // Life-cycle Methods
    //==============================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        attachPresenter(new LoginPresenter(), this);

        if (PreferenceUtils.getUserLoggedInStatus() && PreferenceUtils.getRefreshToken() != null)
        {
            PartyListActivity.start(this);
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        LoginManager.INSTANCE.setRefreshTokenListener(this);
        presenter.onSpotifyAuthReceived(requestCode, resultCode, intent);
    }

    //==============================================================================================
    // ConnectionStateCallback Implementations
    //==============================================================================================

    @Override
    public void onLoggedIn()
    {
        Log.d("Login", "onLoggedIn: success ");
        PreferenceUtils.setUserLoggedInStatus(true);
        presenter.onLoggedIn();
    }

    public void onLoggedOut()
    {
        PreferenceUtils.setUserLoggedInStatus(false);
    }

    @Override
    public void onLoginFailed(Error error)
    {
        Log.d("LOGIN FAILED", "onLoginFailed: " + error.name());
    }


    @Override
    public void onTemporaryError()
    {

    }

    @Override
    public void onConnectionMessage(String s)
    {

    }

    //==============================================================================================
    //   SpotifyPlayer NotificationCallback Implementations
    //==============================================================================================

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

    @Override
    public void startPartyListActivity()
    {
        PartyListActivity.start(this);
        finish();
    }

    //================================================================================
    // Button Click Methods
    //================================================================================

    @OnClick(R.id.spotifyLoginButton)
    public void loginButtonPressed()
    {
        presenter.onAuthenticateSpotifyLoginClicked();
    }

    @Override
    public void setAccessToken(String userToken)
    {
        Log.d("login", "onActivityResult: success");
        Config playerConfig = new Config(DjRoombaApplication.getContext(), userToken, CLIENT_ID);
        spotifyPlayer = Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver()
        {
            @Override
            public void onInitialized(SpotifyPlayer spotifyPlayer)
            {
                AudioPlayerManager.INSTANCE.setPlayer(spotifyPlayer);
                spotifyPlayer.addConnectionStateCallback(LoginActivity.this);
                spotifyPlayer.addNotificationCallback(LoginActivity.this);
            }

            @Override
            public void onError(Throwable throwable)
            {
                Log.e("LoginActivity", "Could not initialize player: " + throwable.getMessage());
            }
        });
    }
}
