package tech.louatiakram.scrapping.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.louatiakram.scrapping.entities.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameAndPrice(String name, Double price);
}
