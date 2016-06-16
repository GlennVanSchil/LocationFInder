package be.location.repository;

import be.location.data.City;
import be.location.data.GeoPoint;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.client.Requests.createIndexRequest;
import static org.elasticsearch.index.query.QueryBuilders.geoDistanceQuery;
import static org.elasticsearch.index.query.QueryBuilders.wildcardQuery;

/**
 * @author Glenn Van Schil
 *         Created on 1/06/2016
 */
public class CityDAO {

    private static final String INDEX = "location";
    private static final String TYPE = "city";
    private final static String MAPPING = "{\"city\":{\"dynamic_templates\":[{\"notanalyzed\":{\"match\":\"*\",\"match_mapping_type\":\"string\",\"mapping\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}],\"properties\":{\"geoPoint\":{\"type\":\"geo_point\" }}}}";
    protected ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private EsClient esClient;

    @PostConstruct
    public void createMapping() {
        try {
            String mapping = MAPPING;
            PutMappingResponse putMappingResponse = esClient.getClient().admin().indices()
                    .preparePutMapping(INDEX)
                    .setType(TYPE)
                    .setSource(mapping)
                    .execute().actionGet();
        } catch (IndexNotFoundException e) {
            CreateIndexResponse createResponse = esClient.getClient().admin().indices().create(createIndexRequest(INDEX)).actionGet();
            this.createMapping();
        }
    }

    public void create(List<City> cities) {
        try {
            if (cities.size() > 0) {
                BulkRequestBuilder bulkRequest = esClient.getClient().prepareBulk();
                for (City city : cities) {
                    byte[] builder = mapper.writeValueAsBytes(city);
                    bulkRequest.add(esClient.getClient().prepareIndex(INDEX, TYPE)
                            .setSource(builder));
                }
                BulkResponse bulkResponse = bulkRequest.get();
                if (bulkResponse.hasFailures()) {
                    for (BulkItemResponse bulkItemResponse : bulkResponse) {
                        System.out.println(bulkResponse.buildFailureMessage());
                    }
                }
                esClient.getClient().admin().indices().refresh(new RefreshRequest(INDEX)).actionGet();
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public List<City> findCityInRange(GeoPoint geoPoint, double distance) {
        List<City> cities = new ArrayList<City>();
        QueryBuilder queryBuilder = geoDistanceQuery("geoPoint")
                .point(geoPoint.getLat(), geoPoint.getLon())
                .distance(distance, DistanceUnit.KILOMETERS)
                .optimizeBbox("memory")
                .geoDistance(GeoDistance.ARC);

        SearchRequestBuilder builder = esClient.getClient()
                .prepareSearch(INDEX)
                .setTypes("city")
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setScroll(new TimeValue(60000))
                .setSize(100).setExplain(true)
                .setPostFilter(queryBuilder)
                .addSort(SortBuilders.geoDistanceSort("geoPoint")
                        .order(SortOrder.ASC)
                        .point(geoPoint.getLat(), geoPoint.getLon())
                        .unit(DistanceUnit.KILOMETERS));

        SearchResponse response = builder
                .execute()
                .actionGet();


        scroll:
        while (true) {
            SearchHit[] hits = response.getHits().getHits();
            for (SearchHit hit : hits) {
                Map<String, Object> result = hit.getSource();
                cities.add(mapper.convertValue(result, City.class));
            }

            response = esClient.getClient().prepareSearchScroll(response.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
            if (response.getHits().getHits().length == 0) {
                break scroll;
            }
        }

        return cities;
    }

    public List<City> findCityByName(String name) {
        List<City> cities = new ArrayList<City>();
        QueryBuilder queryBuilder = wildcardQuery("name", "*" + name + "*");

        SearchRequestBuilder builder = esClient.getClient()
                .prepareSearch(INDEX)
                .setTypes("city")
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setScroll(new TimeValue(60000))
                .setSize(100).setExplain(true)
                .setPostFilter(queryBuilder);

        SearchResponse response = builder
                .execute()
                .actionGet();


        scroll:
        while (true) {
            SearchHit[] hits = response.getHits().getHits();
            for (SearchHit hit : hits) {
                Map<String, Object> result = hit.getSource();
                cities.add(mapper.convertValue(result, City.class));
            }

            response = esClient.getClient().prepareSearchScroll(response.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
            if (response.getHits().getHits().length == 0) {
                break scroll;
            }
        }

        return cities;
    }

    public void deleteIndex() {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(INDEX);
        esClient.getClient().admin().indices().delete(deleteIndexRequest).actionGet();
    }
}
