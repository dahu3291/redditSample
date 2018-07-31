package com.example.ajibade.myreddit.data;


import android.app.Application;
import android.arch.paging.DataSource;
import android.arch.paging.PagedList;
import android.arch.paging.RxPagedListBuilder;
import android.util.Log;

import com.example.ajibade.myreddit.R;
import com.example.ajibade.myreddit.api.Api;
import com.example.ajibade.myreddit.db.AppDb;
import com.example.ajibade.myreddit.db.PostDao;
import com.example.ajibade.myreddit.di.AppScope;
import com.example.ajibade.myreddit.model.Listing;
import com.example.ajibade.myreddit.model.ListingData;
import com.example.ajibade.myreddit.model.Post;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;

@AppScope
public class MyRepository {

    private Application app;
    private Api api;
    private AppDb db;
    private PostDao postDao;

    @Inject
    MyRepository(Application app, Api api, AppDb db, PostDao postDao) {
        this.app = app;
        this.api = api;
        this.db = db;
        this.postDao = postDao;
    }

    /**
     * Inserts the response into the database while also assigning position indices to items.
     */
    private void insertResultIntoDb(String subredditName, ListingData listingData, boolean clearPrevious) {
        Log.d("REPOSITORY", "insertResultIntoDb");
        db.runInTransaction(() -> {
            if (clearPrevious) db.postDao().deleteBySubreddit(subredditName);

            int start = db.postDao().getNextIndexInSubreddit(subredditName);
            List<Post> posts = listingData.getChildren();
           for (int i = 0; i < posts.size(); i ++) {
               posts.get(i).setIndexInResponse(start + i);
           }
           db.postDao().insert(posts);

        });
    }

//    PagingCallBack pagingCallBack = this::insertResultIntoDb;

    Listing getPosts(String subReddit, MyBoundaryCallback.DisposableCallBack disposableCallBack) {

        MyBoundaryCallback boundaryCallback = new MyBoundaryCallback(subReddit, 10,
                app.getString(R.string.default_error), api, this::insertResultIntoDb, disposableCallBack);

        DataSource.Factory<Integer, Post> myConcertDataSource = postDao.postsBySubreddit(subReddit);

        Flowable<PagedList<Post>> concertList = new RxPagedListBuilder<>(myConcertDataSource, 20)
                .setBoundaryCallback(boundaryCallback)
                .buildFlowable(BackpressureStrategy.BUFFER);

        return new Listing(concertList, boundaryCallback.networkState, boundaryCallback.refreshCallBack);
    }

    public interface PagingCallBack {
        void handleResponse(String subReddit, ListingData listingData, boolean clearPrevious);
    }

}
