package tech.louatiakram.scrapping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import tech.louatiakram.scrapping.entities.Product;
import tech.louatiakram.scrapping.services.ProductService;

import java.io.IOException;

@Component
public class TunisianetScraper implements CommandLineRunner {

    @Autowired
    private ProductService productService;

    @Autowired
    private ConfigurableApplicationContext appContext;

    @Override
    public void run(String... args) {
        String baseUrl = "https://www.tunisianet.com.tn/681-pc-portable-gamer";
        try {
            String url = baseUrl;
            while (url != null) {
                Document doc = Jsoup.connect(url).get();
                Elements books = doc.select(".item-product");

                for (Element book : books) {
                    Element titleElement = book.select("h2 > a").first();
                    String title = titleElement.text();

                    Elements priceElements = book.select(".price");
                    if (!priceElements.isEmpty()) {
                        String price = priceElements.first().text();
                        String cleanedPrice = price.replace("DT", "").replace(",", ".").replace(" ", "").trim();
                        cleanedPrice = cleanedPrice.replaceAll("(\\.)(?=.*\\.)", ""); // Remove all dots except the last one

                        try {
                            if (Double.parseDouble(cleanedPrice) >= 0) {
                                String[] components = title.split(" / ");
                                String name = "";
                                String processor = "";
                                String gpu = "";
                                String ram = "";
                                String storage = "";
                                String color = "";

                                if (components.length >= 1) name = components[0];
                                if (components.length >= 2) processor = components[1];
                                if (components.length >= 3) gpu = components[2];
                                if (components.length >= 4) ram = components[3];
                                if (components.length >= 5) storage = components[4];
                                if (components.length >= 6) color = components[5];

                                // Check if the product with the same name and price already exists
                                Product existingProduct = productService.getProductByNameAndPrice(name, Double.parseDouble(cleanedPrice));
                                if (existingProduct != null) {
                                    // Update the existing product
                                    System.out.println("Updating product: " + existingProduct.getName());
                                    existingProduct.setProcessor(processor);
                                    existingProduct.setGpu(gpu);
                                    existingProduct.setRam(ram);
                                    existingProduct.setStorage(storage);
                                    existingProduct.setColor(color);
                                    productService.saveProduct(existingProduct);
                                } else {
                                    // Create a new product
                                    System.out.println("Saving new product: " + name + " with price: " + cleanedPrice);
                                    Product product = new Product();
                                    product.setName(name);
                                    product.setProcessor(processor);
                                    product.setGpu(gpu);
                                    product.setRam(ram);
                                    product.setStorage(storage);
                                    product.setColor(color);
                                    product.setPrice(Double.parseDouble(cleanedPrice));
                                    productService.saveProduct(product);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                // Find the link to the next page
                Element nextPageElement = doc.select(".next").first();
                if (nextPageElement != null && nextPageElement.tagName().equals("a")) {
                    url = nextPageElement.attr("href");
                    if (!url.startsWith("http")) {
                        url = baseUrl + url; // Adjust relative URLs to absolute URLs
                    }
                } else {
                    url = null; // No more pages
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
