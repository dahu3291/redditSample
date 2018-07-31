package com.example.ajibade.myreddit.data;

import android.arch.paging.PagedList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.ajibade.myreddit.api.Api;
import com.example.ajibade.myreddit.api.NetworkState;
import com.example.ajibade.myreddit.model.Post;
import com.example.ajibade.myreddit.util.ErrorHandler;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.BehaviorSubject;

public class MyBoundaryCallback extends PagedList.BoundaryCallback<Post> {
    private String mQuery;
    private int networkPageSize;
    private String defaultError;
    private Api api;
    private MyRepository.PagingCallBack pagingCallBack;
    private DisposableCallBack disposableCallBack;
    BehaviorSubject<NetworkState> networkState = BehaviorSubject.create();

    private Post itemAtEnd;

    MyBoundaryCallback(String query, int networkPageSize, String defaultError, Api api,
                              MyRepository.PagingCallBack pagingCallBack, DisposableCallBack disposableCallBack) {
        mQuery = query;
        this.networkPageSize = networkPageSize;
        this.defaultError = defaultError;
        this.api = api;
        this.pagingCallBack = pagingCallBack;
        this.disposableCallBack = disposableCallBack;
    }

    // Requests initial data from the network, replacing all content currently
    // in the database.
    @Override
    public void onZeroItemsLoaded() {
        reRun(null);
    }

    // Requests additional data from the network, appending the results to the
    // end of the database's existing data.
    @Override
    public void onItemAtEndLoaded(@NonNull Post itemAtEnd) {
        this.itemAtEnd = itemAtEnd;
        reRun(itemAtEnd);
    }

    public interface DisposableCallBack {
        void handleDisposable(Disposable disposable);
    }

    public interface RefreshCallBack {
        void onRefresh();
        void onRetry();
    }

    private void reRun(@Nullable  Post itemAtEnd) {
        Consumer<Throwable> defaultErrorHandler = ErrorHandler.builder()
                .defaultMessage(defaultError)
                .add(message -> networkState.onNext(NetworkState.error(message)))
                .build();
        networkState.onNext(NetworkState.loading());

        if (itemAtEnd == null)
            disposableCallBack.handleDisposable(
                    api.getTop(mQuery, networkPageSize)
                            .subscribe(listingData -> {
                                networkState.onNext(NetworkState.success());
                                pagingCallBack.handleResponse(mQuery, listingData, true);
                            }, defaultErrorHandler));
        else
            disposableCallBack.handleDisposable(
                    api.getTopAfter(mQuery, itemAtEnd.getName(), networkPageSize)
                            .subscribe(listingData -> {
                                networkState.onNext(NetworkState.success());
                                pagingCallBack.handleResponse(mQuery, listingData, false);
                            }, defaultErrorHandler));
    }

    public RefreshCallBack refreshCallBack = new RefreshCallBack() {
        @Override
        public void onRefresh() {
            reRun(null);
        }

        @Override
        public void onRetry() {
            reRun(itemAtEnd);
        }
    };

}
