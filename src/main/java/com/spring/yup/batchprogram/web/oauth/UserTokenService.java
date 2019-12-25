package com.spring.yup.batchprogram.web.oauth;

import com.spring.yup.batchprogram.web.domain.enums.SocialType;
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.List;
import java.util.Map;

// User정보를 얻어오기 위해 소셜 서버와 통신하는 역할을 수행..
// 이때 URI와 clientId정보가 필요..
public class UserTokenService extends UserInfoTokenServices {

    public UserTokenService(ClientResources resources, SocialType socialType){
        super(resources.getResource().getUserInfoUri(), resources.getClient().getClientId());
        setAuthoritiesExtractor(new OAuth2AuthoritiesExtractor(socialType));
    }

    public static class OAuth2AuthoritiesExtractor implements AuthoritiesExtractor {

        private String socialType;

        public OAuth2AuthoritiesExtractor(SocialType socialType){
            this.socialType = socialType.getRoleType();
        }

        @Override
        public List<GrantedAuthority> extractAuthorities(Map<String, Object> map) {
            return AuthorityUtils.createAuthorityList(this.socialType);
        }
    }

}
