package com.example.fdihd.oneradio;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class RadioActivity extends AppCompatActivity {
    TextView text1;
    MediaPlayer mediaPlayer;
    String savedExtra;
    boolean prepared = false;
    boolean started = false;
    private boolean headphones;
    private String tester2="";
    boolean h1=true;
    boolean firststate;
    boolean sndstate;
    boolean trdstate;
    boolean ftstate=true;
    ImageView i_back;
    String url;
    TextView infos;
    ImageView stp;
    TextView rinfo;
    String tinfo;
    String x2;
    ImageView image;


    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isHeadphonesPlugged(){
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        AudioDeviceInfo[] audioDevices = audioManager.getDevices(AudioManager.GET_DEVICES_ALL);
        for(AudioDeviceInfo deviceInfo : audioDevices){
            if(deviceInfo.getType()==AudioDeviceInfo.TYPE_WIRED_HEADPHONES
                    || deviceInfo.getType()==AudioDeviceInfo.TYPE_WIRED_HEADSET){
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
        image = findViewById(R.id.imageView);
        infos= findViewById(R.id.textView2);
        stp = findViewById(R.id.imageView2);
        text1 = findViewById(R.id.textView);
        rinfo =findViewById(R.id.rName);
        tinfo = getIntent().getStringExtra("NAME");
        rinfo.setText(tinfo);
        stp.setImageResource(R.drawable.playh);
        infos.setText("LOADING");
        savedExtra = getIntent().getStringExtra("URL");
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        new PlayerTask().execute(savedExtra);
        stp.setEnabled(false);
        i_back= findViewById(R.id.i_back);
        i_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


        stp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if (started) {
                    started = false;
                    mediaPlayer.pause();
                    stp.setImageResource(R.drawable.play);

                } else {
                    started = true;
                    mediaPlayer.start();
                    stp.setImageResource(R.drawable.pause);

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
                prepared = true;
                generateRandom();


            } catch (IOException e) {
                e.printStackTrace();
            }

            return prepared;
        }

        @Override
        public void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            stp.setEnabled(true);
            infos.setText("");
            stp.setImageResource(R.drawable.play);

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (started) {
            mediaPlayer.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (started) {
            mediaPlayer.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (prepared) {
            mediaPlayer.release();
        }
        prepared = false;
    }

    private void generateRandom() {
        url = getIntent().getStringExtra("METADATA");
        new Thread(new Runnable() {

            @Override
            public void run() {

                while (prepared) {
                    Document finalDoc = null;
                    try {
                                finalDoc = Jsoup.connect(url)
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
                            headphones = isHeadphonesPlugged();
                            if(headphones && h1){
                                Log.d("HEADPHONES","SUNT BAGATE");
                                firststate=true;
                            }
                            else if (!headphones && !firststate ){
                                Log.d("HEADPHONES","NU SUNT BAGATE");
                                sndstate=true;

                            }else if(firststate && sndstate){
                                Log.d("HEADPHONES","AU FOST BAGATE");
                                trdstate=true;
                                h1=false;
                                if(headphones && trdstate){
                                    Log.d("HEADPHONES","AU FOST BAGATE, TREBUIE SA CANTE");
                                    started = true;
                                    ftstate=true;
                                    mediaPlayer.start();
                                    stp.setImageResource(R.drawable.pause);
                                }
                                else if(trdstate && sndstate && ftstate){
                                    Log.d("HEADPHONES","AU FOST BAGATE, si sunt scoase");
                                    started = false;
                                    ftstate=false;
                                    mediaPlayer.pause();
                                    stp.setImageResource(R.drawable.play);
                                }}



                                x2 = StringUtils.substringBetween(finalDoc1.toString(), "28,", "</");



                            if (x2 == null) {
                                Log.d("STATUS ", x2);
                            }else{
                                if(x2.equals(tester2)) {
                                    Log.d("CHANGE STATUS","NOTHING TO CHANGE");

                                }else{
                                    Log.d("CHANGE STATUS", x2);
                                    text1.setText(x2);
                                    tester2 = x2;
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