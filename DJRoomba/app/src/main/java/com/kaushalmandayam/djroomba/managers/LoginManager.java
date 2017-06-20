package com.kaushalmandayam.djroomba.managers;

import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;

import com.kaushalmandayam.djroomba.Utils.PreferenceUtils;
import com.kaushalmandayam.djroomba.models.AccessTokenModel;
import com.kaushalmandayam.djroomba.models.RefreshTokenModel;
import com.kaushalmandayam.djroomba.net.DjRoombaApi;

import java.io.IOException;

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
    INSTANCE;
    RefreshTokenListener refreshTokenListener;
    AccessTokenListener accessTokenListener;
    PartyListAccessTokenListener partyListAccessTokenListener;
    final SpotifyApi api = new SpotifyApi();
    final SpotifyService spotifyService = api.getService();

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
                                if (refreshTokenListener != null)
                                {
                                    refreshTokenListener.setAccessToken(UserManager.INSTANCE.getUserToken());
                                }

                                // Listener for partyDetail Activity
                                if (accessTokenListener != null)
                                {
                                    accessTokenListener.setAccessToken(UserManager.INSTANCE.getUserToken());
                                }

                                // Listener for partyList Activity
                                if (partyListAccessTokenListener != null)
                                {
                                    partyListAccessTokenListener.setAccessToken(UserManager.INSTANCE.getUserToken());
                                }

                                refreshTokenListener = null;
                                accessTokenListener = null;
                                partyListAccessTokenListener = null;
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


    public String getEncodedHeaderString()
    {
        String encodedClientSecret = Base64.encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes(), Base64.NO_WRAP);
        return "Basic " + encodedClientSecret;
    }

    public void setRefreshTokenListener(RefreshTokenListener refreshTokenListener)
    {
        this.refreshTokenListener = refreshTokenListener;
    }

    public SpotifyService getService()
    {
        return spotifyService;
    }

    public void setAccesstokenListener(AccessTokenListener accesstokenListener)
    {
        this.accessTokenListener = accesstokenListener;
    }

    public void setPartyListAccessTokenListener(PartyListAccessTokenListener partyListAccessTokenListener)
    {
        this.partyListAccessTokenListener = partyListAccessTokenListener;
    }


    public interface AccessTokenListener
    {
        void setAccessToken(String userToken);
    }

    public interface RefreshTokenListener
    {
        void setAccessToken(String userToken);
    }

    public interface PartyListAccessTokenListener
    {
        void setAccessToken(String userToken);
    }
}
