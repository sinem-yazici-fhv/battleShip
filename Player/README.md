# Player Microservice

## Übersicht
Der **Player Microservice** verwaltet Spieler im Battleship-Spiel. Er ermöglicht das Erstellen von Spielern und das Abrufen von Spielerinformationen.

### Technologien
- Spring Boot
- H2-Datenbank
- RestTemplate (für Kommunikation mit anderen Microservices)
- Resilience4j (Circuit Breaker)

---

## API-Endpunkte

### **POST /players**
- **Beschreibung**: Erstellt einen neuen Spieler.
- **Parameter**: `name` (Spielername), `gameId` (Spiel-ID).
- **Rückgabe**: Der erstellte Spieler (`Player`).

### **GET /players/{id}**
- **Beschreibung**: Ruft einen Spieler anhand seiner ID ab.
- **Parameter**: `id` (Spieler-ID).
- **Rückgabe**: Der Spieler (`Player`), falls vorhanden.

---

## Abhängigkeiten
- **Game Microservice**: `http://localhost:8081`

---

## Konfiguration
- **Port**: `8082`
- **H2-Datenbank**: `jdbc:h2:mem:playerdb`
  - **Webkonsole**: `http://localhost:8082/h2-console`
  - **Benutzername**: `sa`
  - **Passwort**: (leer)

---

## Circuit Breaker 
Game Service: Fallback-Methode gameServiceFallback erstellt einen Fallback-Spieler,
wenn der Game Microservice nicht verfügbar ist.