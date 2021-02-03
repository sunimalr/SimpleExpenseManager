package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.sqlite.ExpenseManagerDbHelper;

public class PersistentDbAccountDAO implements AccountDAO {

    private ExpenseManagerDbHelper dbHelper;
    private SQLiteDatabase db;

    public PersistentDbAccountDAO(ExpenseManagerDbHelper dbHelper) {
        this.dbHelper = dbHelper;
        this.db = dbHelper.getWritableDatabase();
    }

    @Override
    public List<String> getAccountNumbersList() {
        List<String> accountNumbers = new ArrayList<String>();
//        String[] projection = {
//                dbHelper.COLUMN_ACCOUNT_NO_ACCOUNT
//        };

//        Cursor cursor = db.query(
//          dbHelper.TABLE_NAME,
//          projection,
//          null,
//          null,
//          null,
//          null,
//          null,
//          null
//        );

        Cursor cursor = db.rawQuery("SELECT " + dbHelper.COLUMN_ACCOUNT_NO_ACCOUNT + " FROM " + dbHelper.TABLE_NAME_ACCOUNT, null);

        while (cursor.moveToNext()) {
            String accountNumber = cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_ACCOUNT_NO_ACCOUNT));
            accountNumbers.add(accountNumber);
        }

        cursor.close();
        return accountNumbers;
    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> accounts = new ArrayList<Account>();
//        String[] projection = {
//                dbHelper.COLUMN_ACCOUNT_NO_ACCOUNT,
//                dbHelper.COLUMN_ACCOUNT_HOLDER,
//                dbHelper.COLUMN_BALANCE,
//                dbHelper.COLUMN_BANK,
//        };

//        Cursor cursor = db.query(
//                dbHelper.TABLE_NAME,
//                projection,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null
//        );
        Cursor cursor = db.rawQuery("SELECT * FROM " + dbHelper.TABLE_NAME_ACCOUNT, null);

        while (cursor.moveToNext()) {
            String accountNumber = cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_ACCOUNT_NO_ACCOUNT));
            String accountHolder = cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_ACCOUNT_HOLDER));
            String bank = cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_BANK));
            String balance = cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_BALANCE));
            accounts.add(new Account(accountNumber, bank, accountHolder, Double.parseDouble(balance)));
        }
        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        Account account = new Account("", "", "", 0);
//        String[] projection = {
//                dbHelper.COLUMN_ACCOUNT_NO_ACCOUNT,
//                dbHelper.COLUMN_ACCOUNT_HOLDER,
//                dbHelper.COLUMN_BALANCE,
//                dbHelper.COLUMN_BANK,
//        };

//        String selection = dbHelper.COLUMN_ACCOUNT_NO_ACCOUNT + " = ?";
        String[] selectionArgs = { dbHelper.COLUMN_ACCOUNT_NO_ACCOUNT ,accountNo};

//        Cursor cursor = db.query(
//                dbHelper.TABLE_NAME,
//                projection,
//                selection,
//                selectionArgs,
//                null,
//                null,
//                null,
//                null
//        );

        Cursor cursor = db.rawQuery("SELECT * FROM " + dbHelper.TABLE_NAME_ACCOUNT + " WHERE ? = ?", selectionArgs);
        while (cursor.moveToNext()) {
            String accountNumber = cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_ACCOUNT_NO_ACCOUNT));
            String accountHolder = cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_ACCOUNT_HOLDER));
            String bank = cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_BANK));
            String balance = cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_BALANCE));
            account = new Account(accountNumber, bank, accountHolder, Double.parseDouble(balance));
        }
        return account;
    }

    @Override
    public void addAccount(Account account) {
        try {
            ContentValues values = new ContentValues();
            values.put(dbHelper.COLUMN_ACCOUNT_NO_ACCOUNT, account.getAccountNo());
            values.put(dbHelper.COLUMN_ACCOUNT_HOLDER, account.getAccountHolderName());
            values.put(dbHelper.COLUMN_BANK, account.getBankName());
            values.put(dbHelper.COLUMN_BALANCE, account.getBalance());
            long newRowId = db.insert(dbHelper.TABLE_NAME_ACCOUNT, null, values);
        }
        catch (Exception e) {
            Log.e("My error", e.getMessage());
        }
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        String selection = dbHelper.COLUMN_ACCOUNT_NO_ACCOUNT + " LIKE ?";
        String[] selectionArgs = { accountNo };
        int deleteRows = db.delete(dbHelper.TABLE_NAME_TRANSACTION, selection, selectionArgs);
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        ContentValues values = new ContentValues();
        Account account = getAccount(accountNo);
        switch (expenseType) {
            case EXPENSE:
                account.setBalance(account.getBalance() - amount);
                break;
            case INCOME:
                account.setBalance(account.getBalance() + amount);
                break;
        }
        values.put(dbHelper.COLUMN_AMOUNT, account.getBalance());
        String selection = dbHelper.COLUMN_ACCOUNT_NO_ACCOUNT + " LIKE ?";
        String[] selectionArgs = { accountNo };
        int count = db.update(dbHelper.TABLE_NAME_TRANSACTION, values, selection, selectionArgs);
    }
}
