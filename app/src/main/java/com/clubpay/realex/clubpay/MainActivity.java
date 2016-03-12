package com.clubpay.realex.clubpay;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.clubpay.realex.clubpay.Model.ServerModel;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private BeaconManager beaconManager;

    public static  String HPP_MOTE_ID = "moteId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //new HttpRequestTask().execute();

        beaconManager = new BeaconManager(getApplicationContext());

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startMonitoring(new com.estimote.sdk.Region(
                        "monitored region",
                        UUID.fromString("b9407f30-f5f8-466e-aff9-25556b57fe6d"),
                        24839, 61143));
                getIntent().putExtra(HPP_MOTE_ID, "b9407f30-f5f8-466e-aff9-25556b57fe6d");
            }
        });

        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(com.estimote.sdk.Region region, List<Beacon> list) {
                showNotification(
                        "Your gate closes in 47 minutes.",
                        "Current security wait time is 15 minutes, "
                                + "and it's a 5 minute walk from security to the gate. "
                                + "Looks like you've got plenty of time!");
                new HttpRequestTask().execute();


            }

            @Override
            public void onExitedRegion(com.estimote.sdk.Region region) {
                // could add an "exit" notification too if you want (-:
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showNotification(String title, String message) {
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[]{notifyIntent}, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, ResponseEntity> {

        private  String AMAZONE_URL_TMP = "http://clubpay.vrwuqqpad3.eu-west-1.elasticbeanstalk.com";

        @Override
        protected ResponseEntity doInBackground(Void... params) {
            try {

                RestTemplate restTemplate = new RestTemplate();
                //restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ServerModel model = new ServerModel();
                TelephonyManager tMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                String imeiNo = tMgr.getDeviceId();
                model.setImeiNo(imeiNo);
                String url = AMAZONE_URL_TMP.concat("/payAction?phoneId=").concat(imeiNo);
                ResponseEntity ent = restTemplate.postForEntity(url, model, Boolean.class);
                Boolean isAlreadyReg = (Boolean)ent.getBody();
                if(!isAlreadyReg){
                    //TODO => pass the payment amount to the HPP act.
                    Intent intent = new Intent(getApplicationContext(), HPP.class);
                    String moteId=getIntent().getStringExtra(MainActivity.HPP_MOTE_ID);
                    intent.putExtra(MainActivity.HPP_MOTE_ID, moteId);
                    startActivity(intent);
                }
                //Map<String,String> queryParameter = new HashMap<String,String>();
                //queryParameter.put("phoneId",imeiNo);
                //ResponseEntity ent = restTemplate.postForEntity("http://localhost:8080/payAction", model, Boolean.class, queryParameter);

                return ent;
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(ResponseEntity greeting) {
           // TextView greetingIdText = (TextView) findViewById(R.id.id_value);
           // TextView greetingContentText = (TextView) findViewById(R.id.content_value);
           // greetingIdText.setText(greeting.getId());
           // greetingContentText.setText(greeting.getContent());
        }

    }
}


