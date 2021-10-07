package com.B305.ogym.domain.users.common;

import com.B305.ogym.domain.users.BaseTimeEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.jsonwebtoken.Claims;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "user_base")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorColumn(name = "role")
public class UserBase extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id; // 대리 키

    private String password; // 비밀번호

    private int age; // 나이

    @Embedded
    private Address address; // 주소

    private String username; // 이름

    @Column(unique = true)
    private String nickname; // 닉네임

    private String tel; // 연락처

    @Enumerated
    private Gender gender; // 성별

    @Column(unique = true)
    private String email; // 이메일

    @Embedded
    private ProfilePicture profilePicture; // 프로필 사진

    private String authority; // 권한 정보

    @JsonIgnore
    @Transient
    private String role;

    public UserBase(Claims claims) {
        this.email = claims.get("email").toString();
        this.nickname = claims.get("nickname").toString();
        this.role = claims.get("role").toString();
    }

    public void setRole(String authority) {
        this.authority = authority;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setProfilePicture(ProfilePicture profilePicture) {
        this.profilePicture = profilePicture;
    }
}
