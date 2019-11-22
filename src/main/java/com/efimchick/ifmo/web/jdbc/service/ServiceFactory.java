package com.efimchick.ifmo.web.jdbc.service;

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

public class ServiceFactory {


    /*private List<Employee> getPage(Paging paging, List<Employee> list) {
        List<Employee> resultList = new LinkedList<>();
        int from = paging.itemPerPage*(paging.page - 1);
        int to = Math.min(paging.itemPerPage*paging.page, list.size());
        for (int i = from; i < to; i++) {
            resultList.add(list.get(i));
        }
        return resultList;
    }*/

    private ResultSet getResultSet(String sql) {
        try {
            Connection connection = ConnectionSource.instance().createConnection();
            Statement statement = connection.createStatement();
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            return null;
        }
    }

    private List<Department> departmentList = getDepartmentList();

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

    private Department getDepartment(ResultSet resultSet) throws SQLException {
        BigInteger id = BigInteger.valueOf(resultSet.getInt("id"));
        String name = resultSet.getString("name");
        String location = resultSet.getString("location");
        return new Department(
                id,
                name,
                location);
    }


    private Department getDepartmentById(BigInteger Id) {
        Department resultDep = null;
        for (Department department : departmentList) {
            if (department.getId().equals(Id)) {
                resultDep = department;
            }
        }
        return resultDep;
    }


    private List<Employee> employeeList(ResultSet resultSet) {
        return getEmployeeList(resultSet, true);
    }
    private List<Employee> employeeListWithShortChain(ResultSet resultSet) {
        return getEmployeeList(resultSet, false);
    }

    private List<Employee> getEmployeeList(ResultSet resultSet, boolean chain) {
        try {
            List<Employee> empList = new LinkedList<>();
            if (resultSet != null) {
                while (resultSet.next()) {
                    empList.add(getEmployee(resultSet, chain, true));
                }
                return empList;
            }
            else return null;
        } catch (SQLException e) {
            return null;
        }
    }


    private Employee getEmployee(ResultSet resultSet, boolean chain, boolean firstManager) throws SQLException {
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
        Department department = getDepartmentById(departmentId);
        Employee manager = null;
        if (managerId != null && firstManager) {
            if (!chain) {
                firstManager = false;
            }
            ResultSet newResultSet = getResultSet("select * from employee");
            while (newResultSet.next()) {
                if (BigInteger.valueOf(newResultSet.getInt("id")).equals(managerId)) {
                    manager = getEmployee(newResultSet, chain, firstManager);
                }
            }
            return new Employee(
                    id,
                    fullname,
                    position,
                    hired,
                    salary,
                    manager,
                    department);
        } else {
            return new Employee(
                    id,
                    fullname,
                    position,
                    hired,
                    salary,
                    manager,
                    department);
        }
    }

    /*private List<Employee> getEmployeeByDepartment(Department department) {
        List<Employee> resultList = new LinkedList<>();
        for (Employee employee : employeeListWithShortChain) {
            if (employee.getDepartment() != null && employee.getDepartment().equals(department)) {
                resultList.add(employee);
            }
        }
        return resultList;
    }

    private List<Employee> getEmployeeByManager(Employee manager) {
        List<Employee> resultList = new LinkedList<>();
        for (Employee employee : employeeListWithShortChain) {
            if (employee.getManager() != null && employee.getManager().getId().equals(manager.getId())) {
                resultList.add(employee);
            }
        }
        return resultList;
    }*/



    public EmployeeService employeeService(){

        return new EmployeeService() {
            @Override
            public List<Employee> getAllSortByHireDate(Paging paging) {
                ResultSet resultSet = getResultSet("select * from employee order by hiredate " +
                        " limit " + paging.itemPerPage +
                        " offset " + (paging.itemPerPage * (paging.page - 1)));
                return employeeListWithShortChain(resultSet);
            }

            @Override
            public List<Employee> getAllSortByLastname(Paging paging) {
                ResultSet resultSet = getResultSet("select * from employee order by lastname " +
                        " limit " + paging.itemPerPage +
                        " offset " + (paging.itemPerPage * (paging.page - 1)));
                return employeeListWithShortChain(resultSet);
            }

            @Override
            public List<Employee> getAllSortBySalary(Paging paging) {
                ResultSet resultSet = getResultSet("select * from employee order by salary " +
                        " limit " + paging.itemPerPage +
                        " offset " + (paging.itemPerPage * (paging.page - 1)));
                return employeeListWithShortChain(resultSet);
            }

            @Override
            public List<Employee> getAllSortByDepartmentNameAndLastname(Paging paging) {
                ResultSet resultSet = getResultSet("select * from employee" +
                        " left join department on employee.department = department.id" +
                        " order by department.name, employee.lastname" +
                        " limit " + paging.itemPerPage +
                        " offset " + (paging.itemPerPage * (paging.page - 1)));
                return employeeListWithShortChain(resultSet);
            }

            @Override
            public List<Employee> getByDepartmentSortByHireDate(Department department, Paging paging) {
                ResultSet resultSet = getResultSet("select * from employee" +
                        " left join department on employee.department = department.id" +
                        " where department.id = " + department.getId() +
                        " order by hiredate " +
                        " limit " + paging.itemPerPage +
                        " offset " + (paging.itemPerPage * (paging.page - 1)));
                return employeeListWithShortChain(resultSet);
            }

            @Override
            public List<Employee> getByDepartmentSortBySalary(Department department, Paging paging) {
                ResultSet resultSet = getResultSet("select * from employee" +
                        " left join department on employee.department = department.id" +
                        " where department.id = " + department.getId() +
                        " order by salary " +
                        " limit " + paging.itemPerPage +
                        " offset " + (paging.itemPerPage * (paging.page - 1)));
                return employeeListWithShortChain(resultSet);
            }

            @Override
            public List<Employee> getByDepartmentSortByLastname(Department department, Paging paging) {
                ResultSet resultSet = getResultSet("select * from employee" +
                        " left join department on employee.department = department.id" +
                        " where department.id = " + department.getId() +
                        " order by lastname " +
                        " limit " + paging.itemPerPage +
                        " offset " + (paging.itemPerPage * (paging.page - 1)));
                return employeeListWithShortChain(resultSet);
            }

            @Override
            public List<Employee> getByManagerSortByLastname(Employee manager, Paging paging) {
                ResultSet resultSet = getResultSet("select * from employee" +
                        " where manager = " + manager.getId() +
                        " order by lastname " +
                        " limit " + paging.itemPerPage +
                        " offset " + (paging.itemPerPage * (paging.page - 1)));
                return employeeListWithShortChain(resultSet);
            }

            @Override
            public List<Employee> getByManagerSortByHireDate(Employee manager, Paging paging) {
                ResultSet resultSet = getResultSet("select * from employee" +
                        " where manager = " + manager.getId() +
                        " order by hiredate" +
                        " limit " + paging.itemPerPage +
                        " offset " + (paging.itemPerPage * (paging.page - 1)));
                return employeeListWithShortChain(resultSet);
            }

            @Override
            public List<Employee> getByManagerSortBySalary(Employee manager, Paging paging) {
                ResultSet resultSet = getResultSet("select * from employee" +
                        " where manager = " + manager.getId() +
                        " order by salary" +
                        " limit " + paging.itemPerPage +
                        " offset " + (paging.itemPerPage * (paging.page - 1)));
                return employeeListWithShortChain(resultSet);
            }

            @Override
            public Employee getWithDepartmentAndFullManagerChain(Employee employee) {
                ResultSet resultSet = getResultSet("select * from employee" +
                        " where id = " + employee.getId());
                return employeeList(resultSet).get(0);
            }

            @Override
            public Employee getTopNthBySalaryByDepartment(int salaryRank, Department department) {
                ResultSet resultSet = getResultSet("select * from employee" +
                        " left join department on employee.department = department.id" +
                        " where department.id = " + department.getId() +
                        " order by salary desc " +
                        " limit " + 1 +
                        " offset " + (salaryRank - 1));
                return employeeListWithShortChain(resultSet).get(0);
            }
        };


        //throw new UnsupportedOperationException();
    }
}
