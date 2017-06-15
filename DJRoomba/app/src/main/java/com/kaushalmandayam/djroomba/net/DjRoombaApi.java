package com.kaushalmandayam.djroomba.net;

import com.bumptech.glide.integration.okhttp.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.kaushalmandayam.djroomba.Constants;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by kaushalmandayam on 4/27/17.
 */

public enum DjRoombaApi
{
    INSTANCE;

    private static final int MILLISECONDS_PER_SECOND = 1000;
    private static final int TIMEOUT_SECONDS = 15;

    public static final TokenService tokenService() { return INSTANCE.tokenService; }

    private final TokenService tokenService;

    DjRoombaApi()
    {
        TypeAdapter<Boolean> booleanAsIntAdapter = new TypeAdapter<Boolean>()
        {
            @Override
            public void write(JsonWriter out, Boolean value) throws IOException
            {
                if (value == null)
                {
                    out.nullValue();
                } else
                {
                    out.value(value ? 1 : 0);
                }
            }

            @Override
            public Boolean read(JsonReader in) throws IOException
            {
                JsonToken peek = in.peek();
                switch (peek)
                {
                    case BOOLEAN:
                        return in.nextBoolean();
                    case NULL:
                        in.nextNull();
                        return null;
                    case NUMBER:
                        return in.nextInt() == 1;
                    case STRING:
                        return Boolean.parseBoolean(in.nextString());
                    default:
                        throw new IllegalStateException("Expected BOOLEAN or NUMBER but was " + peek);
                }
            }
        };

        // Set up Retrofit
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Boolean.class, booleanAsIntAdapter)
                .registerTypeAdapter(boolean.class, booleanAsIntAdapter)
                .registerTypeAdapter(Date.class, new GsonUTCDateAdapter())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.ACCOUNTS_BASE_URL)
                .client(getClient(true))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        tokenService = retrofit.create(TokenService.class);

    }

    public static OkHttpClient getClient(boolean showLogs)
    {
        OkHttpClient client;

        // Add detailed logging for requests
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();

        if (showLogs)
        {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            client = new OkHttpClient.Builder().addInterceptor(interceptor)
                    .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .build();
        }
        else
        {
            client = new OkHttpClient.Builder().connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS).readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS).build();
        }

        return client;
    }

    public static class GsonUTCDateAdapter implements JsonSerializer<Date>, JsonDeserializer<Date>
    {
        public GsonUTCDateAdapter()
        {
        }

        @Override
        public synchronized JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext)
        {
            return new JsonPrimitive(date.getTime() / MILLISECONDS_PER_SECOND);
        }

        @Override
        public synchronized Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
        {
            long epoch = jsonElement.getAsLong();

            if (epoch == 0)
            {
                return null;
            }

            return new Date(epoch * MILLISECONDS_PER_SECOND);
        }
    }
}

