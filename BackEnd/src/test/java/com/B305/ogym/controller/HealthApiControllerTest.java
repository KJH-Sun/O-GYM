package com.B305.ogym.controller;

import static com.B305.ogym.ApiDocumentUtils.getDocumentRequest;
import static com.B305.ogym.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

import com.B305.ogym.common.annotation.WithAuthUser;
import com.B305.ogym.common.config.SecurityConfig;
import com.B305.ogym.controller.dto.HealthDto.MyHealthResponse;
import com.B305.ogym.controller.dto.HealthDto.MyStudentsHealthListResponse;
import com.B305.ogym.controller.dto.HealthDto.StudentHealth;
import com.B305.ogym.domain.users.common.Gender;
import com.B305.ogym.exception.user.UserNotFoundException;
import com.B305.ogym.service.HealthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;


@ExtendWith(RestDocumentationExtension.class) // JUnit 5 ????????? ?????? ????????? ?????????
@WebMvcTest(controllers = HealthApiController.class, excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)}
)
@MockBean(JpaMetamodelMappingContext.class) // @EnableJPaAuditing ????????? ?????????????????? ???????????????
class HealthApiControllerTest {

    @MockBean
    private HealthService healthService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    public void setup(WebApplicationContext webApplicationContext,
        RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(documentationConfiguration(restDocumentation))
            .apply(sharedHttpSession())
            .addFilters(new CharacterEncodingFilter("UTF-8", true))
            .build();
    }

    @WithAuthUser(email = "teacher@naver.com", role = "ROLE_PTTEACHER")
    @DisplayName("??? ???????????? ?????? ?????? ?????? - ??????")
    @Test
    public void getMyStudentsHealth_success() throws Exception {
        MyStudentsHealthListResponse healthListResponse = new MyStudentsHealthListResponse();
        List<StudentHealth> studentHealthList = new ArrayList<>();
        studentHealthList.add(StudentHealth.builder()
            .age(21)
            .username("?????????")
            .nickname("???")
            .gender(Gender.WOMAN)
            .heightList(new ArrayList<>(Arrays.asList(
                160,
                160,
                160,
                160,
                160,
                161
            )))
            .weightList(new ArrayList<>(Arrays.asList(
                44,
                43,
                46,
                42,
                43,
                40
            )))
            .build());
        healthListResponse.setStudentHealthList(studentHealthList);
        given(healthService.findMyStudentsHealth(any())).willReturn(healthListResponse);

        mockMvc.perform(get("/api/health/mystudents")
            .header("Authorization", "JWT ACCESS TOKEN"))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("healthApi/getMyStudentsHealth/success",
                getDocumentRequest(),
                getDocumentResponse()
            ));

    }


    @WithAuthUser(email = "student@naver.com", role = "ROLE_PTSTUDENT")
    @DisplayName("??? ?????? ?????? ?????? - ??????")
    @Test
    public void getMyHealth_success() throws Exception {
        MyHealthResponse healthResponse = MyHealthResponse.builder()
            .heightList(new ArrayList<>(Arrays.asList(
                160,
                160,
                160,
                160,
                160,
                161
            )))
            .weightList(new ArrayList<>(Arrays.asList(
                44,
                43,
                46,
                42,
                43,
                40
            )))
            .build();

        given(healthService.getMyHealth(any())).willReturn(healthResponse);

        mockMvc.perform(get("/api/health/myhealth")
            .header("Authorization", "JWT ACCESS TOKEN"))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("healthApi/getMyHealth/success",
                getDocumentRequest(),
                getDocumentResponse()
            ));

    }

    @WithAuthUser(email = "student@naver.com", role = "ROLE_PTSTUDENT")
    @DisplayName("??? ?????? ?????? ?????? - ??????")
    @Test
    public void getMyHealth_failure() throws Exception {
        MyHealthResponse healthResponse = MyHealthResponse.builder().build();

        given(healthService.getMyHealth(any()))
            .willThrow(new UserNotFoundException("????????? ???????????? ????????????"));

        mockMvc.perform(get("/api/health/myhealth")
            .header("Authorization", "JWT ACCESS TOKEN"))
            .andDo(print())
            .andExpect(status().isNotFound());

    }


}