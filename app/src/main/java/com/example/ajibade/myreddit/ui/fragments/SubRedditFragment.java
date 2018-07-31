package com.example.ajibade.myreddit.ui.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ajibade.myreddit.model.Post;
import com.example.ajibade.myreddit.ui.MainActivity;
import com.example.ajibade.myreddit.R;
import com.example.ajibade.myreddit.api.NetworkState;
import com.example.ajibade.myreddit.ui.GlideApp;
import com.example.ajibade.myreddit.ui.PostsAdapter;
import com.example.ajibade.myreddit.ui.customtabsclient.CustomTabActivityHelper;
import com.example.ajibade.myreddit.ui.customtabsclient.WebviewFallback;
import com.example.ajibade.myreddit.util.AppViewModelFactory;
import com.example.ajibade.myreddit.util.NetworkUtils;

import javax.inject.Inject;

public class SubRedditFragment extends BaseFragment implements PostsAdapter.PostAdapterListener {

    private static final String KEY_SUBREDDIT = "last_subreddit";
    private static final String PREFS = "preferences";

    @Inject
    AppViewModelFactory viewModelFactory;
    RecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    PostsAdapter adapter;
    AppBarLayout appBarLayout;
    Toolbar toolbar;
    EditText editText;
    TextView emptyTextView;

    public static SubRedditFragment newInstance() {
        SubRedditFragment fragment = new SubRedditFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_subreddit, container, false);

        adapter = new PostsAdapter(this, GlideApp.with(this));
        adapter.submitList(viewModel.getLastPageList());
        recyclerView = rootView.findViewById(R.id.list);
        recyclerView.setAdapter(adapter);

        toolbar = rootView.findViewById(R.id.toolbar);

        if (getActivity() != null)
            ((MainActivity) getActivity()).setSupportActionBar(toolbar);

        appBarLayout = rootView.findViewById(R.id.appBarLayout);
        refreshLayout = rootView.findViewById(R.id.swipe_refresh);
        refreshLayout.setOnRefreshListener(viewModel::refresh);

        emptyTextView = rootView.findViewById(R.id.empty);
        editText = rootView.findViewById(R.id.input);
        editText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                search();
                return true;
            }
            return false;
        });

        editText.setOnKeyListener((view, keyCode, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                search();
                return true;
            }
            return false;
        });

        String lastSubReddit = getLastSubReddit();
        toolbar.setTitle(lastSubReddit);
        editText.setText(lastSubReddit);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        compositeDisposable.add(viewModel.pagedList.subscribe(pagedList -> {
            adapter.submitList(pagedList);
            viewModel.setLastPageList(pagedList);
            showResults(pagedList != null && pagedList.size() > 0);
            if (pagedList != null && pagedList.size() > 0)
                setLastSubReddit(viewModel.currentSubreddit());
        }));

        compositeDisposable.add(viewModel.networkState.subscribe(networkState -> {
            adapter.setNetworkState(networkState);
            if (networkState.getStatus() == NetworkState.Status.FAILED) {
                if (getActivity() != null && !NetworkUtils.isNetworkAvailable(getActivity())) {
                    Toast.makeText(getContext(), R.string.no_network, Toast.LENGTH_SHORT).show();
                } else if (networkState.getMsg() != null)
                    SubRedditFragment.this.showSnackbar(networkState.getMsg());
            }

            PostsAdapter adapter = ((PostsAdapter) recyclerView.getAdapter());
            showResults(adapter != null && adapter.getItemCount() > 1);

            if (networkState.getStatus() == NetworkState.Status.RUNNING)
                refreshLayout.setRefreshing(true);
            else refreshLayout.setRefreshing(false);

        }));

        viewModel.showSubreddit(getLastSubReddit());

    }

//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putString(KEY_SUBREDDIT, viewModel.currentSubreddit());
//    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.search:
                appBarLayout.setExpanded(true);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostClicked(int position) {
        PostsAdapter adapter = ((PostsAdapter) recyclerView.getAdapter());
        if (getActivity() == null || adapter == null || adapter.getCurrentList() == null) return;

        try {
            Post post = adapter.getCurrentList().get(position);
            if (post == null || post.getUrl() == null) return;

//            Intent intent = new Intent(getContext(), WebviewActivity.class);
//            intent.putExtra(WebviewActivity.EXTRA_URL, post.getUrl());
//            startActivity(intent);

            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.setToolbarColor(getResources().getColor(R.color.colorPrimary)).setShowTitle(false);

            VectorDrawable vectorDrawable = (VectorDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.ic_arrow_back_white_24dp);

            if (vectorDrawable != null) {
                Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                        vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                vectorDrawable.draw(canvas);
                builder.setCloseButtonIcon(bitmap);
            }

            CustomTabsIntent customTabsIntent = builder.build();
            CustomTabActivityHelper.openCustomTab(
                    getActivity(), customTabsIntent, Uri.parse(post.getUrl()), new WebviewFallback());

        } catch (IndexOutOfBoundsException ignore) {
        }
    }

    @Override
    public void onRetryClicked() {
        viewModel.retry();
    }

    private void search() {
        hideKeyboard();
        String query = editText.getText().toString();
        if (query.isEmpty()) return;
        toolbar.setTitle(query);
        if (viewModel.showSubreddit(query)) {
            recyclerView.scrollToPosition(0);
            PostsAdapter adapter = ((PostsAdapter) recyclerView.getAdapter());
            if (adapter != null) adapter.submitList(null);
        }
    }

    private void showResults(boolean show) {
        if (show) {
            refreshLayout.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.INVISIBLE);
        } else {
            refreshLayout.setVisibility(View.INVISIBLE);
            emptyTextView.setVisibility(View.VISIBLE);
        }
    }

    public String getLastSubReddit() {
        if (getActivity() == null) return "funny";
        return getActivity().getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString(KEY_SUBREDDIT, "funny");
    }

    public void setLastSubReddit(String status) {
        if (getActivity() == null) return;
        getActivity().getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_SUBREDDIT, status)
                .apply();
    }
}
