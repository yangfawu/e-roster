package yangfawu.eroster;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import yangfawu.eroster.service.UserService;

@SpringBootApplication
@EnableMongoRepositories
@Log4j2
public class ERosterApplication {

	public static void main(String[] args) {
		SpringApplication.run(ERosterApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner(UserService userSvc) {
		return new CommandLineRunner() {
			@Override
			public void run(String... args) throws Exception {
//				long milliseconds = 1486815313230L;
//				LocalDateTime cvDate = Instant.ofEpochMilli(milliseconds).atZone(ZoneId.systemDefault()).toLocalDateTime();

//				log.info("hasText={}", StringUtils.hasText(null));
				testUserService();
			}

			public void testUserService(){
				log.info("UserService={}", userSvc);
			}

		};
	}

}
