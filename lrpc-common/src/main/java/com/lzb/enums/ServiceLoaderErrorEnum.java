package com.lzb.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;


@AllArgsConstructor
@Getter
@ToString
public enum ServiceLoaderErrorEnum {
    SERVICE_TYPE_IS_NULL("服务类型不应为空"),
    SERVICE_TYPE_IS_NOT_INTERFACE("服务类型必须为接口"),
    SERVICE_TYPE_IS_NOT_ANNOTATED_BY_SPI("服务类型没有 @SPI 注解"),
    SERVICE_NAME_IS_NULL("服务名称为空");

    private final String message;

}
