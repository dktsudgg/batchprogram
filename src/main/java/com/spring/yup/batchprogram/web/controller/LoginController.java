package com.spring.yup.batchprogram.web.controller;

import com.spring.yup.batchprogram.web.annotation.SocialUser;
import com.spring.yup.batchprogram.web.domain.User;
import com.spring.yup.batchprogram.web.domain.enums.SocialType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    // 인증이 성공적으로 처리된 이후에 리다이렉트되는 경로..
    @GetMapping(value = "/{facebook|google|kakao}/complete")
    public String loginComplete(@SocialUser User user){
        // AOP를 이용하여 특정한 파라미터 형식을 취하면 병렬적으로 User객체에 인증된 정보를 가져올 수 있음. AOP로직을 만들어놓으면 User정보를 가져오는 방법에 신경 쓸 필요가 없게 되는 것..
        // 파라미터로 AOP를 구현하는 데에는 두가지 방법이 있음.. 직접AOP로직을 작성하거나 스프링의 전략 인터페이스중 하나인 HandlerMethodArgumentResolver를 사용하는 방법..
        return "redirect:/board/list";
    }
    /*public String loginComplete(HttpSession session){
        // SecurityContextHoler에서 인증된 정보를 OAuth2Authentication형태로 받아옴.. 이것은 기본적인 인증에 대한 정보뿐아니라 OAuth2인증과 관련된 정보도 함께 제공함..
        OAuth2Authentication authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        // 리소스서버에서 받아온 개인정보를 Map타입으로 받아옴..
        Map<String, String> map = (HashMap<String, String>) authentication.getUserAuthentication().getDetails();
        // 세션에다가 인증된 유저정보를 저장..
        session.setAttribute("user", User.builder()
                .name(map.get("name"))
                .email(map.get("email"))
                .principal(map.get("id"))
                .socialType(SocialType.FACEBOOK)
                .createdDate(LocalDateTime.now())
                .build()
        );
        return "redirect:/board/list";
    }*/
}
