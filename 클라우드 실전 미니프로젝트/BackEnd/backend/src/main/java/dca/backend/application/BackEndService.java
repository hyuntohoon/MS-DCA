package dca.backend.application;

<<<<<<< HEAD
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
=======
>>>>>>> upstream/main
import dca.backend.common.module.exception.RestApiException;
import dca.backend.common.module.status.StatusCode;
import dca.backend.dto.K6ResultResponse;
import dca.backend.entity.K6Result;
<<<<<<< HEAD
import dca.backend.infrastructure.BackEndRepository;
=======
import dca.backend.entity.Review;
import dca.backend.entity.User;
import dca.backend.infrastructure.BackEndRepository;
import dca.backend.infrastructure.ReviewRepository;
import dca.backend.infrastructure.UserRepository;
>>>>>>> upstream/main
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
<<<<<<< HEAD
import java.io.FileReader;
=======
>>>>>>> upstream/main
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
<<<<<<< HEAD
=======
import java.util.concurrent.ThreadLocalRandom;
>>>>>>> upstream/main
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class BackEndService {

    private final BackEndRepository backEndRepository;
<<<<<<< HEAD
=======
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
>>>>>>> upstream/main

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
<<<<<<< HEAD
    public void play() throws InterruptedException {
        long sum = 0;
        Random random = new Random();

        for (int i = 1; i <= 10000; i++) {
            for (int j = 1; j <= 100000; j++) {
                int rand = random.nextInt(10000) + 1; // 1 ~ 1000 난수
                sum += (i / j) + rand;
            }
        }
        Thread.sleep(3000);
        System.out.println("Done: " + sum);
=======
    public void play() {
        Random random = new Random();

        // 리뷰 많이 쓴 유저 10명 찾기
        List<Integer> topUsers = reviewRepository.findTop10UsersByReviewCount();

        try {
            for (int userSeq : topUsers) {
                // 각 유저별 리뷰 가져오기
                User user = userRepository.findById(userSeq).orElseThrow();
                List<Review> reviews = user.getReviews();
                System.out.println("User " + userSeq + " 리뷰 개수 = " + reviews.size());

                // 리뷰 삽입
                Review review = Review.builder()
                        .content("부하테스트 리뷰 " + UUID.randomUUID()) // 내용 랜덤
                        .createdAt(LocalDateTime.now())
                        .isActive(true)
                        .isSpoiler(false)
                        .likes(random.nextInt(100))
                        .movieSeq(reviews.get(0).getMovieSeq()) // 임시 영화 번호, 실제 테스트할 때 원하는 값 넣기
                        .reviewRating(ThreadLocalRandom.current().nextDouble(1.0, 5.0))
                        .sentimentScore(ThreadLocalRandom.current().nextDouble(-1.0, 1.0))
                        .user(user) // FK 필수
                        .build();
                reviewRepository.save(review);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RestApiException(StatusCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
>>>>>>> upstream/main
    }

    // K6 테스트 및 결과 저장
    @Transactional
    public void runK6(String category) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "k6", "run",
                    "--env", "CATEGORY=" + category,
                    "src/main/resources/load-test/test.js"
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();

            long successCount = 0;
            double avgResponseTimeSec = 0;
            double errorRate = 0;

            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("K6-OUTPUT >> " + line);

                // checks_succeeded...: 100.00% 30 out of 30
                if (line.contains("checks_succeeded")) {
                    String[] parts = line.trim().split("\\s+");
                    successCount = Long.parseLong(parts[2]); // "30"
                }

                // http_req_failed................: 0.00%  0 out of 30
                if (line.contains("http_req_failed")) {
                    String percent = line.split(":")[1].trim().split("%")[0].trim();
                    errorRate += Double.parseDouble(percent) / 100.0;
                }

                // http_req_duration..............: avg=12.92s ...
                if (line.contains("http_req_duration") && line.contains("avg=")) {
                    int idx = line.indexOf("avg=");
                    if (idx != -1) {
<<<<<<< HEAD
                        String value = line.substring(idx + 4, line.indexOf("s", idx)).trim();
                        avgResponseTimeSec = Double.parseDouble(value);
=======
                        String raw = line.substring(idx + 4).split(" ")[0].trim();
                        // "12.92s" 또는 "111.76ms" 같은 값
                        if (raw.endsWith("ms")) {
                            avgResponseTimeSec = Double.parseDouble(raw.replace("ms", "")) / 1000.0; // ms → 초
                        } else if (raw.endsWith("s")) {
                            avgResponseTimeSec = Double.parseDouble(raw.replace("s", "")); // 초 그대로
                        } else {
                            avgResponseTimeSec = Double.parseDouble(raw); // 단위 없는 경우
                        }
>>>>>>> upstream/main
                    }
                }
            }
            process.waitFor();

            // DB 저장
            K6Result result = K6Result.builder()
                    .category(category)
                    .requestCount(successCount)
                    .avgResponseTime(avgResponseTimeSec)
<<<<<<< HEAD
                    .errorRate(errorRate) // ✅ 출력에서 바로 읽은 값
=======
                    .errorRate(errorRate)
>>>>>>> upstream/main
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