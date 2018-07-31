package com.example.ajibade.myreddit.di;

import com.example.ajibade.myreddit.api.Api;
import com.example.ajibade.myreddit.model.ListingData;
import com.example.ajibade.myreddit.model.Post;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dagger.Module;
import dagger.Provides;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = {NetworkModule.class})
class ApiModule {

    @Provides @AppScope
    Api provideApi(Retrofit retrofit) {
        return retrofit.create(Api.class);
    }

    @Provides @AppScope
    Retrofit provideRetrofit(OkHttpClient okHttpClient, Gson apiGson){
        return new Retrofit.Builder()
                .baseUrl("https://www.reddit.com/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(apiGson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();
    }

    @Provides @AppScope
    Gson provideGson(){

        return new GsonBuilder()
                .registerTypeAdapter(Post.class, new Post.TypeAdapter())
                .registerTypeAdapter(ListingData.class, new ListingData.TypeAdapter())
                .create();
    }
}
