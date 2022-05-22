package yangfawu.eroster;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import yangfawu.eroster.model.User;
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
			public void run(String... args) {
//				User ada = new User("ada", "ada@gmail.com");
//				ada.getCourses().put("random_course_id", User.Role.STUDENT);
				try {
					User user = userSvc.retrieveUser("62897a9992beae13a4423059");
					log.info("{}", user);
				} catch (RuntimeException e) {
					log.error("Failed - {}", e.getMessage());
				}
 			}

		};
	}

}
