package com.example.ajibade.myreddit.di;

import com.example.ajibade.myreddit.ui.fragments.SubRedditFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MainFragBuildersModule {

    @ContributesAndroidInjector
    abstract SubRedditFragment contributeSubRedditFragment();
}
