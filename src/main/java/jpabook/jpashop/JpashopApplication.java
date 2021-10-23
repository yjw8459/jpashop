package jpabook.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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

	/**
	 * 기본 설정으로 사용함.
	 * LAZY로딩을 호출해서 정상적으로 프록시가 초기화된(데이터가 로딩된), api만 출력한다.
	 * @return
	 */
	@Bean	//사용시 fetchType이 LAZY일 경우 조회안함.
	Hibernate5Module hibernate5Module(){
		return new Hibernate5Module();
	}


	/**
	 * 아래의 경우를 추천하지 않음.
	 * 1. 불필요한 쿼리들이 다 날아감.
	 * 2. 엔티티 정보가 api에 그대로 노출됌
	 * 3. 엔티티 정보가 바뀔경우 api 스펙도 바뀌는 위험도 있음.
	 */

//	@Bean	FORCE_LAZY_LOADING 사용 시 지연로딩일 경우도 다조회해서 가져옴
//	Hibernate5Module hibernate5Module(){
//		Hibernate5Module hibernate5Module = new Hibernate5Module();
//		hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true);
//		return hibernate5Module;
//	}

}
