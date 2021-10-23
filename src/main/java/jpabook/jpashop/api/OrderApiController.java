package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1(){
        /**
         * Order와 OrderItem을 같이 조회한다.
         * get을 통해서 프록시를 강제 초기화시킨다.
         *
         * 엔티티를 api로 직접 노출하는 방법
         */
        List<Order> orders = orderRepository.findAll(new OrderSearch());
        for (Order order:
             orders) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach( o -> o.getItem().getName());
        }

        /**
         * 가짜 프록시 객체이기 때문에 get을 사용해서 영속성 컨텍스트를 건드려줌.
         * get이 호출됐으므로 JPA는 출력을 감지하고 데이터를 DB에서 찾아옴.
         */
        return orders;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2(){
        /**
         * Dto로 변환해서 -> api
         *
         * DTO로 api에 보낸다는 것은 단순히 감싸라는 의미가 아니라 엔티티와의 의존을 완전히 끊어야함.
         * 즉, Order만 Dto로 바꾸는 것이 아니라, OrderItems도 Dto로 감싸서 반환한다.
         *
         * fetch join이 아니고 지연로딩도 많이 걸리기 때문에, 많은 쿼리가 DB로 날아간다.
         */
        List<Order> orders = orderRepository.findAll(new OrderSearch());
        List<OrderDto> result = orders.stream().map(OrderDto::new).collect(Collectors.toList());
        return result;
    }

    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithItem();
        /**
         * v3는 v2에서 fetch join으로 데이터를 조회하고, dinstinct를 통해서 중복제거를
         * 작업한 상태.
         */

        List<OrderDto> result = orders.stream().map(OrderDto::new).collect(Collectors.toList());
        return result;
    }


    @Getter
    static class OrderItemDto{
        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem){
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
    /**
     * No properties 에러 : getter, setter가 없기 때문.
     * 생성자를 사용할 경우는 @Setter는 빼도록 한다.
     */
    @Getter
    static class OrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order){
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            order.getOrderItems().stream().forEach(o -> o.getItem().getName()); //프록시 초기화
            orderItems = order.getOrderItems().stream().map(OrderItemDto :: new).collect(Collectors.toList());
        }
    }

}
