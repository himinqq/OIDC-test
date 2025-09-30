package com.neves.status.handler;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@RequiredArgsConstructor
public class ErrorMessage {
    //Blackbox
    public static final ErrorMessage BLACKBOX_NOT_FOUND = new ErrorMessage(HttpStatus.NOT_FOUND,
            "UUID에 해당하는 블랙박스를 찾을 수 없습니다. UUID: %s");
    public static final ErrorMessage ALREADY_REGISTERED_BLACKBOX = new ErrorMessage(HttpStatus.CONFLICT,
            "이미 등록된 블랙박스입니다.");
    public static final ErrorMessage FORBIDDEN = new ErrorMessage(HttpStatus.FORBIDDEN,
            "해당 리소스에 접근할 권한이 없습니다.");

    //Metadata
    public static final ErrorMessage METADATA_NOT_FOUND = new ErrorMessage(HttpStatus.NOT_FOUND,
            "ID에 해당하는 메타데이터를 찾을 수 없습니다. ID: %s");

    // jwt
    public static final ErrorMessage INVALID_JWT = new ErrorMessage(HttpStatus.UNAUTHORIZED,
            "JWT에 user_id 필드가 존재하지 않습니다.");
    public static final ErrorMessage WRONG_FORMAT_JWT = new ErrorMessage(HttpStatus.UNAUTHORIZED,
            "JWT 포멧이 올바르지 않습니다.");

    private final HttpStatus status;
    private final String message;

    public ErrorMessage withArgs(String... args) {
        return new ErrorMessage(this.status, String.format(this.message, (Object[]) args));
    }

    public ClientErrorException asException() {
        return new ClientErrorException(this);
    }

}
