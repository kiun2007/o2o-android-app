package com.kiun.modelcommonsupport.hx;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMCallManager;
import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.Status;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseBaseActivity;
import com.hyphenate.exceptions.EMServiceNotReadyException;
import com.hyphenate.util.EMLog;
import com.kiun.modelcommonsupport.MainApplication;
import com.kiun.modelcommonsupport.data.MCUserInfo;
import com.kiun.modelcommonsupport.utils.MCString;

@SuppressLint("Registered")
public class CallActivity extends EaseBaseActivity {
    public final static String TAG = "CallActivity";
    protected final int MSG_CALL_MAKE_VIDEO = 0;
    protected final int MSG_CALL_MAKE_VOICE = 1;
    protected final int MSG_CALL_ANSWER = 2;
    protected final int MSG_CALL_REJECT = 3;
    protected final int MSG_CALL_END = 4;
    protected final int MSG_CALL_RELEASE_HANDLER = 5;
    protected final int MSG_CALL_SWITCH_CAMERA = 6;

    protected boolean isInComingCall;
    protected boolean isRefused = false;
    protected String username;
    protected CallingState callingState = CallingState.CANCELLED;
    protected String callDruationText;
    protected String msgid;
    protected AudioManager audioManager;
    protected SoundPool soundPool;
    protected Ringtone ringtone;
    protected int outgoing;
    protected EMCallStateChangeListener callStateListener;
    protected boolean isAnswered = false;
    protected int streamID = -1;
    
    EMCallManager.EMCallPushProvider pushProvider;
    
    /**
     * 0：voice call，1：video call
     */
    protected int callType = 0;
    
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        pushProvider = new EMCallManager.EMCallPushProvider() {
            
            void updateMessageText(final EMMessage oldMsg, final String to) {
                // update local message text
                EMConversation conv = EMClient.getInstance().chatManager().getConversation(oldMsg.getTo());
                conv.removeMessage(oldMsg.getMsgId());
            }

            @Override
            public void onRemoteOffline(final String to) {

                //this function should exposed & move to Demo
                EMLog.d(TAG, "onRemoteOffline, to:" + to);
                
                final EMMessage message = EMMessage.createTxtSendMessage("You have an incoming call", to);
                // set the user-defined extension field
                message.setAttribute("em_apns_ext", true);
                
                message.setAttribute("is_voice_call", callType == 0 ? true : false);
                
                message.setMessageStatusCallback(new EMCallBack(){

                    @Override
                    public void onSuccess() {
                        EMLog.d(TAG, "onRemoteOffline success");
                        updateMessageText(message, to);
                    }

                    @Override
                    public void onError(int code, String error) {
                        EMLog.d(TAG, "onRemoteOffline Error");
                        updateMessageText(message, to);
                    }

                    @Override
                    public void onProgress(int progress, String status) {
                    }
                });
                // send messages
                EMClient.getInstance().chatManager().sendMessage(message);
            }
        };
        
        EMClient.getInstance().callManager().setPushProvider(pushProvider);
    }
    
    @Override
    protected void onDestroy() {
        if (soundPool != null)
            soundPool.release();
        if (ringtone != null && ringtone.isPlaying())
            ringtone.stop();
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setMicrophoneMute(false);
        
        if(callStateListener != null)
            EMClient.getInstance().callManager().removeCallStateChangeListener(callStateListener);
        
        if (pushProvider != null) {
            EMClient.getInstance().callManager().setPushProvider(null);
            pushProvider = null;
        }
        releaseHandler();
        super.onDestroy();
    }
    
    @Override
    public void onBackPressed() {
        EMLog.d(TAG, "onBackPressed");
        handler.sendEmptyMessage(MSG_CALL_END);
        saveCallRecord();
        finish();
        super.onBackPressed();
    }
    
    Runnable timeoutHangup = new Runnable() {
        
        @Override
        public void run() {
            handler.sendEmptyMessage(MSG_CALL_END);
        }
    };

    HandlerThread callHandlerThread = new HandlerThread("callHandlerThread");
    { callHandlerThread.start(); }
    protected Handler handler = new Handler(callHandlerThread.getLooper()) {
        @Override
        public void handleMessage(Message msg) {
            EMLog.d("EMCallManager CallActivity", "handleMessage ---enter block--- msg.what:" + msg.what);
            switch (msg.what) {
            case MSG_CALL_MAKE_VIDEO:
            case MSG_CALL_MAKE_VOICE:
                try {
                    if (msg.what == MSG_CALL_MAKE_VIDEO) {
                        MCUserInfo userInfo = MainApplication.getInstance().getUserInfo(false);
                        EMClient.getInstance().callManager().makeVideoCall(username, userInfo.name);
                    } else { 
                        EMClient.getInstance().callManager().makeVoiceCall(username);
                    }
                } catch (final EMServiceNotReadyException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {                            
                            String st2 = e.getMessage();
                            if (e.getErrorCode() == EMError.CALL_REMOTE_OFFLINE) {
                                st2 = "对方不在线";
                            } else if (e.getErrorCode() == EMError.USER_NOT_LOGIN) {
                                st2 = "尚未连接至服务器";
                            } else if (e.getErrorCode() == EMError.INVALID_USER_NAME) {
                                st2 = "用户名不合法";
                            } else if (e.getErrorCode() == EMError.CALL_BUSY) {
                                st2 = "对方正在通话中";
                            } else if (e.getErrorCode() == EMError.NETWORK_ERROR) {
                                st2 = "连接不到聊天服务器";
                            }
                            Toast.makeText(CallActivity.this, st2, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
                break;
            case MSG_CALL_ANSWER:
                EMLog.d(TAG, "MSG_CALL_ANSWER");
                if (ringtone != null)
                    ringtone.stop();
                if (isInComingCall) {
                    try {
                        EMClient.getInstance().callManager().answerCall();
                        isAnswered = true;
                        // meizu MX5 4G, hasDataConnection(context) return status is incorrect
                        // MX5 con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected() return false in 4G
                        // so we will not judge it, App can decide whether judge the network status

//                        if (NetUtils.hasDataConnection(CallActivity.this)) {
//                            EMClient.getInstance().callManager().answerCall();
//                            isAnswered = true;
//                        } else {
//                            runOnUiThread(new Runnable() {
//                                public void run() {
//                                    final String st2 = getResources().getString(R.string.Is_not_yet_connected_to_the_server);
//                                    Toast.makeText(CallActivity.this, st2, Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                            throw new Exception();
//                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        saveCallRecord();
                        finish();
                        return;
                    }
                } else {
                    EMLog.d(TAG, "answer call isInComingCall:false");
                }
                break;
            case MSG_CALL_REJECT:
                if (ringtone != null)
                    ringtone.stop();
                try {
                    EMClient.getInstance().callManager().rejectCall();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    saveCallRecord();
                    finish();
                }
                callingState = CallingState.REFUSED;
                break;
            case MSG_CALL_END:
                if (soundPool != null)
                    soundPool.stop(streamID);
                EMLog.d("EMCallManager", "soundPool stop MSG_CALL_END");
                try {
                    EMClient.getInstance().callManager().endCall();
                } catch (Exception e) {
                    saveCallRecord();
                    finish();
                }
                
                break;
            case MSG_CALL_RELEASE_HANDLER:
                try {
                    EMClient.getInstance().callManager().endCall();
                } catch (Exception e) {
                }
                handler.removeCallbacks(timeoutHangup);
                handler.removeMessages(MSG_CALL_MAKE_VIDEO);
                handler.removeMessages(MSG_CALL_MAKE_VOICE);
                handler.removeMessages(MSG_CALL_ANSWER);
                handler.removeMessages(MSG_CALL_REJECT);
                handler.removeMessages(MSG_CALL_END);
                callHandlerThread.quit();
                break;
            default:
                break;
            }
            EMLog.d("EMCallManager CallActivity", "handleMessage ---exit block--- msg.what:" + msg.what);
        }
    };
    
    void releaseHandler() {
        handler.sendEmptyMessage(MSG_CALL_RELEASE_HANDLER);
    }
    
    /**
     * play the incoming call ringtone
     *
     */
    protected int playMakeCallSounds() {
        try {
            audioManager.setMode(AudioManager.MODE_RINGTONE);
            audioManager.setSpeakerphoneOn(true);

            // play
            int id = soundPool.play(outgoing, // sound resource
                    0.3f, // left volume
                    0.3f, // right volume
                    1,    // priority
                    -1,   // loop，0 is no loop，-1 is loop forever
                    1);   // playback rate (1.0 = normal playback, range 0.5 to 2.0)
            return id;
        } catch (Exception e) {
            return -1;
        }
    }

    protected void openSpeakerOn() {
        try {
            if (!audioManager.isSpeakerphoneOn())
                audioManager.setSpeakerphoneOn(true);
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void closeSpeakerOn() {

        try {
            if (audioManager != null) {
                // int curVolume =
                // audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
                if (audioManager.isSpeakerphoneOn())
                    audioManager.setSpeakerphoneOn(false);
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                // audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                // curVolume, AudioManager.STREAM_VOICE_CALL);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * save call record
     */
    protected void saveCallRecord() {
    }

    enum CallingState {
        CANCELLED, NORMAL, REFUSED, BEREFUSED, UNANSWERED, OFFLINE, NO_RESPONSE, BUSY, VERSION_NOT_SAME
    }
}
