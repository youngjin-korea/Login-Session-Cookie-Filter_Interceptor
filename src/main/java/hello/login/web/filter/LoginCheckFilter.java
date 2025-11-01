package hello.login.web.filter;

import hello.login.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
public class LoginCheckFilter implements Filter {

    private static final String[] whitelist = {"/", "/member/add", "/login", "/logout", "/css/*"};

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // request URI 필요하여 다운 캐스팅
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String requestURI = request.getRequestURI();

        // 서블릿 response 다운 캐스팅
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        try {
            log.info("인증 체크필터 시작 {}", requestURI);

            // 로그인 대상 path 이면 세션에  인증된 사람인지 체크
            if (isLoginCheckPath(requestURI)) {
                // 요청에 세션을 얻고 없으면 세션을 새로 생성하진 않는다.
                HttpSession session = request.getSession(false);
                if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
                    // 미인증자 로그인 페이지로 리다이렉트, 리다이렉트 하면서 쿼리 파라미터에 접근하려던 패스 추가
                    log.info("미인증자 요청 {}", requestURI);
                    response.sendRedirect("/login?redirectURL=" + requestURI);
                    return;
                }
            }
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            throw e;
        } finally {
            log.info("인증 체크 필터 종료 {}", requestURI);
        }
    }

    // 로그인 체크를 해야하는 경로인지 확인
    private boolean isLoginCheckPath(String requestPath) {
        return !PatternMatchUtils.simpleMatch(whitelist, requestPath);
    }

}
