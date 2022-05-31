package yangfawu.eroster;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Set;

@SpringBootApplication
@EnableMongoRepositories
@Log4j2
public class ERosterApplication implements CommandLineRunner {

    private final MongoTemplate mongoTemp;
    @Value("${app.main.wipe-database}")
    private boolean wipeDB;

    @Autowired
    public ERosterApplication(
            MongoTemplate mongoTemp) {
        this.mongoTemp = mongoTemp;
    }

    public static void main(String[] args) {
        SpringApplication.run(ERosterApplication.class, args);
    }

    public void wipeDatabase() {
        if (!wipeDB)
            return;
        Set<String> cols = mongoTemp.getCollectionNames();
        for (String colName : cols)
            mongoTemp.dropCollection(colName);
        log.info("Database wiped.");
    }

    @Override
    public void run(String... args) throws Exception {
        wipeDatabase();
    }

}
