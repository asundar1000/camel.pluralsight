package com.pluralsight.michaelhoffman.camel.customer.integration.addressupdateroute;

import com.pluralsight.michaelhoffman.camel.customer.integration.config.IntegrationConfig;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ToDynamicDefinition;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpointsAndSkip;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/**
 * Unit test for the demonstration route. This is currently just a simple
 * test to verify processing but could be expanded upon for checking message
 * content and exception cases.
 */
@CamelSpringBootTest
@SpringBootApplication
@ContextConfiguration(classes = IntegrationConfig.class)
@TestPropertySource(locations = "classpath:/application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockEndpointsAndSkip("file:.*|rest:.*")
@UseAdviceWith
public class AddressUpdatesToCustomerServiceRouteTest {

    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private CamelContext camelContext;

    @Value("classpath:data/customer-address-update-valid.csv")
    private Resource customerAddressUpdateFileValidResource;

    @Test
    public void route_testValid() throws Exception {
        AdviceWith.adviceWith(camelContext, "address-updates-to-customer-service-route",
            rb -> rb.replaceFromWith("direct:file:start"));

        AdviceWith.adviceWith(camelContext, "address-updates-to-customer-service-route",
            rb -> rb.weaveByType(ToDynamicDefinition.class).replace().toD("mock://rest:patch:customer"));

        camelContext.start();

        GenericFile file = new GenericFile();
        file.setFile(customerAddressUpdateFileValidResource.getFile());

        MockEndpoint restEndpoint =
            camelContext.getEndpoint("mock://rest:patch:customer", MockEndpoint.class);

        restEndpoint.expectedMessageCount(1);
        producerTemplate.sendBody("direct:file:start", file);
        restEndpoint.assertIsSatisfied();
    }

}
