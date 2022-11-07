package com.dgsrz.bancho.game.sprite;

import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.opengl.GLES10;
import android.view.Surface;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.primitive.BaseRectangle;
import org.anddev.andengine.opengl.util.GLHelper;

import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by dgsrz on 15/10/7.
 */
public class VideoSprite extends BaseRectangle
        implements SurfaceTexture.OnFrameAvailableListener {

    private static final int GL_TEXTURE_EXTERNAL_OES = 0x8D65;

    private MediaPlayer mPlayer;

    private int[] mTextures;
    private SurfaceTexture mExternalTexture;

    private boolean mUpdated = false;

    private volatile boolean mIsPlaying = false;

    public VideoSprite(float pX, float pY, float pWidth, float pHeight) {
        super(pX, pY, pWidth, pHeight);
        mPlayer = new MediaPlayer();
    }

    public void setDataSource(String filePath) throws IOException {
        mPlayer.setDataSource(filePath);
        mPlayer.prepare();
    }

    public void play() {
        mPlayer.start();
        mIsPlaying = true;
    }

    public void pause() {
        mPlayer.pause();
    }

    public void stop() {
        mPlayer.stop();
    }

    public void release() {
        mPlayer.release();
        GLES10.glDeleteTextures(1, mTextures, 0);
    }

    @Override
    protected void onInitDraw(GL10 pGL) {
        super.onInitDraw(pGL);
        GLHelper.enableTextures(pGL);
        GLHelper.enableTexCoordArray(pGL);
    }

    protected void doDraw(GL10 pGL, Camera pCamera) {
        pGL.glEnable(GL_TEXTURE_EXTERNAL_OES);
        if (mTextures == null) {
            mTextures = new int[1];
            pGL.glGenTextures(1, mTextures, 0);
            pGL.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextures[0]);
            pGL.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
            pGL.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

            mExternalTexture = new SurfaceTexture(mTextures[0]);
            mExternalTexture.setOnFrameAvailableListener(this);
            Surface surface = new Surface(mExternalTexture);
            mPlayer.setSurface(surface);
            surface.release();

            pGL.glBindTexture(GL_TEXTURE_EXTERNAL_OES, 0);
            pGL.glDisable(GL_TEXTURE_EXTERNAL_OES);
        }
        super.onInitDraw(pGL);
        pGL.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextures[0]);
        //if (mUpdated) {
        mExternalTexture.updateTexImage();
        //mUpdated = false;
        //}
        super.doDraw(pGL, pCamera);
        pGL.glDisable(GL_TEXTURE_EXTERNAL_OES);
        // Log.d("video frame", "finish draw");
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        stop();
        release();
    }

    @Override
    synchronized public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        mUpdated = true;
        // Log.d("video frame", "refresh frame");
        // 经常跑着跑着收不到回调?
    }

    public final boolean getIsPlaying() {
        return mIsPlaying;
    }
}
