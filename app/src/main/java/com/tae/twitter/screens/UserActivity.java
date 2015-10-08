package com.tae.twitter.screens;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.tae.twitter.R;
import com.tae.twitter.adapters.TwitterAdapter;
import com.tae.twitter.common.Utility;
import com.tae.twitter.configs.Config;
import com.tae.twitter.gson.Tweet;
import com.twitter.api.config.Definitions;
import com.twitter.api.core.Twitter;
import com.twitter.api.core.TwitterRequest;
import com.twitter.api.util.Debug;

import java.util.ArrayList;

/**
 * User Home Activity Class
 *
 * @author David Castillo Fuentes <dcaf82@gmail.com>
 *
 */
public class UserActivity extends BaseActivity implements SwipyRefreshLayout.OnRefreshListener,
        TextView.OnEditorActionListener, View.OnClickListener {

    private Twitter mTwitter;
    private ListView mListView;
    private TwitterAdapter mAdapter;
    private SwipyRefreshLayout mRefreshLayout;
    private ProgressBar progressBar;
    private EditText mStatesMessage;
    private boolean mRefreshing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        progressBar = (ProgressBar)findViewById(R.id.waitWhileLoading);
        mStatesMessage = (EditText)findViewById(R.id.msg);
        mStatesMessage.setOnEditorActionListener(this);
        findViewById(R.id.sendMsg).setOnClickListener(this);

        mTwitter = new Twitter(this, Config.CONSUMER_KEY, Config.CONSUMER_SECRET,
                Config.CALLBACK_URL);

        mAdapter = new TwitterAdapter(this, new ArrayList<Tweet>());
        mListView = (ListView) findViewById(R.id.tweets);
        mListView.setAdapter(mAdapter);

        mRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.container);
        mRefreshLayout.setOnRefreshListener(this);

        if (savedInstanceState == null) {
            openIndeterminateBar(true);
            getHomeTimeline();
        }
    }

    private void updateStatus(String status) {
        openIndeterminateBar(true);

        TwitterRequest request = new TwitterRequest(mTwitter.getConsumer(), mTwitter.getAccessToken());

        String updateStatusUrl = Config.ROOT_URL + Config.UPDATE;

        ContentValues params = new ContentValues();
        params.put("status", status);

        request.createRequest(Definitions.POST, updateStatusUrl, params, new TwitterRequest.RequestListener() {

            @Override
            public void onSuccess(String response) {
                if(isFinishing()) return;
                mStatesMessage.setText("");
                showToast(getString(R.string.message_published));
                getHomeTimeline();
            }

            @Override
            public void onError(String error) {
                if(isFinishing()) return;
                openIndeterminateBar(false);
                showToast(error);
            }
        });
    }

    public void openIndeterminateBar(boolean open) {
        if (open) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_signout:{
                mTwitter.clearSession();
                clearCredential();
                startActivity(new Intent(getActivity(), MainActivity.class));
                finish();
            }
                break;
            case R.id.action_exit:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getHomeTimeline() {

        TwitterRequest request = new TwitterRequest(mTwitter.getConsumer(), mTwitter.getAccessToken());

        String updateStatusUrl = Config.ROOT_URL + Config.HOME_TIMELINE;

        ContentValues params = new ContentValues();
        params.put("count","20");

        request.createRequest(Definitions.GET, updateStatusUrl, params, new TwitterRequest.RequestListener() {

            @Override
            public void onSuccess(String response) {
                Debug.i(response);
                close();
                ArrayList<Tweet> tweets;

                try {
                    tweets = new Gson().fromJson(response, new TypeToken<ArrayList<Tweet>>() {}
                            .getType());
                    mAdapter.clear();
                    mAdapter.addAll(tweets);
                    mListView.setSelectionAfterHeaderView();
                } catch(Exception e) {
                    Debug.i("Error :: " + e);
                }

            }

            @Override
            public void onError(String error) {
                showToast(error);
                close();
            }
        });
    }

    private void close() {
        openIndeterminateBar(false);
        mRefreshLayout.setRefreshing(false);
        mRefreshing = false;
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {

        if (mRefreshLayout.isRefreshing()) {
            if (mRefreshing) {
                mRefreshLayout.setRefreshing(false);
            } else {
                mRefreshing = true;
                getHomeTimeline();
            }
        }

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        switch(actionId) {
            case EditorInfo.IME_ACTION_SEND: {
                Utility.hideKeyBoard(this);
                sendMsg();
                return true;
            }
        }

        return false;
    }

    protected void sendMsg() {
        String status = mStatesMessage.getText().toString().trim();

        if (status.equals("")) {
            showToast("Please write your status");
            return;
        }

        updateStatus(status);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.sendMsg:
                Utility.hideKeyBoard(this);
                sendMsg();
                break;
        }
    }
}
