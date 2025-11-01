package hello.login.web;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@RequiredArgsConstructor
@Controller
public class HomeController {

    private final MemberRepository memberRepository;
    private final SessionManager sessionManager;

    //@GetMapping("/")
    public String home() {
        return "home";
    }

    //    @GetMapping("/")
    public String homeLogin(@CookieValue(name = "memberId", required = false) Long memberId, Model model) {
        if (memberId == null) return "home";

        Member loginMember = memberRepository.findById(memberId);
        if (loginMember == null) return "home";

        model.addAttribute("member", loginMember);
        return "loginHome";
    }

    /**
     * @param request : 응답 서블릿의 쿠키 값으로 저장된 세션id로 세션 테이블에 조회한 값에 따라서 화면 응답.
     * @param model
     * @return
     */
//    @GetMapping("/")
    public String homeLoginV2(HttpServletRequest request, Model model) {

        // 요청 서블릿에 온 쿠키 중에 해당하는 세션 값이 있는지 확인
        Member member = (Member) sessionManager.getSession(request);
        // 세션에 로그인 정보가 없으면 home.html으로 응답
        if (member == null) {
            return "home";
        }

        // 세션에 값이 있는 경우는 모델에 찾은 member 객체 넣어서 loginHome.html로 응답
        model.addAttribute("member", member);
        return "loginHome";
    }

    //    @GetMapping("/")
    public String homeLoginV3(HttpServletRequest request, Model model) {

        // 요청 서블릿에 온 쿠키 중에 해당하는 세션 값이 있는지 확인
        HttpSession session = request.getSession(false); // 기존 세션 없으면 새로 생성 안하고 null 반환

        if (session == null) {
            return "home";
        }

        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        // 세션에 로그인 정보가 없으면 home.html으로 응답
        if (member == null) {
            return "home";
        }

        // 세션에 값이 있는 경우는 모델에 찾은 member 객체 넣어서 loginHome.html로 응답
        model.addAttribute("member", member);
        return "loginHome";
    }

    /**
     * @param @SessionAttribute -> 없는 세션 생성 x, 세션중 해당 명의 값이 있는지 찾아줌(스프링이 세션을 쉽게 가져올수 있도록 도와주는 기능)
     * @param model
     * @return
     */
    @GetMapping("/")
    public String homeLoginV3Spring(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member
            , Model model) {

        // 요청 서블릿에 온 쿠키 중에 해당하는 세션 값이 있는지 확인
        // 세션에 로그인 정보가 없으면 home.html으로 응답
        if (member == null) {
            return "home";
        }

        // 세션에 값이 있는 경우는 모델에 찾은 member 객체 넣어서 loginHome.html로 응답
        model.addAttribute("member", member);
        return "loginHome";
    }
}