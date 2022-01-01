package com.rpmsousa.abc;

import android.os.Bundle;
import android.os.Build;
import android.app.ActivityManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity {
    private ImageView mContentView;
    private Lettergrid mLettergrid;
    ActionBar mActionBar;

    private void hide_system_ui() {
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }

    private void update_ui() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(AppCompatActivity.ACTIVITY_SERVICE);
        boolean isLockTaskModeRunning = false;
        int mode;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mode = activityManager.getLockTaskModeState();
            if (mode == ActivityManager.LOCK_TASK_MODE_PINNED || mode == ActivityManager.LOCK_TASK_MODE_LOCKED)
                isLockTaskModeRunning = true;
        } else {
            isLockTaskModeRunning = activityManager.isInLockTaskMode();
        }

        if (isLockTaskModeRunning)
            mActionBar.hide();
        else
            mActionBar.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toolbar myToolbar;

        View decorView;

        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        //       this.setShowWhenLocked(true);

        mContentView = findViewById(R.id.fullscreen_content);
        mLettergrid = new Lettergrid(this.getApplicationContext());

        mContentView.setImageDrawable(mLettergrid);

        myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mActionBar = getSupportActionBar();

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {

                        update_ui();
                    }
                });

    }

    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        if (hasFocus)
            hide_system_ui();
    }

    @Override
    public void onUserInteraction () {
        update_ui();
    }

    @Override
    public void onResume() {
        super.onResume();

        this.startLockTask();

        //hide_system_ui();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x, y;
        int index;
        boolean res = false;

        update_ui();

        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            x = event.getX();
            y = event.getY();
            res = mLettergrid.set(x, y);
        } else if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
            index = event.getActionIndex();
            x = event.getX(index);
            y = event.getY(index);
            res = mLettergrid.set(x, y);
        }

        if (res)
            mContentView.invalidate();

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.app_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_pin:
                this.startLockTask();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

/*
    @Override
    public void onBackPressed() {
 //       mTextView.setText("KEYCODE_BACK");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
 //       mTextView.setText("KEY_DOWN");
        return true;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
 //       mTextView.setText(String.format("Dispatch %d", event.getKeyCode()));
        return true;
    }
*/
}
