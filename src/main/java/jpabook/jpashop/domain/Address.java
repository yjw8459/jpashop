package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
/*
    깂 타입 클래스

    @Setter를 제거하고, 생성자에서 값을 모두 초기화해서 변경 불가능한 클래스를 만듦.
    JPA 스펙 상 엔티티나 임베디드 타입은 자바 기본 생성자를 public, protected로 설정해야함(중요)
    이유 : JPA 구현 라이브러리가 객체를 생성할 때 리플랙션 또는 프록시 같은 기술을 사용할 수 있도록 지원하기 때문.

 */

@Embeddable //어딘가에 내장이 될 수 있다.
@Getter
public class Address {

    private String city;
    private String street;
    private String zipCode;

    protected Address(){    //JPA는 기본 생성자를 public이나 protected로

    }

    //Command + N 생성자, Getter, Setter 등 생성 단축키

    //값 타입은 변경이 되면 안됌.
    public Address(String city, String street, String zipCode){
        this.city = city;
        this.street = street;
        this.zipCode = zipCode;
    }
}
