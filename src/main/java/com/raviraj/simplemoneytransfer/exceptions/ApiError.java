package com.raviraj.simplemoneytransfer.exceptions;

public class ApiError {

	private String message;
	
	public ApiError(String message) {
		this.setMessage(message);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return message;
	}
}
