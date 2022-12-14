package roadiary.behavior.member.service;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import roadiary.behavior.member.authority.Authority;
import roadiary.behavior.member.dto.KakaoTokenResDto;
import roadiary.behavior.member.dto.KakaoUserInfoResDto;
import roadiary.behavior.member.dto.MemberAuthorityDto;
import roadiary.behavior.member.entity.UserEntity;
import roadiary.behavior.member.repository.MemberRepository;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final Authority authority;
    private final MemberRepository memberRepository;

    public void makeLoginStatus(HttpSession session) {
        MemberAuthorityDto memberAuthorityDto = MemberAuthorityDto.of(1, "guest");
        authority.makeLoginStatus(session, memberAuthorityDto);
    }
    public String makeLoginStatus(HttpSession session, long kakaoId) {
        UserEntity userEntity = memberRepository.selectUserByUsingKakaoId(kakaoId);
        if(userEntity.getRegisterStatus().equals("withdrawal")) return "withdrawal";
        else {
            MemberAuthorityDto memberAuthorityDto = MemberAuthorityDto.of(userEntity.getUserId(), userEntity.getNickname());
            authority.makeLoginStatus(session, memberAuthorityDto);
            return "success";
        }
    }

    public void destroyLoginStatus(HttpServletRequest request) {
        // ?????? ??????
        authority.destroyLoginStatus(request);
    }

    public String getKaKaoAccessToken(String code) {

        // body ??????(KakaoTokenReqMap??? git ignore?????????.)
        MultiValueMap<String, String> kakaoTokenReqMap = KakaoTokenReqMap.of(code);

        // requestEntity ??????
        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
            .post(UriComponentsBuilder.fromUriString("https://kauth.kakao.com/oauth/token").build().toUri())
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(kakaoTokenReqMap);

        // ???????????? ??????
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<KakaoTokenResDto> response = restTemplate.exchange(requestEntity, KakaoTokenResDto.class);

        // reponse ??????
        KakaoTokenResDto kakaoTokenResDto = response.getBody();
        return kakaoTokenResDto.getAccess_token();
    }
    public KakaoUserInfoResDto getUserInfoByToken(String accessToken) {

        MultiValueMap<String, Object> kakaoUserInfoReqDto = new LinkedMultiValueMap<>();
        kakaoUserInfoReqDto.add("property_keys", "[\"kakao_account.email\"]");

        // requestEntity ??????
        RequestEntity<Object> requestEntity = RequestEntity
            .post(UriComponentsBuilder.fromUriString("https://kapi.kakao.com/v2/user/me").build().toUri())
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .header("Authorization", "Bearer " + accessToken)
            .body(kakaoUserInfoReqDto);
        
        // ???????????? ??????
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<KakaoUserInfoResDto> response = restTemplate.exchange(requestEntity, KakaoUserInfoResDto.class);

        // reponse ??????
        KakaoUserInfoResDto kakaoUserInfoResDto = response.getBody();
        return kakaoUserInfoResDto;
    }
    public boolean isItRegisteredMember(Long kakaoId) {
        int countNum = memberRepository.countKakaoIdUser(kakaoId);
        if (countNum == 1) return true;
        else return false;
    }
    public void addKakaoUser(KakaoUserInfoResDto kakaoUserInfoResDto) {
        UserEntity userEntity = UserEntity.of(kakaoUserInfoResDto);
        userEntity.setNickname("user");
        memberRepository.insertKakaoUser(userEntity);  // ????????? userId ?????????
        memberRepository.insertPriorityForNewUser(userEntity.getUserId());
    }

    public Integer withdrawalUser(long userId) {
        return memberRepository.withdrawalUser(userId);
    }
}
