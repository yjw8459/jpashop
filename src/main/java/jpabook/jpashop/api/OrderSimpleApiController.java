package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
         */
        List<Order> all = orderRepository.findAll(new OrderSearch());
        return all;
    }
}
