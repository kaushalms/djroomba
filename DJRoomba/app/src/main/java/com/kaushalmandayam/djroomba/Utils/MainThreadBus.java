package com.kaushalmandayam.djroomba.Utils;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

/**
 * Otto bus that posts on main thread from any type of thread
 * <p>
 * Created on 4/27/17.
 *
 * @author Kaushal Mandayam
 */

public class MainThreadBus extends Bus
{
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void post(final Object event)
    {
        if (Looper.myLooper() == Looper.getMainLooper())
        {
            super.post(event);
        }
        else
        {
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    MainThreadBus.super.post(event);
                }
            });
        }
    }
}

