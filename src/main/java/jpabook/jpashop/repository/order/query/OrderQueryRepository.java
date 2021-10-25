package jpabook.jpashop.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Api에 의존되는 쿼리는 따로 서브 디렉토리에 레포지토리 생성함.
 */
@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos() {
        /**
         *  jpql로 DTO를 바로 반환하도록 코드를 짜더라도, 컬렉션을 반환할 순 없기 때문에, OrderItem은 생성자로 만들 수 없다.
         *  그래서 Order를 조회하고 OrderItem을 따로 조회해서 넣어주는 방식이다.
         *
         *  실행 순서
         *   먼저 Order를 조회하고, Order 루프를 돌면서 OrderItem을 setting해줌
         *
         *  정리
         *  ToOne관계는 조인으로 최적화하기 쉬우므로 한번에 조회하고, ToMany 관계는 최적화하기 어려우므로
         *  별도의 메소드를 사용해서 조회한다.
         *  N + 1의 문제 발생
         */
        List<OrderQueryDto> result = findOrders();  //query 1번 -> N개

        result.stream().forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());    //Query N번
            o.setOrderItems(orderItems);  //Order에 OrderItem을 채워줌
        });
        return result;
    }

    public List<OrderQueryDto> findAllByDto_optimization() {
        List<OrderQueryDto> result = findOrders();

        /**
         * 주문의 id를 가져와서 stream을 돌면서 map으로 Order쿼리의 ID를 뽑아서 OrderIDList로 만듬
         */
        List<Long> orderIds = result.stream().map(o -> o.getOrderId()).collect(Collectors.toList());

        /**
         * Order쿼리의 ID를 OrderItem 조회 쿼리에 리스트로 넘겨줌. IN절에 들어감.
         * @Return OrderItemList
         */
        List<OrderItemQueryDto> orderItems = em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id in :orderIds", OrderItemQueryDto.class
        ).setParameter("orderIds", orderIds).getResultList();

        /**
         * 찾아온 OrderItemList를 orderItemQueryDto.getOrderId()를 기준으로 그루핑 함.
         * key는 OrderId, Value는 OrderItemQueryDto
         * Order의 아이디 값 하나에 여러 개의 OrderItems
         */
        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream().
                                                    collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));

        /**
         * 메모리에서 매칭을 해서 값을 세팅해줌.
         * OrderId와 같은 것들 찾아서 꽂아줌 orderItemMap.get(o.getOrderId()))
         */
        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        /**
         * oi.order.id : OrderItem의 ID를 가져올 때, OrderItem에서 Order의 id를 가져온다.
         *               외래키는 Order에 있기 때문
         * OrderItem 과 Item을 조인하는 이유는 OrderItem과 Item은 ToOne 관계이기 때문이다.
         * Order를 ToOne으로 조회, OrderItem을 ToOne으로 조회
         */
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id = :orderId", OrderItemQueryDto.class
        ).setParameter("orderId", orderId).getResultList();
    }

    private List<OrderQueryDto> findOrders() {
        /**
         * fetch join이 아닌 그냥 join
         */
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderQueryDto.class
        ).getResultList();
    }

}
