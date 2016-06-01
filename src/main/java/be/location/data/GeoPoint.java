package be.location.data;

/**
 * @author Glenn Van Schil
 *         Created on 1/06/2016
 */
public class GeoPoint {
    double lat, lon;

    public GeoPoint() {
    }

    public GeoPoint(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
