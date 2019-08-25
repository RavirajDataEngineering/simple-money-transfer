package com.raviraj.simplemoneytransfer.service.utils;


public class TransactionPayload {

	private long debitAccountId;
	private long creditAccountId;
	private double amount;
	
	public long getDebitAccountId() {
		return debitAccountId;
	}
	public void setDebitAccountId(long debitAccountId) {
		this.debitAccountId = debitAccountId;
	}
	public long getCreditAccountId() {
		return creditAccountId;
	}
	public void setCreditAccountId(long creditAccountId) {
		this.creditAccountId = creditAccountId;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
}
