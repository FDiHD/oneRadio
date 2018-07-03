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

    String[] nameArray = {"Radio Liberty","Radio Romania Actualitati","Traditional Radio","Radio Lipova"};
    String stream = "http://live.radiotequila.ro:7000/7.html";
    String[] infoArray = {
            "http://asculta.radioliberty.ro:1989/7.html",
            "http://89.238.227.6:8002/7.html",
            "http://radiotraditional.zapto.org:7600/7.html",
            "http://radiolipova.radiolipova.net:1717/7.html"


    };
    String[] streamurl= {"http://asculta.radioliberty.ro:1989/","http://89.238.227.6:8002/","http://radiotraditional.zapto.org:7600/","http://radiolipova.radiolipova.net:1717/"};
    private boolean prepared = true;
    ListView listView;
    TextView scroll;

    private void generateRandom() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (prepared) {
                    Document finalDoc = null;
                    final String[] arayf = new String[4];
                    try {
                        for (int i=0; i < infoArray.length;i++) {
                            finalDoc = Jsoup.connect(infoArray[i])
                                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                                    .referrer("http://www.google.com")
                                    .get();


                            String x2 = StringUtils.substringBetween(finalDoc.toString(), "28,", "</");
                            Log.d("DAAAAAAAAAA", "NURMAL ESTE" +i + "output"+ x2);
                            arayf[i]=x2;
                            Log.d("DUUUPAAAA", "NURMAL ESTE" +i + "output"+ arayf[i]);


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

                            CustomListAdapter whatever = new CustomListAdapter(MainActivity.this, nameArray, arayf);

                            listView =  findViewById(R.id.listviewID);
                            listView.setAdapter(whatever);


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
        scroll = findViewById(R.id.textView);
        listView =  findViewById(R.id.listviewID);
        listView.setAdapter(whatever);



        new PlayerTask().execute(stream);



        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(MainActivity.this, radioactivity.class);
                String message = streamurl[position];
                String message2 =infoArray[position];
                String message3= nameArray[position];
                intent.putExtra("animal", message);
                intent.putExtra("METADATA",message2);
                intent.putExtra("NAME",message3);
                startActivity(intent);

            }
        });

    }


    class PlayerTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {

          generateRandom();
            return prepared;
        }

        @Override
        public void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

        }
    }
}