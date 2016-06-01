# LocationFInder
Find cities within a range of an other city
## Guide
### Windows
1. Install ElasticSearch 2.3.1: https://www.elastic.co/downloads/past-releases/elasticsearch-2-3-1
2. Run ElasticSearch
3. Import LocationFinder in your IDE as a Maven project
4. Run the Test class from "LocationFinder\src\test\java\be\location\repository\CityTest.java"
5. Change the default city "Brugge" on rule #42 "List<City> centers = esCityDAO.findCityByName("Brugge");". This is case sensitive.

## Aditional information
The location.sql script contains all cities from Belgium. You can delete or create entries if you want too. As long as you don't change the names and/or structure, no code changes are required.

All the configuration of the application happens in "LocationFinder\src\main\resources\spring\config.xml". Everything is default but if you would like to change the host or port of ElasticSearch for example you simply change the host and port property:

    <bean id="esClient" class="be.location.repository.EsClient">
        <property name="host" value="localhost"/>
        <property name="port" value="9300"/>
    </bean>
