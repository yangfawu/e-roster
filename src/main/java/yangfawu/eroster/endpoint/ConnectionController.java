package yangfawu.eroster.endpoint;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import yangfawu.eroster.model.Connection;
import yangfawu.eroster.service.ConnectionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/private/connection")
public class ConnectionController {

    private final ConnectionService conSvc;

    @PostMapping("/invite/{userId}/to/{courseId}")
    public void submitInvitation(
            @AuthenticationPrincipal Jwt token,
            @PathVariable String userId,
            @PathVariable String courseId) {
        conSvc.submitInvitation(token, userId, courseId);
    }

    @PostMapping("/acceptInvite/{courseId}")
    public void acceptInvitation(
            @AuthenticationPrincipal Jwt token,
            @PathVariable String courseId) {
        conSvc.acceptInvite(token, courseId);
    }

    @PostMapping("/request/{courseId}")
    public void submitRequest(
            @AuthenticationPrincipal Jwt token,
            @PathVariable String courseId) {
        conSvc.submitRequest(token, courseId);
    }

    @PostMapping("/acceptRequest/{userId}/into/{courseId}")
    public void acceptRequest(
            @AuthenticationPrincipal Jwt token,
            @PathVariable String userId,
            @PathVariable String courseId) {
        conSvc.acceptRequest(token, userId, courseId);
    }

    @GetMapping("/invitations")
    public List<Connection> getInvitations(
            @AuthenticationPrincipal Jwt token,
            @RequestParam(defaultValue = "0") int start,
            @RequestParam(defaultValue = "10") int size) {
        return conSvc.getInvitations(token, start, size);
    }

    @GetMapping("/requests/{courseId}")
    public List<Connection> getRequests(
            @AuthenticationPrincipal Jwt token,
            @PathVariable String courseId,
            @RequestParam(defaultValue = "0") int start,
            @RequestParam(defaultValue = "10") int size) {
        return conSvc.getRequests(token, courseId, start, size);
    }

}
