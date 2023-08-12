package com.mypractice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.dataformat.JsonLibrary;

import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
public class ContentBaseFilterRouterOptimize {
    private static final String INCOMING_DIRECTORY = "file:D:\\Projects\\sftp-inbound?noop=true";
    private static final String BLACK_SWEATSHIRT_TITLE = "Black Sweatshirt";

    public static void main(String[] args) throws Exception {
        try (CamelContext context = new DefaultCamelContext()) {
            setupContext(context);
            context.start();
            Thread.sleep(5000); // Keep the context running for a while
            context.stop();
        }
    }

    private static void setupContext(CamelContext context) throws CamelFilterException {
        ObjectMapper objectMapper = new ObjectMapper();
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");
        connectionFactory.setTrustedPackages(List.of("com.mypractice"));
        context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() {
                    log.info("Start configure");
                    configureIncomingRoute();
                    configureIncomingProductsRoute();
                    configureJsonProductsRoute();
                    configureXmlProductsRoute();
                    configureCsvProductsRoute();
                    log.info("End configure");
                    }
                private void configureIncomingRoute() {
                    from(INCOMING_DIRECTORY)
                            .log("Received file: ${header.CamelFileName}")
                            .to("jms:incomingProducts")
                            .log("Sent file to JMS: ${header.CamelFileName}");
                }
                private void configureIncomingProductsRoute() {
                    from("jms:incomingProducts")
                            .choice()
                            .when(header("CamelFileName").endsWith(".json")).to("jms:jsonProducts")
                            .when(header("CamelFileName").endsWith(".xml")).to("jms:xmlProducts")
                            .otherwise().to("jms:csvProducts");
                }
                private void configureXmlProductsRoute() {
                    from("jms:xmlProducts")
                            .filter(xpath("/products/product[name='dummyitem']"))
                            .process(e -> log.info("Received xml product: {}", e.getIn().getBody(String.class)));
                }

                private void configureCsvProductsRoute() {
                    from("jms:csvProducts")
                            .process(e -> log.info("Received csv product: {}", e.getIn().getBody(String.class)));
                }
                private void configureJsonProductsRoute() {
                    from("jms:jsonProducts")
                            .unmarshal().json(JsonLibrary.Jackson, List.class)
                            .filter(this::hasBlackSweatshirt)
                            .process(e -> log.info("Received JSON product with '{}' title: {}", BLACK_SWEATSHIRT_TITLE, e.getIn().getBody(String.class)));
                }
                private boolean hasBlackSweatshirt(Exchange exchange) {
                    List<LinkedHashMap<?, ?>> rawProducts = exchange.getIn().getBody(List.class);
                    List<Products> products = objectMapper.convertValue(rawProducts, new TypeReference<>() {});
                    return products.stream().anyMatch(product -> BLACK_SWEATSHIRT_TITLE.equals(product.getTitle()));
                }

            });
        } catch (Exception e) {
            throw new CamelFilterException(e.getMessage());
        }


    }

}
