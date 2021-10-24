package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

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

    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit){
        /**
         * Order, Member, Delivery를 페치조인해서 가져옴.
         * ToOne 관계이기 때문에 한번에 땡겨옴.
         * OrderItem을 루프를 돌면서 컬렉션 조회를 하는데 OrderItem아래 item이 복수개면 복수개만큼 Item을 조회
         *
         * application.yml 파일에 hibernate.default_batch_fetch_size : 100(in query의 갯수)
         * in query : Order 3건일 때 size: 2라면 Order의 일대다 관계인 OrderItem을 최대 2건까지 인쿼리로 미리 조회한다.
         * fetch_size 옵션을 줄 경우 findAllWithMemberDelivery를 조회했을 때, 연관된 OrderItem을
         * 미리 조회해서 가져온다.
         *
         * 정리 : 처음은 fetch join으로 조회한 Order, Member, Delivery를 조회한다. (ToOne 우선 조회)
         *  그 후 fetch join으로 가져온 컬렉션만큼 OrderItem을 가져온다.             (컬렉션 지연로딩)
         *  마지막으로 OrderItem에 연관된 Item을 가져온다.
         *  default_batch_fetch_size가 가져오는 기능
         *  즉, 1 + n + m의 쿼리 갯수가 1 + 1 + 1이 된다.
         *  테이블 단위 쿼리가 나가면서 데이터를 딱 딱 집어오기 때문에 중복이 없다.
         *  페이징을 사용할 수 있다.
         *
         *  일대다 페이징
         *      ToOne을 먼저 조회한 후, 다에 해당하는 것은 default_batch_fetch_size 옵션에 맡긴다.
         *
         * V3.1까지 이해하면 조회 최적화의 90퍼센트정도는 해결할 수 있다.
         */
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);

        /**
         * order, member, delivery는 조회가 되었기 때문에 두번 째 루프부터는 조회하지 않음.
         * OrderItem은 OrderItem X Item 만큼 나감.
         *
         */
        List<OrderDto> result = orders.stream().map(OrderDto::new).collect(Collectors.toList());
        return result;
    }

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4(){
        return orderQueryRepository.findOrderQueryDtos();
    }

    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5(){
        return orderQueryRepository.findAllByDto_optimization();
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
            orderItems = order.getOrderItems().stream()
                    .map(OrderItemDto :: new).collect(Collectors.toList());
        }
    }

}
