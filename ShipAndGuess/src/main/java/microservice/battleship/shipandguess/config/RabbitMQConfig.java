package microservice.battleship.shipandguess.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String SHIP_EVENTS_EXCHANGE = "game.events";
    public static final String SHIP_SUNK_QUEUE = "ship.sunk.queue";

    @Bean
    public TopicExchange shipEventsExchange() {
        return new TopicExchange(SHIP_EVENTS_EXCHANGE);
    }

    @Bean
    public Queue shipSunkQueue() {
        return new Queue(SHIP_SUNK_QUEUE);
    }

    @Bean
    public Binding shipSunkBinding(Queue shipSunkQueue, TopicExchange shipEventsExchange) {
        return BindingBuilder.bind(shipSunkQueue)
                .to(shipEventsExchange)
                .with("ship.sunk");
    }
}