package be.location.data;


/**
 * @author Glenn Van Schil
 *         Created on 1/06/2016
 */
public class City {
    private String zipcode;
    private String name;
    private GeoPoint geoPoint;

    public City() {
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public void setGeoPoint(double lat, double lon) {
        this.geoPoint = new GeoPoint(lat, lon);
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
