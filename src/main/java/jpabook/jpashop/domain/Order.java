package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name ="orders")
@Getter @Setter
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id") //foreign 키 UPDATE를 하는 것이 주인. JPA 규약 다대일관계에서.
    private Member member;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItem = new ArrayList<>();

    /*
    *   1:1 관계에서는 FK를 양쪽 중 아무 쪽에 둬도 상관없음
    *   두는 곳에 따라 장단점이 있음.
    *   엑세스를 많이하는 곳에 두는 것이 좋다.
    *   연관관계의 주인임.
    * */
    @OneToOne
    @JoinColumn(name = "delevery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; //자바 8 이상에선 LocalDateTime을 사용할 경우 Hibernate에서 날짜 매핑해줌

    @Enumerated(EnumType.STRING)
    private OrderStatus status;    //[ORDER, CANCEL]



}
