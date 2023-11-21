package com.lzb.transaction.database.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RequestStatusEnum {
    PROCESSING(0),
    SUCCESS(1),
    ERROR(2);

    private final int status;


}
