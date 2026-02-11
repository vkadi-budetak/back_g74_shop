package de.ait.g_74_shop.repository;

import de.ait.g_74_shop.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/*
Как работает технология Spring Data JPA
1. При старте приложения технология сканирует наш класс Product и его поля.
2. Зная класс и поля, она "понимает", как с этим работать в базе данных.
3. Она сама пишет класс, который реализует наш интерфейс ProductRepository.
4. В этом классе она сама пишет стандартные методы по работе с БД,
   такие как findAll, findById, save и т.д. И в этих методах она сама
   прописывает нужные SQL запросы в БД.
5. Spring создаёт объект этого класса, который написала наша технология
   и помещает его в Spring Context.
 */

// В JpaRepository обовязково прописуємо типи з яким продуктом працюємо а другий тип який тип id
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByActiveTrue(); // імя метода повинна бути вірною щоб технологія знала що потрібно робити
    Optional<Product> findByIdAndActiveTrue(Long id); // метод знаходе мені продукт в базі даниї який = моєму id і він активний
    long countByActiveTrue(); // від бази прийде одне число



    /*
   Як пистаи вірно назви цих змін дивися в інтернеті?!!
     */




}
