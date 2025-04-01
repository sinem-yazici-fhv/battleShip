# Game Microservice


## Übersicht
Der **Game Microservice** verwaltet Spiele im Battleship-Spiel. Er ermöglicht das Erstellen von Spielen, das Hinzufügen von Spielern und das Überprüfen des Spielstatus.

### Technologien
- Spring Boot
- H2-Datenbank
- RestTemplate (für Kommunikation mit anderen Microservices)
- Resilience4j (Circuit Breaker)

---

## API-Endpunkte

### **POST /game**
- **Beschreibung**: Erstellt ein neues Spiel.
- **Rückgabe**: Das erstellte Spiel (`Game`).

### **GET /game/{id}**
- **Beschreibung**: Ruft ein Spiel anhand seiner ID ab.
- **Parameter**: `id` (Spiel-ID).
- **Rückgabe**: Das Spiel (`Game`), falls vorhanden.

### **GET /game/{gameId}/status**
- **Beschreibung**: Überprüft den Status eines Spiels.
- **Parameter**: `gameId` (Spiel-ID).
- **Rückgabe**: `gameOver` (boolean) und `winner` (Spieler-ID).

### **POST /game/{gameId}/addPlayer/{playerId}**
- **Beschreibung**: Fügt einen Spieler zu einem Spiel hinzu.
- **Parameter**: `gameId` (Spiel-ID), `playerId` (Spieler-ID).

---

## Abhängigkeiten
- **Player Microservice**: `http://localhost:8082`
- **Ship Microservice**: `http://localhost:8083`

---

## Konfiguration
- **Port**: `8081`
- **H2-Datenbank**: `jdbc:h2:mem:gamesdb`
  - **Webkonsole**: `http://localhost:8081/h2-console`
  - **Benutzername**: `sa`
  - **Passwort**: (leer)

---
## Circuit Breaker 
Player Service: Fallback-Methode playerServiceFallback gibt eine Fehlermeldung aus,
wenn der Player Microservice nicht verfügbar ist.