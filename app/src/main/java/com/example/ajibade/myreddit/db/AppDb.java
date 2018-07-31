package com.example.ajibade.myreddit.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.ajibade.myreddit.model.Post;

@Database(entities = {Post.class}, version = 1, exportSchema = false)
public abstract class AppDb extends RoomDatabase {

    public abstract PostDao postDao();
}
