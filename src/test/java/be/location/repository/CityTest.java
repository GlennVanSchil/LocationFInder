package be.location.repository;

import be.location.data.City;
import be.location.data.GeoPoint;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @author Glenn Van Schil
 *         Created on 1/06/2016
 */
@ContextConfiguration(locations = {"classpath:spring/config.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class CityTest {

    @Autowired
    JdbcCityDAO jdbcCityDAO;
    @Autowired
    EsCityDAO esCityDAO;


    @Before
    public void setUp() throws Exception {
        List<City> cities = jdbcCityDAO.listCities();
        esCityDAO.create(cities);
    }

    @After
    public void tearDown() throws Exception {
        esCityDAO.deleteIndex();
    }

    @Test
    public void testListCities() throws Exception {
        List<City> centers = esCityDAO.findCityByName("Brugge");
        System.out.println("<===========================================>");
        System.out.println();
        for (City center : centers) {
            List<City> citiesInRange = esCityDAO.findCityInRange(center.getGeoPoint(), 10);
            for (City city : citiesInRange) {
                System.out.println(city.getZipcode() + " - " + city.getName() + " - " + this.distance(center.getGeoPoint(), city.getGeoPoint()) + " km");
            }
            System.out.println();
        }
        System.out.println("<===========================================>");
    }

    private double distance(GeoPoint geoPoint1, GeoPoint geoPoint2) {

        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(geoPoint2.getLat() - geoPoint1.getLat());
        Double lonDistance = Math.toRadians(geoPoint2.getLon() - geoPoint1.getLon());
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(geoPoint1.getLat())) * Math.cos(Math.toRadians(geoPoint2.getLat()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;
        return Math.round(distance * 100) / 100.0;
    }
}