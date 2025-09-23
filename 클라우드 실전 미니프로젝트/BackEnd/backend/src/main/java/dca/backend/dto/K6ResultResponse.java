package dca.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class K6ResultResponse {

    private String category;       // docker or kubernetes
    private long requestCount;     // 요청 횟수
    private double avgResponseTime; // 평균 응답 시간 (s)
    private double errorRate;      // 에러율
    private String executedAt;     // 실행 시각
}