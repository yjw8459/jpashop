package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;


    //v1의 장점은 CreateMemberRequest같은 DTO를 만들지 않아도 됌
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemeberV1(@RequestBody @Valid Member member){
        /**
         * @Valid를 사용하면 자바X 벨리데이션 관련된 내용들이 자동으로 벨리데이션 된다.
         * @RequestBody를 사용하면 API 통신을 하면 JSON으로만 사용하는데
         * Json으로 온 Body를 Member에 그대로 매핑에서 넣어줌
         *
         * Member는 어떤 값이 들어올 지 모름.
         * 엔티티에 Address가 들어오든, 다른 데이터가 들어오든..
         * 하지만, CreateMemberRequest라는 DTO를 사용하면 api에서 어떤 값을 사용하는지
         * 단번에 파악하기 쉬움.
         * CreateMemberRequest같은 DTO는 api 스펙에 맞춰서 핏하게 작성하는 것이 좋다.
         */
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    //v2의 장점은 엔티티가 수정되어도 api는 전혀 영향을 받지 않음
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){
        /**
         * 별도의 데이터 트레이스 오브젝트를 사용
         */
        Member member = new Member();
        member.setName(request.getName());
        Long memberId = memberService.join(member); //가입된 회원의 id
        return new CreateMemberResponse(memberId);
    }

    /**
     * CreateMemberRequest같은 DTO는 api 스펙에 맞춰서 핏하게 작성하는 것이 좋다.
     */
    @Data
    static class CreateMemberRequest{
        private String name;
    }



    @Data
    static class CreateMemberResponse{
        private Long id;

        //생성자
        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

}
