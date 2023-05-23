package com.sarmale.AC;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
    }
    public void openMain2(View view) {
        startActivity(new Intent(this, MainActivity2.class));

    }


}
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp"
    android:background="@drawable/bg2">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Sample Text"
        android:textSize="18sp" />
    <LinearLayout

        android:layout_width="match_parent"
        android:layout_height="wrap_content"

        android:orientation="horizontal">

        <Button
            android:id="@+id/back1"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:background="@drawable/rounded_button"

            android:textSize="10dp"
            android:text="Back "
            android:onClick="penMain1"

            />



    </LinearLayout>
    <LinearLayout
        android:id="@+id/first_addressContainer"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:gravity="center"
        android:orientation="vertical"
        >

        <ImageView
            android:id="@+id/address"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:src="@drawable/graph2" />

    </LinearLayout>

</LinearLayout>
