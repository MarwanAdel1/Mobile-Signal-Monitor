package com.example.android.fireapp.menuButtons;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.android.fireapp.R;

public class SoundSetting extends AppCompatActivity {

    public static boolean flag_sound_toast = true;
    public static boolean flag_sound_alert = true;
    Switch general_sound, alert_sound;
    private SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_setting);pref=getSharedPreferences("info", MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();

        /////////////////
        general_sound = (Switch) findViewById(R.id.toast_setting_sound);
        alert_sound = (Switch) findViewById(R.id.alert_setting_sound);

        general_sound.setChecked(pref.getBoolean("your_key", true));
        alert_sound.setChecked(pref.getBoolean("your_key1", true));


        general_sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("your_key", isChecked);
                editor.commit();
                if (!general_sound.isChecked()) {
                    flag_sound_toast= false;
                    //  flag_sound_toast1= false;

                    Toast.makeText(SoundSetting.this, "switch Off", Toast.LENGTH_SHORT).show();
                }else {
                    flag_sound_toast= true;
                    //  flag_sound_toast1=true;
                    Toast.makeText(SoundSetting.this, "switch On", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alert_sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("your_key1", isChecked);
                editor.commit();
                if (!alert_sound.isChecked()) {
                    flag_sound_alert= false;
                    //flag_sound_alert1=false;
                    Toast.makeText(SoundSetting.this, "switch Off", Toast.LENGTH_SHORT).show();
                }else {
                    flag_sound_alert= true;
                    // flag_sound_alert1=true;
                    Toast.makeText(SoundSetting.this, "switch On", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
