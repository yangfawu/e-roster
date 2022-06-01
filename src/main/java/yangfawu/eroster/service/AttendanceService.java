package yangfawu.eroster.service;

import org.springframework.stereotype.Service;
import yangfawu.eroster.model.Attendance;
import yangfawu.eroster.repository.AttendanceRepository;

@Service
public class AttendanceService {

    private final AttendanceRepository formRepo;

    public AttendanceService(AttendanceRepository formRepo) {
        this.formRepo = formRepo;
    }

    public Attendance getForm(String id) {
        return formRepo.findById(id).orElseThrow();
    }

}
