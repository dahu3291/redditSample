package com.example.ajibade.myreddit;

import android.app.Activity;
import android.app.Application;

import com.example.ajibade.myreddit.di.AppComponent;
import com.example.ajibade.myreddit.di.DaggerAppComponent;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

public class MyApp extends Application implements HasActivityInjector {

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    @Override
    public void onCreate() {
        super.onCreate();

        AppComponent component = DaggerAppComponent.builder().application(this).build();
        component.inject(this);
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }
}
