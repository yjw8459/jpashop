package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)      //클래스 레벨에 Transactional을 사용할 경우 public은 모두 포함
//@AllArgsConstructor  생성자를 만들어줌
@RequiredArgsConstructor    //방법 2 : final이 있는 필드만 생성자를 만들어줌 (권장)
//JPA가 조회하는 곳에서는 성능을 조금 더 최적화함.
public class MemberService {

    //@Autowired   스프링 최신버전에는 생성자가 하나만 있는 경우 자동으로 Injection을 해준다.
    private final MemberRepository memberRepository;    //바꿀 일이 없기 때문에 final로 사용하는 것을 권장
/*
    방법 1
    public MemberService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }
*/

    //회원 가입
    @Transactional
    public Long join(Member member){
        try{
            validateDuplicateMember(member);    //중복 회원 검증
        }catch (Exception e){
            e.printStackTrace();
        }
        memberRepository.save(member);
        return member.getId();  //값이 없을 수 없음.
    }

    private void validateDuplicateMember(Member member) throws IllegalAccessException {
        List<Member> findMembers = memberRepository.findbyName(member.getName());
        if ( !findMembers.isEmpty() ){
            throw new IllegalAccessException("이미 존재하는 회원입니다.");
        }
    }

    //회원 전체 조회
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }
}
