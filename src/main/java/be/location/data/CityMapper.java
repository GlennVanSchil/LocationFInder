package be.location.data;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Glenn Van Schil
 *         Created on 1/06/2016
 */
public class CityMapper implements RowMapper<City> {
    public City mapRow(ResultSet resultSet, int i) throws SQLException {
        City city = new City();
        city.setZipcode(resultSet.getString("zipcode"));
        city.setName(resultSet.getString("name"));
        city.setGeoPoint(resultSet.getDouble("lat"), resultSet.getDouble("lon"));
        return city;
    }
}
