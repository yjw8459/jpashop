package jpabook.jpashop.repository.order.simplequery;

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

    public OrderSimpleQueryDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address){
        this.orderId = orderId;
        this.name = name;          //가짜 프록시이므로 DB에서 데이터 끌고옴(LAZY 초기화)
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;  //가짜 프록시이므로 DB에서 데이터 끌고옴(LAZY 초기화)
    }
}