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
        System.out.println("🔵 [SHIP SUNK EVENT] Received: " + message);
        // Parse the message
        String[] parts = message.split(":");
        if (parts.length >= 3) {
            Long gameId = Long.parseLong(parts[0]);
            Long playerId = Long.parseLong(parts[1]);
            Long shipId = Long.parseLong(parts[2]);
            System.out.println("Ship " + shipId + " sunk by player " + playerId + " in game " + gameId);
        }
    }

    @RabbitListener(queues = "game.over.queue")
    public void handleGameOver(String message) {
        System.out.println("🔵 [GAME OVER EVENT] Received: " + message);
        // Parse the message
        String[] parts = message.split(":");
        if (parts.length >= 2) {
            Long gameId = Long.parseLong(parts[0]);
            Long winnerId = Long.parseLong(parts[1]);
            System.out.println("Game " + gameId + " over. Winner: " + winnerId);
        }
    }
}