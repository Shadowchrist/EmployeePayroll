package com.practice.EmployeePayrollSystem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class DatabaseOperations {

	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	}

	public static final Scanner SC = new Scanner(System.in);
	private List<Employee> employeePayrollList;
	private DatabaseIOService employeePayrollDBService;

	public DatabaseOperations() {
		this.employeePayrollList = new ArrayList<Employee>();
		employeePayrollDBService = DatabaseIOService.getInstance();
	}

	public DatabaseOperations(List<Employee> employeeList) {
		this();
		this.employeePayrollList = employeeList;
	}

	public int sizeOfEmployeeList() {
		return this.employeePayrollList.size();
	}

	public void readEmployeeData(IOService ioType) {
		if (ioType.equals(IOService.CONSOLE_IO)) {
			System.out.println("Enter employee id:");
			int employeeId = SC.nextInt();
			System.out.println("Enter employee name:");
			SC.nextLine();
			String employeeName = SC.nextLine();
			System.out.println("Enter employee salary:");
			double employeeSalary = SC.nextDouble();
			String startDate = SC.nextLine();
			Employee newEmployee = new Employee(employeeId, employeeName, employeeSalary, LocalDate.parse(startDate) );
			employeePayrollList.add(newEmployee);
		} else if (ioType.equals(IOService.FILE_IO))
			this.employeePayrollList = new FileIOService().readData();
		else if (ioType.equals(IOService.DB_IO)) {
			try {
				this.employeePayrollList = employeePayrollDBService.readData();
			} catch (CustomExceptions e) {
				e.printStackTrace();
			}
		}
	}

	public List<Employee> readEmployeeDataForDateRange(IOService ioType, LocalDate startDate,LocalDate endDate) {
		if (ioType.equals(IOService.DB_IO)) {
			try {
				return employeePayrollDBService.readEmployeeDataForDateRange(startDate, endDate);
			} catch (CustomExceptions e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public Map<String, Double> readAverageSalaryByGender(IOService ioType) {
		if (ioType.equals(IOService.DB_IO))
			try {
				return employeePayrollDBService.readAverageSalaryByGender();
			} catch (CustomExceptions e) {
				e.printStackTrace();
			}
		return null;
	}

	public void updateEmployeeSalary(String name, double salary) throws CustomExceptions {
		int result = employeePayrollDBService.updateEmployeeData(name, salary);
		if(result == 0)
			throw new CustomExceptions("Cannot update the employee payroll data", CustomExceptions.ExceptionType.UPDATE_ERROR);
		Employee Employee = this.getEmployee(name);
		if(Employee != null)
			Employee.setSalary(salary);
		else
			throw new CustomExceptions("Cannot find the employee payroll data", CustomExceptions.ExceptionType.INVALID_PAYROLL_DATA);
	}

	private Employee getEmployee(String name) {
		return this.employeePayrollList.stream()
				   .filter(employeeData -> employeeData.getEmployeeName().equals(name))
				   .findFirst()
				   .orElse(null);
	}

	public boolean checkEmployeePayrollInSyncWithDB(String name) throws CustomExceptions {
		List<Employee> EmployeeList = employeePayrollDBService.getEmplyoeePayrollDataUsingName(name);
		return EmployeeList.get(0).equals(getEmployee(name));
	}

	public void writeEmployeeData(IOService ioType) {
		if (ioType.equals(IOService.CONSOLE_IO)) {
			for (Employee o : employeePayrollList)
				System.out.println(o.toString());
		} else if (ioType.equals(IOService.FILE_IO)) {
			new FileIOService().writeData(employeePayrollList);
		}
	}

	public long countEnteries(IOService ioType) {
		if (ioType.equals(IOService.FILE_IO))
			return new FileIOService().countEntries();
		return 0;
	}

	public void printEmployee(IOService ioType) {
		if (ioType.equals(IOService.FILE_IO))
			new FileIOService().printEmployeePayrolls();
		else
			this.employeePayrollList.stream().forEach(employeeData -> System.out.println(employeeData.toString()));
	}
}
