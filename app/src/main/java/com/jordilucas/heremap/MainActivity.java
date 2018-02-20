package com.jordilucas.heremap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.here.android.mpa.mapping.Map;


public class MainActivity extends AppCompatActivity {

    private Map map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
