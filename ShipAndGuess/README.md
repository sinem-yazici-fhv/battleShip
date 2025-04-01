# Ship and Guess Microservice

## Übersicht
Der **Ship and Guess Microservice** verwaltet Schiffe und Schüsse im Battleship-Spiel. Er ermöglicht das Platzieren von Schiffen und das Verarbeiten von Schüssen.

### Technologien
- Spring Boot
- H2-Datenbank
- RestTemplate (für Kommunikation mit anderen Microservices)
- Resilience4j (Circuit Breaker)

---

## API-Endpunkte

### **POST /ships**
- **Beschreibung**: Platziert ein Schiff auf dem Spielfeld.
- **Parameter**: `playerId`, `gameId`, `row`, `col`, `size`, `isHorizontal`.
- **Rückgabe**: Das platzierte Schiff (`Ship`).

### **GET /ships**
- **Beschreibung**: Ruft alle Schiffe eines Spielers in einem Spiel ab.
- **Parameter**: `gameId`, `playerId`.
- **Rückgabe**: Liste der Schiffe (`ShipDTO`).

### **POST /guesses**
- **Beschreibung**: Verarbeitet einen Schuss.
- **Parameter**: `playerId`, `gameId`, `rowIndex`, `col`.
- **Rückgabe**: Ergebnis des Schusses (`result`, `shipId`, `gameOver`, `winner`).

---

## Abhängigkeiten
- **Player Microservice**: `http://localhost:8082`
- **Game Microservice**: `http://localhost:8081`

---

## Konfiguration
- **Port**: `8083`
- **H2-Datenbank**: `jdbc:h2:mem:shipdb`
  - **Webkonsole**: `http://localhost:8083/h2-console`
  - **Benutzername**: `sa`
  - **Passwort**: (leer)

---

## Circuit Breaker 
- Player Service: Fallback-Methode playerServiceFallback gibt ein leeres PlayerDTO zurück.
- Game Service: Fallback-Methode gameServiceFallback gibt ein leeres GameDTO zurück.