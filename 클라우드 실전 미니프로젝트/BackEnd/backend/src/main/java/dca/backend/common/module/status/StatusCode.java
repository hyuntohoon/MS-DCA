package dca.backend.common.module.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor

public enum StatusCode {

    SUCCESS(HttpStatus.OK, 200, "정상적으로 요청이 완료되었습니다."),

    BAD_REQUEST(HttpStatus.BAD_REQUEST, 400, "잘못된 요청입니다."),
    NO_SUCH_ELEMENT(HttpStatus.OK,401,"정상적으로 요청이 완료되었지만, 요청 정보를 찾을 수 없습니다."),
    UNAUTHORIZED_REQUEST(HttpStatus.BAD_REQUEST, 402, "로그인되지 않은 사용자입니다."),
    FORBIDDEN_ACCESS(HttpStatus.BAD_REQUEST, 403, "권한이 없는 사용자입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, 404, "요청 정보를 찾을 수 없습니다."),



    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500, "서버에서 처리 중 에러가 발생했습니다."),
    SERVICE_STOP(HttpStatus.INTERNAL_SERVER_ERROR, 501, "현재 서버가 이용 불가능 상태입니다."),

    NO_CONTENT(HttpStatus.OK, 412, "요청한 정보가 없습니다.");



    private final HttpStatus httpStatus;
    private final int serviceStatus;
    private final String message;
}