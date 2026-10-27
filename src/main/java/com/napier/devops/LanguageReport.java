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
    public void getLanguageSpeakers() {
        try {
            ResultSet rs = executeStatement(
                    "SELECT cl.Language, sum(c.Population) as sumPop FROM countrylanguage as cl, country as c WHERE cl.CountryCode = c.Code GROUP BY cl.Language ORDER BY sum(c.Population) DESC;"
            );

            System.out.println("Name | Population");
            while (rs.next()) {
                System.out.println(
                        rs.getString("Language") + " - " +
                                rs.getInt("sumPop")
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public ResultSet executeStatement(String query) {
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
