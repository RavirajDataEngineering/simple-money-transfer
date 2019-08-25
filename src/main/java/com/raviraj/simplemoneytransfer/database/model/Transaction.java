package com.raviraj.simplemoneytransfer.database.model;

import com.raviraj.simplemoneytransfer.service.utils.TransactionType;

public class Transaction {

	private long transactionId;
	
	private long creditAccountId;
	
	private long debitAccountId;
	
	private double amount;
	
	private Source transactionParty;
	
	public long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(long transactionId) {
		this.transactionId = transactionId;
	}

	public long getCreditAccountId() {
		return creditAccountId;
	}

	public void setCreditAccountId(long creditAccountId) {
		this.creditAccountId = creditAccountId;
	}

	public long getDebitAccountId() {
		return debitAccountId;
	}

	public void setDebitAccountId(long debitAccountId) {
		this.debitAccountId = debitAccountId;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	

	

	
}
