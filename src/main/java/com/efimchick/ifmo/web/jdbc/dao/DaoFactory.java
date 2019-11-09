package com.efimchick.ifmo.web.jdbc.dao;

import com.efimchick.ifmo.web.jdbc.ConnectionSource;
import com.efimchick.ifmo.web.jdbc.domain.Department;
import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class DaoFactory {

    private ResultSet getResultSet(String sql) {
        try {
            Connection connection = ConnectionSource.instance().createConnection();
            Statement statement = connection.createStatement();
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            return null;
        }
    }

    private Employee getEmployee(ResultSet resultSet) throws SQLException {
            BigInteger id = BigInteger.valueOf(resultSet.getInt("id"));
            FullName fullname = new FullName(
                    resultSet.getString("firstname"),
                    resultSet.getString("lastname"),
                    resultSet.getString("middlename")
            );
            Position position = Position.valueOf(resultSet.getString("position"));
            LocalDate hired = LocalDate.parse(resultSet.getString("hiredate"));
            BigDecimal salary = BigDecimal.valueOf(resultSet.getDouble("salary"));
            BigInteger managerId = BigInteger.valueOf(resultSet.getInt("manager"));
            BigInteger departmentId = BigInteger.valueOf(resultSet.getInt("department"));
            return new Employee(
                    id,
                    fullname,
                    position,
                    hired,
                    salary,
                    managerId,
                    departmentId);
    }

    private List<Employee> getEmployeeList() {
        try {
            List<Employee> empList = new LinkedList<>();
            ResultSet resultSet = getResultSet("select * from employee");
            while (resultSet.next()) {
                empList.add(getEmployee(resultSet));
            }
            return empList;
        } catch (SQLException e) {
            return null;
        }
    }

    private List<Employee> employeeList = getEmployeeList();


    private Department getDepartment(ResultSet resultSet) throws SQLException {
            BigInteger id = BigInteger.valueOf(resultSet.getInt("id"));
            String name = resultSet.getString("name");
            String location = resultSet.getString("location");
            return new Department(
                    id,
                    name,
                    location);
    }

    private List<Department> getDepartmentList() {
        try {
            List<Department> depList = new LinkedList<>();
            ResultSet resultSet = getResultSet("select * from department");
            while (resultSet.next()) {
                depList.add(getDepartment(resultSet));
            }
            return depList;
        } catch (SQLException e) {
        return null;
    }
    }

    private List<Department> departmentList = getDepartmentList();

    public EmployeeDao employeeDAO() {

        return new EmployeeDao() {
            @Override
            public List<Employee> getByDepartment(Department department) {
                List<Employee> resultList = new LinkedList<>();
                for (Employee employee : employeeList) {
                    if (employee.getDepartmentId().equals(department.getId())) {
                        resultList.add(employee);
                    }
                }
                return resultList;
            }

            @Override
            public List<Employee> getByManager(Employee employee) {
                List<Employee> resultList = new LinkedList<>();
                for (Employee value : employeeList) {
                    if (value.getManagerId().equals(employee.getId())) {
                        resultList.add(value);
                    }
                }
                return resultList;
            }

            @Override
            public Optional<Employee> getById(BigInteger Id) {
                Optional<Employee> resultList = Optional.empty();
                for (Employee employee : employeeList) {
                    if (employee.getId().equals(Id)) {
                        resultList = Optional.of(employee);
                    }
                }
                return resultList;
            }

            @Override
            public List<Employee> getAll() {
                return employeeList;
            }

            @Override
            public Employee save(Employee employee) {
                for (int i = 0; i < employeeList.size(); i++) {
                    if (employeeList.get(i).getId().equals(employee.getId())) {
                        employeeList.remove(i);
                    }
                }
                employeeList.add(employee);
                return employee;
            }

            @Override
            public void delete(Employee employee) {
                employeeList.remove(employee);
            }
        };

        //throw new UnsupportedOperationException();
    }

    public DepartmentDao departmentDAO() {

        return new DepartmentDao() {
            @Override
            public Optional<Department> getById(BigInteger Id) {
                Optional<Department> resultList = Optional.empty();
                for (Department department : departmentList) {
                    if (department.getId().equals(Id)) {
                        resultList = Optional.of(department);
                    }
                }
                return resultList;
            }

            @Override
            public List<Department> getAll() {
                return departmentList;
            }

            @Override
            public Department save(Department department) {
                for (int i = 0; i < departmentList.size(); i++) {
                    if (departmentList.get(i).getId().equals(department.getId())) {
                        departmentList.remove(i);
                    }
                }
                departmentList.add(department);
                return department;
            }

            @Override
            public void delete(Department department) {
                departmentList.remove(department);
            }
        };
        //throw new UnsupportedOperationException();
    }
}
