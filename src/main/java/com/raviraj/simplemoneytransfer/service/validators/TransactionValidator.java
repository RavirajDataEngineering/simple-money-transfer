package com.raviraj.simplemoneytransfer.service.validators;

import com.raviraj.simplemoneytransfer.database.model.Transaction;
import com.raviraj.simplemoneytransfer.exceptions.ValidationException;

public class TransactionValidator {

	public static void validateTransactionId(long id) throws ValidationException {
		if(id<=0) {
			throw new ValidationException("Invalid Transactin Id");
		}
	}
	
	public static void validateTransaction(Transaction tr) throws ValidationException {
		if(tr==null) {
			throw new ValidationException("Transaction cannot be null");
		}
		validateTransactionId(tr.getTransactionId());
		if(tr.getAmount()<=0) {
			throw new ValidationException("Invalid Amount, tranaction amount cannot be less than zero");
		}
		
		AccountValidator.validateId(tr.getCreditAccountId());
		AccountValidator.validateId(tr.getDebitAccountId());
	}
}
