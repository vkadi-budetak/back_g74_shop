package de.ait.g_74_shop.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    // метод який
    String uploadAndGetUrl(MultipartFile file);

}
