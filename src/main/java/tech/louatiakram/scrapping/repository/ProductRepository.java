package tech.louatiakram.scrapping.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.louatiakram.scrapping.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Product findByName(String name);
}
