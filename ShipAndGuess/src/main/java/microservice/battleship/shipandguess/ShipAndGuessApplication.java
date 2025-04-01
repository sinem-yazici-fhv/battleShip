package microservice.battleship.shipandguess;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class ShipAndGuessApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShipAndGuessApplication.class, args);
	}

}
