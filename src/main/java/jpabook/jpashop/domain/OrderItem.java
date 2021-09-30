package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  //protected를 생성자를 만들어줌  Protected로 제약하는 경우 new 생성 방지 때문
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice;     //주문 당시 가격
    private int count;          //주문 당시 수량

//    protected OrderItem(){
//
//    }

    //==생성 메서드==//
    public static OrderItem createOrderItem(Item item, int orderPrice, int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count);
        return orderItem;
    }

    //==비즈니스 로직==//
    public void cancel() {
        //JPA의 장점
        //도메인의 값을 수정할 경우 JPA가 더티체킹을 통해서 알아서 값을 수정하는 UPDATE 쿼리를 날려줌
        //더티체킹 : 변경내역 감지
        getItem().addStock(count);
    }

    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }

}
