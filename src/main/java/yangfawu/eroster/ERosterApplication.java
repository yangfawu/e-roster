package yangfawu.eroster;

import com.google.api.client.util.Lists;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@SpringBootApplication
@RequiredArgsConstructor
@Log4j2
public class ERosterApplication implements CommandLineRunner {

    @Value("${app.main.wipe-database}")
    private boolean wipeDB;

    private final FirebaseAuth auth;
    private final Firestore db;

    public static void main(String[] args) {
        SpringApplication.run(ERosterApplication.class, args);
    }

    public void wipeDatabase() throws FirebaseAuthException, ExecutionException, InterruptedException {
        if (!wipeDB)
            return;
        // wipe database
        log.warn("Please manually wipe Firestore on app.");

        // wipe accounts
        ListUsersPage userPage;
        while (true) {
            userPage = auth.listUsers(null);
            if (userPage == null)
                break;
            List<String> users = Lists.newArrayList(userPage.getValues())
                            .stream()
                            .map(ExportedUserRecord::getUid)
                            .collect(Collectors.toList());
            if (users.size() < 1)
                break;
            DeleteUsersResult result = auth.deleteUsersAsync(users).get();
            log.info("Successfully deleted {} users.", result.getSuccessCount());
            log.info("Failed to delete {} users.", result.getFailureCount());
            for (ErrorInfo error : result.getErrors())
                log.error("Error #{}, reason: {}", error.getIndex(), error.getReason());
        }

        log.info("Database wiped.");
    }

    @Override
    public void run(String... args) throws Exception {
        wipeDatabase();
    }

}
