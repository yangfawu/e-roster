package yangfawu.eroster.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import yangfawu.eroster.exception.InputValidationException;
import yangfawu.eroster.model.Attendance;
import yangfawu.eroster.model.Course;
import yangfawu.eroster.repository.AttendanceRepository;
import yangfawu.eroster.util.ServiceUtil;

import java.time.Instant;
import java.util.HashMap;

@Service
public class AttendanceService {

    private final AttendanceRepository formRepo;
    private final CourseService courseSvc;
    private final MongoTemplate mongoTemp;

    @Autowired
    public AttendanceService(
            AttendanceRepository formRepo,
            CourseService courseSvc,
            MongoTemplate mongoTemp) {
        this.formRepo = formRepo;
        this.courseSvc = courseSvc;
        this.mongoTemp = mongoTemp;
    }

    public Attendance getForm(String id) {
        return formRepo.findById(id).orElseThrow();
    }

    public String startForm(String courseId) {
        Course course = courseSvc.getCourse(courseId);
        if (course.isArchived())
            throw new InputValidationException("Course is archived.");

        Attendance form = formRepo.save(new Attendance(course.getId()));
        HashMap<String, Attendance.Mark> marks = form.getMarks();
        for (String studentId: course.getStudentIds())
            marks.put(studentId, Attendance.Mark.UNMARKED);

        return formRepo.save(form).getId();
    }

    public Page<Attendance> getCourseForms(String courseId, int page, int size) {
        return formRepo.findByCourseIdOrderByCreated(
                courseId,
                ServiceUtil.generatePageable(page, size)
        );
    }

    public Page<Attendance> getCourseMarks(String courseId, String studentId, int page, int size) {
        Pageable pageable = ServiceUtil.generatePageable(
                page,
                size,
                Sort.by(Sort.Direction.ASC, "created")
        );
        Query query = new Query();
        query.addCriteria(Criteria.where("courseId").is(courseId));
        query.addCriteria(Criteria.where("marks." + studentId).exists(true));
        query.with(pageable);
        return PageableExecutionUtils.getPage(
                mongoTemp.find(query, Attendance.class),
                pageable,
                () -> mongoTemp.count(query, Attendance.class)
        );
    }

    public void markStudent(String attendanceId, String studentId, Attendance.Mark mark) {
        Attendance form = getForm(attendanceId);
        Course course = courseSvc.getCourse(form.getCourseId());
        if (course.isArchived())
            throw new InputValidationException("Course is archived.");

        form.getMarks().put(studentId, mark);
        form.setUpdated(Instant.now());
        formRepo.save(form);
    }

}
