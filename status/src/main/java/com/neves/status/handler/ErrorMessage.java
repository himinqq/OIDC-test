package com.neves.status.handler;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage {
    //Blackbox
    BLACKBOX_NOT_FOUND("UUID에 해당하는 블랙박스를 찾을 수 없습니다. UUID: %s"),
    ALREADY_REGISTERED_BLACKBOX("이미 등록된 블랙박스입니다."),

    //Metadata
    METADATA_NOT_FOUND("ID에 해당하는 메타데이터를 찾을 수 없습니다. ID: %s");

    private final String message;

    public String getMessage(Object... args) {
        return String.format(this.message, args);
    }
}
