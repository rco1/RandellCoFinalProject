package rco1.com.workoutfinalproject.Workout.WorkoutTypes;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
//  WorkoutHoldActivity : This workout type is a simple timer
//      that you can start and pause. This activity can sense
//      if it was from a regiment or an infinite use.
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
public class WorkoutHoldActivity extends BaseAdaptor implements View.OnClickListener {

    DBManeger dbmaneger;
    SQLiteDatabase database;
    int RID = 1;
    int mode = 0;
    boolean Refresh =false;
    private boolean infiniteuse = false;
    private int DurationeLimit = 0;
    Button OneButton;
    private boolean norep = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_hold_layout);
        dbmaneger = new DBManeger(this);
        database = dbmaneger.getReadableDatabase();
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
                            " , " +DBManeger.C_WORKOUT_DURATION +
                            " FROM "+ DBManeger.DB_WORKOUT_TABLE +
                            " WHERE " + DBManeger.C_WORKOUT_RID + " = " + RID;
            Cursor cursor = database.rawQuery(queryString,null);
            cursor.moveToPosition(mode);
            DurationeLimit = cursor.getInt(1);
            cursor.close();
        }
        OneButton = (Button)findViewById(R.id.WorkoutRestNextButton);
        OneButton.setOnClickListener(this);
        updateDisplay();
    }
    private void updateDisplay() {
        final Timer timer = new Timer();

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
                                    count++;
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
                                    if(norep)
                                    {
                                        if(MeetRequirements(count))
                                        {
                                            norep = false;
                                            Intent intent = new Intent(getApplicationContext(), WorkoutMapper.class);
                                            intent.putExtra("RID",RID);
                                            intent.putExtra("MODE",mode + 1);
                                            startActivity(intent);
                                            timer.cancel();
                                        }
                                    }

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
    private boolean MeetRequirements(int count)
    {
        if(infiniteuse)
        {
            return false;
        }
        else
        {
            if(DurationeLimit < count)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    @Override
    public void onClick(View v)
    {
        if(Refresh)
        {
            Refresh = false;
            OneButton.setText("Start Workout");
        }
        else
        {
            Refresh = true;
            OneButton.setText("Rest");
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), WorkoutMain.class);
        startActivity(intent);
    }
}
