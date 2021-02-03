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

    public PersistentDbAccountDAO(ExpenseManagerDbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public List<String> getAccountNumbersList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<String> accountNumbers = new ArrayList<String>();

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
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Account> accounts = new ArrayList<Account>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + dbHelper.TABLE_NAME_ACCOUNT, null);

        while (cursor.moveToNext()) {
            String accountNumber = cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_ACCOUNT_NO_ACCOUNT));
            String accountHolder = cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_ACCOUNT_HOLDER));
            String bank = cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_BANK));
            String balance = cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_BALANCE));
            accounts.add(new Account(accountNumber, bank, accountHolder, Double.parseDouble(balance)));
        }
        cursor.close();
        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Account account = new Account("", "", "", 0);
            String[] selectionArgs = {accountNo};

            Cursor cursor = db.rawQuery("SELECT * FROM " + dbHelper.TABLE_NAME_ACCOUNT + " WHERE " + dbHelper.COLUMN_ACCOUNT_NO_ACCOUNT + " = ?", selectionArgs);
            if (cursor.moveToFirst()) {
                String accountNumber = cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_ACCOUNT_NO_ACCOUNT));
                String accountHolder = cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_ACCOUNT_HOLDER));
                String bank = cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_BANK));
                String balance = cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_BALANCE));
                account = new Account(accountNumber, bank, accountHolder, Double.parseDouble(balance));
            }
            cursor.close();
            return account;
        }
        catch (Exception e) {
            throw new InvalidAccountException("Something went wrong...");
        }
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
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
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String selection = dbHelper.COLUMN_ACCOUNT_NO_ACCOUNT + " LIKE ?";
            String[] selectionArgs = {accountNo};
            int deleteRows = db.delete(dbHelper.TABLE_NAME_TRANSACTION, selection, selectionArgs);
        }
        catch (Exception e) {
            throw new InvalidAccountException("Something went wrong...");
        }
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
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
            values.put(dbHelper.COLUMN_BALANCE, account.getBalance());
            String selection = dbHelper.COLUMN_ACCOUNT_NO_ACCOUNT + " LIKE ?";
            String[] selectionArgs = {accountNo};
            int count = db.update(dbHelper.TABLE_NAME_ACCOUNT, values, selection, selectionArgs);
        }
        catch (Exception e) {
            throw new InvalidAccountException("Something went wrong...");
        }
    }
}
