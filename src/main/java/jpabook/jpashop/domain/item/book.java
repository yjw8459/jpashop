package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("B")    //저장할 때 구분할 값
@Getter @Setter
public class book extends Item{

    private String author;
    private String isbn;

}
