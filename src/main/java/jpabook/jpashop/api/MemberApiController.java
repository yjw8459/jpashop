package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.xml.transform.Result;
import java.util.List;
import java.util.stream.Collectors;

//Member api 컨트롤러
@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("/api/v1/members")
    public List<Member> memberV1(){
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result memberV2(){
        List<Member> findMembers = memberService.findMembers();
        List<MemberDTO> collect = findMembers.stream()  //findMembers를 Stream화
                .map(m -> new MemberDTO(m.getName()))   //요소를 Member에서 MemberDTO로 변경
                .collect(Collectors.toList())           //리턴 타입
                ;

        return new Result(collect.size(), collect);
    }

    /**
     * MemberApiController에서만 사용하는 클래스이기 때문에
     * 이너 클래스로 생성해서 사용한다.
     *
     * 엔티티를 직접 api로 전달하지 않고 DTO를 사용한다.
     * 계속 언급했듯이, 엔티티를 화면에 유출하지 않도록 한다.
     * 엔티티를 변경함으로써 api에 영향이 없도록 개발하는 것은 굉장히 중요하다.
     */
    @Data
    @AllArgsConstructor
    static class MemberDTO{
        private String name;
    }

    /**
     * 그냥 List를 반환하지 않고, Result라는 클래스로 한 번 감싸주는 이유는
     * 확장성을 고려하기 위함.
     * 예를 들어 List의 Count도 같이 반환해야 한다면,
     * Result 클래스에 변수를 추가하고 사용하면 되기 때문에 확장에 용이함.
     * @param <T>
     */
    @Data
    @AllArgsConstructor
    static class Result<T>{
        private int count;
        private T data;
    }

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
         * 실무에서는 엔티티를 외부로 노출하지 않도록 한다.
         * 이런 것을 *사이드 이펙트라고 한다.
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


    //PutMapping을 사용할 경우, 같은 데이터로 여러번 호출해도 결과는 같음.
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id,
                                               @RequestBody @Valid UpdateMemberRequest request){
        /**
         * update DTO를 따로 생성
         * 등록과 수정은 api 수정이 대부분 다르기 때문에 별도의 DTO 생성
         *
         * update 후 update한 엔티티를 바로 반환해도 괜찮지만,
         * update와 select를 따로 분리해서 사용하는 것이 가독성 측면도 좋고,
         * 단순해 보이기 때문에 따로 사용한다.
         */
        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    /**
     * CreateMemberRequest같은 DTO는 api 스펙에 맞춰서 핏하게 작성하는 것이 좋다.
     * api에 대해서는 DTO만 보면 돼서 좋다.
     * 인터페이스 개념으로 엔티티와 api 사이에 벽이라고 생각하면 될 듯.
     */
    @Data
    static class CreateMemberRequest{
        private String name;
    }

    @Data
    static class UpdateMemberRequest{
        private String name;
    }
    /**
     * 엔티티에는 lombok 어노테이션을 최소화 하고,
     * DTO는 막 사용(크게 로직 있는 것이 아니고 데이터만 왔다 갔다 하기 때문)
     * @AllArgsConstructor를 사용하는 이유는 생성자를 만들기 위함.
     * Response에만 있는 이유는 response는 return할 때 생성자를 통해 객체를 생성하고 바로 return하기 때문이고,
     * Request에 없는 이유는 Controller에서 파라미터로 받을 때 setter를 통해서 전달되기 때문이다.
     * Request에서 @AllArgsConstructor를 사용할 경우 400 Bad Request가 발생한다.
     */
    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse{
        private Long id;
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
