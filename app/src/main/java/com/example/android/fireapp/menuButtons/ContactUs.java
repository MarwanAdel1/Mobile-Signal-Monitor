package com.example.android.fireapp.menuButtons;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.fireapp.MainActivity;
import com.example.android.fireapp.R;

public class ContactUs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        Button copy= (Button) findViewById(R.id.copy_contactus);
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied Text", "Signal.Monitor@gmail.com");
                Toast.makeText(ContactUs.this, "Text Copied", Toast.LENGTH_SHORT).show();
                clipboard.setPrimaryClip(clip);
            }}
        );
    }
}
