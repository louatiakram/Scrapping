import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Tunisianet {
    public static void main(String[] args) {
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
                        // Remove "DT", replace commas with dots, remove spaces and handle multiple dots
                        String cleanedPrice = price.replace("DT", "").replace(",", ".").replace(" ", "").trim();
                        cleanedPrice = cleanedPrice.replaceAll("(\\.)(?=.*\\.)", ""); // Remove all dots except the last one

                        try {
                            if (Double.parseDouble(cleanedPrice) >= 11000) {
                                // Split title into components
                                String[] components = title.split(" / ");
                                String name = "";
                                String processor = "";
                                String gpu = "";
                                String ram = "";
                                String storage = "";
                                String color = "";

                                if (components.length >= 1) {
                                    name = components[0];
                                }
                                if (components.length >= 2) {
                                    processor = components[1];
                                }
                                if (components.length >= 3) {
                                    gpu = components[2];
                                }
                                if (components.length >= 4) {
                                    ram = components[3];
                                }
                                if (components.length >= 5) {
                                    storage = components[4];
                                }
                                if (components.length >= 6) {
                                    color = components[5];
                                }

                                System.out.println("Name: " + name);
                                System.out.println("Processor: " + processor);
                                System.out.println("GPU: " + gpu);
                                System.out.println("RAM: " + ram);
                                System.out.println("Storage: " + storage);
                                System.out.println("Color: " + color);
                                System.out.println("Price: " + price);
                                System.out.println("----------------------------------");
                            }
                        } catch (NumberFormatException e) {
                            System.err.println("Error parsing price: " + price + " - " + e.getMessage());
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
