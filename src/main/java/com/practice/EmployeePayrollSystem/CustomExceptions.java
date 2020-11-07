package com.practice.EmployeePayrollSystem;

public class CustomExceptions extends Exception {

	private static final long serialVersionUID = 1L;

	public enum ExceptionType {
		CONNECTION_FAIL, SQL_ERROR, UPDATE_ERROR, INVALID_PAYROLL_DATA, RETRIEVE_ERROR
	}

	ExceptionType type;

	public CustomExceptions(String message, ExceptionType type) {
		super(message);
		this.type = type;
	}
}
