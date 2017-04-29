package com.kaushalmandayam.djroomba.screens.login;

import com.kaushalmandayam.djroomba.screens.base.BasePresenter;
import com.kaushalmandayam.djroomba.screens.base.BaseView;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import static com.kaushalmandayam.djroomba.Constants.CLIENT_ID;

/**
 * Presenter for LoginActivity
 *
 * Created by kaushalmandayam on 4/29/17.
 */

public class LoginPresenter  extends BasePresenter<LoginPresenter.LoginView>
{
    private static final String REDIRECT_URI = "" ;

    //==============================================================================================
    // Constructor
    //==============================================================================================

    public LoginPresenter()
    {
        // Required empty constructor
    }

    public void onCreate()
    {
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();
        view.ShowLoginActivity(request);
    }

    //==============================================================================================
    // View Interface
    //==============================================================================================

    public interface LoginView extends BaseView
    {
        void ShowLoginActivity(AuthenticationRequest request);

    }
}
