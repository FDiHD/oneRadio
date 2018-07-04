package com.example.fdihd.oneradio;

import android.content.Context;
import android.content.Intent;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;

public final class RadioActivity extends AppCompatActivity {
    private TextView textLabel;
    private MediaPlayer mediaPlayer;
    private String savedExtra;
    protected boolean mPrepared = false;
    protected boolean mStarted = false;
    private boolean headPhones;
    private String tester2 = "";
    protected boolean firstState;
    protected boolean sndState;
    protected boolean trdState;
    protected boolean ftState = true;
    private ImageView imageBack;
    private String mURL;
    private TextView mInfos;
    private ImageView stopPlay;
    private TextView rInfo;
    private String tInfo;
    private String rString;
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isHeadphonesPlugged() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        AudioDeviceInfo[] audioDevices = audioManager.getDevices(AudioManager.GET_DEVICES_ALL);
        for (AudioDeviceInfo deviceInfo : audioDevices) {
            if (deviceInfo.getType() == AudioDeviceInfo.TYPE_WIRED_HEADPHONES
                    || deviceInfo.getType() == AudioDeviceInfo.TYPE_WIRED_HEADSET) {
                return true;
            }
        }
        return false;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radioactivity);
        mInfos = findViewById(R.id.textView2);
        stopPlay = findViewById(R.id.stopPlayImg);
        textLabel = findViewById(R.id.tLabel);
        rInfo = findViewById(R.id.rName);
        tInfo = getIntent().getStringExtra("NAME");
        rInfo.setText(tInfo);
        stopPlay.setImageResource(R.drawable.playh);
        mInfos.setText("LOADING");
        savedExtra = getIntent().getStringExtra("URL");
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        new PlayerTask().execute(savedExtra);
        stopPlay.setEnabled(false);
        imageBack= findViewById(R.id.iBack);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        stopPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mStarted) {
                    mStarted = false;
                    mediaPlayer.pause();
                    stopPlay.setImageResource(R.drawable.play);
                } else {
                    mStarted = true;
                    mediaPlayer.start();
                    stopPlay.setImageResource(R.drawable.pause);
                }
            }
        });
    }
    class PlayerTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                mediaPlayer.setDataSource(strings[0]);
                mediaPlayer.prepare();
                mPrepared = true;
                generateRandom();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return mPrepared;
        }
        @Override
        public void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            stopPlay.setEnabled(true);
            mInfos.setText("");
            stopPlay.setImageResource(R.drawable.play);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if (mStarted) {
            mediaPlayer.pause();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (mStarted) {
            mediaPlayer.start();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPrepared) {
            mediaPlayer.release();
        }
        mPrepared = false;
    }
    private void generateRandom() {
        mURL = getIntent().getStringExtra("METADATA");
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mPrepared) {
                    Document finalDoc = null;
                    try {
                        finalDoc = Jsoup.connect(mURL)
                                .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                                .referrer("http://www.google.com")
                                .get();
                    } catch (NullPointerException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (HttpStatusException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    final Document finalDoc1 = finalDoc;
                    runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void run() {
                            headPhones = isHeadphonesPlugged();
                            if (headPhones) {
                                Log.d("HEADPHONES STATUS", "ARE PLUGGED IN");
                                firstState = true;
                            } else if (!headPhones && !firstState) {
                                Log.d("HEADPHONES STATUS", "ARE NOT PLUGGED IN");
                                sndState = true;
                            } else if (firstState && sndState) {
                                Log.d("HEADPHONES STATUS", "ARE PLUGGED IN");
                                trdState = true;
                                if (headPhones && trdState) {
                                    Log.d("HEADPHONES STATUS", "ARE PLUGGED IN,PLAY");
                                    mStarted = true;
                                    ftState = true;
                                    mediaPlayer.start();
                                    stopPlay.setImageResource(R.drawable.pause);
                                } else if (trdState && sndState && ftState) {
                                    Log.d("HEADPHONES STATUS", "WERE PLUGGED IN, NEW STATE OUT");
                                    mStarted = false;
                                    ftState = false;
                                    mediaPlayer.pause();
                                    stopPlay.setImageResource(R.drawable.play);
                                }
                            }
                            rString = StringUtils.substringBetween(finalDoc1.toString(), "28,", "</");
                            if (rString == null) {
                                Log.d("STATUS ",rString);
                            } else {
                                if (rString.equals(tester2)) {
                                    Log.d("CHANGE STATUS", "NOTHING TO CHANGE");
                                } else {
                                    Log.d("CHANGE STATUS", rString);
                                    textLabel.setText(rString);
                                    tester2 = rString;
                                }
                            }
                        }
                    });
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}