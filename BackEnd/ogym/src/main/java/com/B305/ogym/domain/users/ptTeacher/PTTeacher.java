package com.B305.ogym.domain.users.ptTeacher;

import com.B305.ogym.domain.mappingTable.PTStudentPTTeacher;
import com.B305.ogym.domain.users.common.UserBase;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("Teacher")
@Table(name = "pt_teacher")
@PrimaryKeyJoinColumn(name = "pt_teacher_id")
public class PTTeacher extends UserBase {

//    public PTTeacher(Long id, String password, Address address, String nickname,
//        String tel, Gender gender, String email, String major, int price,
//        String description, String snsAddr){
//        this.major = major;
//        this.price = price;
//        this.description = description;
//        this.snsAddr = snsAddr;
//    }

    // 평점
    private float starRating;

    // 전문분야
    private String major;

    // 자격증 리스트
    @Builder.Default
    @OneToMany(mappedBy = "id", cascade = CascadeType.ALL)
    private List<Certificate> certificates = new ArrayList<>();

    // 가격
    private int price;

    // 자기소개
    private String description;

    // SNS 링크
    @Embedded
    private Sns sns;

    // 경력 리스트
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "pt_teacher_id")
    private List<Career> careers = new ArrayList<>();

    // 학생 리스트
    @Builder.Default
    @OneToMany(mappedBy = "ptTeacher", cascade = CascadeType.ALL)
    private Set<PTStudentPTTeacher> ptStudentPTTeachers = new LinkedHashSet<>();

    // 이력?

    //
    public void addCertificate(Certificate certificate){
        this.certificates.add(certificate);
        if(certificate.getPtTeacher() != this){
            certificate.setPtTeacher(this);
        }
    }
}
