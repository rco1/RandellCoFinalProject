package rco1.com.workoutfinalproject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import rco1.com.workoutfinalproject.Database.DBManeger;
import rco1.com.workoutfinalproject.Intro.IntroSetup;
import rco1.com.workoutfinalproject.Regiment.RegimentViewActivity;
import rco1.com.workoutfinalproject.Workout.WorkoutMain;
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
//  MainActivity : this activity will check the database
//      if the user have inputed their personal information
//      before they navigate on to our app.If not they would
//      be put into the Setup view to add their measurements.
//      This also displays the 3 mainviews that they can acceess.
//------------------------------------------------
//------------------------------------------------
//  Citation:
//
//------------------------------------------------

public class MainActivity extends BaseAdaptor {
    DBManeger dbmaneger;
    SQLiteDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbmaneger = new DBManeger(this);
        database = dbmaneger.getReadableDatabase();

        String queryString =
                "SELECT "+ DBManeger.C_USER_ID +
                        " FROM "+ DBManeger.DB_USER_TABLE;
        Cursor cursor = database.rawQuery(queryString,null);
        if(cursor.getCount() == 0)
        {
            cursor.close();
            Intent intent = new Intent(getApplicationContext(), IntroSetup.class);
            startActivity(intent);
        }
        cursor.close();

        Button RegimentManegerButton = (Button)findViewById(R.id.HomeRegimentManegerButton);
        RegimentManegerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegimentViewActivity.class);
                startActivity(intent);
            }
        });

        Button StartWorkoutButton = (Button)findViewById(R.id.HomeStartWorkoutButton);
        StartWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WorkoutMain.class);
                startActivity(intent);
            }
        });

        Button ChangeSettingsButton = (Button)findViewById(R.id.HomeChangeSettingsButton);
        ChangeSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), IntroSetup.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}
