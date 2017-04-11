package rco1.com.workoutfinalproject.Workout.WorkoutTypes;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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
//  WorkoutStandardActivity : This activity is for the standard
//      workouts like push ups and situps that require tracking of reps and sets.
//
//
//      If it was clicked from the workoutMain it will continue
//      functioning indefinetly. but if it's from a regiment
//      then the goals is set so after the user reach the
//      goal it will automaticaly go to  the next workout.
//------------------------------------------------
//------------------------------------------------
//  Citation:
//
//------------------------------------------------
public class WorkoutStandardActivity extends BaseAdaptor implements View.OnClickListener {

    DBManeger dbmaneger;
    SQLiteDatabase database;
    int RID = 1;
    int mode = 0;

    EditText WorkoutName;
    EditText Reps;


    TextView Sets;
    TextView AccumulatedReps;
    TextView AVGStart;
    TextView AVGRest;

    Button StartButton;
    Button StopButton;
    boolean firsttime = true;
    boolean working = false;
    boolean infiniteuse = false;
    long time;
    int setlimit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_standard_layout);
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

        WorkoutName=(EditText)findViewById(R.id.WorkoutStandardWorkoutNameEditText);
        Reps=(EditText)findViewById(R.id.WorkoutStandardRepsEditView);

        Sets=(TextView)findViewById(R.id.WorkoutStandardSetsTextView);
        AccumulatedReps=(TextView)findViewById(R.id.WorkoutStandardAccRepsTextView);
        AVGStart=(TextView)findViewById(R.id.WorkoutStandardAvgStartTimeTextView);
        AVGRest=(TextView)findViewById(R.id.WorkoutStandardAvgRestTimeTextView);

        StartButton=(Button)findViewById(R.id.WorkoutStandardStartButton);
        StopButton=(Button)findViewById(R.id.WorkoutStandardStopButton);

        StartButton.setOnClickListener(this);
        StopButton.setOnClickListener(this);
        Sets.setText("0");
        AccumulatedReps.setText("0");
        AVGRest.setText("0");
        AVGStart.setText("0");
        if(!infiniteuse)
        {
            String queryString =
                    "SELECT "+ DBManeger.C_WORKOUT_WID +
                            " , " +DBManeger.C_WORKOUT_WORKOUTNAME +
                            " , " +DBManeger.C_WORKOUT_REPS +
                            " , " +DBManeger.C_WORKOUT_SETS +
                            " FROM "+ DBManeger.DB_WORKOUT_TABLE +
                            " WHERE " + DBManeger.C_WORKOUT_RID + " = " + RID;
            Cursor cursor = database.rawQuery(queryString,null);
            cursor.moveToPosition(mode);
            WorkoutName.setText(cursor.getString(1));
            WorkoutName.setFocusable(false);
            WorkoutName.setFocusableInTouchMode(false);
            WorkoutName.setClickable(false);
            Reps.setText(String.valueOf(cursor.getInt(2)));
            Reps.setFocusable(false);
            Reps.setFocusableInTouchMode(false);
            Reps.setClickable(false);
            setlimit = cursor.getInt(3);
            cursor.close();
        }



    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.WorkoutStandardStartButton:
            {
                if(!working)
                {
                    if(firsttime)
                    {
                        time = System.currentTimeMillis()/1000;
                        firsttime = false;
                    }
                    else
                    {
                        AVGRest.setText(String.valueOf((System.currentTimeMillis()/1000) - time));
                        time = System.currentTimeMillis()/1000;
                    }
                    working = true;
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"The workout has already Started",Toast.LENGTH_LONG).show();
                }

                break;
            }
            case R.id.WorkoutStandardStopButton:
            {
                if(working)
                {
                    AVGStart.setText(String.valueOf((System.currentTimeMillis()/1000) - time));
                    time = System.currentTimeMillis()/1000;
                    Sets.setText(String.valueOf(Integer.parseInt(Sets.getText().toString()) + 1));
                    AccumulatedReps.setText(String.valueOf(Integer.parseInt(Sets.getText().toString()) * Integer.parseInt(Reps.getText().toString())));
                    working = false;
                    if(MeetRequirements())
                    {
                        Intent intent = new Intent(getApplicationContext(), WorkoutMapper.class);
                        intent.putExtra("RID",RID);
                        intent.putExtra("MODE",mode + 1);
                        startActivity(intent);
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"The workout has already Stopped",Toast.LENGTH_LONG).show();
                }

                break;
            }
            case R.id.WorkoutStandardSaveButton:
            {
                break;
            }
        }
    }

    private boolean MeetRequirements()
    {
        if(infiniteuse)
        {
            return false;
        }
        else
        {
            if(Integer.parseInt(Sets.getText().toString()) >= setlimit)
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
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), WorkoutMain.class);
        startActivity(intent);
    }
}
