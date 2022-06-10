package yangfawu.eroster.repository;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import com.google.firebase.database.utilities.encoding.CustomClassMapper;
import lombok.Getter;
import yangfawu.eroster.exception.ApiExecutionException;
import yangfawu.eroster.service.ServiceUtil;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractRootRepository<T> {

    @Getter
    private final Firestore db;
    private final Class<T> type;

    @Getter
    private final String name;

    public AbstractRootRepository(Firestore db, Class<T> type, String name) {
        this.db = db;
        this.type = type;
        this.name = name;
    }

    public final CollectionReference ref() {
        return db.collection(name);
    }

    public final DocumentReference ref(String id) {
        return ref().document(id);
    }

    public final T find(String id) {
        return ServiceUtil.handleFuture(ref(id).get()).toObject(type);
    }

    public final String newId() {
        return ref().document().getId();
    }

    public final void create(String id, T data) {
        ServiceUtil.handleFuture(ref(id).create(data));
    }

    public final void update(String id, T data, String... fields) {
        if (fields.length < 1)
            return;

        // convert data to a pojo
        Object pojo = CustomClassMapper.convertToPlainJavaTypes(data);
        if (!(pojo instanceof Map))
            throw new ApiExecutionException("Cannot convert data into JSON.");
        Map<String, Object> rawUpdate = (Map<String, Object>) pojo;

        // only add non-null values to the update
        Map<String, Object> update = new HashMap<>();
        Object value;
        for (String key : fields) {
             value = rawUpdate.get(key);
             if (value == null)
                throw new IllegalArgumentException("Requested update value is not suppose to be null.");
             update.put(key, value);
        }

        // commit update
        if (update.size() < 1)
            return;
        ServiceUtil.handleFuture(ref(id).set(update, SetOptions.merge()));
    }

}
