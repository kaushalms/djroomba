package com.kaushalmandayam.djroomba.net;

import com.kaushalmandayam.djroomba.models.RefreshTokenModel;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
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
    @POST("/api/token")
    Call<ResponseBody> fetchRefreshToken(@Body RequestBody body,
                                         @Header("Authorization") String auth);

    @FormUrlEncoded
    @POST("/api/token")
    Call<ResponseBody> getRefreshToken(@Field("grant_type") String grantType,
                                       @Field("code") String code,
                                       @Field("redirect_uri") String redirect_uri,
                                       @Header("Authorization") String auth);

    @FormUrlEncoded
    @POST("/api/token")
    Call<ResponseBody> getToken(@Field("grant_type") String grantType,
                                @Field("refresh_token") String code,
                                @Header("Authorization") String auth);
}
