package com.clubpay.realex.clubpay;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.realexpayments.hpp.HPPError;
import com.realexpayments.hpp.HPPManager;
import com.realexpayments.hpp.HPPManagerListener;
import com.realexpayments.hpp.HPPResponse;

public class HPP extends AppCompatActivity implements HPPManagerListener {

    public static String HPP_RESPONSE_RES ="hppResponse";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hpp);
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

        Fragment hppFragement = initHppManager("12").newInstance();
        getFragmentManager().beginTransaction().add(R.id.hpp_content, hppFragement).commit();
    }

    private HPPManager initHppManager(String amount){
        HPPManager manager = new HPPManager();

        String moteId = getIntent().getStringExtra(MainActivity.HPP_MOTE_ID);
        String imei = getIntent().getStringExtra(MainActivity.HPP_IMEI);
        manager.setHppRequestProducerURL("http://clubpay.vrwuqqpad3.eu-west-1.elasticbeanstalk.com/generateJsonRequest");
        manager.setHppURL("https://hpp.test.realexpayments.com/pay");
        manager.setHppResponseConsumerURL("http://clubpay.vrwuqqpad3.eu-west-1.elasticbeanstalk.com/validateJsonResponse");
        manager.setMerchantId(moteId +","+ imei);
        manager.setCommentOne(moteId);
        manager.setCommentTwo(imei);

        manager.setSupplementaryData(MainActivity.HPP_MOTE_ID,moteId);
        manager.setSupplementaryData(MainActivity.HPP_IMEI,imei);




        manager.setAmount(amount);




        return manager;
    }

    @Override
    public void hppManagerCompletedWithResult(Object response) {
        Gson gson = new GsonBuilder().create();
        HPPResponse res = new HPPResponse();
        String jsonString = gson.toJson(response);
        res = gson.fromJson(jsonString, HPPResponse.class);

        Intent intent = new Intent(this, PaymentRes.class);

        intent.putExtra(HPP_RESPONSE_RES, jsonString);

        startActivity(intent);



    }

    @Override
    public void hppManagerFailedWithError(HPPError error) {
        String res = error.toString();
        res = res;
    }

    @Override
    public void hppManagerCancelled() {

    }

}
