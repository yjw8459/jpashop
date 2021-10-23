package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class OrderSimpleQueryDto{
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;

    public OrderSimpleQueryDto(Order order){
        orderId = order.getId();
        name = order.getMember().getName();          //가짜 프록시이므로 DB에서 데이터 끌고옴(LAZY 초기화)
        orderDate = order.getOrderDate();
        orderStatus = order.getStatus();
        address = order.getDelivery().getAddress();  //가짜 프록시이므로 DB에서 데이터 끌고옴(LAZY 초기화)
    }
}