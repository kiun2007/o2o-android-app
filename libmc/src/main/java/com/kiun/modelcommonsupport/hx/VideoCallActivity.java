/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kiun.modelcommonsupport.hx;

import android.hardware.Camera;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMCallManager.EMCameraDataProcessor;
import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMVideoCallHelper;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.media.EMCallSurfaceView;
import com.hyphenate.util.EMLog;
import com.kiun.modelcommonsupport.R;
import com.kiun.modelcommonsupport.utils.MCString;
import com.superrtc.sdk.VideoView;
import java.util.UUID;


public class VideoCallActivity extends CallActivity implements OnClickListener {

    private boolean isMuteState = false;
    private boolean isHandsfreeState;
    private boolean isAnswered;
    private boolean endCallTriggerByMe = false;
    private boolean monitor = true;

    // 视频通话画面显示控件，这里在新版中使用同一类型的控件，方便本地和远端视图切换
    protected EMCallSurfaceView localSurface;
    protected EMCallSurfaceView oppositeSurface;
    private int surfaceState = -1;

    private TextView callStateTextView;

    private LinearLayout comingBtnContainer;
    private ImageView refuseBtn;
    private ImageView answerBtn;
    private ImageView hangupBtn;
    private ImageView muteImage;
    private ImageView switchCameraIV;
    private TextView nickTextView;
    private Chronometer chronometer;
    private LinearLayout voiceContronlLayout;
    private RelativeLayout rootContainer;
    private LinearLayout topContainer;
    private LinearLayout bottomContainer;
    private TextView netwrokStatusVeiw;
    private String heNickName;

    private Handler uiHandler;

    private boolean isInCalling;
    boolean isRecording = false;
//    private Button recordBtn;
    private EMVideoCallHelper callHelper;
    private Button toggleVideoBtn;

    private BrightnessDataProcess dataProcessor = new BrightnessDataProcess();

    // dynamic adjust brightness
    class BrightnessDataProcess implements EMCameraDataProcessor {
        byte yDelta = 0;
        synchronized void setYDelta(byte yDelta) {
            Log.d("VideoCallActivity", "brigntness uDelta:" + yDelta);
            this.yDelta = yDelta;
        }

        // data size is width*height*2
        // the first width*height is Y, second part is UV
        // the storage layout detailed please refer 2.x demo CameraHelper.onPreviewFrame
        @Override
        public synchronized void onProcessData(byte[] data, Camera camera, final int width, final int height, final int rotateAngel) {
            int wh = width * height;
            for (int i = 0; i < wh; i++) {
                int d = (data[i] & 0xFF) + yDelta;
                d = d < 16 ? 16 : d;
                d = d > 235 ? 235 : d;
                data[i] = (byte)d;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
        	finish();
        	return;
        }
        setContentView(R.layout.em_activity_video_call);
        callType = 1;

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        uiHandler = new Handler();

        callStateTextView = (TextView) findViewById(R.id.tv_call_state);
        comingBtnContainer = (LinearLayout) findViewById(R.id.ll_coming_call);
        rootContainer = (RelativeLayout) findViewById(R.id.root_layout);
        refuseBtn = findViewById(R.id.btn_refuse_call);
        answerBtn = findViewById(R.id.btn_answer_call);
        hangupBtn = findViewById(R.id.btn_hangup_call);
        muteImage = (ImageView) findViewById(R.id.iv_mute);
        callStateTextView = (TextView) findViewById(R.id.tv_call_state);
        nickTextView = (TextView) findViewById(R.id.tv_nick);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        voiceContronlLayout = (LinearLayout) findViewById(R.id.ll_voice_control);
        RelativeLayout btnsContainer = (RelativeLayout) findViewById(R.id.ll_btns);
        topContainer = (LinearLayout) findViewById(R.id.ll_top_container);
        bottomContainer = (LinearLayout) findViewById(R.id.ll_bottom_container);
        netwrokStatusVeiw = (TextView) findViewById(R.id.tv_network_status);
        switchCameraIV = findViewById(R.id.btn_switch_camera);

        refuseBtn.setOnClickListener(this);
        answerBtn.setOnClickListener(this);
        hangupBtn.setOnClickListener(this);
        muteImage.setOnClickListener(this);
        rootContainer.setOnClickListener(this);
        switchCameraIV.setOnClickListener(this);
//        recordBtn.setOnClickListener(this);

        msgid = UUID.randomUUID().toString();
        isInComingCall = getIntent().getBooleanExtra("isComingCall", false);
        username = getIntent().getStringExtra("username");
        String ext = getIntent().getStringExtra("ext");
        heNickName = getIntent().getStringExtra(EaseConstant.EXTRA_HE_NICK_NAME);

        if(!TextUtils.isEmpty(heNickName)){
            heNickName = heNickName.split("\\|")[0];
            nickTextView.setText(heNickName);
        }else{
            if(!TextUtils.isEmpty(ext)){
                nickTextView.setText(ext.split("\\|")[0]);
            }else{
                nickTextView.setText(username);
            }
        }

        // local surfaceview
        localSurface = (EMCallSurfaceView) findViewById(R.id.local_surface);
        localSurface.setOnClickListener(this);
        localSurface.setZOrderMediaOverlay(true);
        localSurface.setZOrderOnTop(true);

        // remote surfaceview
        oppositeSurface = (EMCallSurfaceView) findViewById(R.id.opposite_surface);

        // set call state listener
        addCallStateListener();
        if (!isInComingCall) {// outgoing call
            soundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
            outgoing = soundPool.load(this, R.raw.em_outgoing, 1);

            comingBtnContainer.setVisibility(View.INVISIBLE);
            hangupBtn.setVisibility(View.VISIBLE);
            String st = getResources().getString(R.string.Are_connected_to_each_other);
            callStateTextView.setText(st);
            EMClient.getInstance().callManager().setSurfaceView(localSurface, oppositeSurface);
            handler.sendEmptyMessage(MSG_CALL_MAKE_VIDEO);
            handler.postDelayed(new Runnable() {
                public void run() {
                    streamID = playMakeCallSounds();
                }
            }, 300);
        } else { // incoming call

            callStateTextView.setText("连接建立完成");
            if(EMClient.getInstance().callManager().getCallState() == EMCallStateChangeListener.CallState.IDLE
                    || EMClient.getInstance().callManager().getCallState() == EMCallStateChangeListener.CallState.DISCONNECTED) {
                // the call has ended
                finish();
                return;
            }
//            voiceContronlLayout.setVisibility(View.INVISIBLE);
            localSurface.setVisibility(View.INVISIBLE);
            Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            audioManager.setMode(AudioManager.MODE_RINGTONE);
            audioManager.setSpeakerphoneOn(true);
            ringtone = RingtoneManager.getRingtone(this, ringUri);
            ringtone.play();
            EMClient.getInstance().callManager().setSurfaceView(localSurface, oppositeSurface);
        }

        final int MAKE_CALL_TIMEOUT = 50 * 1000;
        handler.removeCallbacks(timeoutHangup);
        handler.postDelayed(timeoutHangup, MAKE_CALL_TIMEOUT);

        // get instance of call helper, should be called after setSurfaceView was called
        callHelper = EMClient.getInstance().callManager().getVideoCallHelper();
        EMClient.getInstance().callManager().setCameraDataProcessor(dataProcessor);

        openSpeakerOn();
    }

    class YDeltaSeekBarListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            dataProcessor.setYDelta((byte)(20.0f * (progress - 50) / 50.0f));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }

    /**
     * 切换通话界面，这里就是交换本地和远端画面控件设置，以达到通话大小画面的切换
     */
    private void changeCallView() {
        if (surfaceState == 0) {
            surfaceState = 1;
            EMClient.getInstance().callManager().setSurfaceView(oppositeSurface, localSurface);
        } else {
            surfaceState = 0;
            EMClient.getInstance().callManager().setSurfaceView(localSurface, oppositeSurface);
        }
    }

    /**
     * set call state listener
     */
    void addCallStateListener() {
        callStateListener = new EMCallStateChangeListener() {

            @Override
            public void onCallStateChanged(final CallState callState, final CallError error) {
                switch (callState) {

                case CONNECTING: // is connecting
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callStateTextView.setText(R.string.Are_connected_to_each_other);
                        }
                    });
                    break;
                case CONNECTED: // connected
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            callStateTextView.setText(R.string.have_connected_with);
                        }
                    });
                    break;

                case ACCEPTED: // call is accepted
                    surfaceState = 0;
                    handler.removeCallbacks(timeoutHangup);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                if (soundPool != null)
                                    soundPool.stop(streamID);
                                EMLog.d("EMCallManager", "soundPool stop ACCEPTED");
                            } catch (Exception e) {
                            }
                            openSpeakerOn();
                            if(!TextUtils.isEmpty(heNickName)){
                                ((TextView)findViewById(R.id.tv_is_p2p)).setText(heNickName);
                            }
                            isHandsfreeState = true;
                            isInCalling = true;
                            chronometer.setVisibility(View.VISIBLE);
                            chronometer.setBase(SystemClock.elapsedRealtime());
                            // call durations start
                            chronometer.start();
                            nickTextView.setVisibility(View.INVISIBLE);
                            callStateTextView.setText(R.string.In_the_call);
//                            recordBtn.setVisibility(View.VISIBLE);
                            callingState = CallingState.NORMAL;
                        }

                    });
                    break;
                case NETWORK_DISCONNECTED:
                    runOnUiThread(new Runnable() {
                        public void run() {
                            netwrokStatusVeiw.setVisibility(View.VISIBLE);
                            netwrokStatusVeiw.setText(R.string.network_unavailable);
                        }
                    });
                    break;
                case NETWORK_UNSTABLE:
                    runOnUiThread(new Runnable() {
                        public void run() {
                            netwrokStatusVeiw.setVisibility(View.VISIBLE);
                            if(error == CallError.ERROR_NO_DATA){
                                netwrokStatusVeiw.setText("没有通话数据");
                            }else{
                                netwrokStatusVeiw.setText("网络不稳定");
                            }
                        }
                    });
                    break;
                case NETWORK_NORMAL:
                    runOnUiThread(new Runnable() {
                        public void run() {
                            netwrokStatusVeiw.setVisibility(View.INVISIBLE);
                        }
                    });
                    break;
                case VIDEO_PAUSE:
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "VIDEO_PAUSE", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case VIDEO_RESUME:
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "VIDEO_RESUME", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case VOICE_PAUSE:
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "VOICE_PAUSE", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case VOICE_RESUME:
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "VOICE_RESUME", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case DISCONNECTED: // call is disconnected
                    handler.removeCallbacks(timeoutHangup);
                    @SuppressWarnings("UnnecessaryLocalVariable")
                    final CallError fError = error;

                    runOnUiThread(new Runnable() {
                        private void postDelayedCloseMsg() {
                            uiHandler.postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    removeCallStateListener();
                                    saveCallRecord();
                                    Animation animation = new AlphaAnimation(1.0f, 0.0f);
                                    animation.setDuration(1200);
                                    rootContainer.startAnimation(animation);
                                    finish();
                                }

                            }, 200);
                        }

                        @Override
                        public void run() {
                            chronometer.stop();
                            callDruationText = chronometer.getText().toString();
                            String s1 = getResources().getString(R.string.The_other_party_refused_to_accept);
                            String s2 = getResources().getString(R.string.Connection_failure);
                            String s3 = getResources().getString(R.string.The_other_party_is_not_online);
                            String s4 = getResources().getString(R.string.The_other_is_on_the_phone_please);
                            String s5 = getResources().getString(R.string.The_other_party_did_not_answer);

                            String s6 = getResources().getString(R.string.hang_up);
                            String s7 = getResources().getString(R.string.The_other_is_hang_up);
                            String s8 = getResources().getString(R.string.did_not_answer);
                            String s9 = getResources().getString(R.string.Has_been_cancelled);
                            String s10 = getResources().getString(R.string.Refused);

                            if (fError == CallError.REJECTED) {
                                callingState = CallingState.BEREFUSED;
                                callStateTextView.setText(s1);
                            } else if (fError == CallError.ERROR_TRANSPORT) {
                                callStateTextView.setText(s2);
                            } else if (fError == CallError.ERROR_UNAVAILABLE) {
                                callingState = CallingState.OFFLINE;
                                callStateTextView.setText(s3);
                            } else if (fError == CallError.ERROR_BUSY) {
                                callingState = CallingState.BUSY;
                                callStateTextView.setText(s4);
                            } else if (fError == CallError.ERROR_NORESPONSE) {
                                callingState = CallingState.NO_RESPONSE;
                                callStateTextView.setText(s5);
                            }else if (fError == CallError.ERROR_LOCAL_SDK_VERSION_OUTDATED || fError == CallError.ERROR_REMOTE_SDK_VERSION_OUTDATED){
                                callingState = CallingState.VERSION_NOT_SAME;
                                callStateTextView.setText("通话协议版本不一致");
                            } else {
                                if (isRefused) {
                                    callingState = CallingState.REFUSED;
                                    callStateTextView.setText(s10);
                                }
                                else if (isAnswered) {
                                    callingState = CallingState.NORMAL;
                                    if (endCallTriggerByMe) {
//                                        callStateTextView.setText(s6);
                                    } else {
                                        callStateTextView.setText(s7);
                                    }
                                } else {
                                    if (isInComingCall) {
                                        callingState = CallingState.UNANSWERED;
                                        callStateTextView.setText(s8);
                                    } else {
                                        if (callingState != CallingState.NORMAL) {
                                            callingState = CallingState.CANCELLED;
                                            callStateTextView.setText(s9);
                                        } else {
                                            callStateTextView.setText(s6);
                                        }
                                    }
                                }
                            }
                            Toast.makeText(VideoCallActivity.this, callStateTextView.getText(), Toast.LENGTH_SHORT).show();
                            postDelayedCloseMsg();
                        }

                    });
                    break;

                default:
                    break;
                }

            }
        };
        EMClient.getInstance().callManager().addCallStateChangeListener(callStateListener);
    }

    void removeCallStateListener() {
        EMClient.getInstance().callManager().removeCallStateChangeListener(callStateListener);
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.local_surface){
            changeCallView();
        }else if (v.getId() == R.id.btn_refuse_call){
            isRefused = true;
            refuseBtn.setEnabled(false);
            handler.sendEmptyMessage(MSG_CALL_REJECT);
        }else if (v.getId() == R.id.btn_answer_call){
            EMLog.d(TAG, "btn_answer_call clicked");
            answerBtn.setEnabled(false);
            openSpeakerOn();
            if (ringtone != null)
                ringtone.stop();

            callStateTextView.setText("answering...");
            handler.sendEmptyMessage(MSG_CALL_ANSWER);
            isAnswered = true;
            isHandsfreeState = true;
            comingBtnContainer.setVisibility(View.INVISIBLE);
            hangupBtn.setVisibility(View.VISIBLE);
            voiceContronlLayout.setVisibility(View.VISIBLE);
            localSurface.setVisibility(View.VISIBLE);
        }else if (v.getId() == R.id.btn_hangup_call){
            hangupBtn.setEnabled(false);
            chronometer.stop();
            endCallTriggerByMe = true;
            callStateTextView.setText(getResources().getString(R.string.hanging_up));
            if(isRecording){
                callHelper.stopVideoRecord();
            }
            EMLog.d(TAG, "btn_hangup_call");
            handler.sendEmptyMessage(MSG_CALL_END);
        }else if (v.getId() == R.id.iv_mute){
            isMuteState = !isMuteState;
            muteImage.setSelected(isMuteState);
            try {
                if (isMuteState){
                    EMClient.getInstance().callManager().pauseVoiceTransfer();
                }else{
                    EMClient.getInstance().callManager().resumeVoiceTransfer();
                }
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }else if (v.getId() == R.id.root_layout){
//            if (callingState == CallingState.NORMAL) {
//                if (bottomContainer.getVisibility() == View.VISIBLE) {
//                    bottomContainer.setVisibility(View.GONE);
//                    topContainer.setVisibility(View.GONE);
//                    oppositeSurface.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFill);
//
//                } else {
//                    bottomContainer.setVisibility(View.VISIBLE);
//                    topContainer.setVisibility(View.VISIBLE);
//                    oppositeSurface.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFit);
//                }
//            }
        }else if (v.getId() == R.id.btn_switch_camera){
            switchCameraIV.setSelected(!switchCameraIV.isSelected());
            EMClient.getInstance().callManager().switchCamera();
        }
    }

    @Override
    protected void onDestroy() {

        if(isRecording){
            callHelper.stopVideoRecord();
            isRecording = false;
        }
        localSurface.getRenderer().dispose();
        localSurface = null;
        oppositeSurface.getRenderer().dispose();
		oppositeSurface = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        callDruationText = chronometer.getText().toString();
        super.onBackPressed();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if(isInCalling){
            try {
                EMClient.getInstance().callManager().pauseVideoTransfer();
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isInCalling){
            try {
                EMClient.getInstance().callManager().resumeVideoTransfer();
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
    }

}
