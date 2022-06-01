package yangfawu.eroster.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import yangfawu.eroster.model.CourseInvitation;

@Repository
public interface CourseInvitationRepository extends MongoRepository<CourseInvitation, String> {

    boolean existsByCourseIdAndAndInviteeId(String courseId, String inviteeId);

    long deleteByCourseIdAndInviteeId(String courseId, String inviteeId);

}
