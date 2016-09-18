package com.acemen.android.wasters.entity;

/**
 * Created by Audrik ! on 24/08/2016.
 */
public class WasteEntity {

    private String captureFilename;
    private String wasteType;
    private String adresse;
    private double longitude;
    private double latitude;

    public WasteEntity(String captureFilename, String wasteType, String adresse, double longitude, double latitude) {
        this.captureFilename = captureFilename;
        this.wasteType = wasteType;
        this.adresse = adresse;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getCaptureFilename() {
        return captureFilename;
    }

    public String getWasteType() {
        return wasteType;
    }

    public String getAdresse() {
        return adresse;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
