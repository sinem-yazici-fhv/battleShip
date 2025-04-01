package microservice.battleship.game.messaging;

import microservice.battleship.game.application.GameService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class ShipEventListener {
    private final GameService gameService;

    public ShipEventListener(GameService gameService) {
        this.gameService = gameService;
    }

    @RabbitListener(queues = "ship.sunk.queue")
    public void handleShipSunk(String message) {
        String[] parts = message.split(":");
        Long gameId = Long.parseLong(parts[0]);
        Long playerId = Long.parseLong(parts[1]);
        Long shipId = Long.parseLong(parts[2]);

        // Update game status when a ship is sunk
        gameService.getGameStatus(gameId);
    }
}