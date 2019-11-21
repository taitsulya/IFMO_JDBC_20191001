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

    private void execute(String sql) {
        try {
            Connection connection = ConnectionSource.instance().createConnection();
            Statement statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException ignored) {}
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

    private List<Employee> getEmployeeList(ResultSet resultSet) {
        try {
            List<Employee> empList = new LinkedList<>();
            while (resultSet.next()) {
                empList.add(getEmployee(resultSet));
            }
            return empList;
        } catch (SQLException e) {
            return null;
        }
    }


    private Department getDepartment(ResultSet resultSet) throws SQLException {
            BigInteger id = BigInteger.valueOf(resultSet.getInt("id"));
            String name = resultSet.getString("name");
            String location = resultSet.getString("location");
            return new Department(
                    id,
                    name,
                    location);
    }

    private List<Department> getDepartmentList(ResultSet resultSet) {
        try {
            List<Department> depList = new LinkedList<>();
            while (resultSet.next()) {
                depList.add(getDepartment(resultSet));
            }
            return depList;
        } catch (SQLException e) {
        return null;
    }
    }

    public EmployeeDao employeeDAO() {

        return new EmployeeDao() {
            @Override
            public List<Employee> getByDepartment(Department department) {
                ResultSet resultSet = getResultSet("select * from employee" +
                        " where department = " + department.getId());
                return getEmployeeList(resultSet);
            }

            @Override
            public List<Employee> getByManager(Employee employee) {
                ResultSet resultSet = getResultSet("select * from employee" +
                        " where manager = " + employee.getId());
                return getEmployeeList(resultSet);
            }

            @Override
            public Optional<Employee> getById(BigInteger Id) {
                try {
                    ResultSet resultSet = getResultSet("select * from employee" +
                            " where id = " + Id);
                    if (resultSet.next()) {
                        return Optional.of(getEmployee(resultSet));
                    } else {
                        return Optional.empty();
                    }
                } catch (SQLException e) {
                    return Optional.empty();
                }
            }

            @Override
            public List<Employee> getAll() {
                ResultSet resultSet = getResultSet("select * from employee");
                return getEmployeeList(resultSet);
            }

            @Override
            public Employee save(Employee employee) {
                if (getById(employee.getId()).equals(Optional.empty())) {
                    execute("insert into employee values (" +
                            employee.getId() + " , '" +
                            employee.getFullName().getFirstName() + "' , '" +
                            employee.getFullName().getLastName() + "' , '" +
                            employee.getFullName().getMiddleName() + "' , '" +
                            employee.getPosition() + "' , " +
                            employee.getManagerId() + " , '" +
                            employee.getHired() + "' , " +
                            employee.getSalary() + " , " +
                            employee.getDepartmentId() + ")");
                } else {
                    execute("update employee set" +
                            " id = " + employee.getId() + " ," +
                            " firstname = '" + employee.getFullName().getFirstName() + "' ," +
                            " lastname = '" + employee.getFullName().getLastName() + "' ," +
                            " middlename = '" + employee.getFullName().getMiddleName() + "' ," +
                            " position = '" + employee.getPosition() + "' ," +
                            " manager = " + employee.getManagerId() + " ," +
                            " hiredate = '" + employee.getHired() + "' ," +
                            " salary = " + employee.getSalary() + " ," +
                            " department = " + employee.getDepartmentId() +
                            " where id = " + employee.getId());
                }
                return employee;
            }

            @Override
            public void delete(Employee employee) {
                execute("delete from employee where id =" + employee.getId());
            }
        };

        //throw new UnsupportedOperationException();
    }

    public DepartmentDao departmentDAO() {

        return new DepartmentDao() {
            @Override
            public Optional<Department> getById(BigInteger Id) {
                try {
                    ResultSet resultSet = getResultSet("select * from department" +
                            " where id = " + Id);
                    if (resultSet.next()) {
                        return Optional.of(getDepartment(resultSet));
                    } else {
                        return Optional.empty();
                    }
                } catch (SQLException e) {
                    return Optional.empty();
                }
            }

            @Override
            public List<Department> getAll() {
                ResultSet resultSet = getResultSet("select * from department");
                return getDepartmentList(resultSet);
            }

            @Override
            public Department save(Department department) {
                if (getById(department.getId()).equals(Optional.empty())) {
                    execute("insert into department values (" +
                            department.getId() + " , '" +
                            department.getName() + "' , '" +
                            department.getLocation() + "')");
                } else {
                    execute("update department set" +
                            " id = " + department.getId() + " ," +
                            " name = '" + department.getName() + "' ," +
                            " location = '" + department.getLocation() +
                            "' where id = " + department.getId());
                }
                return department;
            }

            @Override
            public void delete(Department department) {
                execute("delete from department where id =" + department.getId());
            }
        };
        //throw new UnsupportedOperationException();
    }
}
