package mx.com.ceti.calculator.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Carlos Rodríguez on 3/11/19.
 */
public class DataSQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "PegasusControl";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_OPERATIONS = "operations";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_OPERATION = "operation";
    private static final String COLUMN_RESULT = "result";
    private static final String COLUMN_DATETIME = "dateTime";

    public static int getDatabaseVersion() {
        return DATABASE_VERSION;
    }

    public static String getTableOperations() {
        return TABLE_OPERATIONS;
    }

    public static String getColumnOperation() {
        return COLUMN_OPERATION;
    }

    public static String getColumnResult() {
        return COLUMN_RESULT;
    }

    public static String getColumnDatetime() {
        return COLUMN_DATETIME;
    }

    private static final String DATABASE_CREATE = "CREATE TABLE "
            + TABLE_OPERATIONS
            + "("
            + COLUMN_OPERATION + " TEXT, "
            + COLUMN_RESULT + " TEXT, "
            + COLUMN_DATETIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
            + ");";

    private static final String DATABASE_DELETE_ALL = "DELETE FROM TABLE " + TABLE_OPERATIONS;

    private static DataSQLiteHelper dataSQLiteHelper;

    private DataSQLiteHelper(Context applicationContext) {
        super(applicationContext, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DataSQLiteHelper getInstance(Context context){
        if (dataSQLiteHelper == null){
            dataSQLiteHelper = new DataSQLiteHelper(context.getApplicationContext());
        }
        return dataSQLiteHelper;
    }

    public DataSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DATABASE_DELETE_ALL);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DATABASE_DELETE_ALL);
    }
}
