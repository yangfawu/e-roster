package yangfawu.eroster.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yangfawu.eroster.model.ListReferenceItem;
import yangfawu.eroster.repository.UserRepository;

@Service
public class CoursesCollectionService extends AbstractListCollectionService<ListReferenceItem> {

    private final UserRepository userRepo;

    @Autowired
    public CoursesCollectionService(UserRepository userRepo) {
        super(ListReferenceItem.class, userRepo, "courses");
        this.userRepo = userRepo;
    }

}
