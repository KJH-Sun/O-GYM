package com.B305.ogym.domain.users.ptTeacher;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "career")
public class Career {

    @Builder
    public Career(String role,String company, LocalDate startDate, LocalDate endDate) {
        this.role = role;
        this.company = company;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "career_id")
    private Long id; // 대리키

    private String role;

    // 시작일, 종료일
    private LocalDate startDate;
    private LocalDate endDate;

    private String company;

    public static Career CreateCareer(String company , LocalDate startDate , LocalDate endDate,String role   ){
        return Career.builder()
            .company(company)
            .startDate(startDate)
            .endDate(endDate)
            .role(role)
            .build();
    }
}
