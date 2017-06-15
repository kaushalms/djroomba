package com.kaushalmandayam.djroomba.net;

import com.kaushalmandayam.djroomba.models.RefreshTokenParameters;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * API methods to refresh token
 * <p>
 * Created on 6/15/17.
 *
 * @author Kaushal Mandayam
 */

public interface TokenService
{
    @POST("api/token")
    Call<ResponseBody> fetchRefreshToken(@Body RequestBody params);
}
