package microservice.battleship.shipandguess.application;

import microservice.battleship.shipandguess.domain.BoardConfig;
import microservice.battleship.shipandguess.domain.Guess;
import microservice.battleship.shipandguess.domain.Ship;
import microservice.battleship.shipandguess.dto.GameDTO;
import microservice.battleship.shipandguess.dto.PlayerDTO;
import microservice.battleship.shipandguess.persistence.GuessRepository;
import microservice.battleship.shipandguess.persistence.ShipRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GuessService {
    private final GuessRepository guessRepository;
    private final ShipRepository shipRepository;
    private final RestTemplate restTemplate;

    public GuessService(GuessRepository guessRepository, ShipRepository shipRepository, RestTemplate restTemplate) {
        this.guessRepository = guessRepository;
        this.shipRepository = shipRepository;
        this.restTemplate = restTemplate;
    }

    public Map<String, Object> makeGuess(Long playerId, Long gameId, int rowIndex, int col) {
        if (rowIndex < 0 || rowIndex >= BoardConfig.BOARD_SIZE || col < 0 || col >= BoardConfig.BOARD_SIZE) {
            throw new IllegalArgumentException("Schuss liegt außerhalb des Boards.");
        }

        PlayerDTO playerDTO = getPlayerFromPlayerService(playerId);
        GameDTO gameDTO = getGameFromGameService(gameId);


        Long opponentId = gameDTO.getPlayerIds().stream()
                .filter(id -> !id.equals(playerId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Opponent not found"));

        boolean hit = false;
        Long hitShipId = null;
       List<Ship> opponentShips = shipRepository.findByGameIdAndPlayerId(gameId, opponentId);
        for (Ship ship : opponentShips) {
            if (ship.isHit(rowIndex, col)) {
                hit = true;
                hitShipId = ship.getId();
                ship.markHit();
                shipRepository.save(ship);
                break;
            }
        }

        // Guess speichern
        Guess guess = new Guess(rowIndex, col, hit, playerDTO.getId(), gameDTO.getId());
        if (hit) {
            guess.setShipId(hitShipId);
        }
        guessRepository.save(guess);


        Map<String, Object> gameStatus = getGameStatus(gameId);


        Map<String, Object> response = new HashMap<>();
        response.put("result", hit ? "hit" : "miss");
        response.put("shipId", hit ? hitShipId : null);
        response.put("gameOver", gameStatus.get("gameOver"));
        response.put("winner", gameStatus.get("winner"));

        return response;
    }
    @CircuitBreaker(name = "playerServiceCircuitBreaker", fallbackMethod = "playerServiceFallback")
    private PlayerDTO getPlayerFromPlayerService(Long playerId) {
        String playerServiceUrl = "http://localhost:8082/players/" + playerId;
        return restTemplate.getForObject(playerServiceUrl, PlayerDTO.class);
    }

    @CircuitBreaker(name = "gameServiceCircuitBreaker", fallbackMethod = "gameServiceFallback")
    private GameDTO getGameFromGameService(Long gameId) {
        String gameServiceUrl = "http://localhost:8081/game/" + gameId;
        return restTemplate.getForObject(gameServiceUrl, GameDTO.class);
    }

    @CircuitBreaker(name = "gameServiceCircuitBreaker", fallbackMethod = "gameStatusFallback")
    private Map<String, Object> getGameStatus(Long gameId) {
        String gameServiceUrl = "http://localhost:8081/game/" + gameId + "/status";
        return restTemplate.getForObject(gameServiceUrl, Map.class);
    }
}