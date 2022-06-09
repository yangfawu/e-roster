package yangfawu.eroster.service;

import com.google.cloud.firestore.*;
import lombok.Getter;
import yangfawu.eroster.model.ListMeta;
import yangfawu.eroster.model.ListReferenceItem;
import yangfawu.eroster.repository.AbstractRootRepository;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractListCollectionService<T extends ListReferenceItem> {

    private static final String META_KEY = "_meta";

    private final Class<T> itemType;
    private final AbstractRootRepository rootRepo;

    @Getter
    private final String name;

    public AbstractListCollectionService(
            Class<T> itemType,
            AbstractRootRepository rootRepo,
            String name) {
        this.itemType = itemType;
        this.rootRepo = rootRepo;
        this.name = name;
    }

    public CollectionReference ref(String id) {
        return rootRepo.ref(id).collection(name);
    }

    public DocumentReference metaRef(String id) {
        return ref(id).document(META_KEY);
    }

    public ListMeta getMeta(String id) {
        return ServiceUtil.handleFuture(metaRef(id).get()).toObject(ListMeta.class);
    }

    public String newItemId(String id) {
        return ref(id).document().getId();
    }

    public DocumentReference itemRef(String id, String itemId) {
        return ref(id).document(itemId);
    }

    public List<T> getItems(String id, int start, int size) {
        Query query = ref(id)
                .whereNotEqualTo(FieldPath.documentId(), META_KEY)
                .orderBy("index", Query.Direction.DESCENDING)
                .startAt(start)
                .limit(size);
        return ServiceUtil.handleFuture(query.get()).getDocuments()
                .stream()
                .map(doc -> doc.toObject(itemType))
                .collect(Collectors.toList());
    }

    public void addItem(String id, T item) {
        if (item.getRef() == null)
            throw new IllegalArgumentException("Item must contain reference.");

        item.setIndex(getMeta(id).getCount());
        item.setId(newItemId(id));

        ServiceUtil.handleFutures(
                metaRef(id).update("count", FieldValue.increment(1)),
                itemRef(id, item.getId()).create(item)
        );
    }

}
