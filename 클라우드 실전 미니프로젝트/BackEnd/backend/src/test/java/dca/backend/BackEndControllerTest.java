package dca.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import dca.backend.application.BackEndService;
import dca.backend.dto.K6ResultResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BackEndControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BackEndService backEndService;  // @TestConfiguration 에서 등록한 Mock 사용

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public BackEndService backEndService() {
            return Mockito.mock(BackEndService.class);
        }
    }

    @Test
    @DisplayName("더미 연산 API 호출 성공")
    void play() throws Exception {
        mockMvc.perform(post("/backend/api/play"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.serviceStatus").value(200))
                .andExpect(jsonPath("$.data").value("연산 완료"));

        verify(backEndService, times(1)).play();
    }

    @Test
    @DisplayName("K6 부하테스트 실행 성공")
    void k6() throws Exception {
        mockMvc.perform(post("/backend/api/k6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.serviceStatus").value(200))
                .andExpect(jsonPath("$.data").value("테스트 성공"));

        verify(backEndService, times(1)).runK6("docker");
        verify(backEndService, times(1)).runK6("kubernetes");
    }

    @Test
    @DisplayName("최근 결과 조회 성공")
    void getK6Results() throws Exception {
        List<K6ResultResponse> mockResults = List.of(
                K6ResultResponse.builder()
                        .category("docker")
                        .requestCount(100)
                        .avgResponseTime(1.5)
                        .errorRate(0.05)
                        .executedAt(LocalDateTime.now().toString())
                        .build(),
                K6ResultResponse.builder()
                        .category("kubernetes")
                        .requestCount(200)
                        .avgResponseTime(0.8)
                        .errorRate(0.01)
                        .executedAt(LocalDateTime.now().toString())
                        .build()
        );
        Mockito.when(backEndService.getRecentResults()).thenReturn(mockResults);

        mockMvc.perform(get("/backend/api/k6/results"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.serviceStatus").value(200))
                .andExpect(jsonPath("$.data[0].category").value("docker"))
                .andExpect(jsonPath("$.data[1].category").value("kubernetes"));
    }
}