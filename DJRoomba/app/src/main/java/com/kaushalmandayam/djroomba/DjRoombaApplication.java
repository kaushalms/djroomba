package com.kaushalmandayam.djroomba;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.GlideModule;
import com.kaushalmandayam.djroomba.Utils.MainThreadBus;
import com.kaushalmandayam.djroomba.net.DjRoombaApi;
import com.squareup.otto.Bus;

import java.io.InputStream;

/**
 * Created by kaushalmandayam on 4/27/17.
 */

public class DjRoombaApplication extends MultiDexApplication implements GlideModule
{
    private static Context context;
    private static Activity currentActivity;
    private static MainThreadBus bus;

    @Override
    public void onCreate()
    {
        super.onCreate();
        context = getApplicationContext();
        bus = new MainThreadBus();

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks()
        {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle)
            {

            }

            @Override
            public void onActivityStarted(Activity activity)
            {

            }

            @Override
            public void onActivityResumed(Activity activity)
            {
                currentActivity = activity;
            }

            @Override
            public void onActivityPaused(Activity activity)
            {
                if (currentActivity == activity)
                {
                    currentActivity = null;
                }
            }

            @Override
            public void onActivityStopped(Activity activity)
            {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle)
            {

            }

            @Override
            public void onActivityDestroyed(Activity activity)
            {

            }
        });
    }

    public static Context getContext()
    {
        return context;
    }

    public static Activity getCurrentActivity()
    {
        return currentActivity;
    }

    public static Bus getBus()
    {
        return bus;
    }

    //==============================================================================================
    // Glide Module
    //==============================================================================================

    @Override
    public void applyOptions(Context context, GlideBuilder builder)
    {
        // Improve photo quality
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
    }

    @Override
    public void registerComponents(Context context, Glide glide)
    {
        glide.register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader
                .Factory(DjRoombaApi.getClient(false)));
    }
}