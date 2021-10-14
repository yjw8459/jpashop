package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//상품 처리 서비스
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item){
        itemRepository.save(item);
    }

//    @Transactional
//    public void updateItem(Long itemId, Book param){
//
//        //추 후에 수정하기.
//        //아래 코드는 예제의 경우 코드이다.
//        // 실무에서는 단발 set으로 사용하기 보다 findItem.change(price, name, stockQuantity)처럼 의미있는 메서드를 사용한다.
//        //즉, setter를 사용하지 않는 것이 좋다.(엔티티의 경우)
//        Item findItem = itemRepository.findOne(itemId); //findOne으로 찾아온 엔티티는 영속상태의 엔티티
//        findItem.setPrice(param.getPrice());            //파라미터의 값으로 값을 변경
//        findItem.setName(param.getName());
//        findItem.setStockQuantity(param.getStockQuantity());
//
//    }   //라인이 끝날 경우 스프링의 Transactional에 의해서 트랜잭션이 커밋됌.
//        //커밋이 되면 JPA는 flush를 날림. flush : 영속성 컨텍스트에 있는 엔티티 중에 변경된 게 있는지 찾음. 바뀐 값을 DB에 UPDATE


    //위 코드의 수정
    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity){

        //필요한 데이터만 가지고 오는 것이 좋다.
        Item findItem = itemRepository.findOne(itemId);
        findItem.setName(name);
        findItem.setPrice(price);
        findItem.setStockQuantity(stockQuantity);

    }   //라인이 끝날 경우 스프링의 Transactional에 의해서 트랜잭션이 커밋됌.
    //커밋이 되면 JPA는 flush를 날림. flush : 영속성 컨텍스트에 있는 엔티티 중에 변경된 게 있는지 찾음. 바뀐 값을 DB에 UPDATE

    public List<Item> findItems(){
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId){
        return itemRepository.findOne(itemId);
    }

}
