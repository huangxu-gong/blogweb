package com.example.blogweb.controller;

import com.example.blogweb.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class UploadController {
    private static final Set<String> ALLOWED =Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );
    @Value("${file.upload-path}")
    private String uploadPath;
    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()){
            return Result.error("请选择图片");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED.contains(contentType)){
            return Result.error("只支持 jpg / png / gif / webp");
        }
        String original =file.getOriginalFilename();
        String ext = "png";
        if (original != null && original.contains(".")){
            ext = original.substring(original.lastIndexOf(".") + 1).toLowerCase();
        }
        if (!Set.of("jpg", "jpeg", "png", "gif", "webp").contains(ext)){
            return Result.error("文件后缀不合格");
        }
        Path dir = Paths.get(uploadPath).toAbsolutePath().normalize();
        Files.createDirectories(dir);
        String filename = UUID.randomUUID().toString().replace("_","")+ "." + ext;
        Path dest =dir.resolve(filename);
        file.transferTo(dest.toFile());
        return Result.success("/uploads/" + filename);

    }

}
