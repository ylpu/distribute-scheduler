package com.yl.distribute.scheduler.common.jersey;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class JerseyClient {
    
    public static <T> Response add(String uri,T t) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(uri);
        Response response = target.request().buildPost(Entity.entity(t, MediaType.APPLICATION_JSON_TYPE)).invoke();
        return response;
    }
    
    public static <T> Response update(String uri,T t) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(uri);
        Response response = target.request().buildPut(Entity.entity(t, MediaType.APPLICATION_JSON_TYPE)).invoke();
        return response;
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T get(String uri, Class<?> cls) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(uri);
        Response response = target.request().get();
        return (T) response.readEntity(cls);
    }
    
    public static <T> Response delete(String uri,T t) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(uri);
        Response response = target.request().delete();
        return response;
    }
}
