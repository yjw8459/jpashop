package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

//JPA가 실제 DB까지 도는 것을 확인하기 위해 DB까지 엮어서 테스트
@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    @Test
    public void 회원가입() throws Exception{
        //given -> when -> then : 테스트 절차
        //given : 주어짐
        Member member = new Member();
        member.setName("yoo");

        //when : 이렇게 처리
        Long saveId = memberService.join(member);

        //then : 결과
        assertEquals(member, memberRepository.findOne(saveId));
        //assertEquals() : member와 repository에서 찾은 member와 같을 경우
    }

    @Test
    public void 중복_회원_예외() throws Exception{
        //given

        //when

        //then
    }

}