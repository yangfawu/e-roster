package yangfawu.eroster.service;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
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
import yangfawu.eroster.model.ListMeta;
import yangfawu.eroster.model.User;
import yangfawu.eroster.payload.request.UserRegisterRequest;
import yangfawu.eroster.payload.request.UserUpdateRequest;
import yangfawu.eroster.repository.UserRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserService {

    @Value("${app.auth.custom-claims-name}")
    private String CUSTOM_CLAIMS;

    private final FirebaseAuth auth;
    private final Firestore db;
    private final UserRepository userRepo;

    public DocumentReference userRef(String id) {
        return db.collection("users").document(id);
    }

    public CollectionReference userCoursesRef(String id) {
        return userRef(id).collection("courses");
    }

    public CollectionReference userInvitationsRef(String id) {
        return userRef(id).collection("invitations");
    }

    public DocumentReference metaRef(CollectionReference ref) {
        return ref.document("_meta");
    }

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
                userRef(UID).create(newUser),
                metaRef(userCoursesRef(UID)).create(ListMeta.defaultBuild()),
                metaRef(userInvitationsRef(UID)).create(ListMeta.defaultBuild())
        );

        // return newly created user if everything is successful
        return newUser;
    }

    public User getUserById(String id) {
        User user = ServiceUtil.handleFuture(userRef(id).get()).toObject(User.class);
        return Optional.ofNullable(user)
                .orElseThrow(() -> new NoSuchElementException("Can't find user."));
    }

    public String getUserIdFromFirebaseJwt(Jwt token) {
        String id = token.getClaimAsString("user_id");
        return Optional.ofNullable(id)
                .orElseThrow(() -> new InputValidationException("Token does not contain required information."));
    }

    public User updateUser(Jwt token, UserUpdateRequest req) {
        // fetch user ID from token
        final String UID = getUserIdFromFirebaseJwt(token);
        UpdateRequest update = new UpdateRequest(UID);
        Map<String, Object> dbUpdate = new HashMap<>();
        try {
            if (req.getName() != null) {
                update.setDisplayName(req.getName());
                update.setPhotoUrl(ServiceUtil.create(req.getName()));
                dbUpdate.put("name", req.getName());
            }
            // add other changes
        } catch (IllegalArgumentException e) {
            throw new InputValidationException("Some inputs are illegal.");
        }

        // apply auth and database updates
        ServiceUtil.handleFutures(
                auth.updateUserAsync(update),
                userRef(UID).set(dbUpdate, SetOptions.merge())
        );

        // return the new User object after the update
        return getUserById(UID);
    }

}
