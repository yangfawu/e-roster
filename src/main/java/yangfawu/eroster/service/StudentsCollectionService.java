package yangfawu.eroster.service;

import org.springframework.beans.factory.annotation.Autowired;
import yangfawu.eroster.model.ListReferenceItem;
import yangfawu.eroster.repository.CourseRepository;

public class StudentsCollectionService extends AbstractListCollectionService<ListReferenceItem> {

    @Autowired
    public StudentsCollectionService(CourseRepository courseRepo) {
        super(ListReferenceItem.class, courseRepo, "studentIds");
    }

    @Override
    public void addReference(String rootId, String reference) {

    }
}
