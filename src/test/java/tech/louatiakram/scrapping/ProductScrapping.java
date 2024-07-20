package tech.louatiakram.scrapping;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import tech.louatiakram.scrapping.entities.Product;
import tech.louatiakram.scrapping.repository.ProductRepository;
import tech.louatiakram.scrapping.services.ProductService;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ProductScrapping {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllProducts() {
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Laptop A");

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Laptop B");

        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        assertEquals(2, productService.getAllProducts().size());
    }

    @Test
    void testGetProductById() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop A");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertEquals("Laptop A", productService.getProductById(1L).getName());
    }

    @Test
    void testSaveProduct() {
        Product product = new Product();
        product.setName("Laptop A");

        when(productRepository.save(product)).thenReturn(product);

        assertEquals("Laptop A", productService.saveProduct(product).getName());
    }
}
