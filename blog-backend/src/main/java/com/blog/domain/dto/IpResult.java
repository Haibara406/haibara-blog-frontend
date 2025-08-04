package com.blog.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author haibara
 * @since 2025/7/28 13:58
 */
@Data
public class IpResult<T> implements Serializable {

    private Integer code;
    private String msg;
    private T data;

    public boolean isSuccess() {
        return Objects.nonNull(this.code) && this.code == 0;
    }
}
