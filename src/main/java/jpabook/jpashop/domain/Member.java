package jpabook.jpashop.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue //GeneratedValue를 쓸 경우 Seq 값으로 사용
    @Column(name = "member_id") //Column명을 지정하지 않을경우 id라는 변수 이름 그대로 사용
    private long id;

    private String name;

    @Embedded   //Embedded, Embedable 둘 중 하나만 있어도 상관없음
    private Address address;

    @OneToMany(mappedBy = "member") //order 테이블의 mem용ber필드에 의해서 매핑된 것.(매핑된 거울, 읽기전)
    private List<Order> orders = new ArrayList<>();

}
