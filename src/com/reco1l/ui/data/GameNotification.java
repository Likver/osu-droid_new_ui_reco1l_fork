package com.reco1l.ui.data;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.reco1l.utils.Animation;
import com.reco1l.utils.ViewTouchHandler;
import com.reco1l.interfaces.IReferences;
import com.reco1l.UI;

import java.io.IOException;
import java.io.InputStream;

import ru.nsu.ccfit.zuev.osuplus.R;

// Created by Reco1l on 1/7/22 00:04

public class GameNotification implements IReferences {

    public View layout;
    public Drawable icon;
    public String header, message;
    public Runnable runOnClick, onDismiss;

    public int progressMax = 100, progress = 0;
    public boolean
            isSilent = false,
            showProgressBar = false,
            showDismissButton = true,
            isProgressBarIndeterminate = true;

    private ImageView close;
    private View body, innerBody;
    private ShapeableImageView iconIv;
    private TextView titleTv, messageTv;
    private LinearProgressIndicator progressBar;

    //----------------------------------------------------------------------------------------//

    /**
     * If you change something after the creation of the notification, don't forget to call {@link #update()}.
     */
    public GameNotification(String header) {
        this.header = header;
        if (icon == null) {
            try {
                InputStream asset = mActivity.getAssets().open("music_list.png");
                icon = Drawable.createFromStream(asset, null);
            } catch (IOException ignored) { }
        }
    }

    public View build() {
        layout = LayoutInflater.from(platform.context).inflate(R.layout.notification, null);
        body = layout.findViewById(R.id.n_body);
        iconIv = layout.findViewById(R.id.n_icon);
        close = layout.findViewById(R.id.n_close);
        titleTv = layout.findViewById(R.id.n_title);
        messageTv = layout.findViewById(R.id.n_message);
        innerBody = layout.findViewById(R.id.n_innerBody);
        progressBar = layout.findViewById(R.id.n_progress);
        return layout;
    }

    public boolean isAdded() {
        return UI.notificationCenter.container.indexOfChild(layout) != -1;
    }

    public boolean hasPriority() {
        if (!showDismissButton)
            return true;
        return showProgressBar && isProgressBarIndeterminate;
    }

    public void load() {
        if (!isAdded())
            return;

        titleTv.setText(header);
        messageTv.setText(message);

        if (runOnClick != null) {
            new ViewTouchHandler(runOnClick).apply(body);
        }

        if (hasPriority()) {
            close.setVisibility(View.INVISIBLE);
        } else {
            close.setVisibility(View.VISIBLE);
            new ViewTouchHandler(() -> {
                if (onDismiss != null) {
                    onDismiss.run();
                }
                UI.notificationCenter.remove(this);
            }).apply(close);
        }

        if (icon != null)
            iconIv.setImageDrawable(icon);

        if (!showProgressBar) {
            progressBar.setVisibility(View.GONE);
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setMax(progressMax);
        progressBar.setProgress(progress);
        progressBar.setIndeterminate(isProgressBarIndeterminate);
    }

    /**
     * Update the notification with the new values.
     * <p>
     * Note: To update the progress of the ProgressBar use {@link #updateProgress(int)} instead.
     */
    public void update() {
        mActivity.runOnUiThread(() -> {
            if (!UI.notificationCenter.isShowing) {
                UI.notificationCenter.createBadgeNotification(this);
                return;
            }
            new Animation(innerBody).moveX(0, -50).fade(1, 0)
                    .runOnEnd(() -> {
                        load();
                        new Animation(innerBody).moveX(50, 0).fade(0, 1).play(120);
                    })
                    .play(120);
        });
    }

    public void updateProgress(int progress) {
        this.progress = progress;
        if (UI.notificationCenter.isShowing && progressBar != null)
            mActivity.runOnUiThread(() -> progressBar.setProgress(progress));
    }
}