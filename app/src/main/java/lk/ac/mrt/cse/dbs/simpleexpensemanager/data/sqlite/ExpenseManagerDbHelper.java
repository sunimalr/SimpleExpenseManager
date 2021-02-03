package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class ExpenseManagerDbHelper extends SQLiteOpenHelper implements BaseColumns {

    public final String TABLE_NAME_ACCOUNT = "account";
    public final String COLUMN_ACCOUNT_NO_ACCOUNT = "account_no";
    public final String COLUMN_BANK = "bank";
    public final String COLUMN_ACCOUNT_HOLDER = "account_holder";
    public final String COLUMN_BALANCE = "balance";

    public final String SQL_CREATE_ENTRIES_ACCOUNT = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_ACCOUNT + " (" +
            COLUMN_ACCOUNT_NO_ACCOUNT + " VARCHAR(10) PRIMARY KEY," +
            COLUMN_BANK + " VARCHAR(20)," +
            COLUMN_ACCOUNT_HOLDER + " VARCHAR(50)," +
            COLUMN_BALANCE + " FLOAT)";

    public final String SQL_DELETE_ENTRIES_ACCOUNT =
            "DROP TABLE IF EXISTS " + TABLE_NAME_ACCOUNT;

    public final String TABLE_NAME_TRANSACTION = "transactions";
    public final String COLUMN_ACCOUNT_NO_TRANSACTION = "account_no";
    public final String COLUMN_TYPE = "type";
    public final String COLUMN_AMOUNT = "amount";
    public final String COLUMN_DATE = "date";

    public final String SQL_CREATE_ENTRIES_TRANSACTION = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_TRANSACTION + " (" +
            _ID + " INTEGER PRIMARY KEY," +
            COLUMN_ACCOUNT_NO_TRANSACTION + " VARCHAR(10)," +
            COLUMN_TYPE + " VARCHAR(10)," +
            COLUMN_AMOUNT + " FLOAT," +
            COLUMN_DATE + " DATE, FOREIGN KEY (" + COLUMN_ACCOUNT_NO_TRANSACTION + ") REFERENCES " +
            TABLE_NAME_ACCOUNT + " (" + COLUMN_ACCOUNT_NO_ACCOUNT + "))";

    public final String SQL_DELETE_ENTRIES_TRANSACTION =
            "DROP TABLE IF EXISTS " + TABLE_NAME_TRANSACTION;

    public ExpenseManagerDbHelper(Context context) {
        super(context, "ExpenseManager.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        db.execSQL(SQL_CREATE_ENTRIES_ACCOUNT);
        db.execSQL(SQL_CREATE_ENTRIES_TRANSACTION);
        Log.i("test","test");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES_ACCOUNT);
        db.execSQL(SQL_DELETE_ENTRIES_TRANSACTION);
        onCreate(db);
    }


}
