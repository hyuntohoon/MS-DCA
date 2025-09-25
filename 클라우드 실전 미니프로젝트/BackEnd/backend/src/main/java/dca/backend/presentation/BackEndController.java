package dca.backend.presentation;

import dca.backend.application.BackEndService;
import dca.backend.common.module.response.ResponseDto;
import dca.backend.common.module.status.StatusCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/backend/api")
public class BackEndController {

    private final BackEndService backEndService;


    // 성능부하테스트용 더미 API
    @PostMapping("/play")
<<<<<<< HEAD
    public ResponseEntity<ResponseDto> play() throws InterruptedException {
=======
    public ResponseEntity<ResponseDto> play() {
>>>>>>> upstream/main
        backEndService.play();
        return ResponseDto.response(StatusCode.SUCCESS, "연산 완료");
    }

    // k6 요청 API
    @PostMapping("/k6")
    public ResponseEntity<ResponseDto> k6() {
        backEndService.runK6("docker");
        backEndService.runK6("kubernetes");
        return ResponseDto.response(StatusCode.SUCCESS, "테스트 성공");
    }

    // 결과 조회
    @GetMapping("/k6/results")
    public ResponseEntity<ResponseDto> getK6Results() {
        return ResponseDto.response(StatusCode.SUCCESS, backEndService.getRecentResults());
    }

    // 더미데이터 삽입 API
//    @PostMapping("/insert")
//    public ResponseEntity<ResponseDto> insertDummyData() {
//        backEndService.insertDummyData();
//        return ResponseDto.response(StatusCode.SUCCESS, "더미 데이터 삽입 완료");
//    }
}
