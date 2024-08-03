package package1;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

class WeatherApp extends JFrame implements ActionListener{
	static //one query textfield and search button
	JSONObject finaldata;
	JTextField searchfield;
	JButton searchbutton;
	static ImageIcon mag_glass,cloud,humidity,windspeed;
	JLabel weatherimg;
	static JLabel weathertext;
	JLabel weatherstate;
	JLabel humidityimg;
	static JLabel humiditytext;
	JLabel windspeedimg;
	static JLabel windspeedtext;
	WeatherApp(){
		super("MY WEATHER APP");//title
		//setting size of frame
		setSize(450,650);
		//setting layout to null so that layout can be set manually
		setLayout(null);
		//setting the whole frame to the centre of your screen
		setLocationRelativeTo(null);
		//not allowing java to resize gui
		setResizable(false);
		//url of search png
		String urlsearch="src/pictures/search.png";
		String urlweather="src/pictures/cloudy.png";
		String urlhumidity="src/pictures/humidity.png";
		String urlwindspeed="src/pictures/windspeed.png";
		String urlsnow="src/pictures/snow.png";
		String urlrain="src/pictures/rain.png";
		mag_glass=new ImageIcon(urlsearch);
		cloud=new ImageIcon(urlweather);
		humidity=new ImageIcon(urlhumidity);
		windspeed=new ImageIcon(urlwindspeed);
		searchfield=new JTextField();
		
		//setting size and position of textfield
		searchfield.setBounds(15,15,351,45);
		//setting font family,bold(or)italics(or)plain as required and text size
		searchfield.setFont(new Font("Dialog",Font.PLAIN,24));
		//weather image
		weatherimg=new JLabel(cloud);
		weatherimg.setBounds(0,125,450,217);
		//temperature text
		weathertext=new JLabel("10 C");
		weathertext.setBounds(0,325,450,54);
		weathertext.setHorizontalAlignment(SwingConstants.CENTER);
		weathertext.setFont(new Font("Dialog",Font.BOLD,48));
		//current weather state
		weatherstate=new JLabel("cloudy");
		weatherstate.setBounds(0,375,450,36);
		weatherstate.setFont(new Font("Dialog",Font.PLAIN,32));
		weatherstate.setHorizontalAlignment(SwingConstants.CENTER);
		//humidity indicator
		humidityimg=new JLabel(humidity);
		humidityimg.setBounds(15,500,74,66);
		//humidity text
		humiditytext=new JLabel("<html><b>Humidity</b> 100%</html>");
		humiditytext.setBounds(85,500,85,55);
		humiditytext.setFont(new Font("Dialog",Font.PLAIN,16));
		//windspeed image
		windspeedimg=new JLabel(windspeed);
		windspeedimg.setBounds(220,500,74,66);
		//windspeed text
		windspeedtext=new JLabel("<html><b>Windspeed</b> 12km/h</html>");
		windspeedtext.setBounds(310,495,90,66);
		windspeedtext.setFont(new Font("Dialog",Font.PLAIN,16));
		searchbutton=new JButton(mag_glass);
		//setting position and size of button
		searchbutton.setBounds(375,13,47,45);
		//changing cursor to hand cursor when hovering over the button
		searchbutton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		//adding elements to frame
		add(searchfield);
		add(searchbutton);
		add(weatherimg);
		add(weathertext);
		add(weatherstate);
		add(humidityimg);
		add(humiditytext);
		add(windspeedimg);
		add(windspeedtext);
		searchbutton.addActionListener(this);
		setVisible(true);
		
	}
	public void actionPerformed(ActionEvent ae) {
		String getcity=searchfield.getText();
		JSONObject getgeodata=(JSONObject)getlocationdata(getcity);
		double latitude=(double)getgeodata.get("latitude");
		double longitude=(double)getgeodata.get("longitude");
		
		JSONObject finaldata= displayWeatherData(latitude,longitude);
		String weathercondition=(String)finaldata.get("weather_condition");
		switch(weathercondition) {
		case "clear":
			cloud=new ImageIcon("src/pictures/clear.png");
			weatherimg.setIcon(cloud);
			break;
		case "cloudy":
			cloud=new ImageIcon("src/pictures/cloudy.png");
			weatherimg.setIcon(cloud);
			break;
		case "rainy":
			cloud=new ImageIcon("src/pictures/rain.png");
			weatherimg.setIcon(cloud);
			break;
		case "snow":
			cloud=new ImageIcon("src/pictures/snow.png");
			weatherimg.setIcon(cloud);
			break;
		}
		weathertext.setText((double)finaldata.get("temperature")+"C");
		humiditytext.setText(("<html><b>Humidity</b> "+(long)finaldata.get("humidity")+"</html>"));
		windspeedtext.setText(("<html><b>Windspeed</b> "+(double)finaldata.get("windspeed")+"</html>"));
		weatherstate.setText((String)finaldata.get("weather_condition"));
		
	}
	public static JSONObject getlocationdata(String city) {
		city=city.replaceAll(" ", "+");
		String urlString="https://geocoding-api.open-meteo.com/v1/search?name="+city+"&count=1&language=en&format=json";
		try {
		HttpURLConnection apiConnection=fetchApiResponse(urlString);
		if(apiConnection.getResponseCode()!=200) {
			System.out.println("could not connect");
			return null;
		}
		String jsonResponse=readApiResponse(apiConnection);
		JSONParser parser=new JSONParser();
		JSONObject resultsJsonObj=(JSONObject) parser.parse(jsonResponse);
		
		JSONArray locationData =(JSONArray) resultsJsonObj.get("results");
		return (JSONObject) locationData.get(0);
		}catch(Exception e) {
			System.out.println("third smh"+e);
		}
		return null;
		
	}
	public static HttpURLConnection fetchApiResponse(String urlString) {
		try {
			URL url=new URL(urlString);
			HttpURLConnection conn=(HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			return conn;
		}catch(IOException e) {
			System.out.println("idhu romba new pa"+e);
		}
		return null;
	}
	public static String readApiResponse(HttpURLConnection apiConnection) {
		try {
			StringBuilder resultJson=new StringBuilder();
			Scanner scanner=new Scanner(apiConnection.getInputStream());
			while(scanner.hasNext()) {
				resultJson.append(scanner.nextLine());
			}
			scanner.close();
			return resultJson.toString();
		}catch(IOException e) {
			System.out.println("new pa"+e);
		}
		return null;
	}
	public static JSONObject displayWeatherData(double latitude,double longitude) {
		JSONObject finalweatherdata=new JSONObject();
		try {
			String url="https://api.open-meteo.com/v1/forecast?latitude="+latitude+"&longitude="+longitude+"&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m";
			HttpURLConnection apiconnection=fetchApiResponse(url);
			
			if(apiconnection.getResponseCode()!=200) {
				System.out.println("could not connect to api");
				return finalweatherdata;
			}
			String jsonResponse=readApiResponse(apiconnection);
			
			JSONParser parser=new JSONParser();
			JSONObject jsonObject=(JSONObject) parser.parse(jsonResponse);
			JSONObject currentdata=(JSONObject) jsonObject.get("current");
			
			double temp=(double) currentdata.get("temperature_2m");
			//System.out.println(temp);
			long humidity=(long) currentdata.get("relative_humidity_2m");
			//System.out.println(humidity);
			double windspeed=(double) currentdata.get("wind_speed_10m");
			//System.out.println(windspeed);
			long weathercode=(long) currentdata.get("weather_code");
			//System.out.println(weathercode);
			/*weathertext.setText(Double.toString(temp));
			humiditytext.setText(Long.toString(humidity));
			windspeedtext.setText(Double.toString(windspeed));*/
			String weatherupdate=weathercodecalculation(weathercode);
			
			
			finalweatherdata.put("temperature", temp);
			finalweatherdata.put("weather_condition", weatherupdate);
			finalweatherdata.put("humidity", humidity);
			finalweatherdata.put("windspeed", windspeed);
			
			
			
			
		}catch(Exception e) {
			System.out.println("this is the error"+e);
		}
		return finalweatherdata;
	}
	public static String weathercodecalculation(long weathercode) {
		String weather="";
		if(weathercode==0L) {
			weather="clear";
		}
		else if(weathercode<=3L) {
			weather="cloudy";
		}
		else if((weathercode>=51L && weathercode<=67L)||(weathercode>=80L && weathercode<=90L)) {
			weather="rainy";
		}
		else if(weathercode>=71L && weathercode<=77L) {
			weather="snow";
		}
		return weather;
	}
}

public class WeatherPredcition {
	public static void main(String args[]) {
		WeatherApp w=new WeatherApp();
		w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
