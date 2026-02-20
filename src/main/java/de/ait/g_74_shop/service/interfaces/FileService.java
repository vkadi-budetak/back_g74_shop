package de.ait.g_74_shop.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {

    // метод який
    String uploadAndGetUrl(MultipartFile file) throws IOException;

}
