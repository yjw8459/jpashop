package jpabook.jpashop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication	//동 레벨 컴포넌트와 하위 디렉터리에 컴포넌트를 전체 스캔함.
public class JpashopApplication {
	//성능이슈, 스프링부트에서 jsp를 권장하지 않음 : Thymeleaf 사용
	//Terminal -> 프로젝트 경로 -> ./gradlew dependencies : 의존 관계들이 쭉 나옴
	//HikariCP : Connection Pool
	//slf4j - logback 사용한다. (대부분)
    /*
        ThymeLeaf : 1. 마크업을 깨지 않음. (Natural Templete)
                    2. FreeMarker, jsp는 웹 브라우저에서 열리지 않는데, Thymeleaf같은 경우는 열림
                    3. 매핑 : resources:templetes/ + {ViewName} +.html

        스프링 부트 Thymeleaf viewName 매핑
        resources:templetes/ + {ViewName} +.html
     */

	public static void main(String[] args) {
		SpringApplication.run(JpashopApplication.class, args);
	}

}
