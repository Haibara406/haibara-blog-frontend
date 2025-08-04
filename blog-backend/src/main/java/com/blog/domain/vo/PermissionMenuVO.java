package com.blog.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * @author haibara
 * @description 权限菜单返回
 * @since 2025/7/29 13:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "权限菜单返回")
public class PermissionMenuVO {

    @Schema(description = "权限ID")
    private Integer id;

    @Schema(description = "菜单名称")
    private String menuName;

    @Schema(description = "菜单ID")
    private Long menuId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PermissionMenuVO that = (PermissionMenuVO) o;
        return Objects.equals(menuId, that.menuId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(menuId);
    }
}