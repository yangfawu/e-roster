package yangfawu.eroster;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
@Log4j2
public class ERosterApplication {

	public static void main(String[] args) {
		SpringApplication.run(ERosterApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner() {
		return new CommandLineRunner() {

			@Override
			public void run(String... args) {

 			}

		};
	}

}
