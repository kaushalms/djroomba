package com.kaushalmandayam.djroomba.screens.login;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.kaushalmandayam.djroomba.managers.LoginManager;
import com.kaushalmandayam.djroomba.managers.UserManager;
import com.kaushalmandayam.djroomba.models.AccessTokenModel;
import com.kaushalmandayam.djroomba.models.RefreshTokenModel;
import com.kaushalmandayam.djroomba.net.DjRoombaApi;
import com.kaushalmandayam.djroomba.screens.base.BasePresenter;
import com.kaushalmandayam.djroomba.screens.base.BaseView;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.io.IOException;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Image;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kaushalmandayam.djroomba.Constants.CLIENT_ID;
import static com.kaushalmandayam.djroomba.Constants.REDIRECT_URI;
import static com.kaushalmandayam.djroomba.Constants.REQUEST_CODE;

/**
 * Presenter for LoginActivity
 * <p>
 * Created by kaushalmandayam on 4/29/17.
 */

public class LoginPresenter extends BasePresenter<LoginPresenter.LoginView>
{


    //==============================================================================================
    // Constructor
    //==============================================================================================

    public LoginPresenter()
    {
        // Required empty constructor
    }

    public void onAuthenticateSpotifyLoginClicked()
    {
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(
                CLIENT_ID,
                AuthenticationResponse.Type.CODE,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();
        view.ShowLoginActivity(request);
    }

    public void onSpotifyAuthReceived(int requestCode, int resultCode, Intent intent)
    {
        if (requestCode == REQUEST_CODE)
        {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            UserManager.INSTANCE.setUserCode(response.getCode());
            LoginManager.INSTANCE.getRefreshToken(response.getCode());
        }
    }

    public void onLoggedIn()
    {
        AsyncTask.execute(new Runnable()
        {
            @Override
            public void run()
            {
                SpotifyApi api = new SpotifyApi();
                api.setAccessToken(UserManager.INSTANCE.getUserToken());
                SpotifyService spotify = api.getService();
                String id = spotify.getMe().id;
                List<Image> userImages = spotify.getMe().images;
                if (id != null)
                {
                    UserManager.INSTANCE.setUserId(id);
                }
                if (userImages.size() > 0)
                {
                    UserManager.INSTANCE.setUserImageUrl(userImages.get(0).url);
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

        void startPartyListActivity();
    }
}
