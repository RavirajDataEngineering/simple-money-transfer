package com.raviraj.simple_money_transfer.database.model.dao;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.raviraj.simplemoneytransfer.database.model.Account;
import com.raviraj.simplemoneytransfer.exceptions.DatabaseServiceException;
import com.raviraj.simplemoneytransfer.inmemorydatabase.InMemoryDatabase;

public class AccountDao {

	InMemoryDatabase db = InMemoryDatabase.getInstance();

	private static final Logger LOGGER = LoggerFactory.getLogger(AccountDao.class);
		
	public Account getAccountById(long id) {
		return db.getAccounts().get(id);
	}

	public Account createAccount(Account account) throws DatabaseServiceException {
		Account acc = db.getAccounts().get(account.getAccountId());
		if(acc!=null) {
			throw new DatabaseServiceException("Account already exists");
		}
		db.getAccounts().put(account.getAccountId(), account);
		return account;
		
	}

	public List<Account> getAllAccounts() {
		synchronized (db.getAccounts()) {
			return new ArrayList<Account>(db.getAccounts().values());
		}
	}

	public void updateAccount(Account account) throws DatabaseServiceException  {

		Account acc = db.getAccounts().get(account.getAccountId());
		if(acc==null) {
			LOGGER.info("Account to update doesnt exist : " + account.getAccountId());
			throw new DatabaseServiceException("Account to update doesnt exist");
		}
		db.getAccounts().put(account.getAccountId(), account);
	}

	public void deleteAccount(long id) throws DatabaseServiceException {

		Account acc = db.getAccounts().get(id);
		if(acc==null) {
			LOGGER.info("Account to delete doesnt exist : " + id);
			throw new DatabaseServiceException("Account to update doesnt exist");
		}
		db.getAccounts().remove(id);
	}
	
	public void updateBalance(long id,double amount,int multiplier) throws DatabaseServiceException {
		Account acc = db.getAccounts().get(id);
		
		if(acc==null) {
			LOGGER.info("Account doesnt exist " + id);
			throw new DatabaseServiceException("account doesnt exist");
		}
		
		acc.setBalance(acc.getBalance()+(multiplier*amount));
	}

}
