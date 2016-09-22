package comdmen555.github.sleepmanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log;
import java.lang.Math;

public class VolumeService extends Service {

    //declarations
    PendingIntent pendingIntent;
    AlarmManager volumeManager;
    BroadcastReceiver mReceiver;
    AudioManager audioManager;

    int volumeSofFar;


    public void onCreate() {

    }

    //on service start
    @Override
    public void onStart(Intent intent, int startid)
    {
        Log.d("Volume Service", "Volume Service is On");

        //audio manager to control the volume
        AudioManager volumeControl = (AudioManager) getSystemService(AUDIO_SERVICE);

         //shared preferences to get data inputted by user
        SharedPreferences sharedpreferences =
                getApplicationContext().getSharedPreferences("musicTimerData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        if (sharedpreferences.getLong("timeThatHasPassed", 0) == 0) {
            editor.putInt("startingVolume", volumeControl.getStreamVolume(AudioManager.STREAM_MUSIC));
            editor.putBoolean("showGraph", true);

        }
        RegisterAlarmBroadcast();

        editor.putBoolean("musicCompleted", false);
        editor.commit();

        int functionChoice = sharedpreferences.getInt("functionSet",0);

        //using a periodic alarm to turn down the volume (wakes device up)
        volumeManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                1000,pendingIntent);
    }

    //destorying the service
    @Override
    public void onDestroy()
    {
        UnregisterAlarmBroadcast();
        super.onDestroy();

    }

    //registering the alarm
    private void RegisterAlarmBroadcast()
    {
        mReceiver = new BroadcastReceiver()
        {

            @Override
            public void onReceive(Context context, Intent intent)
            {

                audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

                SharedPreferences sharedpreferences =
                        getApplicationContext().getSharedPreferences("musicTimerData", MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putLong("timeThatHasPassed", sharedpreferences.getLong("timeThatHasPassed", 0)+1);
                editor.commit();

                int functionChoice = sharedpreferences.getInt("functionSet",0);


                //executes appropriate function and sets proper volume depending on which type of decay was called
                switch (functionChoice) {
                    case 0:
                        volumeSofFar = linearDecay(sharedpreferences.getLong("timeThatHasPassed", 0));
                        break;
                    case 1:
                        volumeSofFar = parabolicDecay(sharedpreferences.getLong("timeThatHasPassed", 0));
                        break;
                    case 2:
                        volumeSofFar = logarithmicDecay(sharedpreferences.getLong("timeThatHasPassed", 0));
                        break;
                    case 3:
                        volumeSofFar = exponentialDecay(sharedpreferences.getLong("timeThatHasPassed", 0));
                        break;

                }
                //setting stream volume
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        volumeSofFar, 0);
                if (volumeSofFar == 0) {
                    onDestroy();
                    editor.putBoolean("musicCompleted", true);
                    editor.commit();
                }

            }
        };
        registerReceiver(mReceiver, new IntentFilter("sample") );
        pendingIntent = PendingIntent.getBroadcast( this, 0, new Intent("sample"),
                PendingIntent.FLAG_UPDATE_CURRENT);
        volumeManager = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));
    }

    //unregister the alarm
    private void UnregisterAlarmBroadcast()
    {
        volumeManager.cancel(pendingIntent);
        getBaseContext().unregisterReceiver(mReceiver);
        Log.d("Volume Service", "Volume Manager off");

        SharedPreferences sharedpreferences =
                getApplicationContext().getSharedPreferences("musicTimerData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        //editor.putBoolean("alreadyRunning", false);

        editor.commit();

    }

    //volume calculations for particular types of decay
    private int linearDecay(long secondsPassed) {
        SharedPreferences sharedpreferences =
                getApplicationContext().getSharedPreferences("musicTimerData", MODE_PRIVATE);
        int totalSeconds = (sharedpreferences.getInt("hoursSet", 0)*60 +
                sharedpreferences.getInt("minutesSet", 0))*60;

        double volumeLevel = -((double)
                (sharedpreferences.getInt("startingVolume", 0))/totalSeconds)*secondsPassed +
                (double) sharedpreferences.getInt("startingVolume", 0);
        Log.d("Volume Service", String.valueOf(secondsPassed));
        Log.d("Volume Service", String.valueOf(totalSeconds));
        Log.d("Volume Service", String.valueOf(sharedpreferences.getInt("startingVolume", 0)));
        Log.d("Volume Service", String.valueOf(volumeLevel));

        if (volumeLevel > 0.5)
            return (int) (Math.round(volumeLevel));
        else if (volumeLevel > 0.001)
            return 1;
        else
            return 0;

    }

    private int parabolicDecay(long secondsPassed) {
        SharedPreferences sharedpreferences =
                getApplicationContext().getSharedPreferences("musicTimerData", MODE_PRIVATE);
        int totalSeconds = (sharedpreferences.getInt("hoursSet", 0)*60 +
                sharedpreferences.getInt("minutesSet", 0))*60;

        double volumeLevel = -((secondsPassed*1.0 - totalSeconds)*(secondsPassed*1.0 + totalSeconds)/
                (totalSeconds*totalSeconds))*(double) sharedpreferences.getInt("startingVolume", 0);

        Log.d("Volume Service", String.valueOf(secondsPassed));
        Log.d("Volume Service", String.valueOf(totalSeconds));
        Log.d("Volume Service", String.valueOf(sharedpreferences.getInt("startingVolume", 0)));
        Log.d("Volume Service", String.valueOf(volumeLevel));

        if (volumeLevel > 0.5)
            return (int) (Math.round(volumeLevel));
        else if (volumeLevel > 0.001)
            return 1;
        else
            return 0;

    }

    private int logarithmicDecay(long secondsPassed) {
        SharedPreferences sharedpreferences =
                getApplicationContext().getSharedPreferences("musicTimerData", MODE_PRIVATE);
        int totalSeconds = (sharedpreferences.getInt("hoursSet", 0)*60 +
                sharedpreferences.getInt("minutesSet", 0))*60;

        double volumeLevel = -(sharedpreferences.getInt("startingVolume", 0)*1.0/Math.log(totalSeconds + 1)) *
                (Math.log(secondsPassed+1)) + sharedpreferences.getInt("startingVolume", 0);

        Log.d("Volume Service", String.valueOf(secondsPassed));
        Log.d("Volume Service", String.valueOf(totalSeconds));
        Log.d("Volume Service", String.valueOf(sharedpreferences.getInt("startingVolume", 0)));
        Log.d("Volume Service", String.valueOf(volumeLevel));

        if (volumeLevel > 0.5)
            return (int) (Math.round(volumeLevel));
        else if (volumeLevel > 0.001)
            return 1;
        else
            return 0;

    }

    private int exponentialDecay(long secondsPassed) {
        SharedPreferences sharedpreferences =
                getApplicationContext().getSharedPreferences("musicTimerData", MODE_PRIVATE);


        int totalSeconds = (sharedpreferences.getInt("hoursSet", 0)*60 +
                sharedpreferences.getInt("minutesSet", 0))*60;

        double volumeLevel = -1.0*
                (Math.exp((Math.log((sharedpreferences.getInt("startingVolume", 0) +1))/totalSeconds)*
                        secondsPassed)) + sharedpreferences.getInt("startingVolume", 0) + 1;


        Log.d("Volume Service", String.valueOf(secondsPassed));
        Log.d("Volume Service", String.valueOf(totalSeconds));
        Log.d("Volume Service", String.valueOf(sharedpreferences.getInt("startingVolume", 0)));
        Log.d("Volume Service", String.valueOf(volumeLevel));

        if (volumeLevel > 0.5)
            return (int) (Math.round(volumeLevel));
        else if (volumeLevel > 0.001)
            return 1;
        else
            return 0;

    }


    @Override
    public IBinder onBind(Intent arg0)
    {
// TODO Auto-generated method stub
        return null;
    }

}