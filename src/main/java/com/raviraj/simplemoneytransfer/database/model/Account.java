package com.raviraj.simplemoneytransfer.database.model;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Account {

	private long accountId;
	
	private String userId;

	private String currency;

	private double balance;
	
	public ReentrantLock lock = new ReentrantLock();
	
	public ReentrantLock getLock() {
		return lock;
	}

	public void setLock(ReentrantLock lock) {
		this.lock = lock;
	}

	public long getAccountId() {
		return accountId;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}
	
}
