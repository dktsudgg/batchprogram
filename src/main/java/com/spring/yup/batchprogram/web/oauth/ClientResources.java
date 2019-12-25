package com.spring.yup.batchprogram.web.oauth;

import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;

public class ClientResources {

    // @NestedConfigurationProperty == 해당 필드가 단일값이 아닌 중복으로 바인딩된다고 표시하는 어노테이션..

    // AuthorizationCodeResourceDetails == 프로퍼티 설정한 각 소셜의 프로퍼티 값 중 'client'를 기준으로 하위의 키/값을 매핑해주는 대상 객체
    // ResourceServerProperties == 원래 Oauth2 리소스 값을 매핑하는 데 사용하지만, 예제에서는 회원 정보를 얻는 userInfoUri값을 받는 데 사용함..

    @NestedConfigurationProperty
    private AuthorizationCodeResourceDetails client = new AuthorizationCodeResourceDetails();

    @NestedConfigurationProperty
    private ResourceServerProperties resource = new ResourceServerProperties();

    public AuthorizationCodeResourceDetails getClient(){
        return client;
    }

    public ResourceServerProperties getResource(){
        return resource;
    }

}
