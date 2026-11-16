package com.napier.devops;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.mockito.Mockito.*;

/**
 * Unit test for LanguageReport, using a fake database so no real connection is needed
 */
public class LanguageReportTest {

    // Fake database objects
    Connection con;
    Statement stmt;
    ResultSet rs;
    LanguageReport languageReport;

    /**
     * Creates a fake databse before each test
     */
    @BeforeEach
    void init() throws SQLException {
        con = mock(Connection.class);
        stmt = mock(Statement.class);
        rs = mock(ResultSet.class);
        when(con.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(anyString())).thenReturn(rs);


        languageReport = new LanguageReport(con);

    }

    /**
     * Checks that SQL groups by language and orders from highest number first
     */

    @Test
    void getLanguageSpeakersTest() throws SQLException {
        // Run the method, letting every language
        languageReport.getLanguageSpeakers();
        // Check that SQL adds up the population for each language
        verify(stmt).executeQuery(contains("GROUP BY cl.Language"));
        // Check it sorts from largest number first
        verify(stmt).executeQuery(contains("ORDER BY sum(c.Population) DESC"));


    }

}
