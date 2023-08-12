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
public class ContentBaseFilterRouter {
    public static void main(String[] args) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        try (CamelContext context = new DefaultCamelContext()) {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");
            connectionFactory.setTrustedPackages(List.of("com.mypractice"));
            context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() {
                    log.info("Start configure");
                    from("file:D:\\Projects\\sftp-inbound?noop=true").log("Received file: ${header.CamelFileName}").to("jms:incomingProducts").log("Sent file to JMS: ${header.CamelFileName}");
                    from("jms:incomingProducts").choice()
                            .when(header("CamelFileName").endsWith(".json")).to("jms:jsonProducts")
                            .when(header("CamelFileName").endsWith(".xml")).to("jms:xmlProducts")
                            .otherwise().to("jms:csvProducts");
                    from("jms:jsonProducts")
                            .unmarshal().json(JsonLibrary.Jackson, List.class)
                            .filter(exchange -> {
                                List<LinkedHashMap<?,?>> rawProducts = exchange.getIn().getBody(List.class);
                                List<Products> products = objectMapper.convertValue(rawProducts, new TypeReference<>() {
                                });
                                return products.stream()
                                        .anyMatch(product -> "Black Sweatshirt".equals(product.getTitle()));
                            })

                            .process((Exchange exchange) -> log.info("Received JSON product with 'Black Sweatshirt' title: {}", exchange.getIn().getBody(String.class)));
                    from("jms:xmlProducts")
                            .filter(xpath("/products/product[name='dummyitem']"))
                            .process((Exchange exchange)-> log.info("Received xml product: {}", exchange.getIn().getBody(String.class)));
                    from("jms:csvProducts").process((Exchange exchange)-> log.info("Received csv product: {}", exchange.getIn().getBody(String.class)));
                    log.info("End configure");
                }
            });
            context.start();
            Thread.sleep(5000); // Keep the context running for a while
            context.stop();
        }
    }
}