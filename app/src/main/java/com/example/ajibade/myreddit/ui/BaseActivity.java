package com.example.ajibade.myreddit.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.ajibade.myreddit.R;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import io.reactivex.disposables.CompositeDisposable;

public abstract class BaseActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    protected CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 1)
            super.onBackPressed();
        else finish();
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }

    public void showFragment(@NonNull Fragment fragment) {
        String backStateName = fragment.getClass().getSimpleName();
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        int count = fragmentManager.getBackStackEntryCount();
        if (count > 1) {
            String lastBackStateName = fragmentManager.getBackStackEntryAt(count - 1).getName();
            if (lastBackStateName != null && lastBackStateName.equals(backStateName)) return;
        }

        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment, backStateName)
                .addToBackStack(backStateName)
                .commit();
    }
}
