package com.raviraj.simplemoneytransfer.service.utils;

import com.raviraj.simplemoneytransfer.database.model.Transaction;
import com.raviraj.simplemoneytransfer.inmemorydatabase.InMemoryDatabase;

public class TransactionUtil {
	
	public static Transaction buildTransaction(TransactionPayload payload) {
		Transaction tr = new Transaction();
		tr.setCreditAccountId(payload.getCreditAccountId());
		tr.setDebitAccountId(payload.getDebitAccountId());
		tr.setAmount(payload.getAmount());
		tr.setTransactionId(generateTransactionId());
		return tr;
	}
	
	public static Transaction buildTransaction(Transaction transaction) {
		Transaction tr = new Transaction();
		tr.setCreditAccountId(transaction.getCreditAccountId());
		tr.setDebitAccountId(transaction.getDebitAccountId());
		tr.setAmount(transaction.getAmount());
		tr.setTransactionId(generateTransactionId());
		return tr;
	}

	private static long generateTransactionId() {
		long tr = 0;
		synchronized (InMemoryDatabase.getInstance().getTransactions()) {
			tr = InMemoryDatabase.getLatestTrId();
			tr++;
			InMemoryDatabase.setLatestTrId(tr);
		}
		return tr;
	}
	
	
}
