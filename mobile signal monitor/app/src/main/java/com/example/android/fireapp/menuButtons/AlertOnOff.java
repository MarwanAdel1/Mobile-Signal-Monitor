package com.example.android.fireapp.menuButtons;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.android.fireapp.R;

public class AlertOnOff extends AppCompatActivity {
    private Switch mySwitch;
    // public static boolean flag_sound_toast1;
    //  public static boolean flag_sound_alert1;

    public static boolean flag_Alert = false;
    private SharedPreferences shar_alert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_on_off);
        shar_alert=getSharedPreferences("alert1", MODE_PRIVATE);
        final SharedPreferences.Editor editor1 = shar_alert.edit();
        mySwitch = (Switch) findViewById(R.id.switch1);
        mySwitch.setChecked(shar_alert.getBoolean("Alert1", false));

        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor1.putBoolean("Alert1", isChecked);
                editor1.commit();
                if (mySwitch.isChecked()) {
                    flag_Alert = true;
                    Toast.makeText(AlertOnOff.this, "switch On", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(AlertOnOff.this, "switch Off", Toast.LENGTH_SHORT).show();
                    flag_Alert = false;
                }
            }
        });

    }
}
