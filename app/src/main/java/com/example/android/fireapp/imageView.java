package com.example.android.fireapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;


public class imageView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

      ImageView img=(ImageView) findViewById(R.id.fullScreen);
        if(MainActivity.retrievedImage == null){
            img.setImageResource(R.drawable.user);
        }else {
            img.setImageBitmap(MainActivity.retrievedImage);
        }
    }

}
