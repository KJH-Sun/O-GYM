package com.B305.ogym.domain.users.ptTeacher;

import com.B305.ogym.controller.dto.PTDto.CareerDto;
import com.B305.ogym.controller.dto.PTDto.CertificateDto;
import com.B305.ogym.controller.dto.PTDto.PTTeacherDto;
import com.B305.ogym.controller.dto.PTDto.SnsDto;
import com.B305.ogym.domain.mappingTable.PTStudentPTTeacher;
import com.B305.ogym.domain.users.common.UserBase;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
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

    // 평점
    private float starRating;

    // 전문분야
    private String major;

    // 자격증 리스트
    @Builder.Default
    @OneToMany(mappedBy = "ptTeacher", cascade = CascadeType.ALL)
    private List<Certificate> certificates = new ArrayList<>();

    // 가격
    private int price;

    // 자기소개
    private String description;

    // SNS 링크
    @Builder.Default
    @OneToMany(mappedBy = "ptTeacher", cascade = CascadeType.ALL)
    private List<Sns> snss = new ArrayList<>();

    // 경력 리스트
    @Builder.Default
    @OneToMany(mappedBy = "ptTeacher", cascade = CascadeType.ALL)
    private List<Career> careers = new ArrayList<>();

    // 학생 리스트
    @Builder.Default
    @OneToMany(mappedBy = "ptTeacher", cascade = CascadeType.ALL)
    private Set<PTStudentPTTeacher> ptStudentPTTeachers = new LinkedHashSet<>();

    public void addCertificate(Certificate certificate) {
        this.certificates.add(certificate);
        if (certificate.getPtTeacher() != this) {
            certificate.setPtTeacher(this);
        }
    }

    public void addCareer(Career career) {
        this.careers.add(career);
        if (career.getPtTeacher() != this) {
            career.setPtTeacher(this);
        }
    }

    public void addSns(Sns sns) {
        this.snss.add(sns);
        if (sns.getPtTeacher() != this) {
            sns.setPtTeacher(this);
        }
    }

    public Object getInfo(String req){
        if("id".equals(req)){
            return this.getId();
        }else if("email".equals(req)){
            return this.getEmail();
        }else if("username".equals(req)){
            return this.getUsername();
        }else if("nickname".equals(req)){
            return this.getNickname();
        }else if("age".equals(req)){
            return this.getAge();
        }else if("gender".equals(req)){
            return this.getGender();
        }else if("tel".equals(req)){
            return this.getTel();
        }else if("address".equals(req)){
            return this.getAddress();
        }else if("role".equals(req)){
            return this.getAuthority();
        }else if("major".equals(req)){
            return this.getMajor();
        }else if("price".equals(req)){
            return this.getPrice();
        }else if("description".equals(req)){
            return this.getDescription();
        }else if("profilePictureURL".equals(req)){
            if(this.getProfilePicture()!= null)
                return this.getProfilePicture().getPictureAddr();
            else
                return null;
        }else{
            return null;
        }
    }


}
