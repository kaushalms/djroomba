package com.kaushalmandayam.djroomba.managers;

import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;

import com.kaushalmandayam.djroomba.Utils.PreferenceUtils;
import com.kaushalmandayam.djroomba.models.AccessTokenModel;
import com.kaushalmandayam.djroomba.models.RefreshTokenModel;
import com.kaushalmandayam.djroomba.net.DjRoombaApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kaushalmandayam.djroomba.Constants.CLIENT_ID;
import static com.kaushalmandayam.djroomba.Constants.CLIENT_SECRET;
import static com.kaushalmandayam.djroomba.Constants.REDIRECT_URI;

/**
 * Manager to handle login requests
 * <p>
 * Created by kaushalmandayam on 4/29/17.
 */

public enum LoginManager
{
    //==============================================================================================
    // Class Properties
    //==============================================================================================

    INSTANCE;

    private List<AccessTokenListener> listeners;
    private final SpotifyApi api = new SpotifyApi();
    private final SpotifyService spotifyService = api.getService();

    //==============================================================================================
    // Constructor
    //==============================================================================================

    LoginManager()
    {
        listeners = new ArrayList<>();
    }

    //==============================================================================================
    // Class Instance Methods
    //==============================================================================================

    public void getRefreshToken(String accessCode)
    {
        if (accessCode == null)
        {
            accessCode = PreferenceUtils.getAccessCode();
        }

        DjRoombaApi.tokenService().getRefreshToken("authorization_code", accessCode, REDIRECT_URI, getEncodedHeaderString())
                .enqueue(new Callback<ResponseBody>()
                {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                    {
                        if (response.isSuccessful())
                        {
                            String bodyString = "";
                            try
                            {
                                bodyString = new String(response.body().bytes());
                            }
                            catch (IOException e)
                            {
                                Log.d("refresh token", "onError " + e.getMessage());
                            }

                            if (!bodyString.isEmpty())
                            {
                                PreferenceUtils.setRefreshToken(parseRefreshTokenResponse(bodyString));
                                parseRefreshTokenResponse(bodyString);
                            }

                            fetchAccessToken(parseRefreshTokenResponse(bodyString));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t)
                    {
                        Log.d("refresh token", "onError");
                    }
                });
    }

    public void fetchAccessToken(String refreshToken)
    {
        DjRoombaApi.tokenService().getToken("refresh_token", refreshToken, getEncodedHeaderString())
                .enqueue(new Callback<ResponseBody>()
                {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                    {
                        if (response.isSuccessful())
                        {
                            String bodyString = "";
                            try
                            {
                                bodyString = new String(response.body().bytes());
                            }
                            catch (IOException e)
                            {
                                Log.d("refresh token", "onError " + e.getMessage());
                            }

                            if (!bodyString.isEmpty())
                            {
                                UserManager.INSTANCE.setUserAccessToken(parseAccessTokenResponse(bodyString));
                                api.setAccessToken(UserManager.INSTANCE.getUserToken());


                                // Listener for login Activity
                                setAccessToken(UserManager.INSTANCE.getUserToken());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t)
                    {
                        Log.d("refresh token", "onError");
                    }
                });
    }

    public void setAccessToken(String accessToken)
    {
        for (AccessTokenListener listener : listeners)
        {
            listener.setAccessToken(accessToken);
        }
    }

    private String parseAccessTokenResponse(String bodyString)
    {
        Gson gson = new Gson();
        AccessTokenModel accessTokenModel = gson.fromJson(bodyString, AccessTokenModel.class);
        return accessTokenModel.access_token;
    }

    private String parseRefreshTokenResponse(String bodyString)
    {
        Gson gson = new Gson();
        RefreshTokenModel refreshTokenModel = gson.fromJson(bodyString, RefreshTokenModel.class);
        return refreshTokenModel.refresh_token;
    }

    public SpotifyService getService()
    {
        return spotifyService;
    }

    public String getEncodedHeaderString()
    {
        String encodedClientSecret = Base64.encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes(), Base64.NO_WRAP);
        return "Basic " + encodedClientSecret;
    }

    // =============================================================================================
    // Listeners Methods
    // =============================================================================================

    public void subscribeStateListener(AccessTokenListener listener)
    {
        if (!listeners.contains(listener))
        {
            listeners.add(listener);
        }
    }

    public void unSubscribeStateListener(AccessTokenListener listener)
    {
        listeners.remove(listener);
    }

    // =============================================================================================
    // Listener Interface
    // =============================================================================================

    public interface AccessTokenListener
    {
        void setAccessToken(String userToken);
    }


}
