package com.blog.controller;

import com.blog.service.SysPopupService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



/**
 * @author haibara
 * @description 弹窗管理相关接口
 * @since 2025/8/7
 */
@Slf4j
@RestController
@Tag(name = "弹窗管理相关接口")
@RequestMapping("/sysPopup")
@Validated
public class SysPopupController {

    @Resource
    private SysPopupService sysPopupService;

}
