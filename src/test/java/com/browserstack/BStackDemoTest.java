package com.browserstack;

import org.openqa.selenium.By;
import org.testng.annotations.Test;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

import java.io.IOException;

import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BStackDemoTest extends SeleniumTest {
    @Test
    public static void main() throws MalformedURLException {


        // Initialize the WebDriver with BrowserStack hub
        //WebDriver driver = new RemoteWebDriver();
        //System.setProperty("webdriver.edge.driver", "C:\\Users\\vishnu\\Downloads\\edgedriver_win64\\msedgedriver.exe");
        //System.setProperty("webdriver.chrome.driver","");
        //WebDriver driver = new EdgeDriver();
        driver.get("https://elpais.com/opinion/");
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        ArrayList<String> Header = null;
        ArrayList<String> Body = null;
        ArrayList<String> EngHeading = null;
        try {
            driver.findElement(By.id("didomi-notice-agree-button")).click();
            //driver.findElement(By.xpath("(//a[text()='Opinión'])[1]"));
            Header = new ArrayList<String>();
            Body = new ArrayList<String>();
            EngHeading = new ArrayList<String>();

            for (int i = 1; i <= 5; i++) {
                driver.findElement(By.xpath("(//h2[@class=\"c_t c_t-i \"])[" + i + "]")).click();
                String heading = driver.findElement(By.xpath("//*[@class=\"a_t\"]")).getText();
                String body = driver.findElement(By.xpath("//*[@class=\"a_st\"]")).getText();
                Header.add(heading);
                Body.add(body);
                driver.navigate().back();
                System.out.println(i+". Heading: "+ heading);
                EngHeading.add(translateText(heading));
                System.out.println("   Body: "+ body);
                System.out.println();
                String imageUrl = driver.findElement(By.cssSelector("img")).getAttribute("src");
                String Filename = "image "+ i+".jpg" ;
                downloadImage(imageUrl, Filename); // Specify the local file name and extension
                System.out.println("Image downloaded successfully.");

            }
            Map<String, Integer> wordCount = new HashMap<>();


            for(String value : EngHeading){


                String[] words = value.toLowerCase().replaceAll("[^a-zA-Z0-9 ]", "").split("\\s+");

                // Count the occurrences of each word
                for (String word : words) {
                    wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
                }


            }
            System.out.println("Repeated words and their counts:");
            wordCount.entrySet().stream().filter(entry -> entry.getValue() > 1).forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));


            driver.quit();
        } catch (Exception e) {
            System.out.println();
            System.out.println(e);
        }
    }
    public static void downloadImage(String imageUrl, String fileName) throws IOException {
        URL url = new URL(imageUrl);
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());

        // Specify the local path where the image will be saved
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }

        // Close the channel
        rbc.close();
    }
    public static String translateText( String text)
            throws IOException {

        GoogleCredentials credentials = GoogleCredentials.fromStream(Files.newInputStream(Paths.get("C:\\Users\\hii\\Downloads\\second-folio-446009-e6-9b0c1c3149f6.json")));
        Translate translate = TranslateOptions.newBuilder().setCredentials(credentials).build().getService();
        com.google.cloud.translate.Translation translation = translate.translate(text, Translate.TranslateOption.targetLanguage("en"));
        System.out.println("Heading(English): "+translation.getTranslatedText());
        return translation.getTranslatedText();
    }
}
