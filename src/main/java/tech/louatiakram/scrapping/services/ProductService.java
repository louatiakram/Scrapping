package tech.louatiakram.scrapping.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.louatiakram.scrapping.entities.Product;
import tech.louatiakram.scrapping.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product saveProduct(Product product) {
        List<Product> existingProducts = productRepository.findByNameAndPrice(product.getName(), product.getPrice());
        if (existingProducts.isEmpty()) {
            productRepository.save(product);
            System.out.println("Saving new product: " + product.getName() + " with price: " + product.getPrice());
        } else {
            System.out.println("Product already exists: " + product.getName() + " with price: " + product.getPrice());
            Product existingProduct = existingProducts.get(0);
            existingProduct.setProcessor(product.getProcessor());
            existingProduct.setProcessorRef(product.getProcessorRef());
            existingProduct.setMemory(product.getMemory());
            existingProduct.setHardDrive(product.getHardDrive());
            existingProduct.setGpu(product.getGpu());
            existingProduct.setGpuRef(product.getGpuRef());
            existingProduct.setScreenSize(product.getScreenSize());
            existingProduct.setScreenType(product.getScreenType());
            existingProduct.setTouchScreen(product.getTouchScreen());
            existingProduct.setNetwork(product.getNetwork());
            existingProduct.setCamera(product.getCamera());
            existingProduct.setWarranty(product.getWarranty());
            existingProduct.setRefreshRate(product.getRefreshRate());
            existingProduct.setColor(product.getColor());
            productRepository.save(existingProduct);
            System.out.println("Updated existing product: " + existingProduct.getName() + " with price: " + existingProduct.getPrice());
        }
        return product;
    }

    public boolean deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public Product getProductByNameAndPrice(String name, Double price) {
        return productRepository.findByNameAndPrice(name, price).stream().findFirst().orElse(null);
    }
}
