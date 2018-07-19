package com.yl.distribute.scheduler.web;


import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import javax.ws.rs.ApplicationPath;


/**
 * Jersey configuration class.
 *
 */
@ApplicationPath("api")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {   
        
        packages("com.yl.distribute.scheduler.web.resource");        
        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);        
        property(ServerProperties.BV_DISABLE_VALIDATE_ON_EXECUTABLE_OVERRIDE_CHECK, true);        
        property(CommonProperties.FEATURE_AUTO_DISCOVERY_DISABLE, true);
        property(ServerProperties.FEATURE_AUTO_DISCOVERY_DISABLE, true);        
        property(ServerProperties.MONITORING_STATISTICS_ENABLED,true);
        register(JacksonJsonProvider.class);
        register(ObjectMapperProvider.class);          
        register(io.swagger.jaxrs.listing.ApiListingResource.class); 
        register(io.swagger.jaxrs.listing.AcceptHeaderApiListingResource.class); 
        register(io.swagger.jaxrs.listing.SwaggerSerializers.class);
        
        setApplicationName("job application");
    }
}