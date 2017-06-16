package com.example.alip.evcma;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //redirect to MainActivity after 2 seconds
        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(1000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent Intent = new Intent(SplashScreen.this, LoginActivity.class);
                    startActivity(Intent);
                    finish();
                }
            }
        };
        timerThread.start();

    }
}