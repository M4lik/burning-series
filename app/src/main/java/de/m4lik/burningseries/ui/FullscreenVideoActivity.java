package de.m4lik.burningseries.ui;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IntRange;
import android.support.v4.app.NavUtils;
import android.util.AttributeSet;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.devbrackets.android.exomedia.ui.widget.VideoControls;
import com.devbrackets.android.exomedia.ui.widget.VideoView;

import de.m4lik.burningseries.ActivityComponent;
import de.m4lik.burningseries.R;
import de.m4lik.burningseries.ui.base.ActivityBase;
import de.m4lik.burningseries.ui.dialogs.DialogBuilder;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenVideoActivity extends ActivityBase {

    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private final Runnable mShowPart2Runnable = () -> {

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
    };
    private VideoView videoView;
    private long position = 0;
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        //@SuppressLint("InlinedApi")
        @Override
        public void run() {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private boolean visible;
    private final Runnable hideUI = this::hide;

    @Override
    protected void injectComponent(ActivityComponent appComponent) {
        appComponent.inject(this);
    }

    @Override
    protected void onPause() {
        videoView.pause();
        position = videoView.getCurrentPosition();
        super.onPause();
    }

    @Override
    protected void onResume() {
        videoView.seekTo(position);
        videoView.start();
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen_video);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        visible = true;
        mContentView = findViewById(R.id.bufferedVideoView);

        mContentView.setOnClickListener(view -> toggle());

        Intent intent = getIntent();

        String videoURL = intent.getStringExtra("burning-series.videoURL");

        if (!Patterns.WEB_URL.matcher(videoURL).matches()) {

            DialogBuilder.start(FullscreenVideoActivity.this)
                    .title("Ungültige URL")
                    .content("Beim Parsen der Video-URL ist eine Fehler aufgetreten.\n" +
                            "Bitte melde dich bei M4lik im Forum.")
                    .positive("OK")
                    .build()
                    .show();

            Answers.getInstance().logCustom(new CustomEvent("Invalid URL")
                    .putCustomAttribute("URL", videoURL));

            return;
        }

        Uri uri = Uri.parse(videoURL);


        videoView = (VideoView) mContentView;
        videoView.setVideoURI(uri);

        videoView.start();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggle() {
        if (visible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        visible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        visible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }


    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(hideUI);
        mHideHandler.postDelayed(hideUI, delayMillis);
    }

    private class CustomControlls extends VideoControls {

        public CustomControlls(Context context) {
            super(context);
        }

        public CustomControlls(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public CustomControlls(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        public CustomControlls(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public void setPosition(@IntRange(from = 0L) long position) {

        }

        @Override
        public void setDuration(@IntRange(from = 0L) long duration) {

        }

        @Override
        public void updateProgress(@IntRange(from = 0L) long position, @IntRange(from = 0L) long duration, @IntRange(from = 0L, to = 100L) int bufferPercent) {

        }

        @Override
        protected int getLayoutResource() {
            return 0;
        }

        @Override
        protected void animateVisibility(boolean toVisible) {

        }

        @Override
        protected void updateTextContainerVisibility() {

        }

        @Override
        public void showLoading(boolean initialLoad) {

        }

        @Override
        public void finishLoading() {

        }
    }
}
