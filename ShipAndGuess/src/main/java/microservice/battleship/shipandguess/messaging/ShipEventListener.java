package microservice.battleship.shipandguess.messaging;

import jakarta.persistence.Transient;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShipEventListener {

    @RabbitListener(queues = "ship.sunk.queue")
    @Transactional
    public void handleShipSunkEvent(String message) {
        System.out.println("Received ship sunk event: " + message);
    }
}