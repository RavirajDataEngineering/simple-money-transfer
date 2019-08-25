package com.raviraj.simplemoneytransfer.service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.raviraj.simple_money_transfer.database.model.dao.AccountDao;
import com.raviraj.simple_money_transfer.database.model.dao.TransactionDao;
import com.raviraj.simplemoneytransfer.database.model.Account;
import com.raviraj.simplemoneytransfer.database.model.Transaction;
import com.raviraj.simplemoneytransfer.exceptions.ApiError;
import com.raviraj.simplemoneytransfer.exceptions.DatabaseServiceException;
import com.raviraj.simplemoneytransfer.exceptions.InsufficientFundsException;
import com.raviraj.simplemoneytransfer.exceptions.LockTimeOutException;
import com.raviraj.simplemoneytransfer.exceptions.TransactionException;
import com.raviraj.simplemoneytransfer.exceptions.ValidationException;
import com.raviraj.simplemoneytransfer.inmemorydatabase.InMemoryDatabase;
import com.raviraj.simplemoneytransfer.service.utils.TransactionPayload;
import com.raviraj.simplemoneytransfer.service.utils.TransactionUtil;
import com.raviraj.simplemoneytransfer.service.validators.AccountValidator;

/**
 * 
 * @author Raviraj
 * 
 * Threadsafe implementaion of Money Transfer across multiple accounts, This is implemented by taking 
 * locks only on the required accounts and not the whole in-memory table. 
 * 
 * Deadlock prevention is ensured by taking necessary locks atomically. it cannot happen that Thread A , acquires 
 * lock for Account a1 and waits for lock on Account a2 while Thread B acquires lock on  Account a2 while waiting for lock on 
 * Account a1
 *
 */
public class TransferManager {

	AccountDao accountDao = new AccountDao();
	TransactionDao transactionDao = new TransactionDao();
	private static final Logger LOGGER = LoggerFactory.getLogger(TransferManager.class);
	
	public Response transfer(TransactionPayload payload) throws InsufficientFundsException, TransactionException, LockTimeOutException, DatabaseServiceException, ValidationException {

		Account a1 = accountDao.getAccountById(payload.getDebitAccountId());
		Account a2 = accountDao.getAccountById(payload.getCreditAccountId());
		ReentrantLock dbLock = InMemoryDatabase.getInstance().getLock();
			try {
				AccountValidator.validateAccount(a1);
				AccountValidator.validateAccount(a2);
				
				if (a1 == null || a2 == null) {
					LOGGER.info("One of the accounts to transfer doesnt exist ");
					ApiError error = new ApiError("Invalid Accounts ");
					return Response.status(Status.BAD_REQUEST).entity(error.toString()).build();
				}
	
				if (a1.getBalance() < payload.getAmount()) {
					LOGGER.info("Insufficient funds in the source account to transfer money");
					throw new InsufficientFundsException("Not sufficent funds to transfer");
				}
	
				boolean dbLockSuccess = false;
				try {
					dbLockSuccess = dbLock.tryLock(10, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					throw new TransactionException("Exception while getting lock");
				}
				if (!dbLockSuccess) {
					LOGGER.error("Couldnt get dblock in specified time , hence aborting ");
					throw new LockTimeOutException("Transaction timed out");
				}
	
				boolean a1LockSuccess = false;
				boolean a2LockSuccess = false;
				try {
					a1LockSuccess = a1.lock.tryLock(10, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					throw new TransactionException("Exception while getting lock");
				}
				if (!a1LockSuccess) {
					LOGGER.error("Couldnt get lock for the account , aborting" );
					throw new LockTimeOutException("Transaction timed out");
				}
	
				try {
					a2LockSuccess = a2.lock.tryLock(10, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					throw new TransactionException("Exception while getting lock");
				}
	
				if (!a2LockSuccess) {
					LOGGER.error("Couldnt get lock for the account , aborting");
					throw new LockTimeOutException("Transaction timed out");
				}
	
				/*
				 * Unlocing the dblock so that any other transactions working on different accounts can proceed
				 */
				dbLock.unlock();
	
				accountDao.updateBalance(a1.getAccountId(), payload.getAmount(), -1);
				accountDao.updateBalance(a2.getAccountId(), payload.getAmount(), 1);
				
				a1.lock.unlock();
				a2.lock.unlock();
				
				Transaction tr = TransactionUtil.buildTransaction(payload);
				transactionDao.addTransaction(tr);
				
				return Response.ok("Successfull Transaction Id " + tr.getTransactionId()).build();
			}
			finally {
				if (dbLock.isHeldByCurrentThread())
					dbLock.unlock();
				if (a1!=null && a1.lock.isHeldByCurrentThread())
					a1.lock.unlock();
				if (a2!=null && a2.lock.isHeldByCurrentThread())
					a2.lock.unlock();
	
			}

	
	}
}
