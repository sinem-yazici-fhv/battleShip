package microservice.battleship.shipandguess.messaging;

import microservice.battleship.shipandguess.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class ShipEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public ShipEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishShipSunk(Long gameId, Long playerId, Long shipId) {
        String message = gameId + ":" + playerId + ":" + shipId;
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.SHIP_EVENTS_EXCHANGE,
            "ship.sunk",
            message
        );
    }
}