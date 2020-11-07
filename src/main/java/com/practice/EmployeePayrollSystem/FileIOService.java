package com.practice.EmployeePayrollSystem;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

public class FileIOService {
	public static final String PAYROLL_FILE_NAME = "employee-payroll-file.txt";

	public void writeData(List<Employee> employeeList) {

		StringBuffer employeeBufferString = new StringBuffer();
		employeeList.forEach(employee -> {
			String employeeDataString = employee.toString().concat("\n");
			employeeBufferString.append(employeeDataString);
		});

		try {
			Files.write(Paths.get(PAYROLL_FILE_NAME), employeeBufferString.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public long countEntries() {
		long countOfEntries = 0;
		try {
			countOfEntries = Files.lines(Paths.get(PAYROLL_FILE_NAME)).count();
		} catch (IOException e) {
		}
		return countOfEntries;
	}

	public void printEmployeePayrolls() {
		try {
			Files.lines(Paths.get(PAYROLL_FILE_NAME)).forEach(System.out::println);
		} catch (IOException e) {
		}
	}

	public List<Employee> readData() {
		List<Employee> employeeReadList = new ArrayList<Employee>();
		try {
			Files.lines(Paths.get(PAYROLL_FILE_NAME)).map(line -> line.trim()).forEach(line -> {
				String[] data = line.split("[ a-zA-Z]+ : ");
				int id = Character.getNumericValue(data[1].charAt(0));
				String name = data[2];
				double salary = Double.parseDouble(data[3]);
				String startDate = data[4];
				Employee employeeobject = new Employee(id, name, salary, LocalDate.parse(startDate));
				employeeReadList.add(employeeobject);
			});
		} catch (IOException e) {
		}
		return employeeReadList;
	}
}
