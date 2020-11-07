package com.practice.EmployeePayrollSystem;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

public class DatabaseIOService {

		private PreparedStatement EmployeeStatement;
		private static DatabaseIOService employeeDBService;

		private DatabaseIOService() {
		}

		public static DatabaseIOService getInstance() {
			if(employeeDBService == null)
				employeeDBService = new DatabaseIOService();
			return employeeDBService;
		}

		private Connection establishConnection() throws SQLException {
			String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?characterEncoding=utf8";
			String userName = "root";
			String password = "Shubham@1998";
			System.out.println("Establishing connection to database : " + jdbcURL);
			return (Connection) DriverManager.getConnection(jdbcURL, userName, password);
		}

		private void prepareStatementForEmployeeData() throws CustomExceptions {
			String sql = "select * from employee_payroll where name = ?";
			try {
				Connection connection = this.establishConnection();
				System.out.println("Connection is successfull!!! " + connection);
				this.EmployeeStatement = (PreparedStatement) connection.prepareStatement(sql);
			}catch (SQLException e) {
				throw new CustomExceptions("Cannot establish connection", CustomExceptions.ExceptionType.CONNECTION_FAIL);
			}
		}

		public List<Employee> readData() throws CustomExceptions {
			String sql = "select * from employee_payroll;";
			return this.getEmplyoeePayrollDataUsingDB(sql);
		}

		public List<Employee> getEmplyoeePayrollDataUsingName(String name) throws CustomExceptions {
			List<Employee> employeePayrollList = null;
			if(this.EmployeeStatement == null)
				this.prepareStatementForEmployeeData();
			try {
				EmployeeStatement.setString(1, name);
				ResultSet resultSet = EmployeeStatement.executeQuery();
				employeePayrollList = this.getEmplyoeePayrollDataUsingResultSet(resultSet);			
			} catch (SQLException e) {
				throw new CustomExceptions("Cannot execute query", CustomExceptions.ExceptionType.SQL_ERROR);
			}
			return employeePayrollList;
		}

		public List<Employee> readEmployeeDataForDateRange(LocalDate startDate, LocalDate endDate) throws CustomExceptions {
			String sql = String.format("select * from employee_payroll where start between '%s' and '%s';",
										Date.valueOf(startDate), Date.valueOf(endDate));
			return this.getEmplyoeePayrollDataUsingDB(sql);
		}

		public Map<String, Double> readAverageSalaryByGender() throws CustomExceptions {
			String sql = "select gender, avg(salary) as average_salary from employee_payroll group by gender;";
			Map<String, Double> genderToAverageSalaryMap = new HashMap<>();
			try (Connection connection = this.establishConnection()) {
				System.out.println("Connection is successfull!!! " + connection);
				Statement statement = (Statement) connection.createStatement();
				ResultSet resultSet = statement.executeQuery(sql);
				while (resultSet.next()) {
					String gender = resultSet.getString("gender");
					double averageSalary = resultSet.getDouble("average_salary");
					genderToAverageSalaryMap.put(gender, averageSalary);
				}
			} catch (SQLException e) {
				throw new CustomExceptions("Cannot establish connection",CustomExceptions.ExceptionType.CONNECTION_FAIL);
			}
			return genderToAverageSalaryMap;
		}

		private List<Employee> getEmplyoeePayrollDataUsingDB(String sql) throws CustomExceptions {
			List<Employee> employeePayrollList = null;
			try (Connection connection = this.establishConnection()) {
				System.out.println("Connection is successfull!!! " + connection);
				Statement statement = (Statement) connection.createStatement();
				ResultSet resultSet = statement.executeQuery(sql);
				employeePayrollList = this.getEmplyoeePayrollDataUsingResultSet(resultSet);
			} catch (SQLException e) {
				throw new CustomExceptions("Cannot establish connection",CustomExceptions.ExceptionType.CONNECTION_FAIL);
			}
			return employeePayrollList;
		}

		private List<Employee> getEmplyoeePayrollDataUsingResultSet(ResultSet resultSet) throws CustomExceptions {
			List<Employee> employeePayrollList = new ArrayList<Employee>();
			try {
				while (resultSet.next()) {
					int id = resultSet.getInt("id");
					String employeeName = resultSet.getString("name");
					double salary = resultSet.getDouble("salary");
					LocalDate startDate = resultSet.getDate("start").toLocalDate();
					employeePayrollList.add(new Employee(id, employeeName, salary, startDate));
				}
			} catch (SQLException e) {
				throw new CustomExceptions("Cannot populate employee payroll data", CustomExceptions.ExceptionType.RETRIEVE_ERROR);
			}
			return employeePayrollList;
		}

		public int updateEmployeeData(String name, double salary) throws CustomExceptions {
			return this.updateEmployeeDataUsingPreparedStatement(name, salary);
		}

		private int updateEmployeeDataUsingPreparedStatement(String name, double salary) throws CustomExceptions {
			String sql = "update employee_payroll set salary = ? where name = ?";
			try (Connection connection = this.establishConnection()){
				System.out.println("Connection is successfull!!! " + connection);
				PreparedStatement employeePayrollUpdateStatement = (PreparedStatement) connection.prepareStatement(sql);
				employeePayrollUpdateStatement.setDouble(1,salary);
				employeePayrollUpdateStatement.setString(2, name);
				return employeePayrollUpdateStatement.executeUpdate();
			} catch (SQLException e) {
				throw new CustomExceptions("Cannot establish connection", CustomExceptions.ExceptionType.CONNECTION_FAIL);
			}
		}

		@SuppressWarnings("unused")
		private int updateEmployeeDataUsingStatement(String name, double salary) throws CustomExceptions {
			String sql = String.format("update employee_payroll set salary = %.2f where name = '%s'", salary, name);
			try (Connection connection = this.establishConnection()) {
				System.out.println("Connection is successfull!!! " + connection);
				Statement statement = (Statement) connection.createStatement();
				return statement.executeUpdate(sql);
			} catch (SQLException e) {
				throw new CustomExceptions("Cannot establish connection", CustomExceptions.ExceptionType.CONNECTION_FAIL);
			}
		}
}
