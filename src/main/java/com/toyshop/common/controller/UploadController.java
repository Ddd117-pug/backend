package com.toyshop.common.controller;

import com.toyshop.common.dto.UploadFileResponse;
import com.toyshop.common.response.ApiResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/api/common")
public class UploadController {

    private static final Path UPLOAD_ROOT = Paths.get(System.getProperty("user.dir"), "uploads");

    @PostMapping("/upload")
    public ApiResponse<UploadFileResponse> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return ApiResponse.fail(400, "请选择图片文件");
        }
        Files.createDirectories(UPLOAD_ROOT);
        String originalName = StringUtils.hasText(file.getOriginalFilename()) ? file.getOriginalFilename() : "image";
        String ext = "";
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex >= 0) {
            ext = originalName.substring(dotIndex);
        }
        String filename = UUID.randomUUID().toString().replace("-", "") + ext;
        Path target = UPLOAD_ROOT.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return ApiResponse.success(new UploadFileResponse("/uploads/" + filename));
    }
}
