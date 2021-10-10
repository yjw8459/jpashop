package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

//Member 도메인 엔티티로도 사용할 수 있지만 도메인에서 원하는 벨리데이션과 컨트롤러에서 원하는 벨리데이션이 다르다.
//컨트롤러에서 원하는 벨리데이션에 핏된 DTO를 따로 사용하는 것이 가독성 면에서 좋고 편리할 수 있다.
@Getter @Setter
public class MemberForm {

    //에러 시 name에 메세지가 주입됌
    @NotEmpty(message="회원 이름은 필수입니다.")
    private String name;

    private String city;
    private String street;
    private String zipcode;
}
