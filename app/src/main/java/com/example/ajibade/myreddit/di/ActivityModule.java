package com.example.ajibade.myreddit.di;

import com.example.ajibade.myreddit.ui.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector(modules = MainFragBuildersModule.class)
    abstract MainActivity contributeMainActivity();

}
