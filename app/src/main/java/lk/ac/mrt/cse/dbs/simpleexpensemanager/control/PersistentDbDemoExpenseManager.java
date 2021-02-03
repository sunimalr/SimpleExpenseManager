package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.util.Log;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentDbAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentDbTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.sqlite.ExpenseManagerDbHelper;

public class PersistentDbDemoExpenseManager extends ExpenseManager {

    public ExpenseManagerDbHelper dbHelper;

    public PersistentDbDemoExpenseManager(ExpenseManagerDbHelper dbHelper) {
        this.dbHelper = dbHelper;
        try {
            setup();
        }
        catch (ExpenseManagerException e) {
            Log.e("Error in expenseMngr",e.getMessage());
        }

    }

    @Override
    public void setup() throws ExpenseManagerException {
        try {
            TransactionDAO transactionDAO = new PersistentDbTransactionDAO(dbHelper);
            setTransactionsDAO(transactionDAO);

            AccountDAO accountDAO = new PersistentDbAccountDAO(dbHelper);
            setAccountsDAO(accountDAO);

            Account dummyAcct1 = new Account("12345A", "Yoda Bank", "Anakin Skywalker", 10000.0);
            Account dummyAcct2 = new Account("78945Z", "Clone BC", "Obi-Wan Kenobi", 80000.0);
            getAccountsDAO().addAccount(dummyAcct1);
            getAccountsDAO().addAccount(dummyAcct2);
        }
        catch (Exception e) {
            throw new ExpenseManagerException("Something went wrong...");
        }
    }
}
