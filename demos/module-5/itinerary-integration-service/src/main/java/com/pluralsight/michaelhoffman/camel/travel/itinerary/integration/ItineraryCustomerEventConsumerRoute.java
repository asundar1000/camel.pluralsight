package com.pluralsight.michaelhoffman.camel.travel.itinerary.integration;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.rabbitmq.RabbitMQConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Route that consumes customer events for the Itinerary domain
 */
@Component
public class ItineraryCustomerEventConsumerRoute extends RouteBuilder {

    private static final Logger log =
        LoggerFactory.getLogger(ItineraryCustomerEventConsumerRoute.class);

    @Override
    public void configure() throws Exception {
        errorHandler(
            deadLetterChannel(
                "log:com.pluralsight.michaelhoffman.camel.travel.itinerary.integration?level=ERROR"));

        from("rabbitmq:customer" +
            "?connectionFactory=#rabbitConnectionFactory" +
            "&autoDelete=false" +
            "&bridgeErrorHandler=true" +
            "&declare=false" +
            "&exchangeType=topic" +
            "&passive=true" +
            "&queue=itinerary_customer"
        )
            .choice()
                .when(header(RabbitMQConstants.ROUTING_KEY).isEqualToIgnoreCase("customer.delete"))
                    .to("direct:postToItineraryEndpoint")
                .otherwise()
                    .stop();

        from("direct:postToItineraryEndpoint")
            .to("rest:post:itinerary/customer?host={{app.itinerary-service.host}}");
    }
}
