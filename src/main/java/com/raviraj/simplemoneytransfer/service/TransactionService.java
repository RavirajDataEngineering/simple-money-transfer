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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.raviraj.simplemoneytransfer.database.model.Transaction;
import com.raviraj.simplemoneytransfer.exceptions.ApiError;
import com.raviraj.simplemoneytransfer.exceptions.DatabaseServiceException;
import com.raviraj.simplemoneytransfer.exceptions.ValidationException;
import com.raviraj.simplemoneytransfer.service.utils.TransactionUtil;
import com.raviraj.simplemoneytransfer.service.validators.TransactionValidator;
import com.raviraj.simple_money_transfer.database.model.dao.TransactionDao;

@Path("/transactions")
public class TransactionService {

	private  TransactionDao transactionDao = new TransactionDao();
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionService.class);
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{transactionId}")
	public Response getTransaction(@PathParam("transactionId") long id) {
		try {	
			TransactionValidator.validateTransactionId(id);
			Transaction tr = transactionDao.getTranactionById(id);
			if(tr==null) {
				LOGGER.info("Transaction Id was not found ");
				ApiError error = new ApiError("Tranaction id not found");
				return Response.status(Status.NOT_FOUND).entity(error.toString()).build();
			}else {
				LOGGER.debug("Transaction get succesful");
				return Response.ok(tr).build();
			}
		}catch(ValidationException ex) {
			LOGGER.info("Validation failed with meassage " + ex.getMessage());
			ApiError error = new ApiError("Invalid Transaction Id");
			return Response.status(Status.BAD_REQUEST).entity(error.toString()).build();
		}catch(Throwable t) {
			LOGGER.info("Exception while serving request " , t);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addTransaction(Transaction tr) {
		try {
			if (tr.getTransactionId() > 0) {
				Transaction trx = transactionDao.getTranactionById(tr.getTransactionId());
				if(trx!=null) {
					LOGGER.info("Transaction already exists, cannot add");
					return Response.status(Status.BAD_REQUEST).build();
				}
			}
			TransactionValidator.validateTransaction(tr);
			tr = TransactionUtil.buildTransaction(tr);
			transactionDao.addTransaction(tr);
			return Response.ok(tr).build();
		} catch (ValidationException ex) {
			LOGGER.info("Validation of transactin failed ", ex);
			ApiError error = new ApiError(ex.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(error.toString()).build();
		} catch (Throwable t) {
			LOGGER.info("Error serving trasanction request ", t);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateTransaction(Transaction tr) {
		try {
			TransactionValidator.validateTransaction(tr);
			transactionDao.updateTranscation(tr);
			return Response.ok(tr).build();
		}catch(ValidationException ex) {
			LOGGER.info("Validation of transactin failed " , ex);
			ApiError error = new ApiError(ex.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(error).build();
		}catch(DatabaseServiceException ex) {
			LOGGER.info("Error serving trasanction update request " , ex);
			return Response.status(Status.BAD_REQUEST).build();
		}catch(Throwable t) {
			LOGGER.info("Error serving trasanction update request " , t);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	
	@DELETE
	@Path("/{transactionId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteTransaction(@PathParam("transactionId") long id) {
		try {
			TransactionValidator.validateTransactionId(id);
			transactionDao.deleteTransaction(id);
			return Response.ok().build();
		}catch(ValidationException ex) {
			LOGGER.info("Delete Transaction faied with validation error " , ex.getMessage());
			ApiError error = new ApiError("Invalid Transaction Id");
			return Response.status(Status.BAD_REQUEST).entity(error).build();
		}catch(DatabaseServiceException ex) {
			LOGGER.info("Transaction id doesnt exist for delete" , ex);
			ApiError error = new ApiError("Transaction Id doesnt exist");
			return Response.status(Status.BAD_REQUEST).entity(error).build();
		}catch(Throwable t) {
			LOGGER.info("Error occured processing delete request " , t);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
}
