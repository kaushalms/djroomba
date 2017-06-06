package com.kaushalmandayam.djroomba.screens.login;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.kaushalmandayam.djroomba.managers.UserManager;
import com.kaushalmandayam.djroomba.screens.base.BasePresenter;
import com.kaushalmandayam.djroomba.screens.base.BaseView;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

import static com.kaushalmandayam.djroomba.Constants.CLIENT_ID;

/**
 * Presenter for LoginActivity
 * <p>
 * Created by kaushalmandayam on 4/29/17.
 */

public class LoginPresenter extends BasePresenter<LoginPresenter.LoginView>
{
    private static final String REDIRECT_URI = "http://localhost:8888/callback";

    // Request code that will be used to verify if the result comes from correct activity
    // Can be any integer
    private static final int REQUEST_CODE = 1337;

    //==============================================================================================
    // Constructor
    //==============================================================================================

    public LoginPresenter()
    {
        // Required empty constructor
    }

    public void onAuthenticateSpotifyLoginClicked()
    {
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();
        view.ShowLoginActivity(request);
    }

    public void onSpotifyAuthReceived(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_CODE)
        {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN)
            {
                UserManager.INSTANCE.setUserToken(response.getAccessToken());
                view.configPlayer(response);
            }
        }

    }

    public void onLoggedIn() {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                SpotifyApi api = new SpotifyApi();
                api.setAccessToken(UserManager.INSTANCE.getUserToken());
                SpotifyService spotify = api.getService();
                String id = spotify.getMe().id;
                if (id != null)
                {
                    UserManager.INSTANCE.setUserId(id);
                }

                Log.d("Login Presenter", "onLoggedIn: userid" + id);
            }
        });

        view.startPartyListActivity();
    }


    //==============================================================================================
    // View Interface
    //==============================================================================================

    public interface LoginView extends BaseView
    {
        void ShowLoginActivity(AuthenticationRequest request);

        void configPlayer(AuthenticationResponse response);

        void startPartyListActivity();
    }
}
