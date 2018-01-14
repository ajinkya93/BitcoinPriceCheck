package com.ajiinkkyab.app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONTokener;
import org.json.JSONObject;
import org.json.JSONException;

public class BitcoinPriceCheck {

	//Array list of USD and INR prices
	ArrayList<Double> value_in_USD, value_in_INR;
	
	//Array list consisting of dates for the past 7 weeks
	ArrayList<String> dates_for_previous_7_wks;
	
	//Last updated time and disclaimer
	String last_updated_time, disclaimer;
	
	//Flags to identify if any error was encountered in retrieving value for any of the desired dates
	boolean flag_USD, flag_INR;
	
	//Define the date format in which in you wish to retrieve the dates
	DateFormat dateFormat;
	
	BitcoinPriceCheck(){
		value_in_USD = new ArrayList<Double>();
		value_in_INR = new ArrayList<Double>();
		dates_for_previous_7_wks = new ArrayList<String>();
		flag_USD = false;
		flag_INR = false;
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		last_updated_time = "";
		disclaimer = "";
	}
	
	BitcoinPriceCheck(ArrayList<Double> value_in_USD, ArrayList<Double> value_in_INR, String last_updated_time, String disclaimer){
		this.value_in_USD = value_in_USD;
		this.value_in_INR = value_in_INR;
		dates_for_previous_7_wks = null;
		flag_USD = false;
		flag_INR = false;
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		this.last_updated_time = last_updated_time;
		this.disclaimer = disclaimer;
	}
	
	
	public void makeRequest() throws IOException, URISyntaxException{
		
		//Define a calendar instance for date manipulation
		Calendar cal = Calendar.getInstance();
		
		//Add today's date to the arraylist dates_for_previous_7_wks
		dates_for_previous_7_wks.add(dateFormat.format(new Date()));
		
		//Retrieve dates in the desired format representative of the past 7 weeks
		for(int count = 0 ; count < 7; count++){
			cal.add(Calendar.DATE, -7);
			String prevDate = dateFormat.format(cal.getTime());
			dates_for_previous_7_wks.add(prevDate);
		}
		
		try{
			this.makeRequestForValueinUSD(dates_for_previous_7_wks);
			this.makeRequestForValueinINR(dates_for_previous_7_wks);
		} catch(RuntimeException re) {
			DisplayData dd = new DisplayData();
			try {
			dd.displayFnForIncompleteData(re.getMessage(),this.getCurrentPrice());
			} catch(MalformedURLException mue) {
				this.logErrors(mue);
			}
		}catch(MalformedURLException mue) {
			//mue.printStackTrace();
			this.logErrors(mue);
		}catch(UnknownHostException uhe) {
			DisplayData displayData = new DisplayData();
			displayData.displayErrorInConnection();
			System.exit(1);
		}catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			this.logErrors(e);
		}
		
		
		//Generate the html file containing the results
		DisplayData displayData = new DisplayData();
		displayData.displayResults(value_in_USD, value_in_INR, flag_USD, flag_INR,last_updated_time,disclaimer,dates_for_previous_7_wks);
		
	}
	
	public void makeRequestForValueinUSD(ArrayList<String> dates_for_previous_7_wks) throws JSONException, IOException, MalformedURLException {
		
		URL url = null;
		HttpURLConnection connection = null;
		
		for(int i = dates_for_previous_7_wks.size()-1; i >=1 ; i--) {
			
			//URL to connect to
			url = new URL("https://api.coindesk.com/v1/bpi/historical/close.json?start="+dates_for_previous_7_wks.get(i)+"&end="+dates_for_previous_7_wks.get(i));
			
			connection = (HttpURLConnection)url.openConnection(); // Type-cast to HttpURLConnection
			connection.setRequestMethod("GET"); //Use HTTP GET method
			connection.setRequestProperty("Accept","application/json"); //Express in what format is the response desired
			
			//If the response from the server is not OK(Status Code 200), print the error message
			if(connection.getResponseCode() != 200) {
				flag_USD = true;
				String err_msg = "Failed for the date "+dates_for_previous_7_wks.get(i)+"Could not retrieve data from the server. HTTP Error Code: "+connection.getResponseCode();
				throw new RuntimeException(err_msg);
			}
			else {
				//JSON Response parsing (A reference implementation of a JSON in java : https://github.com/stleary/JSON-java)
				
				JSONTokener jsonTokener = new JSONTokener(connection.getInputStream());
				JSONObject jsonResponse = new JSONObject(jsonTokener);
				value_in_USD.add((Double)jsonResponse.getJSONObject("bpi").get(dates_for_previous_7_wks.get(i)));
			}
			
			//Close the connection
			connection.disconnect();
		}
			
			url = new URL("https://api.coindesk.com/v1/bpi/currentprice/USD.json"); //URL to connect to
			connection = (HttpURLConnection)url.openConnection(); // Type-cast to HttpURLConnection
			connection.setRequestMethod("GET"); //Use HTTP GET method
			connection.setRequestProperty("Accept","application/json"); //Express in what format is the response desired
			
			//If the response from the server is not OK(Status Code 200), print the error message
			if(connection.getResponseCode() != 200) {
				flag_USD = true;
				String err_msg = "Failed for the date "+dates_for_previous_7_wks.get(0)+"Could not retrieve data from the server. HTTP Error Code: "+connection.getResponseCode();
				throw new RuntimeException(err_msg);
			}
			else {
				JSONTokener jsonTokener = new JSONTokener(connection.getInputStream());
				JSONObject jsonResponse = new JSONObject(jsonTokener);
				
				String current_rate_USD = jsonResponse.getJSONObject("bpi").getJSONObject("USD").getString("rate");
				try {
				value_in_USD.add((Double) NumberFormat.getNumberInstance(java.util.Locale.US).parse(current_rate_USD));
				} catch(ParseException pe) {
					pe.printStackTrace();
				}
				
				
				//Close the connection
				connection.disconnect();
				
			}
	}
	
public void makeRequestForValueinINR(ArrayList<String> dates_for_previous_7_wks) throws JSONException, IOException, MalformedURLException {
		
		URL url = null;
		HttpURLConnection connection = null;
		
		for(int i = dates_for_previous_7_wks.size()-1; i >=1 ; i--) {
			
			//URL to connect to
			url = new URL("https://api.coindesk.com/v1/bpi/historical/close.json?currency=INR&start="+dates_for_previous_7_wks.get(i)+"&end="+dates_for_previous_7_wks.get(i));
			
			connection = (HttpURLConnection)url.openConnection(); // Type-cast to HttpURLConnection
			connection.setRequestMethod("GET"); //Use HTTP GET method
			connection.setRequestProperty("Accept","application/json"); //Express in what format is the response desired
			
			//If the response from the server is not OK(Status Code 200), print the error message
			if(connection.getResponseCode() != 200) {
				flag_INR = true;
				String err_msg = "Failed for the date "+dates_for_previous_7_wks.get(i)+"Could not retrieve data from the server. HTTP Error Code: "+connection.getResponseCode();
				throw new RuntimeException(err_msg);
			}
			else {
				//JSON Response parsing (A reference implementation of a JSON in java : https://github.com/stleary/JSON-java)
				
				JSONTokener jsonTokener = new JSONTokener(connection.getInputStream());
				JSONObject jsonResponse = new JSONObject(jsonTokener);
				value_in_INR.add((Double)jsonResponse.getJSONObject("bpi").get(dates_for_previous_7_wks.get(i)));
			}
			
			//Close the connection
			connection.disconnect();
		}
			
			url = new URL("https://api.coindesk.com/v1/bpi/currentprice/INR.json"); //URL to connect to
			connection = (HttpURLConnection)url.openConnection(); // Type-cast to HttpURLConnection
			connection.setRequestMethod("GET"); //Use HTTP GET method
			connection.setRequestProperty("Accept","application/json"); //Express in what format is the response desired
			
			//If the response from the server is not OK(Status Code 200), print the error message
			if(connection.getResponseCode() != 200) {
				flag_INR = true;
				String err_msg = "Failed for the date "+dates_for_previous_7_wks.get(0)+"Could not retrieve data from the server. HTTP Error Code: "+connection.getResponseCode();
				throw new RuntimeException(err_msg);
			}
			else {
				JSONTokener jsonTokener = new JSONTokener(connection.getInputStream());
				JSONObject jsonResponse = new JSONObject(jsonTokener);
				
				String current_rate_INR = jsonResponse.getJSONObject("bpi").getJSONObject("INR").getString("rate");
				try {
				value_in_INR.add((Double) NumberFormat.getNumberInstance(java.util.Locale.US).parse(current_rate_INR));
				} catch(ParseException pe) {
					pe.printStackTrace();
				}
				
				disclaimer = jsonResponse.getString("disclaimer");
				last_updated_time = jsonResponse.getJSONObject("time").getString("updated");
				
				//Close the connection
				connection.disconnect();
				
			}
	}
	
	public BitcoinPriceCheck getCurrentPrice() throws IOException, MalformedURLException { 
		
		URL url = null;
		HttpURLConnection connection = null;
		
		value_in_USD = new ArrayList<Double>();
		value_in_INR = new ArrayList<Double>();
		
		//Get the current price in INR
		
		url = new URL("https://api.coindesk.com/v1/bpi/currentprice/INR.json"); //URL to connect to
		connection = (HttpURLConnection)url.openConnection(); // Type-cast to HttpURLConnection
		connection.setRequestMethod("GET"); //Use HTTP GET method
		connection.setRequestProperty("Accept","application/json"); //Express in what format is the response desired
		
		//If the response from the server is not OK(Status Code 200), print the error message
		if(connection.getResponseCode() != 200) {
			flag_INR = true;
			String err_msg = "Failed for the date "+dateFormat.format(new Date())+"Could not retrieve data from the server. HTTP Error Code: "+connection.getResponseCode();
			throw new RuntimeException(err_msg);
		}
		else {
			JSONTokener jsonTokener = new JSONTokener(connection.getInputStream());
			JSONObject jsonResponse = new JSONObject(jsonTokener);
			
			String current_rate_INR = jsonResponse.getJSONObject("bpi").getJSONObject("INR").getString("rate");
			try {
				value_in_INR.add((Double) NumberFormat.getNumberInstance(java.util.Locale.US).parse(current_rate_INR));
			} catch(ParseException pe) {
				pe.printStackTrace();
			}
			
		}
		
		//Close the connection
		connection.disconnect();
		
		//Get the current price in USD
		
		url = new URL("https://api.coindesk.com/v1/bpi/currentprice/USD.json"); //URL to connect to
		connection = (HttpURLConnection)url.openConnection(); // Type-cast to HttpURLConnection
		connection.setRequestMethod("GET"); //Use HTTP GET method
		connection.setRequestProperty("Accept","application/json"); //Express in what format is the response desired
		
		//If the response from the server is not OK(Status Code 200), print the error message
		if(connection.getResponseCode() != 200) {
			flag_INR = true;
			String err_msg = "Failed for the date "+dateFormat.format(new Date())+"Could not retrieve data from the server. HTTP Error Code: "+connection.getResponseCode();
			throw new RuntimeException(err_msg);
		}
		else {
			JSONTokener jsonTokener = new JSONTokener(connection.getInputStream());
			JSONObject jsonResponse = new JSONObject(jsonTokener);
			
			disclaimer = jsonResponse.getString("disclaimer");
			last_updated_time = jsonResponse.getJSONObject("time").getString("updated");
			
			String current_rate_USD = jsonResponse.getJSONObject("bpi").getJSONObject("USD").getString("rate");
			try {
				value_in_USD.add((Double) NumberFormat.getNumberInstance(java.util.Locale.US).parse(current_rate_USD));
			} catch(ParseException pe) {
				pe.printStackTrace();
			}
			
		}	
		
		//Close the connection
		connection.disconnect();
		
		return new BitcoinPriceCheck(value_in_USD, value_in_INR, last_updated_time, disclaimer);
	}
	
	public void logErrors(Exception e) throws IOException {
		DateFormat errFileDateFormat = new SimpleDateFormat("yyyy-mm-dd-hh-mm-ss");
		File err_log_file = new File("err_BiPC_"+errFileDateFormat.format(new Date()));
		BufferedWriter bw = new BufferedWriter(new FileWriter(err_log_file));
		
		bw.write(e.toString());
		
		bw.close();
	}
	
	public static void main(String[] args) throws URISyntaxException, IOException{
		// TODO Auto-generated method stub
			BitcoinPriceCheck bpcObj = new BitcoinPriceCheck();
			try {
				bpcObj.makeRequest();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				bpcObj.logErrors(e);
			}
	}

}
