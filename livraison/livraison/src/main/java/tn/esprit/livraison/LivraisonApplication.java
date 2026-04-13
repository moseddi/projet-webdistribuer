package tn.esprit.livraison;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient // Pour s'enregistrer auprès d'Eureka
@EnableScheduling      // Pour activer l'alerte périodique (Cron/Scheduled)
public class LivraisonApplication {
	public static void main(String[] args) {
		SpringApplication.run(LivraisonApplication.class, args);
	}
}