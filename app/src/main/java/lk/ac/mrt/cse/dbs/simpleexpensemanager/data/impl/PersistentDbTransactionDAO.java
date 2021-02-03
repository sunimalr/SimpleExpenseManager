package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.sqlite.ExpenseManagerDbHelper;

public class PersistentDbTransactionDAO implements TransactionDAO {

    private ExpenseManagerDbHelper dbHelper;

    List<Transaction> transactions = new ArrayList<Transaction>();

    public PersistentDbTransactionDAO(ExpenseManagerDbHelper dbHelper) {
        this.dbHelper = dbHelper;
        getAllTransactionLogs();
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
        ContentValues values = new ContentValues();
        values.put(dbHelper.COLUMN_ACCOUNT_NO_TRANSACTION, accountNo);
        values.put(dbHelper.COLUMN_AMOUNT, amount);
        values.put(dbHelper.COLUMN_DATE, new SimpleDateFormat("dd-MM-yyyy").format(date));
        values.put(dbHelper.COLUMN_TYPE, expenseType.toString());
        long newRowId = db.insert(dbHelper.TABLE_NAME_TRANSACTION, null, values);
        transactions = getAllTransactionLogs();
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        transactions = new ArrayList<Transaction>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + dbHelper.TABLE_NAME_TRANSACTION + " ORDER BY " + dbHelper.COLUMN_DATE + " DESC", null);
        while(cursor.moveToNext()) {
            String accountNumber = cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_ACCOUNT_NO_TRANSACTION));
            String amount = cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_AMOUNT));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_DATE));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_TYPE));
            try {
                transactions.add(new Transaction(new SimpleDateFormat("dd-MM-yyyy").parse(date), accountNumber, ExpenseType.valueOf(type), Double.parseDouble(amount)));
            }
            catch (Exception e) {
                Log.e("transaction_log_error", e.getMessage() );
            }
        }
        cursor.close();

        return transactions;

    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        int size = transactions.size();
        Log.i("My test", transactions.toString());
        if (size <= limit) {
            return transactions;
        }
        // return the last <code>limit</code> number of transaction logs
        return transactions.subList(size - limit, size);
    }
}
