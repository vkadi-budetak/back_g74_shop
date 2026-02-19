package de.ait.g_74_shop.service;

import de.ait.g_74_shop.exceptions.types.FileUploadException;
import de.ait.g_74_shop.service.interfaces.FileService;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

public class FileServiceImpl implements FileService {
    @Override
    public String uploadAndGetUrl(MultipartFile file) {
        // Логика метода:
        // 1. Необходимые проверки
        Objects.requireNonNull(file, "MultipartFile cannot be null");
        // проверяем ли файл не пустой
        if (file.isEmpty()) {
            throw new FileUploadException("File is empty");
        }

        // 2. Сгенерировать уникальные файлы имя файла
        String uniqueName = generateUniqueFileName(file)

        // 3. Создать запрос на загрузку файла в облако под уникальным именем
        // 4. Отправка запроса (фактическая загрузка файла в облако)
        // 5. Отправка запроса с целью получить ссылку на загруженный файл
        // 6. Возвращаем полученную ссылку как результат работы метода


    }

    // метод який буде створювати унікальні імя файлу
    private String generateUniqueFileName(MultipartFile file) {
        // Какие есть варианты
        // Представим что уникальная часть имени файла - f7b4

        //  1. Файл пришел без имени
        // Результат - f7b4

        // 2. Файл пришел с именем без расширения, например - cat
        // Результат - cat-f7b4

        // 3. Файл пришел с именем с расширением, например - cat.jpeg
        // Результат - cat-f7b4.jpeg
    }
}
