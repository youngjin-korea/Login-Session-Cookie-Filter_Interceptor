package hello.login.web.session;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

@Slf4j
@RestController
public class SessionInfoController {

    @GetMapping("/session-info")
    public String sessionInfo(HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        if (session == null) {
            return "세션이 존재하지 않습니다.";
        }

        session.getAttributeNames().asIterator()
                .forEachRemaining(name -> log.info("sessionName: '{}', sessionValue: '{}'", name, session.getAttribute(name)));

        log.info("sessionId={}", session.getId());
        // 마지막으로 요청한 시간을 기준으로 비활성화 까지 시간 설정 기본 1800초 -> 30분
        log.info("maxInactiveInterval={}", session.getMaxInactiveInterval());
        log.info("creationTime={}", new Date(session.getCreationTime()));
        log.info("lastAccessedTime={}", new Date(session.getLastAccessedTime()));
        log.info("isNew={}", session.isNew());
        return "세션 출력";
    }

}
