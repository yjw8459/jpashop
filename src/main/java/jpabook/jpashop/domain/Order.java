package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//주문 엔티티
@Entity
@Table(name ="orders")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") //foreign 키 UPDATE를 하는 것이 주인. JPA 규약 다대일관계에서.
    private Member member;
    /*
        CascadeType.ALL

        Cascade 사용하지 않을 경우
        persist(orderItemA)
        persist(orderItemB)
        persist(orderItemC)
        persist(order)

        Cascade를 사용할 경우
        persist(order)

        cascade는 persist를 전파한다.
        delete도 전체 delete됨
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    /*
    *   1:1 관계에서는 FK를 양쪽 중 아무 쪽에 둬도 상관없음
    *   두는 곳에 따라 장단점이 있음.
    *   엑세스를 많이하는 곳에 두는 것이 좋다.
    *   연관관계의 주인임.
    *
    *   delivery에 Cascade 옵션이 있기 때문에 order persist시 같이 persist됨
    * */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delevery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; //자바 8 이상에선 LocalDateTime을 사용할 경우 Hibernate에서 날짜 매핑해줌

    @Enumerated(EnumType.STRING)
    private OrderStatus status;    //[ORDER, CANCEL]

    /*

      연관관계 편의 메서드
        연관관계 메서드는 컨트롤 하는 쪽이 가지고 있는 것이 좋다.
        member를 set하고 member에 order 추가
     */
    public void setMember(Member member){
        this.member = member;
        member.getOrders().add(this);
    }

    /*
        연관관계 편의 메서드
       OrderItem을 리스트에 추가하고 orderItem에 Order 추가
     */
    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    /*
        연관관계 편의 메서드
     */
    public void setDelivery(Delivery delivery){
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //== 생성 메서드 ==//
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem: orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }
    //== 비즈니스 로직==//
    /**
     * 주문 취소
     */
    public void cancel(){
        if ( delivery.getStatus() == DeliveryStatus.COMP ){
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }

        //상태를 CANCEL로 바꾸고 따로 save를 하지 않아도 변경감지를 통해서 UPDATE 쿼리를 JPA가 날려주고 트랜잭션 커밋함
        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : orderItems){
            orderItem.cancel();
        }
    }

    //==조회 로직==//

    /**
     * 전체 주문 가격 조회
     * @return
     */
    public int getTotalPrice(){
        //option + enter로 다작업화 코드로 변환
//        int totalPrice = orderItems.stream().mapToInt(OrderItem::getTotalPrice).sum(); //stream 방식
//        for (OrderItem orderItem : orderItems) totalPrice += orderItem.getTotalPrice();
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems){
            totalPrice += orderItem.getTotalPrice();
        }

        return totalPrice;
    }

}
