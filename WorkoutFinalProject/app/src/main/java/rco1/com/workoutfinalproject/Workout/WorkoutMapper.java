package rco1.com.workoutfinalproject.Workout;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import rco1.com.workoutfinalproject.BaseAdaptor;
import rco1.com.workoutfinalproject.Database.DBManeger;
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
//  WorkoutMapper : This routes and directs the flow of different workout types.
//
//------------------------------------------------
//------------------------------------------------
//  Citation:
//
//------------------------------------------------

public class WorkoutMapper extends BaseAdaptor
{
    DBManeger dbmaneger;
    SQLiteDatabase database;
    int RID = 1;
    int mode = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbmaneger = new DBManeger(this);
        database = dbmaneger.getReadableDatabase();
        try {
            RID = getIntent().getExtras().getInt("RID");
        }
        catch (Exception e)
        {
            Intent intent = new Intent(getApplicationContext(), WorkoutMain.class);
            startActivity(intent);
        }
        try {
            mode = getIntent().getExtras().getInt("MODE");
        }
        catch (Exception e)
        {
            mode = 0;
        }

        String queryString =
                "SELECT "+ DBManeger.C_WORKOUT_TYPE +
                        " FROM "+ DBManeger.DB_WORKOUT_TABLE +
                        " WHERE " + DBManeger.C_WORKOUT_RID + " = " + RID;
        Cursor cursor = database.rawQuery(queryString,null);
        if(cursor.getCount() <= mode)
        {
            Toast.makeText(this,"Finish Workout",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, WorkoutMain.class);
            startActivity(intent);
        }
        else
        {
            cursor.moveToPosition(mode);
            int type = cursor.getInt(0);
            if(type == 0)
            {
                Intent intent = new Intent(getApplicationContext(), WorkoutStandardActivity.class);
                intent.putExtra("RID",RID);
                intent.putExtra("MODE",mode);
                startActivity(intent);
            }
            if(type == 1)
            {
                Intent intent = new Intent(getApplicationContext(), WorkoutRunningActivity.class);
                intent.putExtra("RID",RID);
                intent.putExtra("MODE",mode);
                startActivity(intent);
            }
            if(type == 2)
            {
                Intent intent = new Intent(getApplicationContext(), WorkoutHoldActivity.class);
                intent.putExtra("RID",RID);
                intent.putExtra("MODE",mode);
                startActivity(intent);
            }
        }

        cursor.close();

    }


}
