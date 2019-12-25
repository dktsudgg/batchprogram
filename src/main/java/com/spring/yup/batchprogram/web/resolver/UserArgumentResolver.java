package com.spring.yup.batchprogram.web.resolver;

import com.spring.yup.batchprogram.web.annotation.SocialUser;
import com.spring.yup.batchprogram.web.domain.User;
import com.spring.yup.batchprogram.web.domain.enums.SocialType;
import com.spring.yup.batchprogram.web.repository.UserRepository;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.spring.yup.batchprogram.web.domain.enums.SocialType.*;

@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    private UserRepository userRepository;

    // HandlerMethodArgumentResolver 인터페이스는 다음 두 메소드를 제공함..
    // supportsParameter - HandlerMethodArgumentResolver가 해당하는 파라메타를 지원할 지 여부를 반환.. true이면 resolveArgument메서드가 수행됨..
    // resolveArgument - 파라미터 인잣값의 대한 정보를 바탕으로 실제 객체를 생성하여 해당 파라미터 객체에 바인딩함.

    public boolean supportsParameter(MethodParameter parameter){
        // MethodParameter로 해당 파리미터의 정보를 받게 됨. 이제 파라미터에 @SocialUser 어노테이션이 있고, 타입이 User인 파라미터만 true를 반환할 것임..
        // supportsParameter에서 처음 한번 체크된 부분은 캐시되어 이후의 동일한 호출 시에는 체크되지 않고 캐시된 결과값을 바로 반환함..
        return parameter.getParameterAnnotation(SocialUser.class) != null && parameter.getParameterType().equals(User.class);
    }

    public Object resolveArgument(MethodParameter parameter
            , ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception{
        // resolveArgument메서드는 검증이 완료된 파라미터 정보를 받음.. 이미 검증이 되어 세션에 해당 User객체가 있으면 User객체를 구성하는 로직을 수행하지 않도록
        // 세션을 먼저 확인하는 코드를 구현.. 세션은 RequestContextHolder를 통해서 가져올 수 있음.

        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession();

        User user = (User)session.getAttribute("user");

        // getUser - 세션에서 가져온 User객체가 없으면 새로 생성하고 이미 있다면 바로 사용하도록 반환함..
        return getUser(user, session);
    }

    // 인증된 User객체를 만드는 메인 메서드
    private User getUser(User user, HttpSession session) {
        if(user == null){   // 세션에 유저 정보가 없을 때만 실행됨..
            try{
                // SecurityContextHolder를 사용해 인증된 OAuth2Authentication객체를 가져옴..
                OAuth2Authentication authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
                // 불러온 OAuth2Authentication객체에서 getDetails메소드를 사용하여 사용자 개인정보를 Map타입으로 매핑.
                Map<String, String> map = (HashMap<String, String>)authentication.getUserAuthentication().getDetails();

                // 어떤 소셜 미디어로 인증을 받았는지 String.valueOf(authentication.getAuthorities().toArray()[0]) 으로 불러옴..
                // 이전에 넣어주었던 권한이 하나뿐이라서 배열의 첫번째 값만 불러오도록 작성함..
                User convertUser = convertUser(String.valueOf(authentication.getAuthorities().toArray()[0]), map);

                // 소셜에서 항상 이메일 정보를 제공한다는 전제하에 작성함!!!!!
                // 이메일을 사용해 이미 DB에 저장된 사용자라면 바로 User객체를 반환하고, 저장되지 않은 사용자라면 User테이블에 저장하는 로직을 수행..
                user = userRepository.findByEmail(convertUser.getEmail());
                if(user == null) { user = userRepository.save(convertUser); }

                setRoleIfNotSame(user, authentication, map);
                session.setAttribute("user", user);

            } catch(ClassCastException e){
                return user;
            }
        }
        return user;
    }

    // 사용자의 인증된 소셜 미디어 타입에 따라 빌더를 사용하여 User객체를 만들어 주는 가교 역할을 하는 메소드.. 카카오의 경우 별도의 메서드 사용.
    private User convertUser(String authority, Map<String, String> map){
        if(FACEBOOK.isEquals(authority)) return getModernUser(FACEBOOK, map);
        else if(GOOGLE.isEquals(authority)) return getModernUser(GOOGLE, map);
        else if(KAKAO.isEquals(authority)) return getKakaoUser(map);
        return null;
    }

    // 페이스북이나 구글과 같이 공통되는 명명규칙을 가진 그룹을 User객체로 매핑해줌.
    private User getModernUser(SocialType socialType, Map<String, String> map){
        return User.builder()
                .name(map.get("name"))
                .email(map.get("email"))
                .principal(map.get("id"))
                .socialType(socialType)
                .createdDate(LocalDateTime.now())
                .build();
    }

    // 키의 네이밍이 타 소셜 미디어와 달라서 메소드를 따로 제작함.. 카카오 회원을 위한 메서드.. 기능은 getModernUser과 동일하게 User객체로 매핑하는 일..
    private User getKakaoUser(Map<String, String> map){
        HashMap<String, String> propertyMap = (HashMap<String, String>)(Object)map.get("properties");
        return User.builder()
                .name(propertyMap.get("nickname"))
                .email(map.get("kaccount_email"))
                .principal(String.valueOf(map.get("id")))
                .socialType(KAKAO)
                .createdDate(LocalDateTime.now())
                .build();
    }

    // 인증된 authentication이 권한을 갖고 있는지 체크하는 용도로 쓰임.. 만약, 저장된 User권한이 없으면 SecurityContextHolder를 사용하여, 해당 소셜미디어 타입으로 권한을 저장함.
    private void setRoleIfNotSame(User user, OAuth2Authentication authentication, Map<String, String> map){
        if( !authentication.getAuthorities().contains(new SimpleGrantedAuthority(user.getSocialType().getRoleType()))){
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(map, "N/A", AuthorityUtils.createAuthorityList(user.getSocialType().getRoleType()))
            );
        }
    }

}
