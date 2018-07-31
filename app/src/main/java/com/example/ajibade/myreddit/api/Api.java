package com.example.ajibade.myreddit.api;

import com.example.ajibade.myreddit.model.ListingData;
import com.example.ajibade.myreddit.model.Post;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {

    @GET("r/{subreddit}/.json")
    Flowable<List<Post>> listRepos(@Path("subreddit") String subReddit);

    @GET("/r/{subreddit}/hot.json")
    Single<ListingData> getTop(@Path("subreddit") String subreddit, @Query("limit") int limit);

    // for after/before param, either get from RedditDataResponse.after/before,
    // or pass RedditNewsDataResponse.name (though this is technically incorrect)
    @GET("/r/{subreddit}/hot.json")
    Single<ListingData> getTopAfter(@Path("subreddit") String subreddit, @Query("after") String after,
                                    @Query("limit") int limit);

    @GET("/r/{subreddit}/hot.json")
    Single<ListingData> getTopBefore(@Path("subreddit") String subreddit, @Query("before") String before,
                                     @Query("limit") int limit);
}
