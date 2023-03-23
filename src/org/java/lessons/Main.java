package org.java.lessons;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private final static String URL = System.getenv("DB_URL");
    private final static String USER = System.getenv("DB_USER");
    private final static String PASSWORD = System.getenv("DB_PW");

    public static void main(String[] args) {


        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)){

            String query = """
                    SELECT c.name, c.country_id , r.name, c2.name  FROM countries c\s
                    JOIN regions r ON r.region_id = c.region_id\s
                    JOIN continents c2 ON c2.continent_id = r.continent_id\s
                    WHERE c.name like ?
                    ORDER BY c.name;
                    """;

            Scanner scan = new Scanner(System.in);
            System.out.print("Search a country: ");
            String searchString = scan.nextLine();

            try (PreparedStatement ps = connection.prepareStatement(query,
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {

                List<Nation> nations = new ArrayList<>();

                ps.setString(1, "%" + searchString + "%");

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()){
                        String country = rs.getString("c.name");
                        int countryId = rs.getInt("c.country_id");
                        String region = rs.getString("r.name");
                        String continent = rs.getString("c2.name");

                        Nation nation = new Nation(country, countryId, region, continent);

                        nations.add(nation);
                    }

                    System.out.printf("%45s", "Country");
                    System.out.printf("%10s", "ID");
                    System.out.printf("%30s", "Region");
                    System.out.printf("%30s", "Continent");
                    System.out.println("\n");

                    for (Nation n : nations){
                        printResult(n.getCountry(), n.getCountryId(), n.getRegion(), n.getContinent());
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }



    }
    private static void printResult(String country, int countryId, String region, String continent){
        String stringPattern = "%30s";
        String intPattern = "%10s";
        System.out.printf("%45s", country);
        System.out.printf(intPattern, countryId);
        System.out.printf(stringPattern, region);
        System.out.printf(stringPattern + "\n", continent);

    }
}
