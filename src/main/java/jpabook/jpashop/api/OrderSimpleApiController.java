package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.OrderSimpleQueryDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 연관관계 매핑
 * xToOne
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    @GetMapping("api/v1/simple-orders")
    public List<Order> ordersV1(){
        /**
         * 전체조회, 엔티티를 다이렉트로 api로 보냄
         * 이렇게 조회할 경우 무한루프에 빠짐.(계속 조회)
         * Order에서 Member를 뿌려야 하기 때문에 Member로 감
         * Member에서 다시 Order를 만남. -> 무한루프
         * Jackson 라이브러리는 Order로 갔다가 Member로 갔다가 api를 찾아서 무한루프에 빠짐.
         *
         * 이 경우 반대쪽, (Member, OrderItem, Delivery) 에 @JsonIgnore 어노테이션을 해야함.
         *
         * @JsonIgnore를 할 경우 다시 에러가 나옴.
         *
         * Order로 갔는데 fetch가 LAZY(지연로딩)일 경우 null을 넣어둘 수 없기 때문에
         * 가짜 프록시 Member 객체를 생성해서 넣어놓음(ByteBuddy 라이브러리의 ByteBuddyInterceptor).
         * * 자세한 프록시에 대한 설명은 JPA 프록시 공부할 것.
         *
         * Order에서 member로 갔을 때 Member는 가짜 프록시 객체이고 ByteBuddy 에러 발생.
         *
         * 이 경우 fetchType이 LAZY인 경우 Jackson 라이브러리에게 아무것도 넘기지 말라. 라고 명시할 수 있는
         * Hibernate5Module을 Bean으로 생성해서 사용해야함.
         *
         */
        List<Order> all = orderRepository.findAll(new OrderSearch());
        return all;
    }

    @GetMapping("api/v2/simple-orders")
    public List<OrderSimpleQueryDto> ordersV2(){
        /**
         * (SimpleOrderDto :: new) : 람다 레퍼런스
         * .map() : A를 B로 바꿀 때
         * api 스펙에 핏하게 맞춘 DTO를 Return
         * 변경할 경우 변경해야 할 부분들에 컴파일 에러가 발생하게 하는 것이 가장 좋다.
         *
         * v2의 경우는 데이터 조회도 OK, DTO반환도 OK지만 Hibernate에서 너무 많은 쿼리가 나간다.
         * 테이블 3개를 건들기 때문.
         * 나가는 쿼리 : member, Order, Delivery
         * 나가는 쿼리가 많은 이유는 SimpleOrderDto의 생성자에서
         * 가짜 프록시 엔티티에서 get을 호출하기 때문에 DB에서 데이터를 끌고옴(Member, Delivery)
         * 즉 Order 쿼리는 한번 돌지만, Member, Delivery는 데이터 하나당 한번씩 나간다. (ex : 1 = 3, 2 = 5)
         * 지연로딩은 DB에서 바로 조회하는 것이 아니라, 영속성 컨텍스트를 확인한다.
         * Order를 조회하고 주문 Member가 영속성 컨텍스트에 존재하는 엔티티라면 DB를 조회하지 않고,
         * 영속성 컨텍스트에 있는 엔티티를 사용한다.
         */
        List<Order> orders = orderRepository.findAll(new OrderSearch());
        List<OrderSimpleQueryDto> result = orders.stream().map(OrderSimpleQueryDto :: new)
                .collect(Collectors.toList());

        return result;
    }

    @GetMapping("api/v3/simple-orders")
    public List<OrderSimpleQueryDto> ordersV3(){
        /**
         * v2의 findAll 조회를 fetch join으로 바꿈
         */
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<OrderSimpleQueryDto> result = orders.stream().map( OrderSimpleQueryDto :: new ).collect(Collectors.toList());
        return result;
    }

    @GetMapping("api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4(){
        return orderRepository.findOrderDtos();
    }


}
