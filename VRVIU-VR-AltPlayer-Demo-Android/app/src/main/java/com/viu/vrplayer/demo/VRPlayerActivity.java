package com.viu.vrplayer.demo;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.viu.player.IMediaPlayer;
import com.viu.player.callback.OnBufferingUpdateListener;
import com.viu.player.callback.OnErrorListener;
import com.viu.player.callback.OnInfoListener;
import com.viu.player.callback.OnPreparedListener;
import com.viu.vrplayer.vrwidget.DisplayMode;
import com.viu.vrplayer.vrwidget.AltVrVideoView;
import com.viu.vrplayer.vrwidget.VideoFormat;
import com.viu.vrplayer.demo.R;

/**
 * Created by VRVIU on 2018/3/30.
 */

public class VRPlayerActivity extends BaseActivity   implements View.OnClickListener,
        OnInfoListener,OnPreparedListener,OnErrorListener,
        OnBufferingUpdateListener {
    private ImageButton playButton;
    private ImageButton pauseButton;
    private ImageButton rewindButton;
    private ProgressBar loadingPb;
    private String renderVersion;
    private AltVrVideoView mVideoView;
    private String TAG = "PlayerActivity";
    PowerManager.WakeLock mWakeLock;
    private TextView playPositinTxt;
    private Myhandler handler;
    private static final int MSG_UPDATE_POSITION = 0x1001;
    private int currentMode = DisplayMode.VR_MODE_MONO;
    public static final String URI_LIST_EXTRA = "uri_list";
    public static final String EXTENSION_LIST_EXTRA = "extension_list";
    public static final String EXTENSION_EXTRA = "extension";
    private String mAppId ;
    private String mAccessKey;
    private String mAccessKeyId;
    private String mBizId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        hideBottomUIMenu();
        Intent intent = getIntent();
        setContentView(R.layout.activity_vr_player);
        String uriString = intent.getStringExtra(URI_LIST_EXTRA);

        handler = new Myhandler();
        mAppId = "vrviu_altsdk";
        mAccessKey = "87ab4019c7f624c0310b5c52f1c76419";
        mAccessKeyId = "c832b744e6983a8df217f8af27f1395f";
        mBizId = "altsdk_demo";

        mVideoView = (AltVrVideoView)findViewById(R.id.vr_video);
        mVideoView.init(mAppId,mAccessKey, mAccessKeyId,mBizId);
        mVideoView.setVideoFormat(VideoFormat.FT_ERP_360_2D);
        mVideoView.setUrl(uriString);
        mVideoView.setOnErrorListener(this);
        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnInfoListener(this);
        mVideoView.start();
//        mVideoView.onMeshPrepared(true);
//        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, TAG);
    }

    @Override
    protected void onStart(){
        super.onStart();

    }

    @Override
    protected  void onStop(){
        super.onStop();
    }
    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected  void onResume(){
        super.onResume();
        hideBottomUIMenu();
    }

    protected void hideBottomUIMenu(){
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE;
            decorView.setSystemUiVisibility(uiOptions);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        try {
            InputMethodManager.class.getDeclaredMethod("windowDismissed", IBinder.class).invoke(imm,
                    getWindow().getDecorView().getWindowToken());
        } catch (Exception e){
            e.printStackTrace();
        }
        mVideoView.release();
        handler.removeMessages(MSG_UPDATE_POSITION);
        handler.removeCallbacksAndMessages(null);
        handler = null;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onInfo(IMediaPlayer mp, final int what, int extra) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (what) {
//                    case MediaInfo.BUFFERING_START:
//                        loadingPb.setVisibility(View.VISIBLE);
//                        break;
//                    case MediaInfo.BUFFERING_END:
//                        loadingPb.setVisibility(View.GONE);
//                        break;
                    default:
                        break;
                }

            }
        });
        return true;
    }

    @Override
    public void onPrepared(IMediaPlayer mp) {
        if (handler != null) {
            handler.obtainMessage(MSG_UPDATE_POSITION).sendToTarget();
        }
    }

    @Override
    public boolean onError(IMediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer mp, int percent) {

    }

    private class Myhandler extends Handler {
        @Override
        public void handleMessage(Message ms){
            switch (ms.what){
                case MSG_UPDATE_POSITION:
                    break;
            }
        }
    }
}
