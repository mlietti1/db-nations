package org.java.lessons;

public class Nation {
    private String country;
    private int countryId;
    private String region;
    private String continent;

    public Nation(String country, int countryId, String region, String continent) {
        this.country = country;
        this.countryId = countryId;
        this.region = region;
        this.continent = continent;
    }

    public String getCountry() {
        return country;
    }

    public int getCountryId() {
        return countryId;
    }

    public String getRegion() {
        return region;
    }

    public String getContinent() {
        return continent;
    }
}
