package yangfawu.eroster.service;

import com.google.api.core.ApiFuture;
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

    public int getCount(String id) {
        return getMeta(id).getCount();
    }

    public void initMeta(String id) {
        ServiceUtil.handleFuture(metaRef(id).create(ListMeta.defaultBuild()));
    }

    public boolean hasItem(String id, String ref) {
        Query query = ref(id)
                .whereNotEqualTo(FieldPath.documentId(), META_KEY)
                .whereEqualTo("ref", ref)
                .limit(1);
        return ServiceUtil.handleFuture(query.get()).getDocuments().size() > 0;
    }

    public List<T> getItems(String id, int start, int size) {
        if (start < 0)
            throw new IllegalArgumentException("Start index must be non-negative.");
        if (size < 0)
            throw new IllegalArgumentException("Size must be non-negative.");
        if (size < 1)
            return List.of();
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

    public boolean deleteItem(String id, String ref) {
        if (!hasItem(id, ref))
            return false;
        Query query = ref(id)
                .whereNotEqualTo(FieldPath.documentId(), META_KEY)
                .whereEqualTo("ref", ref)
                .limit(1);
        List<ApiFuture> tasks = ServiceUtil.handleFuture(query.get()).getDocuments()
                .stream()
                .map(DocumentSnapshot::getReference)
                .map(DocumentReference::delete)
                .collect(Collectors.toList());
        tasks.add(metaRef(id).update("pointer", FieldValue.increment(-tasks.size())));
        ServiceUtil.handleFutures(tasks.toArray(new ApiFuture[tasks.size()]));
        return true;
    }

    public void addItem(String id, T item) {
        if (item.getRef() == null)
            throw new IllegalArgumentException("Item must contain reference.");
        if (hasItem(id, item.getRef()))
            throw new IllegalArgumentException("Item already in collection.");

        item.setIndex(getMeta(id).getCount());
        item.setId(newItemId(id));

        ServiceUtil.handleFutures(
                metaRef(id).update(
                        "count", FieldValue.increment(1),
                        "pointer", FieldValue.increment(1)
                ),
                itemRef(id, item.getId()).create(item)
        );
    }

    public abstract void addReference(String rootId, String reference);

}
