package com.spring.yup.batchprogram.web.config;

import com.spring.yup.batchprogram.web.oauth.ClientResources;
import com.spring.yup.batchprogram.web.domain.enums.SocialType;
import com.spring.yup.batchprogram.web.oauth.UserTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.CompositeFilter;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;

import static com.spring.yup.batchprogram.web.domain.enums.SocialType.FACEBOOK;
import static com.spring.yup.batchprogram.web.domain.enums.SocialType.GOOGLE;
import static com.spring.yup.batchprogram.web.domain.enums.SocialType.KAKAO;

@Configuration
@EnableWebSecurity  // 시큐리티 기능을 사용하겠다는 어노테이션..
@EnableOAuth2Client // 나는 권한 및 User정보를 가져오는 서버를 직접 구성하지 않고 모두 각 소셜미디어의 서버를 사용하기 떄문에 @EnableAuthorizationServer, @EnableResourceServer 같은 두 어노테이션을 사용할 필요가 없음..
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private OAuth2ClientContext oAuth2ClientContext;

    // 자동 설정 그대로 사용할 수도 있지만 요청, 권한, 기타 설정에 대해서는 필수적으로 최적화한 설정이 들어가야 함.
    // 최적화 설정을 위해 WebSecurityConfigurerAdapter를 상속받고 configure(HttpSecurity http) 메소드를 오버라이드하여 원하는 형식의 시큐리티 설정을 함..
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        http
                // 인증 매커니즘을 요청한 HttpServletRequest기반으로 설정
                .authorizeRequests()
                    // 요청 패턴을 리스트 형식으로 설정.
                    .antMatchers("/", "/login/**", "/css/**", "/images/**", "/js/**", "/console/**")
                    // 설정한 리퀘스트 패턴을 누구나 접근할 수 있도록 허용
                    .permitAll()
                    // 설정한 요청 이외의 리퀘스트요청을 표현.
                    .anyRequest()
                        // 해당 요청은 인증된 사용자만 할 수 있음.
                        .authenticated()
                .and()
                    // 응답에 해당하는 헤더를 설정. 설정하지 않으면 디폴트값으로 설정됨..
                    .headers()
                        // XFrameOptionsHeaderWriter의 최적화 설정을 허용하지 않음.
                        .frameOptions().disable()
                .and()
                    .exceptionHandling()
                    // 인증의 진입 지점 설정. 인증되지 않은 사용자가 허용되지 않은 경로로 리퀘스트를 날린 경우 "/login"으로 이동됨.
                    .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
                .and()
                    // 로그인 성공하면 설정된 경로로 포워딩됨
                    .formLogin().successForwardUrl("/board/list")
                .and()
                    // 로그아웃에 대한 설정들..
                    .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
                    .deleteCookies("JSESSIONID")
                    .invalidateHttpSession(true)
                .and()
                    // 첫번째 인자보다 먼저 시작될 필터를 등록한다. 문자인코딩 필터보다 Csrf필터를 먼저 실행하도록 설정함.
                    .addFilterBefore(filter, CsrfFilter.class)
                    .addFilterBefore(oauth2Filter(), BasicAuthenticationFilter.class)
                    .csrf().disable();
    }

    // OAuth2 클라이언트용 시큐리티 필터인 OAuth2ClientContextFilter를 불러와서 올바른 순서로 필터가 동작하도록 설정함.
    // 스프링 필터가 실행되기 전에 충분히 낮운 순서로 필터를 등록
    @Bean
    public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(filter);
        registration.setOrder(-100);
        return registration;
    }

    // 각 소셜 미디어 타입을 받아 필터 설정..
    private Filter oauth2Filter(){
        CompositeFilter filter = new CompositeFilter();
        List<Filter> filters = new ArrayList<>();
        filters.add(oauth2Filter(facebook(), "/login/facebook", FACEBOOK));
        filters.add(oauth2Filter(google(), "/login/google", GOOGLE));
        filters.add(oauth2Filter(kakao(), "/login/kakao", KAKAO));
        filter.setFilters(filters);
        return filter;
    }

    // 각 소셜 미디어 필터를 리스트 형식으로 한꺼번에 설정하여 반환..
    private Filter oauth2Filter(ClientResources client, String path, SocialType socialType){
        // 인증이 수행될 경로를 넣어 OAuth2 클라이언트용 인증 처리 필터를 생성
        OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
        // 퀀한 서버와 통신을 위해서 템플릿을 생성. client프로퍼티 정보와 OAuth2ClientContext가 필요함..
        OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oAuth2ClientContext);
        filter.setRestTemplate(template);
        // User의 권한을 최적화해서 생성하고자 UserInfoTokenService를 상속받은 UserTokenService를 생성..
        // OAuth2 Access Token 검증을 위해 생성한 이 서비스를 필터의 토큰 서비스로 등록함..
        filter.setTokenServices(new UserTokenService(client, socialType));
        // 인증이 성공적으로 이루어지면 필터에 리다이렉트될 URL을 설정함..
        filter.setAuthenticationSuccessHandler((request, response, authentication)
                -> response.sendRedirect("/"+socialType.getValue()+"/complete"));
        // 인증이 실패하면 필터에 리다이렉트될 URL을 설정함..
        filter.setAuthenticationFailureHandler((request, response, exception)
            -> response.sendRedirect("/error"));
        return filter;
    }

    // 소셜 미디어 리소스 정보는 시큐리티 설정에서 사용하기 때문에 빈으로 등록했고,
    // 3개의 소셜미디어 프로퍼티를 @ConfigurationProperties 어노테이션에 접두사를 사용하여 바인딩함.. 만약 이 어노테이션이 없었다면 일일이 프로퍼티값을 불러와야했을듯..

    @Bean
    @ConfigurationProperties("facebook")
    public ClientResources facebook(){
        return new ClientResources();
    }

    @Bean
    @ConfigurationProperties("google")
    public ClientResources google(){
        return new ClientResources();
    }

    @Bean
    @ConfigurationProperties("kakao")
    public ClientResources kakao(){
        return new ClientResources();
    }

}
