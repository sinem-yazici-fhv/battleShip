package microservice.battleship.player.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class GameEventListener {

    @RabbitListener(queues = "player.joined.queue")
    public void handlePlayerJoinedGame(String message) {
        String[] parts = message.split(":");
        Long gameId = Long.parseLong(parts[0]);
        Long playerId = Long.parseLong(parts[1]);

        System.out.println("Player " + playerId + " joined game " + gameId);
    }

    @RabbitListener(queues = "game.over.queue")
    public void handleGameOver(String message) {
        String[] parts = message.split(":");
        Long gameId = Long.parseLong(parts[0]);
        Long winnerId = Long.parseLong(parts[1]);
        System.out.println("Game " + gameId + " over. Winner: " + winnerId);
    }
}
