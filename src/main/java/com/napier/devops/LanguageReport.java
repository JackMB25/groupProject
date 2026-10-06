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
    // getLanguageSpeakers()

    public void getLanguageSpeakers() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT cl.Language, sum(c.Population) as sumPop FROM countrylanguage as cl, country as c WHERE cl.CountryCode = c.Code GROUP BY cl.Language ORDER BY sum(c.Population) DESC;"
            );

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

}
