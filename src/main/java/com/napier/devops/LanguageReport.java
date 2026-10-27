package com.napier.devops;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LanguageReport {

    private Connection con;

    public LanguageReport(Connection con) {
        this.con = con;
    }

    /**
     * Gets the population that speaks each language
     */
    // getLanguageSpeakers()
    public ResultSet getLanguageSpeakers() {
        try {
            ResultSet rs = executeStatement(
                    "SELECT cl.Language, sum(c.Population) as sumPop FROM countrylanguage as cl, country as c WHERE cl.CountryCode = c.Code GROUP BY cl.Language ORDER BY sum(c.Population) DESC;"
            );

            return rs;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }

    }

    public ResultSet executeStatement(String query) throws SQLException {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            return rs;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

}
