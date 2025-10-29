package hello.login.web.session;

import hello.login.domain.member.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SessionManagerTest {

    @Autowired
    SessionManager sessionManager;

    /**
     * 1. 세션 생성하여 응답
     * 2. 요청으로 온 세션 조회
     * 3. 세션 만료
     */
    @DisplayName("세션 매니저 테스트")
    @Test
    void sessionMangerTest () {
        //세션 생성 - member를 값으로 넣고 난수를 키값으로 세션 매니저에 저장 후 난수 키값을 값으로 쿠키에 넣음
        Member member = new Member();
        MockHttpServletResponse response = new MockHttpServletResponse();
        sessionManager.createSession(member, response);

        // 세션 조회 - request 서블릿에 난수키값 쿠키 세팅,
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(response.getCookies());

        Object result = sessionManager.getSession(request);
        // 조회한 세션의 값과 생성한 멤버의 값이 같은지 테스트
        Assertions.assertThat(result).isEqualTo(member);

        // 세션 만료 - 요청 서블릿에서 해당하는 쿠키를 찾아서 세션 테이블에서 제거해버림
        sessionManager.expire(request);

        Object expired = sessionManager.getSession(request);
        Assertions.assertThat(expired).isNull();
    }

}