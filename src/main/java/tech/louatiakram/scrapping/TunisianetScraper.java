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
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
                Elements productLinks = doc.select("h2.product-title > a");

                List<CompletableFuture<Void>> futures = productLinks.stream()
                        .map(linkElement -> {
                            String productDetailUrl = linkElement.attr("href");
                            return CompletableFuture.runAsync(() -> extractProductDetails(productDetailUrl));
                        })
                        .collect(Collectors.toList());

                // Wait for all futures to complete
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

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

    private void extractProductDetails(String productDetailUrl) {
        try {
            Document productDoc = Jsoup.connect(productDetailUrl).get();
            String title = productDoc.select("h1[itemprop=name]").text();
            String[] components = title.split(" / ");
            String name = components[0].trim();

            // Initialize fields
            String processor = "";
            String processorRef = "";
            String memory = "";
            String hardDrive = "";
            String gpu = "";
            String gpuRef = "";
            String screenSize = "";
            String screenType = "";
            String touchScreen = "";
            String network = "";
            String camera = "";
            String warranty = "";
            String refreshRate = "";
            String color = "";

            // Extract attributes
            Elements details = productDoc.select(".data-sheet");
            for (Element detail : details.select("dt")) {
                String key = detail.text().trim();
                String value = detail.nextElementSibling().text().trim();

                switch (key) {
                    case "Système d'exploitation":
                        processor = value;
                        break;
                    case "Processeur":
                        processor = value;
                        break;
                    case "Réf processeur":
                        processorRef = value;
                        break;
                    case "Mémoire":
                        memory = value;
                        break;
                    case "Disque Dur":
                        hardDrive = value;
                        break;
                    case "Carte Graphique":
                        gpu = value;
                        break;
                    case "Réf Carte graphique":
                        gpuRef = value;
                        break;
                    case "Taille Ecran":
                        screenSize = value;
                        break;
                    case "Type Ecran":
                        screenType = value;
                        break;
                    case "Ecran Tactile":
                        touchScreen = value;
                        break;
                    case "Réseau":
                        network = value;
                        break;
                    case "Caméra":
                        camera = value;
                        break;
                    case "Garantie":
                        warranty = value;
                        break;
                    case "Taux de Rafraîchissement":
                        refreshRate = value;
                        break;
                    case "Couleur":
                        color = value;
                        break;
                }
            }

            // Extract and clean price
            String priceText = productDoc.select("span[itemprop=price]").first().text();
            String cleanedPrice = priceText.replace("DT", "").replace(",", ".").replace(" ", "").trim();
            cleanedPrice = cleanedPrice.replaceAll("(\\.)(?=.*\\.)", ""); // Remove all dots except the last one

            Double price = null;
            try {
                price = Double.parseDouble(cleanedPrice);
            } catch (NumberFormatException e) {
                System.err.println("Error parsing price: " + cleanedPrice);
            }

            // Print debug info
            System.out.println("Product Name: " + name);
            System.out.println("Price: " + price);

            // Create or update product
            if (name != null && price != null) {
                Product existingProduct = productService.getProductByNameAndPrice(name, price);
                if (existingProduct != null) {
                    System.out.println("Name of product extracted: " + name);
                } else {
                    Product product = new Product(null, name, processor, processorRef, memory, hardDrive, gpu, gpuRef,
                            screenSize, screenType, touchScreen, network, camera, warranty, refreshRate, color, price);
                    productService.saveProduct(product);
                    System.out.println("Saving new product: " + name + " with price: " + price);
                }
            } else {
                System.err.println("Skipping product due to null name or price");
            }
        } catch (IOException e) {
            System.err.println("Error fetching product details from: " + productDetailUrl);
            e.printStackTrace();
        }
    }
}
