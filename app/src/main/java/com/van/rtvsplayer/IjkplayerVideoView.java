package com.van.rtvsplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

//import com.inks.inkslibrary.Utils.L;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class IjkplayerVideoView extends FrameLayout {


    private int specHeightSize;
    private int specWidthSize;
    //是否全屏拉伸填满，false等比例最大，不拉伸
    private boolean fillXY = false;


    public IjkplayerVideoView(@NonNull Context context) {
        super(context);
        initVideoView(context);
    }

    public IjkplayerVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initVideoView(context);
    }

    public IjkplayerVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVideoView(context);
    }

    @SuppressLint("NewApi")
    public IjkplayerVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initVideoView(context);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        specHeightSize = MeasureSpec.getSize(heightMeasureSpec);
        specWidthSize = MeasureSpec.getSize(widthMeasureSpec);
    }

    /**
     * 由ijkplayer提供，用于播放视频，需要给他传入一个surfaceView
     */
    private IMediaPlayer mMediaPlayer = null;

    /**
     * 视频文件地址
     */
    private String mPath = "";

    private SurfaceView surfaceView;

    // private VideoPlayerListener listener;
    private Context mContext;


    private void initVideoView(Context context) {
        mContext = context;
        //获取焦点
//        setFocusable(true);
    }


    private void setFillXY(boolean fillXY) {
        this.fillXY = fillXY;
    }

    /**
     * 设置视频地址。
     * 根据是否第一次播放视频，做不同的操作。
     *
     * @param path the path of the video.
     */
    public void setVideoPath(String path) {
        if (TextUtils.equals("", mPath)) {
            //如果是第一次播放视频，那就创建一个新的surfaceView
            mPath = path;
            createSurfaceView();
        } else {
            //否则就直接load
            mPath = path;
            load();
        }
    }

    /**
     * 新建一个surfaceview
     */
    private void createSurfaceView() {
        //生成一个新的surface view
        surfaceView = new SurfaceView(mContext);
        surfaceView.getHolder().addCallback(new LmnSurfaceCallback());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT
                , LayoutParams.MATCH_PARENT, Gravity.CENTER);
        surfaceView.setLayoutParams(layoutParams);
        this.addView(surfaceView);
    }

    /**
     * surfaceView的监听器
     */
    private class LmnSurfaceCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            //surfaceview创建成功后，加载视频
            load();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    }


    /**
     * 加载视频
     */
    private void load() {
        //每次都要重新创建IMediaPlayer
        createPlayer();
        try {
            mMediaPlayer.setDataSource(mPath);
            mMediaPlayer.setLooping(true);
            //mMediaPlayer.setVolume(0f,0f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //给mediaPlayer设置视图
        mMediaPlayer.setDisplay(surfaceView.getHolder());

        mMediaPlayer.prepareAsync();
    }

    /**
     * 创建一个新的player
     */
    private void createPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.setDisplay(null);
            mMediaPlayer.release();
        }
        IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
        ijkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);

        // 设置播放前的探测时间 1,达到首屏秒开效果
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", 1);

        //开启硬解码
        // ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);

        mMediaPlayer = ijkMediaPlayer;

        mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
       // L.e("     mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);");

//        if (listener != null) {
//            mMediaPlayer.setOnPreparedListener(listener);
//            mMediaPlayer.setOnInfoListener(listener);
//            mMediaPlayer.setOnSeekCompleteListener(listener);
//            mMediaPlayer.setOnBufferingUpdateListener(listener);
//            mMediaPlayer.setOnErrorListener(listener);
//        }
    }


//    public void setListener(VideoPlayerListener listener) {
//        this.listener = listener;
//        if (mMediaPlayer != null) {
//            mMediaPlayer.setOnPreparedListener(listener);
//        }
//    }


    IMediaPlayer.OnVideoSizeChangedListener mSizeChangedListener =
            new IMediaPlayer.OnVideoSizeChangedListener() {
                public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sarNum, int sarDen) {

                    if (width != 0 && height != 0) {
                        if (surfaceView != null) {
                            LayoutParams lp = (LayoutParams) surfaceView.getLayoutParams();
                            if (fillXY) {
                                lp.width = -1;
                                lp.height = -1;
                            } else {
                                float scanXY;
                                if ((specHeightSize / specWidthSize) > (height / width)) {
                                    //高剩余，以宽填满
                                    scanXY = specWidthSize / (float) width;
                                } else {
                                    scanXY = specHeightSize / (float) height;
                                }
                                lp.width = (int) (width * scanXY);
                                lp.height = (int) (height * scanXY);
                            }


                            surfaceView.setLayoutParams(lp);
                        }
                        //requestLayout();
                    }
                }
            };


    /**
     * 下面封装了控制视频的方法
     */

    public void setVolume(float v1, float v2) {
        //关闭声音
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(v1, v2);
        }
    }

    public void start() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void pause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }


    public void reset() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
        }
    }


    public long getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        } else {
            return 0;
        }
    }


    public long getCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }


    public void seekTo(long l) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(l);
        }
    }
}