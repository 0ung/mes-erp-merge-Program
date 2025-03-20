package codehows.com.daehoint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DaehoIntApplication {

	public static void main(String[] args) {
		SpringApplication.run(DaehoIntApplication.class, args);
	}

}
