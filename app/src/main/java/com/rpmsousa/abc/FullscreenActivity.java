package com.rpmsousa.abc;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    private ImageView mContentView;
    private Lettergrid mLettergrid;

    private void hide_system_ui() {
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        //       this.setShowWhenLocked(true);

        mContentView = findViewById(R.id.fullscreen_content);
        mLettergrid = new Lettergrid(this.getApplicationContext());

        mContentView.setImageDrawable(mLettergrid);
    }
/*
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }
*/
    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        if (hasFocus)
            hide_system_ui();
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
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
   //     mTextView.setText("KEYCODE_BACK");
        return false;
    }

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
