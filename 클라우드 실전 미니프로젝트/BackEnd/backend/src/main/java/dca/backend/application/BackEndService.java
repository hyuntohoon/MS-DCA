package dca.backend.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dca.backend.common.module.exception.RestApiException;
import dca.backend.common.module.status.StatusCode;
import dca.backend.dto.K6ResultResponse;
import dca.backend.entity.K6Result;
import dca.backend.infrastructure.BackEndRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class BackEndService {

    private final BackEndRepository backEndRepository;

    // 테스트용 더미데이터 삽입
//    @Transactional
//    public void insertDummyData() {
//        for (int i = 5; i >= 1; i--) {
//            // 공통 실행 시각 (현재시간 - i분)
//            String executedAt = LocalDateTime.now().minusMinutes(i).toString();
//
//            // 도커 (성능 낮음)
//            K6Result docker = K6Result.builder()
//                    .id(UUID.randomUUID().toString())
//                    .category("docker")
//                    .requestCount(50 + i * 5)          // 요청 수 낮음
//                    .avgResponseTime(2.0 + i * 0.5)   // 응답 시간 길음 (초 단위)
//                    .errorRate(0.1 + (i * 0.02))      // 에러율 더 높음
//                    .executedAt(executedAt)
//                    .build();
//            backEndRepository.save(docker);
//
//            // 쿠버네티스 (성능 더 좋음)
//            K6Result kube = K6Result.builder()
//                    .id(UUID.randomUUID().toString())
//                    .category("kubernetes")
//                    .requestCount(80 + i * 10)         // 요청 수 더 많음
//                    .avgResponseTime(0.8 + i * 0.2)   // 응답 시간 더 짧음
//                    .errorRate(0.02 + (i * 0.01))     // 에러율 더 낮음
//                    .executedAt(executedAt)
//                    .build();
//            backEndRepository.save(kube);
//        }
//    }

    // 테스트용API
    @Transactional
    public void play() {
        long sum = 0;
        Random random = new Random();

        for (int i = 1; i <= 10000; i++) {
            for (int j = 1; j <= 100000; j++) {
                int rand = random.nextInt(10000) + 1; // 1 ~ 1000 난수
                sum += (i / j) + rand;
            }
        }
        System.out.println("Done: " + sum);
    }


    // 부하테스트
    @Transactional
    public void runK6(String category) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "k6", "run",
                    "src/main/resources/load-test/test.js",   // js 경로
                    "--out", "json=output.json"
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();
            process.waitFor();

            // 집계용 변수
            long totalRequests = 0;
            long successCount = 0;
            long errorCount = 0;
            double totalDuration = 0;

            // 파싱
            ObjectMapper mapper = new ObjectMapper();
            try (BufferedReader br = new BufferedReader(new FileReader("output.json"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    JsonNode node = mapper.readTree(line);
                    if ("Point".equals(node.get("type").asText())) {
                        String metric = node.get("metric").asText();
                        double value = node.get("data").get("value").asDouble();

                        if ("http_reqs".equals(metric)) {
                            totalRequests += (long) value;
                        } else if ("http_req_duration".equals(metric)) {
                            totalDuration += value; // 🔹 duration은 나중에 성공 기준으로만 평균 낼 거라 일단 누적
                        } else if ("http_req_failed".equals(metric)) {
                            errorCount += (long) value;
                        }
                    }
                }
            }

            // 실제 처리된 요청 수 = 총 요청 수 - 에러 수
            successCount = totalRequests - errorCount;

            // 평균 응답 시간 (성공한 요청 기준)
            double avgResponseTimeSec = successCount > 0 ? (totalDuration / successCount) / 1000.0 : 0;

            // 에러율
            double errorRate = totalRequests > 0 ? (double) errorCount / totalRequests : 0.0;

            // 저장
            K6Result result = K6Result.builder()
                    .category(category)
                    .requestCount(successCount)          //
                    .avgResponseTime(avgResponseTimeSec) //
                    .errorRate(errorRate)
                    .executedAt(LocalDateTime.now().toString())
                    .build();
            backEndRepository.save(result);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RestApiException(StatusCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // 최근 결과 가져오기
    @Transactional(readOnly = true)
    public List<K6ResultResponse> getRecentResults() {
        List<K6Result> dockerResults = backEndRepository.findTop5ByCategoryOrderByExecutedAtDesc("docker");
        List<K6Result> kubeResults = backEndRepository.findTop5ByCategoryOrderByExecutedAtDesc("kubernetes");

        return Stream.concat(dockerResults.stream(), kubeResults.stream())
                .map(result -> K6ResultResponse.builder()
                        .category(result.getCategory())
                        .requestCount(result.getRequestCount())
                        .avgResponseTime(result.getAvgResponseTime())
                        .errorRate(result.getErrorRate())
                        .executedAt(result.getExecutedAt())
                        .build()
                )
                .toList();
    }
}