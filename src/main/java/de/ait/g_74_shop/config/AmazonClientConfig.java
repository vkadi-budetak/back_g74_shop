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
    Пошаговый механизм работы Спринга с нашими конфигами:
    1. Спринг видит аннотацию @Configuration на нашем классе DOProperties,
       он создаёт объект этого класса и помещает в Спринг контекст.
    2. Спринг видит аннотацию @ConfigurationProperties(prefix = "do") на нашем классе DOProperties
       и читает все переменные окружения с префиксом DO_
    3. Спринг сопоставляет имена переменных с именами полей класса.
       Таким образом он значение переменной DO_SECRET_KEY кладёт в поле secretKey.
       То же самое происходит с остальными четырьмя полями.
    4. Теперь в Спринг контексте лежит объект DOProperties со всеми реквизитами
       доступа к бакету.
    5. Спринг видит аннотацию @Configuration на нашем классе AmazonClientConfig,
       он ищет в этом классе методы, помеченные аннотацией @Bean.
    6. Спринг сам запускает эти методы, передаёт в метод объект DOProperties
       из Спринг контекста, а тот объект, который метод вернул,
       Спринг делает бином и помещает в Спринг контекст.

       То есть наша задача - правильно написать метод amazonClient, который создаёт
       объект клиента с заложенными в него реквизитами доступа к бакету.
     */
    @Bean
    public S3Client amazonClient(DOProperties properties) {

        // Создаём объект, который содержит ключи доступа к бакету.
        String accessKey = properties.getAccessKey();
        String secretKey = properties.getSecretKey();
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        // Получаем из объекта настроек регион и эндпоинт
        String region = properties.getRegion();
        String endpoint = properties.getEndpoint();

        // Создаём URI для эндпоинта
        URI endpoinUri = URI.create(endpoint);

        // Создаём объект региона из строки с названием региона
        Region regionInstance = Region.of(region);

        return S3Client.builder()
                .endpointOverride(endpoinUri)
                .region(regionInstance)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build()
                )
                .build();

        /*
        pathStyleAccessEnabled(true) говорит, что к бакету нужно обращаться по адресу
        https://fra1.do.com/bucket-name - работает при любых конфигурациях датацентра
        вместо:
        https://bucket-name.fra1.do.com - работает не всегда, не во всех конфигурациях
         */
    }
}
