package com.example.ajibade.myreddit.model;

import android.arch.paging.PagedList;

import com.example.ajibade.myreddit.api.NetworkState;
import com.example.ajibade.myreddit.data.MyBoundaryCallback;

import io.reactivex.Flowable;
import io.reactivex.subjects.BehaviorSubject;

public class Listing {
    public Flowable<PagedList<Post>> pagedList;
    public BehaviorSubject<NetworkState> networkState;
    public MyBoundaryCallback.RefreshCallBack refreshCallBack;

    public Listing(Flowable<PagedList<Post>> pagedList, BehaviorSubject<NetworkState> networkState,
                   MyBoundaryCallback.RefreshCallBack refreshCallBack) {
        this.pagedList = pagedList;
        this.networkState = networkState;
        this.refreshCallBack = refreshCallBack;
    }
}
