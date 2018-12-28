package com.example.silvanzeller.View;

import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.net.NetworkInfo;

import com.example.silvanzeller.hangmanclient.R;

public class MainActivity extends AppCompatActivity { //first class in the App that gets called

    Button button1;
    Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //fetches last saved state
        setContentView(R.layout.activity_main); //sets up view
        setupActivity(); // calls app activity
    }

    private void setupActivity() {
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(v);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRules(v);
            }
        });
    }

    private void showRules(View v) {
        Intent intent;
        intent = new Intent(this, RuleActivity.class);
        startActivity(intent);
    }

    private void startGame(View v) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(v.getContext().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo(); // needs to be acknowledged in manifest.xml
        if(networkInfo != null && networkInfo.isConnected()){
            Intent intent;
            intent = new Intent(this, DisplayActivity.class);
            startActivity(intent);
        }
    }
}
