package com.nexscale.api.skill.controller;

import com.nexscale.api.common.config.SecurityConfig;
import com.nexscale.api.common.exception.GlobalExceptionHandler;
import com.nexscale.api.common.exception.ResourceNotFoundException;
import com.nexscale.api.common.security.CustomAccessDeniedHandler;
import com.nexscale.api.common.security.CustomAuthenticationEntryPoint;
import com.nexscale.api.common.security.JwtService;
import com.nexscale.api.skill.dto.SkillResponse;
import com.nexscale.api.skill.service.SkillService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {SkillController.class, GlobalExceptionHandler.class})
@Import(SecurityConfig.class)
class SkillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SkillService skillService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomAuthenticationEntryPoint authenticationEntryPoint;

    @MockitoBean
    private CustomAccessDeniedHandler accessDeniedHandler;

    @Test
    @DisplayName("GET /api/skills dapat diakses secara publik dan mengembalikan 200 OK beserta list skill")
    void getAllSkills_publicAccess_returnsOkWithList() throws Exception {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        List<SkillResponse> mockList = List.of(
                new SkillResponse(id1, "PyTorch", "Deep learning framework"),
                new SkillResponse(id2, "TensorFlow", "Machine learning library")
        );

        when(skillService.getAllSkills()).thenReturn(mockList);

        mockMvc.perform(get("/api/skills")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].skillId", is(id1.toString())))
                .andExpect(jsonPath("$[0].name", is("PyTorch")))
                .andExpect(jsonPath("$[1].skillId", is(id2.toString())))
                .andExpect(jsonPath("$[1].name", is("TensorFlow")));
    }

    @Test
    @DisplayName("GET /api/skills/{id} dapat diakses secara publik dan mengembalikan 200 OK beserta detail skill")
    void getSkillById_publicAccess_returnsOk() throws Exception {
        UUID skillId = UUID.randomUUID();
        SkillResponse mockSkill = new SkillResponse(
                skillId,
                "LangChain",
                "Framework orkestrasi LLM"
        );

        when(skillService.getSkillById(skillId)).thenReturn(mockSkill);

        mockMvc.perform(get("/api/skills/" + skillId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.skillId", is(skillId.toString())))
                .andExpect(jsonPath("$.name", is("LangChain")))
                .andExpect(jsonPath("$.description", is("Framework orkestrasi LLM")));
    }

    @Test
    @DisplayName("GET /api/skills/{id} ketika ID tidak ditemukan mengembalikan 404 Not Found")
    void getSkillById_notFound_returnsNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        when(skillService.getSkillById(nonExistentId))
                .thenThrow(new ResourceNotFoundException("Skill tidak ditemukan"));

        mockMvc.perform(get("/api/skills/" + nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Data Tidak Ditemukan")))
                .andExpect(jsonPath("$.message", is("Skill tidak ditemukan")));
    }
}
