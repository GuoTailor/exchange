package com.exchange.controller;

import com.exchange.domain.User;
import com.exchange.dto.ResponseInfo;
import com.exchange.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * create by GYH on 2022/10/30
 */
@Tag(name = "通用")
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {
    @Autowired
    private UserService userService;
    @Value("${fileUploadPath}")
    private String fileUploadPath;

    @PostMapping("/register")
    public Mono<ResponseInfo<User>> register(@RequestBody User user) {
        return userService.register(user).map(ResponseInfo::ok);
    }

    @Operation(summary = "文件上传", security = {@SecurityRequirement(name = "Authorization")})
    @PreAuthorize("hasRole('USER')")
    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Mono<ResponseInfo<String>> uploadFile(@RequestPart("file") Mono<FilePart> filePart) {
        return ReactiveSecurityContextHolder.getContext().map(it -> it.getAuthentication().getPrincipal()).zipWith(filePart).flatMap(it -> {
            var userId = ((User) it.getT1()).getId();
            var file = it.getT2();
            String originalFilename = file.filename();
            var suffix = "";
            if (StringUtils.hasText(originalFilename)) {
                var split = originalFilename.split("\\.");
                if (split.length > 0) {
                    suffix = "." + split[split.length - 1];
                }
            }
            var fileName = userId + File.separator + UUID.randomUUID() + suffix;
            var dest = new File(fileUploadPath + File.separator + fileName);
            if (!dest.getParentFile().exists()) {
                var result = dest.getParentFile().mkdirs();
                if (!result) return Mono.error(new IllegalStateException("文件夹创建失败"));
            }
            log.info("文件上传成功 {}", dest.getPath());
            return file.transferTo(dest).then(Mono.just(dest.getPath()));
        }).map(ResponseInfo::ok);
    }
}
