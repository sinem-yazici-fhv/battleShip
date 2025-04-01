package microservice.battleship.game.application;

import microservice.battleship.game.dto.ShipDTO;
import microservice.battleship.game.messaging.GameEventPublisher;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import microservice.battleship.game.Domain.Game;
import org.springframework.web.client.RestTemplate;
import microservice.battleship.game.dto.PlayerDTO;
import microservice.battleship.game.persistence.GameRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {
    private final GameRepository gameRepository;
    private final RestTemplate restTemplate;
    private final GameEventPublisher gameEventPublisher;
    private final Map<Long, Map<Long, Boolean>> shipStatusCache = new ConcurrentHashMap<>();

    public GameService(GameRepository gameRepository, RestTemplate restTemplate, GameEventPublisher gameEventPublisher) {
        this.gameRepository = gameRepository;
        this.restTemplate = restTemplate;
        this.gameEventPublisher = gameEventPublisher;
    }

    public Game createGame() {
        Game game = new Game();
        return gameRepository.save(game);
    }

    public Optional<Game> getGame(Long id) {
        return gameRepository.findById(id);
    }

    public Map<String, Object> getGameStatus(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));

        return game.checkGameStatus(playerId -> {
            Boolean cachedStatus = shipStatusCache.getOrDefault(gameId, new HashMap<>()).get(playerId);
            if (cachedStatus != null) {
                return cachedStatus;
            }
            return areAllShipsSunk(gameId, playerId);
        },  gameEventPublisher);
    }

    private boolean areAllShipsSunk(Long gameId, Long playerId) {
        String shipServiceUrl = "http://localhost:8083/ships?gameId=" + gameId + "&playerId=" + playerId;
        ShipDTO[] ships = restTemplate.getForObject(shipServiceUrl, ShipDTO[].class);
        return ships != null && Arrays.stream(ships).allMatch(ShipDTO::isSunk);
    }

    @CircuitBreaker(name = "playerServiceCircuitBreaker", fallbackMethod = "playerServiceFallback")
    public void addPlayerToGame(Long gameId, Long playerId) {
        String playerServiceUrl = "http://localhost:8082/players/" + playerId;
        PlayerDTO playerDTO = restTemplate.getForObject(playerServiceUrl, PlayerDTO.class);

        if (playerDTO == null) {
            throw new RuntimeException("Player not found");
        }

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        game.addPlayer(playerDTO.getId());
        gameRepository.save(game);
        gameEventPublisher.publishPlayerJoinedGame(gameId, playerId);
    }

    @RabbitListener(queues = "ship.sunk.queue")
    public void handleShipSunkEvent(String message) {
        String[] parts = message.split(":");
        Long gameId = Long.parseLong(parts[0]);
        Long playerId = Long.parseLong(parts[1]);

        shipStatusCache.computeIfAbsent(gameId, k -> new HashMap<>())
                     .put(playerId, true);

        getGameStatus(gameId);
    }

    public void playerServiceFallback(Long gameId, Long playerId, Exception ex) {
        System.out.println("Player Service not available: " + ex.getMessage());
    }

    public class GameNotFoundException extends RuntimeException {
        public GameNotFoundException(Long gameId) {
            super("Game not found with id: " + gameId);
        }
    }
}