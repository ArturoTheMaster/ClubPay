package com.clubpay.realex.clubpay;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.estimote.sdk.repackaged.gson_v2_3_1.com.google.gson.Gson;
import com.estimote.sdk.repackaged.gson_v2_3_1.com.google.gson.GsonBuilder;
import com.realexpayments.hpp.HPPResponse;

public class PaymentRes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_res);
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

        String jsonResponse = getIntent().getStringExtra(HPP.HPP_RESPONSE_RES);
        Gson gson = new GsonBuilder().create();
        HPPResponse response = gson.fromJson(jsonResponse, HPPResponse.class);

        TextView payResult = (TextView) findViewById(R.id.res_text);
        payResult.setText("Successfull");
    }

}
