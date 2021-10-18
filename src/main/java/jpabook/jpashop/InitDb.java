package jpabook.jpashop;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

/**
 * 총 주문 2개
 * *userA
 *  * JPA1 BOOK
 *  * JPA2 BOOK
 *
 * *userB
 *  * SPRING BOOK
 *  * SPRING BOOK
 *
 */
@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init(){ //애플리케이션 실행 시점에 실행하기 위함.
        initService.dbInit1();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService{

        private final EntityManager em;
        public void dbInit1(){
            Member member = new Member();
            member.setName("userA");
            member.setAddress(new Address("서울", "1", "11111"));
            em.persist(member); //member를 영속상태로 만들어줌


            Book book1 = new Book();
            book1.setName("JPA1 BOOK");
            book1.setPrice(10000);
            book1.setStockQuantity(100); //재고 수량
            em.persist(book1);

            Book book2 = new Book();
            book2.setName("JPA2 BOOK");
            book2.setPrice(20000);
            book2.setStockQuantity(100); //재고 수량
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);//book1을 10000에 구매
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 1);//book2을 20000에 구매

            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());   //고객의 주소
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);//...문법으로 orderItem1, orderItem2
            //...문법은 List는 사용X
            em.persist(order);      //order 영속화
        }

    }
}
