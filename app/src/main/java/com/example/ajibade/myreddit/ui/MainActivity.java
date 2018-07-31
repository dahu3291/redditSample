package com.example.ajibade.myreddit.ui;

import android.os.Bundle;

import com.example.ajibade.myreddit.R;
import com.example.ajibade.myreddit.ui.fragments.SubRedditFragment;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) showFragment(SubRedditFragment.newInstance());
    }
}
