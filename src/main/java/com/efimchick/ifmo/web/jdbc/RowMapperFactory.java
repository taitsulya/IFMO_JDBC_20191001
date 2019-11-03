package com.efimchick.ifmo.web.jdbc;

import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class RowMapperFactory {
    public RowMapper<Employee> employeeRowMapper() {
        try {
            return new RowMapper<Employee>() {
                @Override
                public Employee mapRow(ResultSet resultSet) {
                    try {
                        BigInteger id = new BigInteger(String.valueOf(resultSet.getInt("id")));
                        FullName fullname = new FullName(
                                resultSet.getString("firstname"),
                                resultSet.getString("lastname"),
                                resultSet.getString("middlename"));
                        Position position = Position.valueOf(resultSet.getString("position"));
                        LocalDate hired = LocalDate.parse(resultSet.getString("hiredate"));
                        BigDecimal salary = new BigDecimal(resultSet.getDouble("salary"));
                        return new Employee(id, fullname, position, hired, salary);
                    } catch (SQLException e) {
                        return null;
                    }
                }
            };
        } catch (UnsupportedOperationException e) {
            return null;
        }

    }
}
