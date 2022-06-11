package yangfawu.eroster.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.auth.UserRecord.UpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import yangfawu.eroster.exception.InputValidationException;
import yangfawu.eroster.model.User;
import yangfawu.eroster.model.User.UserBuilder;
import yangfawu.eroster.payload.request.UserRegisterRequest;
import yangfawu.eroster.payload.request.UserUpdateRequest;
import yangfawu.eroster.repository.UserRepository;

import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserService {

    @Value("${app.auth.custom-claims-name}")
    private String CUSTOM_CLAIMS;

    private final FirebaseAuth auth;
    private final UserRepository userRepo;
    private final CoursesCollectionService coursesListSvc;
    private final InvitationsCollectionService invListSvc;

    public User register(UserRegisterRequest req) {
        // create a new auth account
        CreateRequest createRequest;
        try {
            createRequest = new CreateRequest()
                    .setEmail(req.getEmail())
                    .setPassword(req.getPassword())
                    .setDisplayName(req.getName())
                    .setPhotoUrl(ServiceUtil.create(req.getName()));
        } catch (IllegalArgumentException e) {
            throw new InputValidationException("Some inputs are illegal.");
        }
        UserRecord rec = ServiceUtil.handleFuture(auth.createUserAsync(createRequest));

        // update Firebase to include user roles in custom claims
        // add User object to the database
        final String UID = rec.getUid();
        User newUser = User.builder()
                .id(UID)
                .email(rec.getEmail())
                .name(rec.getDisplayName())
                .accountType(req.getAccountType())
                .build();
        Map<String, Object> customClaims = Map.of(
                CUSTOM_CLAIMS,
                Arrays.asList(req.getAccountType().toString())
        );
        ServiceUtil.handleFutures(
                auth.setCustomUserClaimsAsync(UID, customClaims),
                userRepo.ref(UID).create(newUser)
        );
        coursesListSvc.initMeta(UID);
        invListSvc.initMeta(UID);

        // return newly created user if everything is successful
        return newUser;
    }

    public User getUserById(String id) {
        return Optional.ofNullable(userRepo.find(id))
                .orElseThrow(() -> new NoSuchElementException("Can't find user."));
    }

    public String getUserIdFromFirebaseJwt(Jwt token) {
        return Optional.ofNullable(token.getClaimAsString("user_id"))
                .orElseThrow(() -> new InputValidationException("Token does not contain required information."));
    }

    public User updateUser(Jwt token, UserUpdateRequest req) {
        // fetch user ID from token
        final String UID = getUserIdFromFirebaseJwt(token);
        UpdateRequest update = new UpdateRequest(UID);
        UserBuilder updateBuilder = User.builder();
        try {
            if (req.getName() != null) {
                update.setDisplayName(req.getName());
                update.setPhotoUrl(ServiceUtil.create(req.getName()));
                updateBuilder.name(req.getName());
            }
            // add other changes
        } catch (IllegalArgumentException e) {
            throw new InputValidationException("Some inputs are illegal.");
        }

        // apply auth and database updates
        userRepo.update(UID, updateBuilder.build(), "name");
        ServiceUtil.handleFuture(auth.updateUserAsync(update));

        // return the new User object after the update
        return getUserById(UID);
    }

}
