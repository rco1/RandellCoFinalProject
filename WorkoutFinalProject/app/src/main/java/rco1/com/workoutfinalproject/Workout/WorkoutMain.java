package rco1.com.workoutfinalproject.Workout;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;

import rco1.com.workoutfinalproject.Database.DBManeger;
import rco1.com.workoutfinalproject.MainActivity;
import rco1.com.workoutfinalproject.R;
import rco1.com.workoutfinalproject.Workout.WorkoutTypes.WorkoutHoldActivity;
import rco1.com.workoutfinalproject.Workout.WorkoutTypes.WorkoutRunningActivity;
import rco1.com.workoutfinalproject.Workout.WorkoutTypes.WorkoutStandardActivity;
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
//  WorkoutMain : This activity is to
//                  :Start a regiment
//                  :Start infinite Standard workout
//                  :Start infinite Running workout
//                  :Start infinite Holding workout
//------------------------------------------------
//------------------------------------------------
//  Citation:
//
//
//------------------------------------------------
public class WorkoutMain extends Activity implements AdapterView.OnItemSelectedListener,View.OnClickListener {
    ArrayList<HashMap<String,String>> RegimentList = new ArrayList<HashMap<String,String>>();
    Button StartWorkoutButton;
    Button StandardButton;
    Button RunningButton;
    Button HoldingButton;
    DBManeger dbmaneger;
    Spinner RegimentSpinner;
    SQLiteDatabase database;
    int RID = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wokout_main_layout);
        dbmaneger = new DBManeger(this);
        database = dbmaneger.getReadableDatabase();
        RegimentSpinner = (Spinner)findViewById(R.id.WorkoutMainRegimentSpinner);
        RegimentSpinner.setOnItemSelectedListener(this);
        StartWorkoutButton = (Button)findViewById(R.id.WorkoutMainStartButton);
        StandardButton = (Button)findViewById(R.id.WorkoutMainStandard);
        RunningButton = (Button)findViewById(R.id.WorkoutMainRunning);
        HoldingButton = (Button)findViewById(R.id.WorkoutMainHolding);
        StartWorkoutButton.setOnClickListener(this);
        StandardButton.setOnClickListener(this);
        RunningButton.setOnClickListener(this);
        HoldingButton.setOnClickListener(this);

        fillregimentSpinner();

    }
    private void fillregimentSpinner()
    {
        RegimentList.clear();

        String queryString =
                "SELECT "+ DBManeger.C_REGIMENT_NAME +
                        " , " + DBManeger.C_REGIMENT_RID +
                        " FROM "+ DBManeger.DB_REGIMENT_TABLE;
        Cursor cursor = database.rawQuery(queryString,null);
        for(int i=0;i<cursor.getCount();i++)//for each object in the json array
        {
            cursor.moveToNext();
            HashMap<String,String> temp = new HashMap<String,String>();
            temp.put("RegimentName",cursor.getString(0));
            temp.put("RegimentID", String.valueOf(cursor.getInt(1)));
            RegimentList.add(temp);
        }
        cursor.close();
        String[] fieldNames = new String[]{"RegimentName"};
        int[] fieldIDs = new int[]{R.id.SpinnerTab};
        SimpleAdapter adapter =
                new SimpleAdapter(this,RegimentList,R.layout.spinner_row,fieldNames,fieldIDs);
        RegimentSpinner.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        RID = Integer.parseInt(RegimentList.get(position).get("RegimentID"));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v)
    {
      switch (v.getId())
      {
          case R.id.WorkoutMainStartButton:
          {
              Intent intent = new Intent(getApplicationContext(), WorkoutMapper.class);
              intent.putExtra("RID",RID);
              startActivity(intent);
              break;
          }
          case R.id.WorkoutMainStandard:
          {
              Intent intent = new Intent(getApplicationContext(), WorkoutStandardActivity.class);
              startActivity(intent);
              break;
          }
          case R.id.WorkoutMainRunning:
          {
              Intent intent = new Intent(getApplicationContext(), WorkoutRunningActivity.class);
              startActivity(intent);
              break;
          }
          case R.id.WorkoutMainHolding:
          {
              Intent intent = new Intent(getApplicationContext(), WorkoutHoldActivity.class);
              startActivity(intent);
              break;
          }
      }
    }
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}
