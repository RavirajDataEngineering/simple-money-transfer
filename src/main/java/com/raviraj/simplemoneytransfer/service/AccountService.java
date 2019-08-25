package com.raviraj.simplemoneytransfer.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import com.raviraj.simple_money_transfer.database.model.dao.AccountDao;
import com.raviraj.simplemoneytransfer.database.model.Account;
import com.raviraj.simplemoneytransfer.exceptions.ApiError;
import com.raviraj.simplemoneytransfer.exceptions.DatabaseServiceException;
import com.raviraj.simplemoneytransfer.exceptions.ValidationException;
import com.raviraj.simplemoneytransfer.service.validators.AccountValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/accounts")
public class AccountService {

	private AccountDao accountDao = new AccountDao();
	
	private AccountValidator accountValidator = new  AccountValidator();
	
	private static final Logger LOG = LoggerFactory.getLogger(AccountService.class);
	
    @GET
    @Path("/{accountId}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getAccountById(@PathParam("accountId") long id) {
		try {
			accountValidator.validateId(id);
			Account account = accountDao.getAccountById(id);
			if (account == null) {
				LOG.info("Requested account doesnt exist : " + id);
				ApiError error = new ApiError("Account not found");
				return Response.status(Status.NOT_FOUND).entity(error.toString()).build();
			} else {
				return Response.ok(account).build();
			}
		} catch (ValidationException e) {
			LOG.info("Validation failed , invalid account id :" + id);
			ApiError error = new ApiError(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(error).build();
		} catch (Throwable t) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}

	}
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createAccount(Account account) {
    	try {
    		accountValidator.validateAccount(account);
    		accountDao.createAccount(account);
    		return Response.ok(account).build();
    	}catch(DatabaseServiceException dbe) {
    		LOG.info("Account already exists ");
    		ApiError error = new ApiError("Account already exists");
    		return Response.status(Status.BAD_REQUEST).entity(error.toString()).build();
    	}
    	catch(ValidationException ex) {
    		LOG.info("Validation failed while addint the account " , ex.getMessage());
    		ApiError error = new ApiError(ex.getMessage());
    		return Response.status(Status.BAD_REQUEST).entity(error.toString()).build();
    	}
    	catch(Throwable ex) {
    		return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    	}
    }
    
    @GET
    @Produces({"application/json"})
    public Response getAccounts() {
    	try {
    		return Response.ok(accountDao.getAllAccounts()).build();
    	}catch(Throwable ex) {
    		return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    	}
    }
    
    @PUT
    @Produces({"application/json"})
	public Response updateAccount(Account account) {
		try {
			accountValidator.validateAccount(account);
			accountDao.updateAccount(account);
		} catch (DatabaseServiceException dbe) {
			LOG.info("update account failed  with exception" + dbe.getMessage());
			return Response.status(Status.BAD_REQUEST).build();
		} catch (ValidationException ex) {
			LOG.info("Validation failed while account update " + ex.getMessage());
			ApiError error = new ApiError(ex.getMessage());
			return Response.status(Status.BAD_GATEWAY).entity(error.toString()).build();
		} catch (Throwable ex) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		return Response.ok(account).build();
	}
    
    
    @DELETE
    @Produces({"application/json"})
    @Path("/{accountId}")
    public Response deleteAccount(@PathParam("accountId") long id) throws DatabaseServiceException {
    	try {
    		accountValidator.validateId(id);
			accountDao.deleteAccount(id);
		}catch(ValidationException ex) {
			ApiError error = new ApiError(ex.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(error).build();
		}
    	catch (DatabaseServiceException dbe) {
			return Response.status(Status.NOT_FOUND).build();
		} catch (Throwable ex) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
    	return Response.ok().build();
    }
}
