package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Delivery {

    @Id @GeneratedValue
    @Column(name = "delivery_id")
    private long id;

    /*
    *   mappedBy : FK는 Order에 있음,
    *   mappedBy가 있으면 읽기전용의 거울임
    *   주인 : @JoinColumn
    *   거울 : mappedBy
    * */
    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Order order;

    @Embedded
    private Address address;

    /*
    *   Enum 사용 시 @Enumerater EnumType을 설정해야함.
    *   기본 값은 ORDINAL
    *   ORDINAL :   컬럼에 숫자로 들어감.것
    *               (단점, 중간에 어떤 다른 상태가 생기면 망함.
    *               숫자가 하나로 밀리면서 데이터 다 꼬임 절대사용하지 말 것. XXX로 나옴 )
    *   STRING : 중간에 상태가 생겨도 상관없음.
    * */
    @Enumerated(EnumType.ORDINAL)
    private DeliveryStatus status; //READY, COMP

}
