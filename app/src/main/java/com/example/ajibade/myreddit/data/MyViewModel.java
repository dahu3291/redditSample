package com.example.ajibade.myreddit.data;

import android.arch.lifecycle.ViewModel;
import android.arch.paging.PagedList;

import com.example.ajibade.myreddit.api.NetworkState;
import com.example.ajibade.myreddit.model.Listing;
import com.example.ajibade.myreddit.model.Post;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;

public class MyViewModel extends ViewModel {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MyBoundaryCallback.DisposableCallBack disposableCallBack = disposable -> compositeDisposable.add(disposable);
    private BehaviorSubject<String> subredditName = BehaviorSubject.create();
    public Flowable<PagedList<Post>> pagedList;
    public Flowable<NetworkState> networkState;

    private BehaviorSubject<Listing> subjectSubReddit = BehaviorSubject.create();

    private PagedList<Post> lastPageList;


    @Inject
    MyViewModel(MyRepository repository) {
        compositeDisposable.add(subredditName
                .subscribe(subReddit -> subjectSubReddit.onNext(repository.getPosts(subReddit, disposableCallBack))));

        pagedList = subjectSubReddit.toFlowable(BackpressureStrategy.BUFFER)
                .switchMap(listing -> listing.pagedList);

        networkState = subjectSubReddit.toFlowable(BackpressureStrategy.BUFFER)
                .switchMap(listing -> listing.networkState.toFlowable(BackpressureStrategy.BUFFER)).observeOn(AndroidSchedulers.mainThread());
    }

    public void setLastPageList(PagedList<Post> lastPageList) {
        this.lastPageList = lastPageList;
    }

    public PagedList<Post> getLastPageList() {
        return lastPageList;
    }

    public boolean showSubreddit(String subreddit) {
        if (subreddit.equals(subredditName.getValue())) {
            return false;
        }
        subredditName.onNext(subreddit);
        return true;
    }

    public String currentSubreddit() {
        return subredditName.getValue();
    }

    public void refresh() {
        if (subjectSubReddit.getValue() == null) return;
        subjectSubReddit.getValue().refreshCallBack.onRefresh();
    }

    public void retry() {
        if (subjectSubReddit.getValue() == null) return;
        subjectSubReddit.getValue().refreshCallBack.onRetry();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
