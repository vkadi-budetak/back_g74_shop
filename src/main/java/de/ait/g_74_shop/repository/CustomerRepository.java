package de.ait.g_74_shop.repository;

import de.ait.g_74_shop.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findAllByActiveTrue();
    Optional<Customer> findByIdAndActiveTrue(Long id);
    long countByActiveTrue();
}
