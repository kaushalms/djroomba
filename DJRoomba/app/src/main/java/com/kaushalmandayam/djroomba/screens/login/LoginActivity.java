package com.kaushalmandayam.djroomba.screens.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.kaushalmandayam.djroomba.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kaushalmandayam.djroomba.Utils.PreferenceUtils;
import com.kaushalmandayam.djroomba.managers.AudioPlayerManager;
import com.kaushalmandayam.djroomba.screens.base.BaseActivity;
import com.kaushalmandayam.djroomba.screens.login.LoginPresenter.LoginView;
import com.kaushalmandayam.djroomba.screens.partyList.PartyListActivity;
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

import butterknife.OnClick;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

import static com.kaushalmandayam.djroomba.Constants.CLIENT_ID;


/**
 * Created by kaushalmandayam on 4/29/17.
 */

public class LoginActivity extends BaseActivity<LoginPresenter> implements LoginView,
        ConnectionStateCallback, SpotifyPlayer.NotificationCallback {
    // Request code that will be used to verify if the result comes from correct activity
    // Can be any integer
    private static final int REQUEST_CODE = 1337;

    private Player player;
    private DatabaseReference mCommentsReference;
    private DatabaseReference mPostReference;

    //==============================================================================================
    // Static Methods
    //==============================================================================================

    public static void start(Context context) {
        Intent starter = new Intent(context, LoginActivity.class);
        context.startActivity(starter);
    }

    //==============================================================================================
    // Life-cycle Methods
    //==============================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        attachPresenter(new LoginPresenter(), this);
        mPostReference = FirebaseDatabase.getInstance().getReference()
                .child("parties");

        mPostReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
//                                                         Party party = messageSnapshot.getValue(Party.class);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                //     Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });

//        if (PreferenceUtils.getUserLoggedInStatus(this) == true)
//        {
//            PartyListActivity.start(this);
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        presenter.onSpotifyAuthReceived(requestCode, resultCode, intent);
    }

    @Override
    public void onLoggedIn() {
        Log.d("Login", "onLoggedIn: success ");
    //    PreferenceUtils.setUserLoggedInStatus(this, true);
        presenter.onLoggedIn();
    }

    public void onLoggedOut() {
        PreferenceUtils.setUserLoggedInStatus(this, false);
    }

    @Override
    public void onLoginFailed(Error error) {
        Log.d("LOGIN FAILED", "onLoginFailed: " + error.name());
    }

    @Override
    public void onTemporaryError() {

    }

    @Override
    public void onConnectionMessage(String s) {

    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {

    }

    @Override
    public void onPlaybackError(Error error) {

    }


    //==============================================================================================
    // Presenter Method Implementations
    //==============================================================================================

    @Override
    public void ShowLoginActivity(AuthenticationRequest request) {
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    public void configPlayer(AuthenticationResponse response) {
        Log.d("login", "onActivityResult: success");
        Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
        player = Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
            @Override
            public void onInitialized(SpotifyPlayer spotifyPlayer) {
                AudioPlayerManager.INSTANCE.setPlayer(player);

                //TODO move call backs to different activity
                player.addConnectionStateCallback(LoginActivity.this);
                player.addNotificationCallback(LoginActivity.this);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("LoginActivity", "Could not initialize player: " + throwable.getMessage());
            }
        });
    }

    @Override
    public void startPartyListActivity() {
        PartyListActivity.start(this);
    }


    //================================================================================
    // Button Click Methods
    //================================================================================

    @OnClick(R.id.spotifyLoginButton)
    public void logiButtonPressed() {
        presenter.onAuthenticateSpotifyLoginClicked();
    }
}
