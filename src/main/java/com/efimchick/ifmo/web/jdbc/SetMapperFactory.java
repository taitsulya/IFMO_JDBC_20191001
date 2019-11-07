package com.efimchick.ifmo.web.jdbc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;


public class SetMapperFactory {

    public SetMapper<Set<Employee>> employeesSetMapper() {
        try {
            return new SetMapper<Set<Employee>>() {
                @Override
                public Set<Employee> mapSet(ResultSet resultSet){
                    try {
                        Set<Employee> employeesSet = new HashSet<>();
                        while (resultSet.next()) {
                            employeesSet.add(getEmployee(resultSet));
                        }
                        return employeesSet;
                    } catch (SQLException e) {
                        return null;
                    }
                }
            };
        } catch (UnsupportedOperationException e) {
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
        BigDecimal salary = new BigDecimal(resultSet.getString("salary"));
        BigInteger managerId = BigInteger.valueOf(resultSet.getInt("manager"));
        Employee manager = null;
        if (managerId != null) {
            int currentRow = resultSet.getRow();
            resultSet.beforeFirst();
            while (resultSet.next()) {
                if (BigInteger.valueOf(resultSet.getInt("id")).equals(managerId)) {
                    manager = getEmployee(resultSet);
                }
            }
            resultSet.absolute(currentRow);
            return new Employee(id, fullname, position, hired, salary, manager);
        }
        else {
            return new Employee(id, fullname, position, hired, salary, manager);
        }
    }
}
