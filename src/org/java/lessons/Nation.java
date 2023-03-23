package org.java.lessons;

import java.sql.*;
import java.util.List;
import java.util.Scanner;

public class Nation {

    private final static String URL = System.getenv("DB_URL");
    private final static String USER = System.getenv("DB_USER");
    private final static String PASSWORD = System.getenv("DB_PW");

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)){

            String query = """
                    SELECT c.name, c.country_id , r.name, c2.name  FROM countries c\s
                    JOIN regions r ON r.region_id = c.region_id\s
                    JOIN continents c2 ON c2.continent_id = r.continent_id\s
                    ORDER BY c.name;
                    """;

            try (PreparedStatement ps = connection.prepareStatement(query,
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {



                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()){
                        String country = rs.getString("c.name");
                        int countryId = rs.getInt("c.country_id");
                        String region = rs.getString("r.name");
                        String continent = rs.getString("c2.name");

                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
