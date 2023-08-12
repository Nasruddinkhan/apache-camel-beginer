package com.mypractice.routes;

import com.mypractice.routes.dto.Products;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

import javax.jms.ConnectionFactory;
import java.util.List;

@Slf4j
public class ContentBaseRouterWithObjectTransformation {
    public static void main(String[] args) throws Exception {
        try (CamelContext context = new DefaultCamelContext()) {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");
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
                            .unmarshal()
                            .json(List.class)
                            .process((Exchange exchange) -> {
                                log.info("Received json file name: {}", exchange.getIn().getHeader("CamelFileName"));
                                List<Products> products = exchange.getIn().getBody(List.class);
                                log.info("Received list of json products: {}", products);
                        });
                    from("jms:xmlProducts").process((Exchange exchange) -> log.info("Received xml product: {}", exchange.getIn().getBody(String.class)));
                    from("jms:csvProducts").process((Exchange exchange) -> log.info("Received csv product: {}", exchange.getIn().getBody(String.class)));
                    log.info("End configure");
                }
            });
            context.start();
            Thread.sleep(5000); // Keep the context running for a while
            context.stop();
        }
    }



}