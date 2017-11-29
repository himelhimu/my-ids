package com.sabbir.andorid.apps.myids.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import butterknife.ButterKnife;

/**
 * Created by sabbir on 10/22/17.
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected Context mContext = this;
    protected Toolbar mToolbar;
    protected LayoutInflater mInflater;

    protected ActionBar mActionBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResourceLayout());
        ButterKnife.bind(this);
        mInflater = LayoutInflater.from(mContext);
        onViewReady(savedInstanceState);
    }



    protected  abstract int getResourceLayout() ;

    public FragmentManager getBaseFragmentManager() {
        return super.getSupportFragmentManager();
    }

    protected void setupToolbar(final Toolbar toolbar) {
        setupToolbar(toolbar, null);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void setupToolbar(final Toolbar toolbar, final View.OnClickListener onClickListener) {

        mToolbar = toolbar;
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        if (mActionBar != null)
            mActionBar.setHomeButtonEnabled(true);

        if (onClickListener != null)
            toolbar.setNavigationOnClickListener(onClickListener);
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public void setTitle(int title) {
        super.setTitle(title);
        if (mActionBar != null)
            mActionBar.setTitle(getString(title));
    }

    public ActionBar getBaseActionBar() {
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        return actionBar;
    }

    @Override
    public void onBackPressed() {
        if (getBaseFragmentManager().getBackStackEntryCount() > 0) {
            getBaseFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    protected void showToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    protected abstract void onViewReady(Bundle savedInstanceState);
}
