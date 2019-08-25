package com.raviraj.simplemoneytransfer.service.validators;

import org.eclipse.jetty.util.StringUtil;

import com.raviraj.simplemoneytransfer.database.model.Account;
import com.raviraj.simplemoneytransfer.exceptions.ValidationException;

public class AccountValidator {

	public static void validateId(long id) throws ValidationException {
		if(id<=0) {
			throw new ValidationException("Invaild Account Id");
		}
		
	}
	
	public static void validateAccount(Account account) throws ValidationException {
		if(account == null ) {
			throw new ValidationException("account cannot be null");
		}
		validateId(account.getAccountId());
		if(StringUtil.isBlank(account.getUserId())) {
			throw new ValidationException("Userid Cannot be empty");
		}
	}

}
