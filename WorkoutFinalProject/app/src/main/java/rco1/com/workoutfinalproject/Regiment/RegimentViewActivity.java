package rco1.com.workoutfinalproject.Regiment;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rco1.com.workoutfinalproject.BaseAdaptor;
import rco1.com.workoutfinalproject.Database.DBManeger;
import rco1.com.workoutfinalproject.MainActivity;
import rco1.com.workoutfinalproject.R;
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
//  RegimentViewActivity : This navigate trough multiple
//      regiments and allows you to delete add and
//      create a wokout in the selected regiment.
//
//------------------------------------------------
//------------------------------------------------
//  Citation:
//
//------------------------------------------------
public class RegimentViewActivity extends BaseAdaptor implements View.OnClickListener,AdapterView.OnItemSelectedListener {

    ArrayList<HashMap<String,String>> RegimentList = new ArrayList<HashMap<String,String>>();
    ArrayList<HashMap<String,String>> WorkoutList = new ArrayList<HashMap<String,String>>();
    int RID = 1;
    DBManeger dbmaneger;
    SQLiteDatabase database;
    Spinner RegimentSpinner;
    ListView WorkoutListView;
    Button WorkoutButton;
    Button RegimentButton;
    Button DeleteRegimentButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbmaneger = new DBManeger(this);
        database = dbmaneger.getReadableDatabase();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regiment_view_layout);
        try
        {
            RID = getIntent().getExtras().getInt("Selected");

        }
        catch (Exception e)
        {

        }
        RegimentSpinner = (Spinner)findViewById(R.id.RegimentViewRegimentSpinner) ;
        WorkoutListView = (ListView)findViewById(R.id.RegimentViewListView);
        WorkoutButton = (Button)findViewById(R.id.RegimentViewAddWorkoutButton);
        RegimentButton = (Button)findViewById(R.id.RegimentViewAddRegimentButton);
        DeleteRegimentButton = (Button)findViewById(R.id.RegimentViewDeleteRegimentButton) ;
        RegimentSpinner.setOnItemSelectedListener(this);
        WorkoutButton.setOnClickListener(this);
        RegimentButton.setOnClickListener(this);
        DeleteRegimentButton.setOnClickListener(this);
        fillregimentSpinner();
        fillworkoutListView();
        for(int i=0;i<RegimentList.size();i++)
        {
            if(Integer.parseInt(RegimentList.get(i).get("RegimentID")) == RID)
            {
                RegimentSpinner.setSelection(i);
            }
        }

    }

    private void fillworkoutListView()
    {
        WorkoutList.clear();

        String queryString =
                "SELECT "+ DBManeger.C_WORKOUT_NAME +
                        " , " + DBManeger.C_WORKOUT_WID +
                        " FROM "+ DBManeger.DB_WORKOUT_TABLE +
                " WHERE " + DBManeger.C_WORKOUT_RID + " = " + RID;
                        //RegimentList.get(RIDpos).get("WID");

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
        MyListAdaptor adapter =
                new MyListAdaptor(this,R.layout.listview_row2,WorkoutList);
        WorkoutListView.setAdapter(adapter);



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
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.RegimentViewAddWorkoutButton:
            {
                Intent intent = new Intent(this, RegimentAddActivity.class);
                intent.putExtra("RID",RID);
                startActivity(intent);
                break;
            }
            case R.id.RegimentViewAddRegimentButton:
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);

                alert.setTitle("Create regiment");
                alert.setMessage("Please put your Regiment Name");

                // Set an EditText view to get user input
                final EditText input = new EditText(this);
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        ContentValues values = new ContentValues();
                        values.put(DBManeger.C_REGIMENT_NAME,input.getText().toString());
                        RID = (int) database.insertOrThrow(DBManeger.DB_REGIMENT_TABLE, null, values);
                        fillregimentSpinner();
                        for(int i=0;i<RegimentList.size();i++)
                        {
                            if(Integer.parseInt(RegimentList.get(i).get("RegimentID")) == RID)
                            {
                                RegimentSpinner.setSelection(i);
                            }
                        }
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();

                break;
            }
            case R.id.RegimentViewDeleteRegimentButton:
            {
                database.delete(DBManeger.DB_REGIMENT_TABLE,DBManeger.C_REGIMENT_RID + " = " + RID,null);
                Intent intent = new Intent(this,RegimentViewActivity.class);
                intent.putExtra("Selected",RID);
                startActivity(intent);
                break;
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        RID = Integer.parseInt(RegimentList.get(position).get("RegimentID"));
        fillworkoutListView();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class MyListAdaptor extends ArrayAdapter<HashMap<String,String>>
    {
        private List<HashMap<String, String>> values;
        private int layout;

        public MyListAdaptor(Context context, int resource, List<HashMap<String,String>> objects) {
            super(context, resource, objects);
            layout = resource;
            values = objects;
        }
        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            ViewHolder mainViewholder = null;

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(layout,parent,false);
            ViewHolder viewholder = new ViewHolder();
            viewholder.WorkoutName = (TextView)convertView.findViewById(R.id.ListViewTab);
            viewholder.DeleteButton = (Button)convertView.findViewById(R.id.DeleteButton);
            viewholder.DeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    database.delete(DBManeger.DB_WORKOUT_TABLE,DBManeger.C_WORKOUT_WID + " = " + getItem(position).get("WID"),null);
                    Intent intent = new Intent(getContext(),RegimentViewActivity.class);
                    intent.putExtra("Selected",RID);
                    startActivity(intent);

                }
            });
            convertView.setTag(viewholder);

            mainViewholder = (ViewHolder) convertView.getTag();
            mainViewholder.WorkoutName.setText(getItem(position).get("WorkoutName"));
            return convertView;
        }
    }
    public class ViewHolder
    {
        TextView WorkoutName;
        Button DeleteButton;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}
