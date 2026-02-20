package de.ait.g_74_shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
public class AmazonClientConfig {

    /*
    Пошаговый механизм роботы Спринга с нашими конфигами:
    1. Спринг видит аннотацию




     */

    @Bean
    public S3Client amazonClient(DOProperties properties) {

        // створюємо амазон клієнта який має ключи доступу до бакету.
        String accessKey = properties.getAccessKey();
        String secretKey = properties.getSecretKey();
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

       // Получаем из файла обьекта настроек регион и ендпоинт
        String region = properties.getRegion();
        String endpoint = properties.getEndpoint();

        // Создаем URI для эндпоинта
        URI endpointUri = URI.create(region);

        // Создаем обьект региона из строки с названием региона
        Region regionInstance = Region.of(region);

        return S3Client.builder()
                .endpointOverride(endpointUri)
                .region(regionInstance)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build()
                )
                .build();

    }


}
