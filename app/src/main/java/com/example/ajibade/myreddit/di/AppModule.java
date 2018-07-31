package com.example.ajibade.myreddit.di;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.example.ajibade.myreddit.db.AppDb;
import com.example.ajibade.myreddit.db.PostDao;

import dagger.Module;
import dagger.Provides;


@Module(includes = {ApiModule.class, ViewModelModule.class})
class AppModule {

    @AppScope
    @Provides
    AppDb provideDb(Application app) {
        return Room.databaseBuilder(app, AppDb.class,"myreddit.db")
                .fallbackToDestructiveMigration().build();
    }

    @AppScope @Provides
    PostDao provideUserDao(AppDb db) {return db.postDao();}
}
