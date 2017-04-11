package rco1.com.workoutfinalproject.Workout.WorkoutTypes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import rco1.com.workoutfinalproject.BaseAdaptor;
import rco1.com.workoutfinalproject.Database.DBManeger;
import rco1.com.workoutfinalproject.R;
import rco1.com.workoutfinalproject.Workout.WorkoutMain;
import rco1.com.workoutfinalproject.Workout.WorkoutMapper;
//------------------------------------------------
//  Created By  : Randell Daniel Co
//  Funtions    : Add Delete Regiment wich contains multiple workout
//              : Add Delete Workout on a Regiment
//              : 3 type of workout
//                  : Standard Workout
//                  : Running Workout
//                  : Holding Workout
//              : Start Regiment with workout goals. When one workout reach it's goals the next workout starts.
//              : Start a no goal workout from the 3 types of workout
//              : Set Users Measurements for more accurate measurements.
//------------------------------------------------
//------------------------------------------------
//  WorkoutRunningActivity : This workout tracks your steps by the phones step service sensor.
//      this collects multiple event data and orginize them into seconds intervals.
//      Displays the collected information to the graph for the user to see thier progress in ral time.
//      After a minute the graph will start deleting oldest information in response to the new information.
//      But it will still keep your current progress. This view will also calculate the speed of steps per minute,
//      and the total distance. Distance is calculated by the Users height times 0.414 times steps.
//
//      If it was clicked from the workoutMain it will continue
//      functioning indefinetly. but if it's from a regiment
//      then the goals is set so after the user reach the
//      goal it will automaticaly go to  the next workout.
//------------------------------------------------
//------------------------------------------------
//  Citation:
//      Timer every second  - http://stackoverflow.com/questions/9406523/android-want-app-to-perform-tasks-every-second
//
//------------------------------------------------
public class WorkoutRunningActivity extends BaseAdaptor implements View.OnClickListener,SensorEventListener{

    Button Start;
    Button Rest;
    Button Clear;
    Button Save;

    TextView TotalSteps;
    TextView StepSpeed;
    TextView Distance;

    double userheight = 173.736;


    DBManeger dbmaneger;
    SQLiteDatabase database;
    int RID = 1;
    int mode = 0;
    int DistanceLimit = 0;
    boolean infiniteuse = false;
    private boolean Refresh;
    GraphView graph;
    private SensorManager sensorManager;
    Sensor countSensor;
    ArrayList<Integer> Steps = new ArrayList<Integer>();
    ArrayList<Integer> time = new ArrayList<Integer>();
    boolean running = false;
    int starttime = 0;
    int sizeofgraph = 60;
    boolean clear = true;
    Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_running_layout);

        TotalSteps = (TextView)findViewById(R.id.WorkoutRunningStepsTextView);
        StepSpeed = (TextView)findViewById(R.id.WorkoutRunningSpeedTextView);
        Distance = (TextView)findViewById(R.id.WorkoutRunningDistanceTextView);


        TotalSteps.setText("0");
        StepSpeed.setText("0 Steps per minute");
        Distance.setText("0 meters");

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        dbmaneger = new DBManeger(this);
        database = dbmaneger.getReadableDatabase();
        Start = (Button)findViewById(R.id.WorkoutRunningStartButton);
        Rest = (Button)findViewById(R.id.WorkoutRunningRestButton);
        Clear = (Button)findViewById(R.id.WorkoutRunningClearButton);
        Save = (Button)findViewById(R.id.WorkoutRunningSaveButton);

        graph = (GraphView) findViewById(R.id.graph);

        Start.setOnClickListener(this);
        Rest.setOnClickListener(this);
        Clear.setOnClickListener(this);
        Save.setOnClickListener(this);
        try {
            RID = getIntent().getExtras().getInt("RID");
        }
        catch (Exception e)
        {
            infiniteuse = true;
        }
        try {
            mode = getIntent().getExtras().getInt("MODE");
        }
        catch (Exception e)
        {
            infiniteuse = true;
        }
        if(!infiniteuse)
        {
            String queryString =
                    "SELECT "+ DBManeger.C_WORKOUT_WID +
                            " , " +DBManeger.C_WORKOUT_DISTANCE +
                            " FROM "+ DBManeger.DB_WORKOUT_TABLE +
                            " WHERE " + DBManeger.C_WORKOUT_RID + " = " + RID;
            Cursor cursor = database.rawQuery(queryString,null);
            cursor.moveToPosition(mode);
            DistanceLimit = cursor.getInt(1);
            cursor.close();
        }

        String queryString =
                "SELECT "+ DBManeger.C_USER_HEIGHT +
                        " FROM "+ DBManeger.DB_USER_TABLE;
        Cursor cursor = database.rawQuery(queryString,null);

        if(cursor.getCount()!=0)
        {
            cursor.moveToNext();
            userheight = cursor.getInt(0);
        }
        cursor.close();

        if(countSensor != null)
        {
            sensorManager.registerListener(this,countSensor,SensorManager.SENSOR_DELAY_UI);

        }
        else
        {

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            countSensor.getFifoMaxEventCount();
        }
        updateDisplay();
         starttime = (int) (System.currentTimeMillis()/1000);



    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.WorkoutRunningStartButton:

            {
                if(!running)
                {
                    clear = false;
                    Refresh = true;
                    running = true;
                }
                break;
            }
            case R.id.WorkoutRunningRestButton:
            {
                if(running)
                {
                    clear = false;
                    Refresh = false;
                    running = false;
                }

                break;
            }
            case R.id.WorkoutRunningClearButton:
            {
                clear = true;
                clearinfo();
                running = false;
                break;
            }
            case R.id.WorkoutRunningSaveButton:
            {
                break;
            }
        }
    }

    private void clearinfo()
    {
        graph.removeAllSeries();
        Steps.clear();
        time.clear();
        TotalSteps.setText("0");
        StepSpeed.setText("0 Steps per minute");
        Distance.setText("0 meters");
        firstsensor = true;

    }
    boolean firstsensor = true;
    int laststep = 0;
    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if(Refresh)
        {
            if(firstsensor)
            {
                laststep = (int) event.values[0];
                firstsensor = false;
            }
            else
            {

                Steps.add((int) event.values[0] - laststep);
                //laststep = (int) event.values[0];
                int daystill1970 = (int)(System.currentTimeMillis()/1000);
                time.add((daystill1970-starttime));
            }

            if(Steps.size() >= 3)
            {
                int firsTime = time.get(0);
                int lastTime = time.get(time.size()-1);
                if(lastTime - firsTime > 3)
                {
                    //keeps the list of information
                    //limited to 100 seconds to conserve memory
                    if(lastTime - firsTime > sizeofgraph)
                    {

                        //idle time might keep some seconds that might
                        //not be deleted must loop till there is 100 records
                        while (lastTime - firsTime > sizeofgraph)
                        {
                            int needremovetimes = time.get(0);
                            ArrayList<Integer> Stepsmoved = new ArrayList<Integer>();//temporary modified list
                            ArrayList<Integer> timemoved = new ArrayList<Integer>();//temporary modified list
                            for(int i=0;i<Steps.size();i++)//loop trough the steps record
                            {
                                //ignore the oldest time in the list to be moved to the new list
                                if(needremovetimes != time.get(i))
                                {
                                    Stepsmoved.add(Steps.get(i));
                                    timemoved.add(time.get(i));
                                }
                            }
                            //prep for overiding the old list with the new list
                            Steps.clear();
                            Steps = Stepsmoved;
                            time.clear();
                            time = timemoved;
                            //update the times for the while loop to see.
                            firsTime = time.get(0);
                            lastTime = time.get(time.size()-1);
                        }

                        refreshgraph();
                    }
                    else
                    {
                        refreshgraph();
                    }

                }
                refreshLabels();
            }

        }
    }

    private void refreshLabels()
    {
        double distance = 0;
        int Totalsteps = 0;
        Totalsteps = Steps.get(Steps.size()-1);//last step record has the highest step count
        if(time.size()>2)
        {
            double speedratio = ((double)Totalsteps/(double)(time.get(time.size()-1) - time.get(0))*60);
            int i = (int)speedratio;
            StepSpeed.setText(String.valueOf(i) + " Steps per minute");

        }
        distance = (Totalsteps *(userheight * 0.414))/100;
        Distance.setText(String.valueOf((int)distance) + " meters");
        TotalSteps.setText(String.valueOf(Totalsteps));

        if(MeetRequirements(distance))
        {
            timer.cancel();
            sensorManager.unregisterListener(this,countSensor);
            Intent intent = new Intent(getApplicationContext(), WorkoutMapper.class);
            intent.putExtra("RID",RID);
            intent.putExtra("MODE",mode + 1);
            startActivity(intent);
        }
    }
    private boolean MeetRequirements(double distance)
    {
        if(infiniteuse)
        {
            return false;
        }
        else
        {
            if(distance >= DistanceLimit)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }
    private void refreshgraph()
    {
        int totalSteps = 0;
        int firsTime = time.get(0);
        int lastTime = time.get(time.size()-1);
        DataPoint[] daygraph = new DataPoint[lastTime - firsTime + 2];
        for(int i=0;i<lastTime - firsTime + 2;i++)
        {
            if(time.contains(firsTime + i))
            {
                for(int s=0;s<Steps.size();s++)
                {
                    if(firsTime + i == time.get(s))
                    {
                        totalSteps = Steps.get(s);
                    }

                }
            }
            else
            {

            }
            DataPoint v = new DataPoint(i, totalSteps);
            daygraph[i] = v;


        }
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show normal x values

                    return super.formatLabel(value, isValueX);

                } else {
                    // show currency for y values

                    return super.formatLabel(value, isValueX);
                }
            }
        });
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(daygraph);
        graph.removeAllSeries();
        graph.addSeries(series);
        if(running)
        {
            series.setColor(Color.RED);
        }
        else
        {
            series.setColor(Color.BLUE);
        }


    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void updateDisplay() {
        timer = new Timer();

        timer.schedule(new TimerTask()
        {
            public int count = 0;
            @Override
            public void run()
            {
                try
                {
                    synchronized (this)
                    {
                        wait(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView TimeElapse = (TextView)findViewById(R.id.WorkoutRestTimerTextView);

                                if(Refresh)
                                {
                                    if(clear)
                                    {
                                        count = 0;
                                    }
                                    else
                                    {
                                        count++;
                                    }

                                    int hours = 0;
                                    int minutes = 0;
                                    int seconds = 0;
                                    seconds = count%60;
                                    minutes = (count/60)%60;
                                    hours = (count/60/60)%(60/60);
                                    String shours;
                                    String sminutes ;
                                    String sseconds;
                                    if(hours < 10)
                                    {
                                        shours = String.valueOf("0"+hours);
                                    }
                                    else
                                    {
                                        shours = String.valueOf(hours);
                                    }
                                    if(minutes < 10)
                                    {
                                        sminutes = String.valueOf("0"+minutes);
                                    }
                                    else
                                    {
                                        sminutes = String.valueOf(minutes);
                                    }
                                    if(seconds < 10)
                                    {
                                        sseconds = String.valueOf("0"+seconds);
                                    }
                                    else
                                    {
                                        sseconds = String.valueOf(seconds);
                                    }
                                    TimeElapse.setText(shours + ":" + sminutes + ":" + sseconds);
                                }
                            }
                        });
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }



            }

        },0,1000);//Update text every second
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), WorkoutMain.class);
        startActivity(intent);
    }
}
