package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;

import java.util.ArrayList;
import java.util.*;
import android.database.Cursor;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.db.helperDB;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;


public class PersistentAccountDAO implements AccountDAO {
    private final helperDB db_1;

    private static final String TABLE_ACCOUNT = "account";

    private static final String ACCOUNT_NO = "accountno";
    private static final String ACCOUNT_BANKNAME = "bankname";
    private static final String ACCOUNT_HOLDERNAME = "accountHolderName";
    private static final String ACCOUNT_BALANCE = "balance";



    public PersistentAccountDAO(helperDB db){
        this.db_1 = db;
    }


    @Override
    public List<String> getAccountNumbersList() {
        Cursor res = this.db_1.getData(TABLE_ACCOUNT,new String[] {"accountno"}, new String[][] {});
        List<String> accountNumbers = new ArrayList<String>();

        if (res.moveToFirst()) {
            do {
                // Adding account to list
                accountNumbers.add(res.getString(0));
            } while (res.moveToNext());
        }
        res.close();
        return accountNumbers;
    }

    @Override
    public List<Account> getAccountsList() {
        Cursor res = this.db_1.getData(TABLE_ACCOUNT,new String[] {"*"}, new String[][] {});
        List<Account> accounts = new ArrayList<Account>();
        if (res.moveToFirst()) {
            do {
                Account account = new Account(res.getString(0), res.getString(1), res.getString(2), Double.parseDouble(res.getString(3))
                );

                accounts.add(account);
            } while (res.moveToNext());
        }
        res.close();
        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        String[] condition = {"accountNo", "=",accountNo};
        Cursor res = this.db_1.getData(TABLE_ACCOUNT,new String[] {"*"}, new String[][] {condition});
        if (res != null) {
            res.moveToFirst();
            Account account = new Account(res.getString(0), res.getString(1), res.getString(2), Double.parseDouble(res.getString(3))
            );
            res.close();
            return account;
        }
        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    @Override
    public void addAccount(Account account) {
        ContentValues accContent = new ContentValues();
        accContent.put(ACCOUNT_NO, account.getAccountNo());
        accContent.put(ACCOUNT_BANKNAME, account.getBankName());
        accContent.put(ACCOUNT_HOLDERNAME, account.getAccountHolderName());
        accContent.put(ACCOUNT_BALANCE, account.getBalance());

        //helperDB db = helperDB.getInstance();
        this.db_1.insertData(TABLE_ACCOUNT, accContent);

    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        int res = this.db_1.deleteData("account","accountno",accountNo);
        if(res == 0){
            throw new InvalidAccountException("Invalid Account Number");
        }


    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        double balance = 0;
        double total = 0;
        try{
            Account acc = getAccount(accountNo);
            balance = acc.getBalance();
        }catch(Exception e){
            throw new InvalidAccountException("Invalid Account Number");
        }

        switch (expenseType){
            case EXPENSE:
                if(balance < amount){
                    throw new InvalidAccountException("Insufficient Account Balance");
                }else {
                    total = balance - amount;
                    break;
                }
            case INCOME:
                total = amount +balance;
                break;
        }
        String[] condition = {"accountno","=",accountNo};
        ContentValues accContent = new ContentValues();
        accContent.put(ACCOUNT_BALANCE, total);
        boolean res = this.db_1.updateData("account",accContent, condition );
        if(!res){
            throw new InvalidAccountException("Account number is invalid");
        }
    }


}
