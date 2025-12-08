package org.example.tamaapi.dto.feign;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.tamaapi.domain.Gender;
import org.example.tamaapi.domain.user.Authority;
import org.example.tamaapi.domain.user.Member;
import org.example.tamaapi.domain.user.MemberAddress;
import org.example.tamaapi.domain.user.Provider;
import org.example.tamaapi.dto.responseDto.member.MemberAddressesResponse;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class MemberFeignResponse {

    private String email;

    private String phone;

    private String nickname;

    private int point;

    private Gender gender;

    private Integer height;

    private Integer weight;

    private Provider provider;

    private Authority authority;

    public MemberFeignResponse(Member member) {
        this.email = member.getEmail();
        this.phone = member.getPhone();
        this.nickname = member.getNickname();
        this.point = member.getPoint();
        this.gender = member.getGender();
        this.height = member.getHeight();
        this.weight = member.getWeight();
        this.provider = member.getProvider();
        this.authority = member.getAuthority();
    }
}
