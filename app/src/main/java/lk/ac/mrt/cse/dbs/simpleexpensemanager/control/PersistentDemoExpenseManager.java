package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;
import android.support.annotation.Nullable;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.db.helperDB;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;


public class PersistentDemoExpenseManager extends ExpenseManager {
    private helperDB db_1;
    public PersistentDemoExpenseManager(@Nullable Context context){
        this.db_1 = new helperDB(context);
        try{
            setup();
        }catch(Exception e){
            System.out.println("Setup error at Persistenet Expense Manager");
        }

    }
    @Override
    public void setup() throws ExpenseManagerException {


        TransactionDAO persistentTransactionDAO = new PersistentTransactionDAO(this.db_1);
        setTransactionsDAO(persistentTransactionDAO);

        AccountDAO persistentAccountDAO = new PersistentAccountDAO(this.db_1);
        setAccountsDAO(persistentAccountDAO);


    }
}
