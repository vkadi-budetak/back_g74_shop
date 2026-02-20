package de.ait.g_74_shop.service;

import de.ait.g_74_shop.config.DOProperties;
import de.ait.g_74_shop.exceptions.types.FileUploadException;
import de.ait.g_74_shop.service.interfaces.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    private final DOProperties properties;
    private final S3Client client;

    public FileServiceImpl(DOProperties properties, S3Client client) {
        this.properties = properties;
        this.client = client;
    }

    @Override
    public String uploadAndGetUrl(MultipartFile file) throws IOException {
        /// Логика метода:
        /// 1. Необходимые проверки
        Objects.requireNonNull(file, "MultipartFile cannot be null");
        // проверяем ли файл не пустой
        if (file.isEmpty()) {
            throw new FileUploadException("File is empty");
        }

        if (file.getContentType() == null || !file.getContentType().startsWith("image")) {
            throw new FileUploadException("File is not an image");
        }

        /// 2. Сгенерировать уникальные файлы имя файла
        String uniqueName = generateUniqueFileName(file);

        /// 3. Создать запрос на загрузку файла в облако под уникальным именем
        // Ств хмарне сховище в DigitalOcean для зберігання файлів - ств bucket
        // Секретный ключ для бакета - ySFqIjvzUBvY74ROIP7UxiqOr/aqq2PKEXZVy0vvh40
        // Ключ доступа для бакета - DO801EYHRATTRQRK2T7N
        // Имя бакета - shop-74-bucket
        // Регион бакета - fra1
        // Эндпоинт бакета - https://fra1.digitaloceanspaces.com
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(uniqueName)
                .contentType(file.getContentType())
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        /// 4. Отправка запроса (фактическая загрузка файла в облако)
        RequestBody requestBody = RequestBody.fromInputStream(file.getInputStream(), file.getSize());
        client.putObject(request, requestBody);

        /// 5. Отправка запроса с целью получить ссылку на загруженный файл
        /// 6. Возвращаем полученную ссылку как результат работы метода
        return client.utilities()
                .getUrl(
                        x -> x.bucket(properties.getBucket()).key(uniqueName)
                ).toString();
    }

    // метод який буде створювати унікальні імя файлу
    private String generateUniqueFileName(MultipartFile file) {
        // Какие есть варианты
        // Представим что уникальная часть имени файла - f7b4
        String randomPart = UUID.randomUUID().toString();
        String fileName = file.getOriginalFilename(); // отримує оригінальне їм яке було на комп'ютері

        //  1. Файл пришел без имени
        // Результат - f7b4
        if (fileName == null) {
            return randomPart;
        }

        // CAT PICTURE.jpeg -> cat-picture.jpeg
        String normalizedFileName = fileName.trim().replace(" ", "-").toLowerCase(); // якщо є пробіли потрібно замінити на дефіси

        // 2. Файл пришел с именем без расширения, например - cat
        // Результат - cat-f7b4
        // cat.jpeg -> 3 отримаємо індекс 3
        // cat -> -1
        int dotIndex = normalizedFileName.lastIndexOf('.'); // ми шукаємо крапку, lastIndexOf починає пошук з кінця файлу і вертає її індекс

        if (dotIndex == -1) {
            return String.format("%s-%s", normalizedFileName, randomPart);
        }

        // 3. Файл пришел с именем с расширением, например - cat.jpeg
        // Результат - cat-f7b4.jpeg
        // із cat.jpeg отримаємо -> cat
        String fileNameWithoutExtension = normalizedFileName.substring(0, dotIndex); // substring - вирізає із підрядка рядок;
        // із cat.jpeg отримаємо -> .jpeg
        String extension = normalizedFileName.substring(dotIndex);
        // із cat.jpeg отримаємо -> cat-f7b4.jpeg
        return String.format("%s-%s%s", fileNameWithoutExtension, randomPart, extension);
    }
}
