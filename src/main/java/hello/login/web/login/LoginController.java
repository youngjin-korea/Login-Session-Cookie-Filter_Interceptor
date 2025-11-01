package hello.login.web.login;

import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import hello.login.web.SessionConst;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@Controller
public class LoginController {

    private final LoginService loginService;
    private final SessionManager sessionManager;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
        return "login/loginForm";
    }

    //    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletResponse response) {
        // 필드 입력 에러시 처리
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        // 로그인 에러시 처리
        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        log.info("login 여부: '{}'", loginMember);

        if (loginMember == null) {
            // ObjectError 글로벌 오류 생성
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        // 로그인 성공 처리 - 쿠키에 시간 정보를 주지 않으면 세션 쿠키 (브라우저 종료시 모두 종료)
        Cookie idCookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
        response.addCookie(idCookie);
        return "redirect:/";
    }

    // 로그인 성공하면 세션 난수 키, 멤버 객체 값을 Map 구조 메모리에 저장
//    @PostMapping("/login")
    public String loginV2(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletResponse response) {
        // 필드 입력 에러시 처리
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        // 로그인 에러시 처리
        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        log.info("login 여부: '{}'", loginMember);

        if (loginMember == null) {
            // ObjectError 글로벌 오류 생성
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        // 로그인 성공하고 회원 세션 생성하고, 회원 데이터 보관
        sessionManager.createSession(loginMember, response);

        return "redirect:/";
    }

    /**
     * 세션 생성은 HttpServletRequest 에 getSession(true) 으로 HttpSession 생성 이때! 아규먼트로 true면 세션이 존재하면 존재하는거 리턴, 없으면 새로 생성 아규먼트가 false면 세션이 존재하면 리턴, 없으면 null리턴
     * session 에 키, 값을 session.setAttribute(key, value) 하면 JSESSIONID 라는 쿠키 이름에 난수의 값으로 쿠키 세팅되어 응답됨
     */
//    @PostMapping("/login")
    public String loginV3(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletRequest request) {
        // 필드 입력 에러시 처리
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        // 로그인 에러시 처리
        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        log.info("login 여부: '{}'", loginMember);

        if (loginMember == null) {
            // ObjectError 글로벌 오류 생성
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        // 로그인 성공하고 회원 세션 생성하고, 회원 데이터 보관
        HttpSession session = request.getSession(true); // true면 세션 없을시 새로 생성
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        return "redirect:/";
    }

    @PostMapping("/login")
    public String loginV4(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletRequest request, @RequestParam(defaultValue = "/") String redirectURL) {
        // 필드 입력 에러시 처리
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        // 로그인 에러시 처리
        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        log.info("login 여부: '{}'", loginMember);

        if (loginMember == null) {
            // ObjectError 글로벌 오류 생성
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        // 로그인 성공하고 회원 세션 생성하고, 회원 데이터 보관
        HttpSession session = request.getSession(true); // true면 세션 없을시 새로 생성
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        return "redirect:" + redirectURL;
    }

    /**
     * 만료 시킬 쿠키의 최대 시간을 0으로 다시 설정해서 응답 서블릿에 세팅 -> 쿠키 만료 기능
     *
     * @param response
     * @return
     */
//    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        expireCookie(response, "memberId");
        return "redirect:/";
    }

    /**
     * V1 에서 브라우저의 쿠키를 만료시키는 설계에서 -> V2는 서버의 세션 메모리에서 해당 세션의 값을 삭제하는 설계로 교체
     *
     * @param request
     * @return
     */
//    @PostMapping("/logout")
    public String logoutV2(HttpServletRequest request) {
        sessionManager.expire(request);
        // 세션을 만료시키면 홈으로 리다이렉트 되어도 세션 조회시 값이 null로 응답되어 home.html(로그인 안된 창)로 응답 받게 됨
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logoutV3(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // false는 세션이 기존에 없으면 새로 생성 안함
        if (session != null) {
            session.invalidate(); // 서버 메모리에 있는 세션 삭제로 로그아웃
        }
        // 세션을 만료시키면 홈으로 리다이렉트 되어도 세션 조회시 값이 null로 응답되어 home.html(로그인 안된 창)로 응답 받게 됨
        return "redirect:/";
    }

    private void expireCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
