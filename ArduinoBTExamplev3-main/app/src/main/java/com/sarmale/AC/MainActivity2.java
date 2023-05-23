package com.sarmale.AC;
import android.view.View;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }
    public void openMain3(View view) {
        startActivity(new Intent(this, MainActivity3.class));
    }
   

}
