package com.example.ajibade.myreddit.ui.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.ajibade.myreddit.R;
import com.example.ajibade.myreddit.data.MyViewModel;
import com.example.ajibade.myreddit.util.AppViewModelFactory;
import com.example.ajibade.myreddit.util.ErrorHandler;
import com.example.ajibade.myreddit.util.NetworkUtils;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

public class BaseFragment extends Fragment {

    protected CompositeDisposable compositeDisposable = new CompositeDisposable();

    protected Consumer<Throwable> defaultErrorHandler;

    MyViewModel viewModel;

    @Inject
    AppViewModelFactory viewModelFactory;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
        defaultErrorHandler = ErrorHandler.builder()
                .defaultMessage(getString(R.string.default_error))
                .add(message -> {
                    if (getActivity() != null && !NetworkUtils.isNetworkAvailable(getActivity())) {
                        Toast.makeText(getContext(), R.string.no_network, Toast.LENGTH_SHORT).show();
                    }
                    if (message != null)
                        BaseFragment.this.showSnackbar(message);
                })
                .build();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null)
        viewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(MyViewModel.class);
    }

    @Override
    public void onDestroyView() {
        compositeDisposable.clear();
        super.onDestroyView();
    }

    protected void hideKeyboard() {
        if (getActivity() == null) return;
        View view = getActivity().getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null && imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    protected void showSnackbar(String message) {
        if (getView() == null || getActivity() == null) return;
        View coordinator = getActivity().findViewById(getSnackBarParentView());
        if (coordinator != null) Snackbar.make(coordinator, message, Snackbar.LENGTH_LONG).show();
    }

    protected int getSnackBarParentView(){
        return R.id.fragment_container;
    }
}
