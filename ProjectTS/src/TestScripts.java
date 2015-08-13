
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

//This script validates the photo count in JSON against IMDB page
public class TestScripts {

	static WebDriver driver = new FirefoxDriver();
	public static void main(String[] args) throws IOException, JSONException
	{
		parseJSON();
	}
	
	//Read file contents to string object
	protected static String getFileToString() throws IOException
	{
		
		FileInputStream inputStream = null;
		Scanner sc = null;
		StringBuffer buffer = new StringBuffer();
		try {
		    inputStream = new FileInputStream("./img_count_output.txt");
		    sc = new Scanner(inputStream, "UTF-8");
		    
		    while (sc.hasNextLine()) {
		        buffer.append(sc.nextLine());		       
		    }
		}
		catch(FileNotFoundException ex)
		{
			System.out.println("ERROR:Could not locate the file specified");
		}
		finally {
		    if (inputStream != null) {
		        inputStream.close();
		    }
		    if (sc != null) {
		        sc.close();
		    }
		}
		return buffer.toString();
	}
	
	//Compare the photo count of each movie from JSON with IMDB page
	protected static void validateData(String movieUrl,int cnt)
	{
		System.out.println("Validating....");
		//Use selenium webdriver and XPath to find the matching element
		driver.get(movieUrl);
		//Get the A tags having 'mediaindex' part of the href and 'photos' in text - Should always return 1 element
	 	List<WebElement> list = driver.findElements(By.xpath("//a[contains(@href,'mediaindex')][ contains(text(),'photos')]"));
	 	if(list.size()==1)
		{
	 		WebElement ele = list.get(0);
			//System.out.println( ele.getText());
			String eleText = ele.getText();
			int photoCnt = 0;
			if(eleText != null && eleText.length()>0)
			{
				photoCnt = Integer.parseInt(ele.getText().split(" ")[0]);
				String testcaseOutput = photoCnt == cnt ? "[PASS]" :"[FAIL]";
				System.out.println("Actual PhotoCount:"+photoCnt+" vs "+cnt+" "+testcaseOutput);
				System.out.println("---------------------------------------------");
			}
			else
				System.out.println("ERROR:Something changed in the DOM format");
		}
	 	else
	 	{
	 		System.out.println("ERROR:Unable to find the Photo count");
	 	}
	}

	//Read the file contents in JSON format
	protected static void parseJSON() throws IOException, JSONException
	{
		
		String contents = getFileToString();
		//do nothing if file is empty
		if(contents.length() <=0) return;
		//System.out.println(contents);
		//read contents in JSON array format
		JSONArray jsonArray = new JSONArray(contents);
		System.out.println("Movies in theater count:"+jsonArray.length());
		//read each item and validate the counts
		for(int i=0;i<jsonArray.length();i++)
		{
			String movieUrl = (String)((JSONObject)jsonArray.get(i)).get("url");
			int photoCnt = (int)((JSONObject)jsonArray.get(i)).get("count");
			System.out.println("<<<<<<< Data item("+(i+1)+") from JSON >>>>>>>>");
			System.out.println("Movie URL:"+movieUrl);
			System.out.println("ID:"+((JSONObject)jsonArray.get(i)).get("imdb_id"));
			System.out.println("Photo Cnt:"+((JSONObject)jsonArray.get(i)).get("count"));
			validateData(movieUrl,photoCnt);
		}
		//close the browser and connection
		driver.quit();
	}
	
}
