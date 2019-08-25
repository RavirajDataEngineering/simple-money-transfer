package com.raviraj.simplemoneytransfer.inmemorydatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.raviraj.simplemoneytransfer.database.model.Account;
import com.raviraj.simplemoneytransfer.database.model.Transaction;

/**
 * Threadsafe implementation of inmemory database.
 * 
 */
public class InMemoryDatabase {

	/**
	 * Shared database for all requests.
	 */
	private static InMemoryDatabase instance = new InMemoryDatabase();
	
	/**
	 * Concurrent Collections for Thread safety
	 */
	private Map<Long,Account> accounts = new ConcurrentHashMap<Long, Account>();
	private Map<Long,Transaction> transactions = new ConcurrentHashMap<Long, Transaction>();
	private Map<Long,List<Long>> transactionsPerAccount = new ConcurrentHashMap<Long, List<Long>>();
	
	private static volatile long latestTrId = 0;
	
	private ReentrantLock lock = new ReentrantLock();
	
	public static InMemoryDatabase getInstance() {
		return instance;
	}
	
	public Map<Long, Account> getAccounts() {
		return accounts;
	}
	public Map<Long, Transaction> getTransactions() {
		return transactions;
	}
	public Map<Long, List<Long>> getTransactionsPerAccount() {
		return transactionsPerAccount;
	}

	public ReentrantLock getLock() {
		return lock;
	}

	public static long getLatestTrId() {
		return latestTrId;
	}

	public static void setLatestTrId(long latestTrId) {
		InMemoryDatabase.latestTrId = latestTrId;
	}

}
