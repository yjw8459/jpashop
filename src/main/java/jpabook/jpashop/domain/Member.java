package jpabook.jpashop.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

//회원 엔티티
@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue //GeneratedValue를 쓸 경우 Seq 값으로 사용
    @Column(name = "member_id") //Column명을 지정하지 않을경우 id라는 변수 이름 그대로 사용
    private long id;

    /**
     * 값이 비어있으면 안된다.
     * 즉, @Valid 어노테이션이 있을 경우 해당 어노테이션을 사용
     * 어떤 엔티티에서는 값이 비어있을 수 있고 어떤 엔티티는 값이 비어있지 않을 수 있기 때문에
     * @NotEmpty는 화면 단에서 데이터를 받는 DTO에 달아주는 것이 좋다.
     */
    @NotEmpty
    private String name;    //회원 명

    @Embedded   //Embedded, Embedable 둘 중 하나만 있어도 상관없음
    private Address address;

    /**
     * @JsonIgnore
     * json 데이터로 parsing할 때 제외 데이터
     */
    //@JsonIgnore
    @OneToMany(mappedBy = "member") //order 테이블의 mem용ber필드에 의해서 매핑된 것.(매핑된 거울, 읽기전)
    private List<Order> orders = new ArrayList<>();

}
