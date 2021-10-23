package jpabook.jpashop.repository.order.simplequery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    private final EntityManager em;

    public List<OrderSimpleQueryDto> findOrderDtos() {
        /**
         * 가급적 Controller > Service > Repository 한 방향으로 흘러야한다.
         * 엔티티나 ValueObject(Embedable)만 반환할 수 있음.(JPA)
         * DTO로 반환하려면 new 로 생성해서 파라미터를 다 넣어줘야함.
         *
         * Select 절의 Address는 객체이지만 Value타입이기 때문에 조회 가능하다.(@Embeddable)
         * Address를 조회할 경우 city, street, zipcode를 조회해서 객체에 꽂아줌
         *
         * 주의할 점, api 스펙이 바뀔경우 같이 수정해야한다.
         */
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        "from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderSimpleQueryDto.class
        ).getResultList();
    }
}
