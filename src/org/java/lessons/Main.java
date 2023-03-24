package org.java.lessons;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private final static String URL = System.getenv("DB_URL");
    private final static String USER = System.getenv("DB_USER");
    private final static String PASSWORD = System.getenv("DB_PW");

    public static void main(String[] args) {


        Scanner scan = new Scanner(System.in);

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)){

            boolean hasResults = false;
            String query = """
                    SELECT c.name, c.country_id , r.name, c2.name  FROM countries c\s
                    JOIN regions r ON r.region_id = c.region_id\s
                    JOIN continents c2 ON c2.continent_id = r.continent_id\s
                    WHERE c.name like ?
                    ORDER BY c.name;
                    """;

            String queryLang = """
                    SELECT l.`language` from country_languages cl\s
                    join languages l ON l.language_id =cl.language_id\s
                    where cl.country_id = ?
                    ORDER BY l.`language`;
                    """;

            String queryStats = """
                    SELECT `year` , population , gdp\s
                    FROM country_stats cs\s
                    WHERE cs.country_id = ?
                    ORDER BY `year` DESC
                    limit 1;
                    """;

            System.out.print("Search a country: ");
            String searchString = scan.nextLine();

            try (PreparedStatement ps = connection.prepareStatement(query,
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {

                List<Nation> nations = new ArrayList<>();

                ps.setString(1, "%" + searchString + "%");

                try (ResultSet rs = ps.executeQuery()) {
                    if(rs.next()){

                        hasResults = true;
                        System.out.printf("%45s", "Country");
                        System.out.printf("%10s", "ID");
                        System.out.printf("%30s", "Region");
                        System.out.printf("%30s", "Continent");
                        System.out.println("\n");
                        do {

                            String country = rs.getString("c.name");
                            int countryId = rs.getInt("c.country_id");
                            String region = rs.getString("r.name");
                            String continent = rs.getString("c2.name");

                            Nation nation = new Nation(country, countryId, region, continent);

                            nations.add(nation);
                        } while (rs.next());


                    } else {
                        System.out.println("No countries found.");
                    }

                    for (Nation n : nations){
                        printResult(n.getCountry(), n.getCountryId(), n.getRegion(), n.getContinent());
                    }
                }
            }

            if(hasResults){
                System.out.print("Choose a country id: ");
                int countryId = Integer.parseInt(scan.nextLine());

                try(PreparedStatement psLang = connection.prepareStatement(queryLang)){
                    psLang.setInt(1, countryId);
                    try (ResultSet rsLang = psLang.executeQuery()){
                        System.out.println("Languages: ");
                        while (rsLang.next()){
                            System.out.print(rsLang.getString(1));
                            if(!rsLang.isLast()){
                                System.out.print(", ");
                            }else{
                                System.out.println();
                            }
                        }
                    }
                }

                try(PreparedStatement psStats = connection.prepareStatement(queryStats)){
                    psStats.setInt(1, countryId);
                    try(ResultSet rsStats = psStats.executeQuery()){
                        if(rsStats.next()){
                            int year = rsStats.getInt(1);
                            BigDecimal population = rsStats.getBigDecimal(2);
                            BigDecimal gdp = rsStats.getBigDecimal(3);
                            System.out.println("Stats for year " + year + ":");
                            System.out.println("Population: " + population);
                            System.out.println("GDP: " + gdp);
                        }
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
