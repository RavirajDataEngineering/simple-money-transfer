package com.raviraj.simplemoneytransfer.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.raviraj.simplemoneytransfer.database.model.Transaction;
import com.raviraj.simplemoneytransfer.exceptions.ApiError;
import com.raviraj.simplemoneytransfer.exceptions.DatabaseServiceException;
import com.raviraj.simplemoneytransfer.exceptions.InsufficientFundsException;
import com.raviraj.simplemoneytransfer.exceptions.LockTimeOutException;
import com.raviraj.simplemoneytransfer.exceptions.TransactionException;
import com.raviraj.simplemoneytransfer.exceptions.ValidationException;
import com.raviraj.simplemoneytransfer.service.utils.TransactionPayload;
import com.raviraj.simplemoneytransfer.service.utils.TransactionUtil;

@Path("/transfer")
public class TransferService {
	
	private TransferManager tm = new TransferManager();
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TransferService.class);
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response transfer(TransactionPayload payload){
		
		Response returnRespone = null;
		try {
			 returnRespone = tm.transfer(payload);
		} catch(ValidationException ex) {
			LOGGER.info("Validation failed during transfer " , ex);
			ApiError error = new ApiError(ex.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(error.toString()).build();
		}
		catch (InsufficientFundsException e) {
			LOGGER.info("Insufficient funds to transfer money");
			ApiError error = new ApiError("Insufficient Funds to Transfer");
			return Response.status(Status.BAD_REQUEST).entity(error.toString()).build();
		} catch (TransactionException | LockTimeOutException  | DatabaseServiceException e) {
			LOGGER.info("Error occured during transfer " , e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		} catch (Throwable t) {
			
			LOGGER.info("Unexpected error occured during transfer " , t);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		return returnRespone;
	}
}
