package com.kaushalmandayam.djroomba.screens.login;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.ArrayMap;
import android.util.Log;

import com.kaushalmandayam.djroomba.managers.UserManager;
import com.kaushalmandayam.djroomba.net.DjRoombaApi;
import com.kaushalmandayam.djroomba.screens.base.BasePresenter;
import com.kaushalmandayam.djroomba.screens.base.BaseView;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Image;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kaushalmandayam.djroomba.Constants.CLIENT_ID;
import static com.kaushalmandayam.djroomba.Constants.CLIENT_SECRET;

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
            getRefreshToken(response.getCode());
            if (response.getType() == AuthenticationResponse.Type.CODE)
            {
                UserManager.INSTANCE.setUserCode(response.getCode());
                UserManager.INSTANCE.setUserToken(response.getAccessToken());
                view.configPlayer(response);
            }
        }
    }

    private void getRefreshToken(String accessCode)
    {
        Map<String, Object> map = new ArrayMap<>();
        map.put("grant_type", "authorization_code");
        map.put("code", accessCode);
        map.put("redirect_uri", REDIRECT_URI);
        map.put("client_id", CLIENT_ID);
        map.put("client_secret", CLIENT_SECRET);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/x-www-form-urlencoded"),
                                                (new JSONObject(map)).toString());

        DjRoombaApi.tokenService().fetchRefreshToken(body)
                                    .enqueue(new Callback<ResponseBody>()
                                    {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                                        {
                                            Log.d("refresh token", "onResponse: " + response.message());
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t)
                                        {

                                        }
                                    });
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

        void configPlayer(AuthenticationResponse response);

        void startPartyListActivity();
    }
}
