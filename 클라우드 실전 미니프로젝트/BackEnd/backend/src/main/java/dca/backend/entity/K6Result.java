package dca.backend.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Builder
@Document(collection = "k6_results")
public class K6Result {

    @Id
    private String id;

    // "docker" or "kubernetes"
    private String category;

    // 요청 횟수 (총 http_reqs)
    private long requestCount;

    // 평균 응답 시간 (초 단위)
    private double avgResponseTime;

    // 에러율 (0.0 ~ 1.0)
    private double errorRate;

    // 실행 시각
    private String executedAt;
}