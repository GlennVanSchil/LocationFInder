package be.location.repository;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Glenn Van Schil
 *         Created on 1/06/2016
 */
public class EsClient {
    private Client client;
    private String host, clusterName;
    private int port;

    public EsClient() {
    }

    public EsClient(String host, int port, String clusterName) {
        this.host = host;
        this.port = port;
        this.clusterName = clusterName;
    }

    @PostConstruct
    public void init() throws UnknownHostException {
        if (clusterName == null || clusterName.isEmpty()) {
            this.client = TransportClient.builder().build()
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
        } else {
            Settings settings = Settings.settingsBuilder()
                    .put("cluster.name", clusterName).build();
            this.client = TransportClient.builder()
                    .settings(settings)
                    .build()
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
        }
    }

    @PreDestroy
    public void close() {
        this.client.close();
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Client getClient() {
        return client;
    }

}
