package com.reco1l.ui.fragments;

import android.view.View;
import android.widget.TextView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.reco1l.enums.Screens;
import com.reco1l.ui.platform.UIFragment;
import com.reco1l.utils.Animation;

import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.SpriteBackground;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

import java.util.ArrayList;

import ru.nsu.ccfit.zuev.osu.Config;
import ru.nsu.ccfit.zuev.osu.ToastLogger;
import ru.nsu.ccfit.zuev.osuplus.R;

// Created by Reco1l on 7/7/22 01:17

public class LoadingScene extends UIFragment implements IUpdateHandler {

    public static LoadingScene instance;

    public final Scene scene;

    private final ArrayList<String> log;
    private CircularProgressIndicator progress;
    private TextView text;
    private View body;

    private float percentage;

    //--------------------------------------------------------------------------------------------//

    @Override
    protected String getPrefix() {
        return "ls";
    }

    @Override
    protected int getLayout() {
        return R.layout.loading_screen;
    }

    @Override
    protected Screens getParent() {
        return Screens.LOADING;
    }

    //--------------------------------------------------------------------------------------------//

    public LoadingScene() {
        scene = new Scene();
        log = ToastLogger.getLog();

        Rectangle dim = new Rectangle(0, 0, Config.getRES_WIDTH(), Config.getRES_HEIGHT());
        dim.setColor(0, 0, 0);
        dim.setAlpha(0.3f);
        scene.attachChild(dim);
        scene.registerUpdateHandler(this);
    }

    @Override
    protected void onScreenChange(Screens lastScreen, Screens newScene) {
        if (newScene != Screens.LOADING) {
            close();
        }
    }

    @Override
    protected void onLoad() {
        setDismissMode(false, false);

        body = find("loadingBody");
        progress = find("progress");
        text = find("text");

        if (log != null)
            log.clear();

        ToastLogger.setPercentage(-1);
        percentage = -1;

        new Animation(body).fade(0, 1).scale(0.8f, 1).play(200);
    }

    @Override
    public void onUpdate(float pSecondsElapsed) {
        if (ToastLogger.getPercentage() == percentage || !isShowing || isNull(progress, text))
            return;

        percentage = ToastLogger.getPercentage();

        mActivity.runOnUiThread(() -> {

            progress.setMax(100);
            progress.setIndeterminate(false);
            progress.setProgress((int) percentage);

            text.setText("Importing beatmaps... (" + (int) percentage + "%)"); //TODO add translation here

            if (text.getVisibility() == View.GONE) {
                text.setVisibility(View.VISIBLE);
                new Animation(text).moveY(50, 0).fade(0, 1).play(180);
            }
        });
    }

    public void complete(Runnable runOnAnimationEnd) {
        if (!isShowing) {
            runOnAnimationEnd.run();
            return;
        }

        if (text.getVisibility() == View.VISIBLE)
            new Animation(text).moveY(0, -50).fade(1, 0).play(180);

        new Animation(body).fade(1, 0).scale(1, 0.8f)
                .runOnEnd(() -> {
                    super.close();
                    runOnAnimationEnd.run();
                })
                .play(200);
    }

    @Override
    public void show() {
        if (isShowing)
            return;

        TextureRegion texture;

        if (resources.getTexture("::background") != null) {
            texture = resources.getTexture("::background");
        } else {
            texture = resources.getTexture("menu-background");
        }

        float H = texture.getHeight() * (screenWidth / (float) texture.getWidth());
        Sprite background = new Sprite(0, (screenHeight - H) / 2f, screenWidth, H, texture);

        scene.setBackground(new SpriteBackground(background));
        mActivity.runOnUiThread(super::show);
    }

    @Override
    public void reset() { }
}
