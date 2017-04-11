package rco1.com.workoutfinalproject.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
//  DBManeger : this is the database that will collect data thought the app
//      DB_USER_TABLE : This is only row big to hold users measurements
//      DB_REGIMENT_TABLE : This collects the different regiment i have in the database
//      DB_WORKOUT_TABLE : This collects the workout information
//      DB_SAVEDWORKOUT_TABLE : (NOT USED YET) this will take record of the users proud workouts like new records or favorite workout.
//------------------------------------------------
//------------------------------------------------
//  Citation:
//
//------------------------------------------------

public class DBManeger extends SQLiteOpenHelper
{
    static final String TAG = "DBBuilder";
    static final String DB_NAME = "Cirosis.db";
    static final int    DB_VERSION = 5;

    //--------------------------------------------- User
    public static final String DB_USER_TABLE = "User";
    public static final String C_USER_ID = "User_id";
    public static final String C_USER_NAME = "Name";
    public static final String C_USER_WEIGHT = "Weight";
    public static final String C_USER_HEIGHT = "Height";
    public static final String C_USER_AGE = "AGE";
    //---------------------------------------------Regiment
    public static final String DB_REGIMENT_TABLE = "Regiment";
    public static final String C_REGIMENT_RID = "RID";
    public static final String C_REGIMENT_NAME = "Name";

    //---------------------------------------------Workout
    public static final String DB_WORKOUT_TABLE = "Workout";
    public static final String C_WORKOUT_WID = "WID";
    public static final String C_WORKOUT_RID = "RID";
    public static final String C_WORKOUT_NAME = "Name";
    public static final String C_WORKOUT_TYPE = "Type";//0 standard ,1 running , 2 rest
    public static final String C_WORKOUT_WORKOUTNAME = "WorkoutName";
    public static final String C_WORKOUT_SETS = "Sets";
    public static final String C_WORKOUT_REPS = "Reps";
    public static final String C_WORKOUT_DISTANCE = "Distance";
    public static final String C_WORKOUT_DURATION = "Duration";
    //---------------------------------------------Saved Workout\
    public static final String DB_SAVEDWORKOUT_TABLE = "SavedWorkout";
    public static final String C_SAVEDWORKOUT_SWID = "SWID";
    public static final String C_SAVEDWORKOUT_NAME = "Name";
    public static final String C_SAVEDWORKOUT_DATE = "Date";
    public static final String C_SAVEDWORKOUT_TYPE = "Type";
    public static final String C_SAVEDWORKOUT_WORKOUTNAME = "WorkoutName";
    public static final String C_SAVEDWORKOUT_SETS = "Sets";
    public static final String C_SAVEDWORKOUT_REPS = "Reps";
    public static final String C_SAVEDWORKOUT_DISTANCE = "Distance";
    //

    public DBManeger(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database)
    {
        String Usersql = "CREATE TABLE " + DB_USER_TABLE + " ( " +
                C_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                C_USER_NAME + " TEXT, " +
                C_USER_WEIGHT + " INTEGER, " +
                C_USER_HEIGHT + " INTEGER, " +
                C_USER_AGE + " TEXT " + ")";
        String Regimentsql = "CREATE TABLE " + DB_REGIMENT_TABLE + " ( " +
                C_REGIMENT_RID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                C_REGIMENT_NAME + " TEXT " + ")";
        String Workoutsql = "CREATE TABLE " + DB_WORKOUT_TABLE + " ( " +
                C_WORKOUT_WID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                C_WORKOUT_RID + " INTEGER, " +
                C_WORKOUT_NAME + " TEXT, " +
                C_WORKOUT_TYPE + " INTEGER, " +
                C_WORKOUT_WORKOUTNAME + " TEXT, " +
                C_WORKOUT_SETS + " INTEGER, " +
                C_WORKOUT_REPS + " INTEGER, " +
                C_WORKOUT_DISTANCE + " INTEGER, " +
                C_WORKOUT_DURATION + " INTEGER " + ")";

        String SaveWorkoutsql = "CREATE TABLE " + DB_SAVEDWORKOUT_TABLE + " ( " +
                C_SAVEDWORKOUT_SWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                C_SAVEDWORKOUT_NAME + " TEXT, " +
                C_SAVEDWORKOUT_DATE + " TEXT, " +
                C_SAVEDWORKOUT_TYPE + " INTEGER, " +
                C_SAVEDWORKOUT_WORKOUTNAME + " TEXT, " +
                C_SAVEDWORKOUT_SETS + " INTEGER, " +
                C_SAVEDWORKOUT_REPS + " INTEGER, " +
                C_SAVEDWORKOUT_DISTANCE + " INTEGER " + ")";


        database.execSQL(Usersql);
        database.execSQL(Regimentsql);
        database.execSQL(Workoutsql);
        database.execSQL(SaveWorkoutsql);

        ContentValues values =new ContentValues();
        values.put(DBManeger.C_REGIMENT_NAME,"One Punch Training");
        long RID = database.insertOrThrow(DBManeger.DB_REGIMENT_TABLE, null, values);
        values.clear();
        //
        values.put(DBManeger.C_WORKOUT_RID,RID);
        values.put(DBManeger.C_WORKOUT_TYPE,0);
        values.put(DBManeger.C_WORKOUT_WORKOUTNAME,"Push Up");
        values.put(DBManeger.C_WORKOUT_SETS,1);
        values.put(DBManeger.C_WORKOUT_REPS,100);
        values.put(DBManeger.C_WORKOUT_NAME,"100 PUSH UPS !!!!");
        database.insertOrThrow(DBManeger.DB_WORKOUT_TABLE, null, values);
        values.clear();
        values.put(DBManeger.C_WORKOUT_RID,RID);
        values.put(DBManeger.C_WORKOUT_TYPE,0);
        values.put(DBManeger.C_WORKOUT_NAME,"100 SIT UPS !!!!");
        values.put(DBManeger.C_WORKOUT_WORKOUTNAME,"Sit Up");
        values.put(DBManeger.C_WORKOUT_SETS,1);
        values.put(DBManeger.C_WORKOUT_REPS,100);
        database.insertOrThrow(DBManeger.DB_WORKOUT_TABLE, null, values);
        values.clear();
        values.put(DBManeger.C_WORKOUT_RID,RID);
        values.put(DBManeger.C_WORKOUT_TYPE,0);
        values.put(DBManeger.C_WORKOUT_NAME,"100 SQUATS !!!!");
        values.put(DBManeger.C_WORKOUT_WORKOUTNAME,"Squats");
        values.put(DBManeger.C_WORKOUT_SETS,1);
        values.put(DBManeger.C_WORKOUT_REPS,100);
        database.insertOrThrow(DBManeger.DB_WORKOUT_TABLE, null, values);
        values.clear();
        values.put(DBManeger.C_WORKOUT_RID,RID);
        values.put(DBManeger.C_WORKOUT_TYPE,1);
        values.put(DBManeger.C_WORKOUT_NAME,"10KM RUN !!!!");
        values.put(DBManeger.C_WORKOUT_DISTANCE,10000);
        database.insertOrThrow(DBManeger.DB_WORKOUT_TABLE, null, values);
        values.clear();

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        database.execSQL("drop table if exists " + DB_REGIMENT_TABLE);
        database.execSQL("drop table if exists " + DB_SAVEDWORKOUT_TABLE);
        database.execSQL("drop table if exists " + DB_WORKOUT_TABLE);
        database.execSQL("drop table if exists " + DB_USER_TABLE);
        onCreate(database);
    }
}
