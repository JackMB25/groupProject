package com.napier.devops;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * language reports class
 */
public class LanguageReport {

    /**
     * database connection
     */
    private Connection con;

    /**
     * language report constructor
     * @param con database connection to be used
     */
    public LanguageReport(Connection con) {
        this.con = con;
    }

    /**
     * Gets the population that speaks each language
     * @return returns the result set from the query
     */
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

    /**
     * @param query the sql query that will be run
     * @return @return returns the result set from the query
     * @throws SQLException throws error if any issues
     */
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
