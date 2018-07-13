package com.example.android.fireapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.android.fireapp.BaseActivity.notification;
import static com.example.android.fireapp.BaseActivity.notificationManager;
import static com.example.android.fireapp.MainActivity.Signal;
import static com.example.android.fireapp.menuButtons.AlertOnOff.flag_Alert;
import static com.example.android.fireapp.menuButtons.SoundSetting.flag_sound_alert;

public class SignalStrenthListener extends PhoneStateListener {
    public static int x = -1;  /// if no signal retreived
    private MediaPlayer media;
    static Context context;
    public int comparedNotif = 0,comparedSound = 0;
    Dialog dialog;


    public SignalStrenthListener() {}

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        x = signalStrength.getLevel();

        if( MainActivity.Signal==null){
        }
         else if (x == 0) {
            MainActivity.Signal.setText("Unknown");
        } else if (x == 1) {
            MainActivity.Signal.setText("Poor");
        } else if (x == 2) {
            MainActivity.Signal.setText("Intermediate");
        } else if (x == 3) {
            MainActivity.Signal.setText("Good");
        } else if (x == 4) {
            MainActivity.Signal.setText("Great");
        }

        if (BaseActivity.notificationflag) {
            if (comparedNotif == 0 && x <= 1) {
                notificationManager.notify(1, notification);
                comparedNotif = 1;
            }
        }
        if (BaseActivity.soundflag) {
            if (x <= 1) {
                if (comparedSound >= 1) {
                    comparedSound++;
                }
                if (comparedSound == 0) {
                    comparedSound++;
                }
            } else {
                comparedSound = 0;
            }
            if (comparedSound == 1) {
                soundAlert(x, getContxt());
            }
        }

    }

    private void soundAlert(int signal, Context c) {
        if(flag_Alert){
            if ( flag_sound_alert) {
        media = MediaPlayer.create(c, R.raw.sound1);
        media.start();
        dialog = new Dialog(c);
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Alert signal");
        builder.setMessage("you will enter a dead zone");
        builder.setIcon(R.drawable.sp_ic);
        builder.setPositiveButton("okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Go To Dead Zone Button To get Place With a Good Signal", Toast.LENGTH_LONG).show();
            }
        });
        dialog = builder.create();
        dialog.show();
    }
        }
    }

    public Context getContxt() {
        return context;
    }

    public void setContxt(Context contxt) {
        this.context = contxt;
    }


}