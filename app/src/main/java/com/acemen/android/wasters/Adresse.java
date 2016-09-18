package com.acemen.android.wasters;

/**
 * Created by Audrik ! on 21/08/2016.
 */
public class Adresse {
    private String streetNumber, streetName, cp, city;
    private double longitude, latitude;

    public Adresse(String streetNumber, String streetName, String cp, String city, double lon, double lat) {
        this.streetNumber = streetNumber;
        this.streetName = streetName;
        this.cp = cp;
        this.city = city;
        this.longitude = lon;
        this.latitude = lat;
    }

    @Override
    public String toString() {
        return streetNumber+" "+streetName+", "+cp+" "+city
                +" : Coords[lat="+latitude+", lon="+longitude+"]";
    }

    public String getAdresse() {
        return streetNumber+" "+streetName+", "+cp+" "+city;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public String getStreetName() {
        return streetName;
    }

    public String getCp() {
        return cp;
    }

    public String getCity() {
        return city;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
