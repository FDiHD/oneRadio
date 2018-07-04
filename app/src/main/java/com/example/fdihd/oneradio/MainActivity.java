package com.example.fdihd.oneradio;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import android.os.AsyncTask;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    String[] nameArray = {
            "Radio Liberty",
            "Radio Romania Actualitati",
            "Traditional Radio",
            "Radio Lipova",
            "HITRADIO CENTER"};
    String[] infoArray = {
            "http://asculta.radioliberty.ro:1989/7.html",
            "http://89.238.227.6:8002/7.html",
            "http://radiotraditional.zapto.org:7600/7.html",
            "http://radiolipova.radiolipova.net:1717/7.html",
            "http://212.30.80.195:8000/7.html"};
    String[] streamURL = {"http://asculta.radioliberty.ro:1989/",
            "http://89.238.227.6:8002/",
            "http://radiotraditional.zapto.org:7600/",
            "http://radiolipova.radiolipova.net:1717/",
            "http://212.30.80.195:8000/"};
    private boolean mPrepared = true;
    ListView listView;
    TextView mScroll;

    private void generateRandom() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (mPrepared) {
                    Document finalDoc = null;
                    final String[] metaArray = new String[5];
                    try {
                        for (int i = 0; i < infoArray.length; i++) {
                            finalDoc = Jsoup.connect(infoArray[i])
                                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                                    .referrer("http://www.google.com")
                                    .get();
                            String x2 = StringUtils.substringBetween(finalDoc.toString(), "28,", "</");
                            Log.d("STATION", "ID " + i + " output " + x2);
                            metaArray[i] = x2;
                            Log.d("STATION", "ID " + i + " output " + metaArray[i]);
                        }
                    } catch (NullPointerException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (HttpStatusException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void run() {
                            CustomListAdapter listSet = new CustomListAdapter(MainActivity.this, nameArray, metaArray);
                            listView = findViewById(R.id.listviewID);
                            listView.setAdapter(listSet);
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CustomListAdapter whatever = new CustomListAdapter(this, nameArray, infoArray);
        mScroll = findViewById(R.id.textView);
        listView = findViewById(R.id.listviewID);
        listView.setAdapter(whatever);
                listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(MainActivity.this, RadioActivity.class);
                String message = streamURL [position];
                String message2 = infoArray[position];
                String message3 = nameArray[position];
                intent.putExtra("URL", message);
                intent.putExtra("METADATA", message2);
                intent.putExtra("NAME", message3);
                startActivity(intent);
            }
        });
    }
    class PlayerTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            generateRandom();
            return mPrepared;
        }
        @Override
        public void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }
}
