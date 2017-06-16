package com.kaushalmandayam.djroomba.managers;

import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.kaushalmandayam.djroomba.DjRoombaApplication;
import com.kaushalmandayam.djroomba.Utils.PreferenceUtils;
import com.kaushalmandayam.djroomba.models.AccessTokenModel;
import com.kaushalmandayam.djroomba.models.RefreshTokenModel;
import com.kaushalmandayam.djroomba.net.DjRoombaApi;
import com.kaushalmandayam.djroomba.screens.base.BaseView;
import com.kaushalmandayam.djroomba.screens.login.LoginPresenter;
import com.spotify.sdk.android.authentication.AuthenticationRequest;

import java.io.IOException;

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

    AccessTokenListener listener;

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

    private void fetchAccessToken(String refreshToken)
    {
        DjRoombaApi.tokenService().getToken("refresh_token", refreshToken, getEncodedHeaderString())
                .enqueue(new Callback<ResponseBody>()
                {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                    {
                        if(response.isSuccessful())
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
                                parseRefreshTokenResponse(bodyString);
                            }
                            UserManager.INSTANCE.setUserToken(parseAccessTokenResponse(bodyString));
                        }

                        if (UserManager.INSTANCE.getUserToken() != null)
                        {
                            listener.setAccessToken(UserManager.INSTANCE.getUserToken());
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
        AccessTokenModel accessTokenModel = gson.fromJson(bodyString , AccessTokenModel.class);
        return accessTokenModel.access_token;
    }

    private String parseRefreshTokenResponse(String bodyString)
    {
        Gson gson = new Gson();
        RefreshTokenModel refreshTokenModel = gson.fromJson(bodyString , RefreshTokenModel.class);
        return refreshTokenModel.refresh_token;
    }


    public String getEncodedHeaderString()
    {
        String encodedClientSecret = Base64.encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes(), Base64.NO_WRAP);
        return "Basic " + encodedClientSecret;
    }

    public void setTokenListener(AccessTokenListener listener)
    {
        this.listener = listener;
    }

    public interface AccessTokenListener
    {
        void setAccessToken(String accessToken);
    }
}
