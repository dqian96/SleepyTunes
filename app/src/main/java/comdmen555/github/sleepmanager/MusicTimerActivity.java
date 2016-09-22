package comdmen555.github.sleepmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import java.lang.Math;


public class MusicTimerActivity extends ActionBarActivity
    {
        //declarations

        private GraphicalView myChart;
        private XYSeriesRenderer functionRenderer;
        private XYMultipleSeriesRenderer myRenderer;
        private XYSeries functionSeries;
        private XYMultipleSeriesDataset mySeries;

        Spinner functionSpinner;
        SeekBar decayHourSeekBar;
        SeekBar decayMinuteSeekBar;

        TextView decayHoursTextView;
        TextView decayMinutesTextView;

        Chronometer timeTracker;

        Button startDecayButton;
        Button resetDecayButton;

        ProgressBar percentDoneProgressBar;

        TextView percentageCompleteTextView;
        int progressBarProgress;

        @Override
        protected void onCreate(Bundle savedInstanceState)
        {

            //getting to data to see if service already running
            SharedPreferences sharedpreferences =
                    getApplicationContext().getSharedPreferences("musicTimerData", MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedpreferences.edit();
            //editor.clear(); (debugging purposes)
            //editor.commit();

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_music_timer);

            //spinner for types of decay functions

            //R.array.type_of_decay_functions, android.R.layout.simple_spinner_item); (this layout is provided by android)
            final Spinner functionSpinner = (Spinner) findViewById(R.id.functionSpinner);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.type_of_decay_functions, R.layout.selected_item);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(R.layout.dropdown_item);
            // Apply the adapter to the spinner
            functionSpinner.setAdapter(adapter);

            functionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                SharedPreferences sharedpreferences =
                        getApplicationContext().getSharedPreferences("musicTimerData", MODE_PRIVATE);
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {

                    SharedPreferences sharedpreferences =
                            getApplicationContext().getSharedPreferences("musicTimerData", MODE_PRIVATE);

                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    String mselection = functionSpinner.getSelectedItem().toString();

                    if (sharedpreferences.getBoolean("alreadyRunning", false) == true
                            || sharedpreferences.getBoolean("onPause", false) == true) {
                        functionSpinner.setSelection(sharedpreferences.getInt("functionSet",0));
                    }
                    else {
                        editor.putInt("functionSet",functionSpinner.getSelectedItemPosition());
                        editor.commit();

                    }

                }
                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });

            //linking xml
            //setting seekbar values
            decayHoursTextView = (TextView)findViewById(R.id.decayHoursTextView);
            decayMinutesTextView = (TextView)findViewById(R.id.decayMinutesTextView);

            decayHourSeekBar = (SeekBar)findViewById(R.id.decayHoursSeekBar);
            decayMinuteSeekBar = (SeekBar)findViewById(R.id.decayMinutesSeekBar);

            timeTracker = (Chronometer) findViewById(R.id.chronometer);

            decayHourSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                SharedPreferences sharedpreferences =
                        getApplicationContext().getSharedPreferences("musicTimerData", MODE_PRIVATE);
               int progress = 0;
               @Override
               public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                   if (sharedpreferences.getBoolean("alreadyRunning", false) == true ||
                           sharedpreferences.getBoolean("onPause", false) == true) {
                       decayHourSeekBar.setProgress(sharedpreferences.getInt("hoursSet", 0));
                       decayHoursTextView.setText("Decay Time:   " + decayHourSeekBar.getProgress()+"   hour(s)");
                   }
                   else
                       decayHoursTextView.setText("Decay Time:   " + decayHourSeekBar.getProgress()+"   hour(s)");


               }

               @Override
               public void onStartTrackingTouch(SeekBar seekBar) {

               }
               @Override
               public void onStopTrackingTouch(SeekBar seekBar) {
               }
           });

            decayMinuteSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                SharedPreferences sharedpreferences =
                        getApplicationContext().getSharedPreferences("musicTimerData", MODE_PRIVATE);
                int progress = 0;
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    if (sharedpreferences.getBoolean("alreadyRunning", false) == true ||
                            sharedpreferences.getBoolean("onPause", false) == true) {
                        decayMinuteSeekBar.setProgress(sharedpreferences.getInt("minutesSet", 0));
                        decayMinutesTextView.setText("Decay Time:   " + decayMinuteSeekBar.getProgress()+"   minute(s)");
                    }
                    else
                        decayMinutesTextView.setText("Decay Time:   " + decayMinuteSeekBar.getProgress()+"   minute(s)");



                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            startDecayButton = (Button)findViewById(R.id.startDecayButton);
            resetDecayButton = (Button)findViewById(R.id.resetDecayButton);

            //getting appopriate stored values
            if (sharedpreferences.getBoolean("alreadyRunning", false) == true ||
                    sharedpreferences.getBoolean("onPause", false) == true) {
                decayMinuteSeekBar.setProgress(sharedpreferences.getInt("minutesSet", 0));
                decayMinutesTextView.setText("Decay Time:   " + decayMinuteSeekBar.getProgress() + "   minute(s)");
                decayHourSeekBar.setProgress(sharedpreferences.getInt("hoursSet", 0));
                decayHoursTextView.setText("Decay Time:   " + decayHourSeekBar.getProgress() + "   hour(s)");
                startDecayButton.setText("Pause Timer");
                functionSpinner.setSelection(sharedpreferences.getInt("functionSet", 0));

                percentDoneProgressBar = (ProgressBar) this.findViewById(R.id.percentDoneProgressBar);
                percentageCompleteTextView = (TextView) this.findViewById(R.id.percentageCompleteTextView);

                progressBarProgress =  Integer.parseInt(String.valueOf( (long )(
                        (100.0*sharedpreferences.getLong("timeThatHasPassed", 0))/((sharedpreferences.getInt("hoursSet", 0)*60.0 +
                                sharedpreferences.getInt("minutesSet", 0))*60.0))));
                percentDoneProgressBar.setProgress(progressBarProgress);
                percentageCompleteTextView.setText("  "+String.valueOf(progressBarProgress)+"%");

            }

            //setting up start button/dual functions
            startDecayButton.setOnClickListener(new View.OnClickListener() {
                SharedPreferences sharedpreferences =
                        getApplicationContext().getSharedPreferences("musicTimerData", MODE_PRIVATE);

                AudioManager volumeControl = (AudioManager) getSystemService(AUDIO_SERVICE);
                @Override
                public void onClick(View v) {

                    //if volume service is not started

                    if (sharedpreferences.getBoolean("alreadyRunning", false) == false) {

                        if (!(decayHourSeekBar.getProgress() == 0 && decayMinuteSeekBar.getProgress() == 0)) {
                            //getting initial volume
                            if (volumeControl.getStreamVolume(AudioManager.STREAM_MUSIC) != 0) {


                                timeTracker.setBase(SystemClock.elapsedRealtime());
                                timeTracker.start();

                                //rePaint(); (debugging purposes)

                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putInt("hoursSet", decayHourSeekBar.getProgress());
                                editor.putInt("minutesSet", decayMinuteSeekBar.getProgress());
                                editor.putInt("functionSet", functionSpinner.getSelectedItemPosition());
                                editor.putBoolean("alreadyRunning", true);

                                editor.putBoolean("onPause", false);

                                editor.commit();

                                startDecayButton.setText("Pause Timer");


                                final Intent intent = new Intent(startDecayButton.getContext(), VolumeService.class);
                                startService(intent);


                            }




                        }



                    }
                    //if volume service is already started

                    else {
                        startDecayButton.setText("Resume Timer");
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putBoolean("onPause", true);
                        editor.putBoolean("alreadyRunning", false);
                        editor.commit();

                        stopService(new Intent(startDecayButton.getContext(), VolumeService.class));


                        timeTracker.stop();


                    }

                }
            });

            //setting up reset button

            resetDecayButton.setOnClickListener(new View.OnClickListener() {
                SharedPreferences sharedpreferences =
                        getApplicationContext().getSharedPreferences("musicTimerData", MODE_PRIVATE);
                @Override
                public void onClick(View v) {

                    if (sharedpreferences.getBoolean("musicCompleted", false) == false) {
                        if (sharedpreferences.getBoolean("alreadyRunning", false)) {
                            stopService(new Intent(resetDecayButton.getContext(), VolumeService.class));
                        }

                    }

                    //percentage accomplished
                    percentageCompleteTextView.setText("  0%");
                    percentDoneProgressBar.setProgress(0);

                    //resetting values

                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putInt("hoursSet", 0);
                    editor.putInt("minutesSet", 0);
                    editor.putInt("functionSet", 0);
                    editor.putBoolean("alreadyRunning", false);
                    editor.putInt("totalTime", 0);

                    editor.putBoolean("onPause", false);

                    editor.putLong("timeThatHasPassed", 0);
                    editor.putBoolean("musicCompleted", false);

                    editor.commit();

                    decayMinuteSeekBar.setProgress(0);
                    decayHourSeekBar.setProgress(0);
                    decayHoursTextView.setText(getString(R.string.hoursMusicTimer));
                    decayMinutesTextView.setText(getString(R.string.minutesMusicTimer));


                    functionSpinner.setSelection(0);

                    startDecayButton.setText("Start Timer");

                    timeTracker.stop();

                    rePaint();

                    Log.d("Music Timer Activity", "Reset Timer");




                }
            });



            //reponse when volume service is on pause
            if (sharedpreferences.getBoolean("onPause", false) == true)
                startDecayButton.setText("Resume Timer");
            if (sharedpreferences.getBoolean("alreadyRunning", false) == true) {
                if (!timeTracker.isActivated()) {
                    timeTracker.start();
                }
            }

            percentDoneProgressBar = (ProgressBar) this.findViewById(R.id.percentDoneProgressBar);
            percentageCompleteTextView = (TextView) this.findViewById(R.id.percentageCompleteTextView);

            percentDoneProgressBar.setMax(100);


            //chronometer to update values, such as the progres bar
            timeTracker.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                SharedPreferences sharedpreferences =
                        getApplicationContext().getSharedPreferences("musicTimerData", MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedpreferences.edit();
                @Override

                public void onChronometerTick(Chronometer chronometer)
                {


                    //when to show the graph
                    if (sharedpreferences.getBoolean("showGraph", false) == true) {
                        rePaint();
                        editor.putBoolean("showGraph", false);
                        editor.commit();
                    }


                    progressBarProgress =  Integer.parseInt(String.valueOf( (long )(
                            (100.0*sharedpreferences.getLong("timeThatHasPassed", 0))/((sharedpreferences.getInt("hoursSet", 0)*60.0 +
                                    sharedpreferences.getInt("minutesSet", 0))*60.0))));
                    percentDoneProgressBar.setProgress(progressBarProgress);
                    percentageCompleteTextView.setText("  "+String.valueOf(progressBarProgress)+"%");
                    if (sharedpreferences.getBoolean("musicCompleted", false) == true) {
                        resetDecayButton.performClick();

                    }

                }
            });

        }

        //activity resume
        @Override
        protected void onResume()
        {
            super.onResume();
            LinearLayout layout=(LinearLayout)findViewById(R.id.chart);

            //if the chart does not exist
            if(myChart==null)
            {
                initializeChart();
                addData();

                myChart=ChartFactory.getLineChartView(this, mySeries, myRenderer);
                layout.addView(myChart);
            }
            //redraw the chart
            else
            {
                myChart.repaint();

            }
        }

        protected void rePaint() {
            Log.d("Music Timer Activity", "repaint called");
            LinearLayout layout=(LinearLayout)findViewById(R.id.chart);
            layout.removeAllViews();
            initializeChart();
            addData();
            myChart=ChartFactory.getLineChartView(this, mySeries, myRenderer);
            layout.addView(myChart);


        }

        long graphTotalSeconds;
        int graphVolume;

        //creating the chart
        private void initializeChart()
        {

            graphTotalSeconds = (decayHourSeekBar.getProgress()*60 +
                    decayMinuteSeekBar.getProgress())*60;

            functionSeries=new XYSeries("Volume vs Time");
            functionRenderer=new XYSeriesRenderer();

            functionRenderer.setLineWidth(1);
            functionRenderer.setColor(Color.rgb(30, 144, 255));

            functionRenderer.setDisplayBoundingPoints(true);

            //functionRenderer.setPointStyle(PointStyle.CIRCLE);
            //functionRenderer.setPointStrokeWidth(3);  (debugging purposes)

            functionRenderer.setShowLegendItem(false);

            myRenderer=new XYMultipleSeriesRenderer();
            myRenderer.addSeriesRenderer(functionRenderer);

            myRenderer.setPanEnabled(false, false);
            myRenderer.setYAxisMin(0.0001);


            //getting values stored to create the chart
            SharedPreferences sharedpreferences =
                    getApplicationContext().getSharedPreferences("musicTimerData", MODE_PRIVATE);

            AudioManager volumeControl = (AudioManager) getSystemService(AUDIO_SERVICE);

            graphVolume = sharedpreferences.getInt("startingVolume", 0);

            Log.d("Music Timer Activity", String.valueOf("graphVolume: "+graphVolume));

            myRenderer.setYAxisMax(volumeControl.getStreamMaxVolume(AudioManager.STREAM_MUSIC));


            //chart customization
            myRenderer.setXAxisMin(0);
            myRenderer.setXAxisMax(graphTotalSeconds+30);

            myRenderer.setShowGrid(false);

            myRenderer.setChartTitle("Volume Level over Time");
            myRenderer.setXTitle("Time(s)");
            myRenderer.setYTitle("Volume Level (based on phone)");

            myRenderer.setAxisTitleTextSize(30);
            myRenderer.setChartTitleTextSize(40);
            myRenderer.setLabelsTextSize(20);

            myRenderer.setMargins(new int[] {70, 60, 20, 20});
            myRenderer.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));

            myRenderer.setLabelsColor(Color.WHITE);
            myRenderer.setGridColor(Color.WHITE);
            myRenderer.setAxesColor(Color.WHITE);

            myRenderer.setXLabelsColor(Color.WHITE);
            myRenderer.setYLabelsColor(0, Color.WHITE);
            myRenderer.setYLabelsAlign(Paint.Align.RIGHT);

            mySeries=new XYMultipleSeriesDataset();
            mySeries.addSeries(functionSeries);



        }

        //adding data to the chart
        private void addData()
        {
            graphTotalSeconds = (decayHourSeekBar.getProgress()*60 + decayMinuteSeekBar.getProgress())*60;

            SharedPreferences sharedpreferences =
                    getApplicationContext().getSharedPreferences("musicTimerData", MODE_PRIVATE);

            int graphFunction = sharedpreferences.getInt("functionSet", 0);

            //values generated depends on function selected
            switch (graphFunction) {
                case 0:
                    for(int i = 0; i < graphTotalSeconds; i++)
                    {
                        functionSeries.add(i,-i*(graphVolume*1.0/graphTotalSeconds) + graphVolume );
                    }
                    break;
                case 1:
                    for(int i = 0; i < graphTotalSeconds; i++)
                    {
                        functionSeries.add(i, -((i*1.0 - graphTotalSeconds)*(i*1.0 + graphTotalSeconds)/
                                (graphTotalSeconds*graphTotalSeconds))*graphVolume);

                    }

                    break;

                case 2:
                    for(int i = 0; i < graphTotalSeconds; i++)
                    {
                        functionSeries.add(i,-(graphVolume*1.0/Math.log(graphTotalSeconds + 1)) *
                                (Math.log(i+1)) +graphVolume);


                    }

                    break;

                case 3:
                    for(int i = 0; i < graphTotalSeconds; i++)
                    {
                        functionSeries.add(i,-1.0*
                                (Math.exp((Math.log((graphVolume +1))/graphTotalSeconds)*
                                        i)) + graphVolume + 1);

                    }

                    break;

        }


    };



    }
