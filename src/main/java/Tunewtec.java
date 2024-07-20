import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Tunewtec {
    public static void main(String[] args) {
        String baseUrl = "https://tunewtec.com/c/gaming/pc-portable-gamer-gaming/";
        try {
            String url = baseUrl;
            while (url != null) {
                Document doc = Jsoup.connect(url).get();
                Elements books = doc.select(".product-wrapper");

                for (Element book : books) {
                    Element titleElement = book.select("h3 > a").first();
                    String title = titleElement.text();
                    Elements priceElements = book.select(".woocommerce-Price-amount.amount");

                    if (!priceElements.isEmpty()) {
                        String price = priceElements.first().text();
                        String cleanedPrice = price.replace("DT", "").replace(",", ".").replace(" ", "").trim();
                        cleanedPrice = cleanedPrice.replaceAll("(\\.)(?=.*\\.)", "");

                        try {
                            if (Double.parseDouble(cleanedPrice) >= 4000) {
                                Elements componements = book.select(".ul-style.check li");
                                String processor = "";
                                String gpu = "";
                                String ram = "";
                                String storage = "";
                                String screen = "";

                                for (Element component : componements) {
                                    String text = component.text();
                                    if (text.startsWith("Processeur:")) {
                                        processor = text.replace("Processeur:", "").trim();
                                    } else if (text.startsWith("Mémoire:")) {
                                        ram = text.replace("Mémoire:", "").trim();
                                    } else if (text.startsWith("Disque:")) {
                                        storage = text.replace("Disque:", "").trim();
                                    } else if (text.startsWith("Graphique:")) {
                                        gpu = text.replace("Graphique:", "").trim();
                                    } else if (text.startsWith("Ecran:")) {
                                        screen = text.replace("Ecran:", "").trim();
                                    }
                                }

                                System.out.println("Name: " + title);
                                System.out.println("Processor: " + processor);
                                System.out.println("RAM: " + ram);
                                System.out.println("Storage: " + storage);
                                System.out.println("GPU: " + gpu);
                                System.out.println("Screen: " + screen);
                                System.out.println("Price: " + cleanedPrice);
                                System.out.println("----------------------------------");
                            }
                        } catch (NumberFormatException e) {
                            System.err.println("Error parsing price: " + price + " - " + e.getMessage());
                        }
                    }
                }

                Element nextPageElement = doc.select(".next").first();
                if (nextPageElement != null && nextPageElement.tagName().equals("a")) {
                    url = nextPageElement.attr("href");
                    if (!url.startsWith("http")) {
                        url = baseUrl + url;
                    }
                } else {
                    url = null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
