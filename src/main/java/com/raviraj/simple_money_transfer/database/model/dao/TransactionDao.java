package com.raviraj.simple_money_transfer.database.model.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.raviraj.simplemoneytransfer.database.model.Transaction;
import com.raviraj.simplemoneytransfer.exceptions.DatabaseServiceException;
import com.raviraj.simplemoneytransfer.inmemorydatabase.InMemoryDatabase;

public class TransactionDao {

	private InMemoryDatabase db = InMemoryDatabase.getInstance();
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionDao.class);
	
	public Transaction getTranactionById(long id) {
		
		return db.getTransactions().get(id);
	}

	public void addTransaction(Transaction tr) {
		
		db.getTransactions().put(tr.getTransactionId(), tr);
	}

	public void updateTranscation(Transaction tr) throws DatabaseServiceException {
		
		Transaction oldTr = db.getTransactions().get(tr.getTransactionId());
		if(oldTr == null) {
			LOGGER.info("Transaction to update doesnt exist " + tr.getTransactionId());
			throw new DatabaseServiceException("Transaction to update not found");
		}
		db.getTransactions().put(tr.getTransactionId(), tr);
	}

	public void deleteTransaction(long id) throws DatabaseServiceException {
		Transaction oldTr = db.getTransactions().get(id);
		if(oldTr == null) {
			LOGGER.info("Transaction to delete doesnt exist " + id);
			throw new DatabaseServiceException("Transaction to update not found");
		}
		db.getTransactions().remove(id);
	}

}
	