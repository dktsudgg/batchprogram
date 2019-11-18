package com.spring.yup.batchprogram.aboutconfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

// @Data 어노테이션은 롬복 설정임..
// 롬복 사용하기 위해서 프로젝트에 롬복 설정하는 방법!!!!!
// 먼저 롬복 홈페이지가서 gradle의존성 추가하는 그루비 코드 사용하고, 인텔리제이에서 롬복 플러그인 설치한 뒤에,
// Preferences -> Build, Execution, Deployment -> 컴파일러 -> 어노테이션 프로세서
// 로 가서 enable 어노테이 프로세싱 해줘야됨..

@Data
@Component
@ConfigurationProperties("fruit")
public class FruitProperty {
    private List<Fruit> list;

    private String testProperty;

    private String testProperty2;

    private String testProperty3;

    private String testProperty4;
}
