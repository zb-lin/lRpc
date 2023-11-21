package com.lzb;

import lombok.*;

import java.io.Serializable;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Hello implements Serializable {
    private static final long serialVersionUID = 197223949245791676L;
    private String message;
    private String description;
}
