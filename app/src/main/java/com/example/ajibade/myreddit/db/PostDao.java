package com.example.ajibade.myreddit.db;

import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.ajibade.myreddit.model.Post;

import java.util.List;


import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface PostDao {

    @Insert(onConflict = REPLACE)
    void insert(List<Post> posts);

    @Query("SELECT * FROM posts WHERE subreddit = :subreddit ORDER BY indexInResponse ASC")
    DataSource.Factory<Integer, Post> postsBySubreddit(String subreddit);

    @Query("DELETE FROM posts WHERE subreddit = :subreddit")
    void deleteBySubreddit(String subreddit);

    @Query("SELECT MAX(indexInResponse) + 1 FROM posts WHERE subreddit = :subreddit")
    int getNextIndexInSubreddit(String subreddit);

}
