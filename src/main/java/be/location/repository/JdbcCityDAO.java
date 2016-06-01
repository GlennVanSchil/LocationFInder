package be.location.repository;

import be.location.data.City;
import be.location.data.CityMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author Glenn Van Schil
 *         Created on 1/06/2016
 */
public class JdbcCityDAO {
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplateObject;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplateObject = new JdbcTemplate(dataSource);
    }

    public List<City> listCities() {
        String SQL = "select * from cities";
        List<City> cities = jdbcTemplateObject.query(SQL,
                new CityMapper());
        return cities;
    }
}
