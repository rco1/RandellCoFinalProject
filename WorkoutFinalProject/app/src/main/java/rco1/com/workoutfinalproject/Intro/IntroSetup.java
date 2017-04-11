package rco1.com.workoutfinalproject.Intro;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

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
//  IntroSetup : this makes the user add his basic
//      information into the database for the service
//      in the app.
//      Name not used,
//      Age not used,
//      Weight not used,
//      Height used in measuring the distance in the running workout.
//
//------------------------------------------------
//------------------------------------------------
//  Citation:
//
//------------------------------------------------
public class IntroSetup extends Activity implements View.OnClickListener {
    EditText Name;
    EditText Weight;
    EditText Height;
    DatePicker Age;
    DBManeger dbmaneger;
    SQLiteDatabase database;
    boolean EditMode = false;
    private int UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_setup_layout);
        dbmaneger = new DBManeger(this);
        database = dbmaneger.getReadableDatabase();
        Name = (EditText)findViewById(R.id.SetupNameEditText);
        Weight = (EditText)findViewById(R.id.SetupWeightEditText);
        Height = (EditText)findViewById(R.id.SetupHeightEditText);
        Age = (DatePicker)findViewById(R.id.SetupAgeDatePicker);
        Button saveetting = (Button)findViewById(R.id.SetupSetMeasurements);
        saveetting.setOnClickListener(this);

        String queryString =
                "SELECT "+ DBManeger.C_USER_NAME +
                        " , " + DBManeger.C_USER_WEIGHT +
                        " , " + DBManeger.C_USER_HEIGHT +
                        " , " + DBManeger.C_USER_AGE +
                        " , " + DBManeger.C_USER_ID +
                        " FROM "+ DBManeger.DB_USER_TABLE;
        Cursor cursor = database.rawQuery(queryString,null);
        if(cursor.getCount() !=0)
        {
            EditMode = true;
            cursor.moveToNext();
            Name.setText(cursor.getString(0));
            Weight.setText(String.valueOf(cursor.getInt(1)));
            Height.setText(String.valueOf(cursor.getInt(2)));
            UID = cursor.getInt(4);
        }
        cursor.close();

    }

    @Override
    public void onClick(View v)
    {
        if(EditMode)
        {
            //update
            ContentValues values = new ContentValues();
            values.put(DBManeger.C_USER_NAME,Name.getText().toString());
            values.put(DBManeger.C_USER_HEIGHT,Integer.parseInt(Height.getText().toString()));
            values.put(DBManeger.C_USER_WEIGHT,Integer.parseInt(Weight.getText().toString()));
            values.put(DBManeger.C_USER_AGE,0);
            database.update(DBManeger.DB_USER_TABLE,values,DBManeger.C_USER_ID + " = " +UID ,null);
        }
        else
        {

            //insert4
            ContentValues values = new ContentValues();
            values.put(DBManeger.C_USER_NAME,Name.getText().toString());
            values.put(DBManeger.C_USER_HEIGHT,Integer.parseInt(Height.getText().toString()));
            values.put(DBManeger.C_USER_WEIGHT,Integer.parseInt(Weight.getText().toString()));
            values.put(DBManeger.C_USER_AGE,0);
            database.insertOrThrow(DBManeger.DB_USER_TABLE, null, values);
        }
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}
