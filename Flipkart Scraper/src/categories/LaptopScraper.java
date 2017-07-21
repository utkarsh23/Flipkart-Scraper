package categories;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.Thread;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

public class LaptopScraper {
	public static void main(String[] args) throws InterruptedException {
		//Start timer and initialize products counter
		long startTime = System.currentTimeMillis();
		Integer productsCounter = 0;
		
		//List out brands to scrape and sort this list
		List<String> brands = Arrays.asList("Dell","Microsoft","HP","Apple","Lenovo","Acer","Asus","Micromax","Alienware","Fujitsu","MSI","Reach","Smartron");
		Collections.sort(brands);
		
		//JSON data object
		JSONObject data = new JSONObject();
		
		//Setup PhantomJS driver
		System.setProperty("phantomjs.binary.path","phantomjs.exe");
		WebDriver driver = new PhantomJSDriver();
		
		//Scraping begins
		for (int brs = 0; brs < brands.size(); brs++) {
			
			//JSONArray of products for each brand
			JSONArray productsArray = new JSONArray();
			
			//Scraping first page
	        driver.get("https://www.flipkart.com/laptops/pr?p%5B%5D=facets.brand%255B%255D%3D" + brands.get(brs) + "&page=1&sid=6bo%2Fb5g&viewType=list");
	        Thread.sleep(3000);
	        String html = driver.getPageSource();
	        Element doc = Jsoup.parse(html);
	        Elements page = doc.getElementsByClass("_3v8VuN");
	        if (page.size() > 0) {
	        	System.out.println(brands.get(brs) + " " + page.get(0).text());
	        }
	        else {
	        	System.out.println(brands.get(brs) + " " + "Page 1 of 1");
	        }
	        Elements product = doc.getElementsByClass("_2-gKeQ");
	        
	        for (int a = 0; a < product.size(); a++) {
	        	Elements names = product.get(a).getElementsByClass("_3wU53n");
	        	Elements prices = product.get(a).getElementsByClass("_1vC4OE");
	        	String price = "";
	        	String name = "";
	        	if (names.size() != 0) {
	        		name = names.get(0).text();
	        	}
	        	else {
	        		continue;
	        	}
	        	if (prices.size() != 0) {
	        		price = prices.get(0).text();
	        	}
	        	
	        	//Add JSONObject of product to JSONArray
	        	JSONObject productObject = new JSONObject();
	        	productObject.put("name", name);
	        	productObject.put("price", price.replaceAll("\\u20b9", "Rs."));
	        	productsArray.put(productObject);
	        	
	        	productsCounter++;
	        }
	        
	        //Scraping remaining pages
	        Integer count = 2;
	        while (true) {
	        	driver.get("https://www.flipkart.com/laptops/pr?p%5B%5D=facets.brand%255B%255D%3D" + brands.get(brs) + "&page=" + count.toString() + "&sid=6bo%2Fb5g&viewType=list");
	        	Thread.sleep(3000);
		        html = driver.getPageSource();
		        doc = Jsoup.parse(html);
		        page = doc.getElementsByClass("_3v8VuN");
		        if (page.size() > 0) {
		        	System.out.println(brands.get(brs) + " " + page.get(0).text());
		        }
		        else {
		        	break;
		        }
		        product = doc.getElementsByClass("_2-gKeQ");
		        
		        for (int a = 0; a < product.size(); a++) {
		        	Element name = product.get(a).getElementsByClass("_3wU53n").get(0);
		        	Elements prices = product.get(a).getElementsByClass("_1vC4OE");
		        	String price = "";
		        	if (prices.size() != 0) {
		        		price = prices.get(0).text();
		        	}
		        	
		        	//Add JSONObject of product to JSONArray
		        	JSONObject productObject = new JSONObject();
		        	productObject.put("name", name.text());
		        	productObject.put("price", price.replaceAll("\\u20b9", "Rs."));
		        	productsArray.put(productObject);
		        	
		        	productsCounter++;
		        }
		        count++;
	        }
	        
	        //Add all products data for one brand in data JSONObject
	        data.put(brands.get(brs), productsArray);
		}
		
		//Close driver
	    driver.close();
	    
	    //Write data to file
	    try (FileWriter file = new FileWriter("Data/laptop.json")) {
	    	file.write(data.toString(4));
	    	file.flush();
	    }
	    catch (IOException e) {
	    	e.printStackTrace();
	    }
	    
	    //End timer and print time, products scraped
	    long endTime = System.currentTimeMillis();
	    long timeTaken = endTime - startTime;
	    System.out.println("\nTotal products scraped: " + productsCounter.toString());
	    System.out.print("Time taken: ");
	    System.out.println(timeTaken);
       
        System.exit(0);
	}
}