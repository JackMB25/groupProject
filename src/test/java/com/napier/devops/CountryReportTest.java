package com.napier.devops;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CountryReportTest {
    Connection con;
    Statement stmt;
    ResultSet rs;
    CountryReport report;

    /**
     * Creates and executes moc database objects before each test
     *
     * Configures the behaviour of the moc database
     *
     * Creates the object to run the tests on
     * @throws SQLException - exception if database fails
     */
    @BeforeEach
    void setUp() throws SQLException {

        // Create mock database objects
        con = mock(Connection.class);
        stmt = mock(Statement.class);
        rs = mock(ResultSet.class);

        // Configure mock behaviour
        when(con.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(anyString())).thenReturn(rs);

        // Create the object to test
        report = new CountryReport(con);
    }

}
