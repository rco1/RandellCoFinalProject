package rco1.com.workoutfinalproject.Regiment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import rco1.com.workoutfinalproject.BaseAdaptor;
import rco1.com.workoutfinalproject.Database.DBManeger;
import rco1.com.workoutfinalproject.MainActivity;
import rco1.com.workoutfinalproject.R;
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
//  RegimentAddActivity : This makes you add different
//      types of workout to your previusly selected regiment.
//      And create a workout with one of the types of workout.
//      There is a selection of previusly made workout that you
//      cold select from the list to create a similar workout.
//
//------------------------------------------------
//------------------------------------------------
//  Citation:
//
//------------------------------------------------

public class RegimentAddActivity extends BaseAdaptor implements View.OnClickListener,AdapterView.OnItemSelectedListener,AdapterView.OnItemClickListener {

    EditText StandardName;
    EditText StandardReps;
    EditText StandardSets;
    EditText StandardWorkoutName;

    EditText RunningDistance;
    EditText RunningName;

    EditText RestingDuration;
    EditText RestingName;

    Spinner WorkoutTypeSpinner;
    ListView PreviusWorkouts;

    Button AddWorkout;

    LinearLayout Standardcell;
    LinearLayout Runningcell;
    LinearLayout Holdingcell;

    DBManeger dbmaneger;
    SQLiteDatabase database;
    int RID = 0;
    int workoutType = 0;
    ArrayList<HashMap<String,String>> WorkoutList = new ArrayList<HashMap<String,String>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regiment_add_layout);
        RID = getIntent().getExtras().getInt("RID");
        dbmaneger = new DBManeger(this);
        database = dbmaneger.getReadableDatabase();
        StandardName = (EditText)findViewById(R.id.RegimentStandardName);
        StandardReps = (EditText)findViewById(R.id.RegimentStandardReps);
        StandardSets = (EditText)findViewById(R.id.RegimentStandardSets);
        StandardWorkoutName = (EditText)findViewById(R.id.RegimentStandardWorkoutName);

        RunningDistance = (EditText)findViewById(R.id.RegimentRunningDistance);
        RunningName = (EditText)findViewById(R.id.RegimentRunningName);

        RestingDuration = (EditText)findViewById(R.id.RegimentRestingDuration);
        RestingName = (EditText)findViewById(R.id.RegimentRestingName);

        WorkoutTypeSpinner = (Spinner)findViewById(R.id.RegimentAddWorkoutTypeSpinner);
        PreviusWorkouts = (ListView)findViewById(R.id.RegimentAddListview);

        AddWorkout = (Button)findViewById(R.id.RegimentAddWorkoutButton);

        WorkoutTypeSpinner.setOnItemSelectedListener(this);
        PreviusWorkouts.setOnItemClickListener(this);
        AddWorkout.setOnClickListener(this);

        Standardcell = (LinearLayout)findViewById(R.id.regimentAddStandLayout);
        Runningcell = (LinearLayout)findViewById(R.id.regimentAddRunningLayout);
        Holdingcell = (LinearLayout)findViewById(R.id.regimentAddRestLayout);

        selectcell(0);
        FillWorkoutType();
        FillPreviusWorkout();


    }

    private void FillPreviusWorkout()
    {
        String queryString =
                "SELECT "+ DBManeger.C_WORKOUT_NAME +
                        " , " + DBManeger.C_WORKOUT_WID+
                        " FROM "+ DBManeger.DB_WORKOUT_TABLE;
        Cursor cursor = database.rawQuery(queryString,null);
        for(int i=0;i<cursor.getCount();i++)//for each object in the json array
        {
            cursor.moveToNext();
            HashMap<String,String> temp = new HashMap<String,String>();
            temp.put("WorkoutName",cursor.getString(0));
            temp.put("WID", String.valueOf(cursor.getInt(1)));
            WorkoutList.add(temp);
        }
        cursor.close();
        String[] fieldNames = new String[]{"WorkoutName"};
        int[] fieldIDs = new int[]{R.id.ListViewTab};
        SimpleAdapter adapter =
                new SimpleAdapter(this,WorkoutList,R.layout.listview_row,fieldNames,fieldIDs);
        PreviusWorkouts.setAdapter(adapter);
    }

    private void FillWorkoutType()
    {
        ArrayList<HashMap<String,String>> WorkoutTypeList = new ArrayList<HashMap<String,String>>();
        String[] TabItems = new String[]{"Standard","Running","Hold"};
        for(int i=0;i<TabItems.length;i++)//for each object in the json array
        {
            HashMap<String,String> temp = new HashMap<String, String>();
            temp.put("WorkoutType",TabItems[i]);
            WorkoutTypeList.add(temp);
        }
        String[] fieldNames = new String[]{"WorkoutType"};
        int[] fieldIDs = new int[]{R.id.SpinnerTab};
        SimpleAdapter adapter =
                new SimpleAdapter(this,WorkoutTypeList,R.layout.spinner_row,fieldNames,fieldIDs);
        WorkoutTypeSpinner.setAdapter(adapter);
    }

    private void selectcell(int i)
    {
        if(i == 0)
        {
            Standardcell.setVisibility(View.VISIBLE);
            Runningcell.setVisibility(View.GONE);
            Holdingcell.setVisibility(View.GONE);
        }
        if(i == 1)
        {
            Standardcell.setVisibility(View.GONE);
            Runningcell.setVisibility(View.VISIBLE);
            Holdingcell.setVisibility(View.GONE);
        }
        if(i == 2)
        {
            Standardcell.setVisibility(View.GONE);
            Runningcell.setVisibility(View.GONE);
            Holdingcell.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v)
    {
        ContentValues values = new ContentValues();
        values.put(DBManeger.C_WORKOUT_RID,RID);
        values.put(DBManeger.C_WORKOUT_TYPE,workoutType);
        if(workoutType == 0)
        {
            values.put(DBManeger.C_WORKOUT_NAME,StandardName.getText().toString());
            values.put(DBManeger.C_WORKOUT_WORKOUTNAME,StandardWorkoutName.getText().toString());
            values.put(DBManeger.C_WORKOUT_REPS,Integer.parseInt(StandardReps.getText().toString()));
            values.put(DBManeger.C_WORKOUT_SETS,Integer.parseInt(StandardSets.getText().toString()));
        }
        if(workoutType == 1)
        {
            values.put(DBManeger.C_WORKOUT_NAME,RunningName.getText().toString());
            values.put(DBManeger.C_WORKOUT_DISTANCE,Integer.parseInt(RunningDistance.getText().toString()));
        }
        if(workoutType == 2)
        {
            values.put(DBManeger.C_WORKOUT_NAME,RestingName.getText().toString());
            values.put(DBManeger.C_WORKOUT_DURATION,Integer.parseInt(RestingDuration.getText().toString()));
        }
        database.insertOrThrow(DBManeger.DB_WORKOUT_TABLE, null, values);
        Intent intent = new Intent(getApplicationContext(), RegimentViewActivity.class);
        intent.putExtra("Selected",RID);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        clearlabels();
        int WID = Integer.parseInt(WorkoutList.get(position).get("WID"));
        String queryString =
                "SELECT "+ DBManeger.C_WORKOUT_NAME +
                        " , " + DBManeger.C_WORKOUT_DISTANCE+
                        " , " + DBManeger.C_WORKOUT_DURATION+
                        " , " + DBManeger.C_WORKOUT_REPS+
                        " , " + DBManeger.C_WORKOUT_SETS+
                        " , " + DBManeger.C_WORKOUT_WORKOUTNAME+
                        " , " + DBManeger.C_WORKOUT_TYPE+
                        " FROM "+ DBManeger.DB_WORKOUT_TABLE +
                        " WHERE " + DBManeger.C_WORKOUT_WID + " = " + WID;
        Cursor cursor = database.rawQuery(queryString,null);
        cursor.moveToNext();
        selectcell(cursor.getInt(6));
        WorkoutTypeSpinner.setSelection(cursor.getInt(6));
        if(cursor.getInt(6) == 0)
        {

            StandardName.setText(cursor.getString(0));
            StandardWorkoutName.setText(cursor.getString(5));
            StandardSets.setText(String.valueOf(cursor.getInt(4)));
            StandardReps.setText(String.valueOf(cursor.getInt(3)));
        }
        if(cursor.getInt(6) == 1)
        {
            RunningName.setText(cursor.getString(0));
            RunningDistance.setText(String.valueOf(cursor.getInt(1)));
        }
        if(cursor.getInt(6) == 2)
        {
            RestingName.setText(cursor.getString(0));
            RestingDuration.setText(String.valueOf(cursor.getInt(2)));
        }
        cursor.close();
    }

    private void clearlabels()
    {
        StandardName.setText("");
        StandardReps.setText("");
        StandardSets.setText("");
        StandardWorkoutName.setText("");
        RunningDistance.setText("");
        RunningName.setText("");
        RestingDuration.setText("");
        RestingName.setText("");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        clearlabels();
        workoutType = position;
        selectcell(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), RegimentViewActivity.class);
        startActivity(intent);
    }
}
