package com.ajiinkkyab.app;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class DisplayData {
	
	public void displayErrorInConnection() throws IOException {
		File resultFile = new File("Bitcoin_Value_Trends.html");
		BufferedWriter bw = new BufferedWriter(new FileWriter(resultFile));
		
		bw.write("<html>");
		bw.write("<body>");
		bw.write("<h3> <center> Could not get the requested data. Please check your internet connection and try again later. In case of other errors or "
				+" if the issue persists, contact at the given email-id. </center></h3>"
				);
		bw.write("</body>");
		bw.write("</html>");
		
		bw.close();
		
		Desktop.getDesktop().browse(resultFile.toURI());
	}
	
	public void displayFnForIncompleteData(String err_msg, BitcoinPriceCheck bpcObj) throws IOException {
		System.out.println(err_msg);
		
		//Create and open a new HTML File to show the results
		
		File resultFile = new File("Bitcoin_Value_Trends.html");
		BufferedWriter bw = new BufferedWriter(new FileWriter(resultFile));
		
		bw.write("<html>");
		bw.write("<head>\n"
				+ "<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta/css/bootstrap.min.css\" integrity=\"sha384-/Y6pD6FV/Vv2HJnA6t+vslU6fwYXjCFtcEpHbNJ0lyAFsXTsjBbfaDjzALeQsN6M\" crossorigin=\"anonymous\">\n"
				+ "</head>\n");
		bw.write("<body>");
		bw.write("Last updated at "+bpcObj.last_updated_time+"<br>\n");
		bw.write("<br>");
		
		bw.write("<span class=\"badge badge-pill badge-primary\"><h4 >Bitcoin's Current value (USD) : $ "+bpcObj.value_in_USD.get(0)+"</h4></span>"
				+ "<span class=\"badge badge-pill badge-primary float-right m-2\" ><h4>Bitcoin's Current value (INR) : Rs. "+bpcObj.value_in_INR.get(0) +"</h4></span>\n");
		
		bw.write("Disclaimer: "+bpcObj.disclaimer);
		bw.write("<br>\n");
		
		bw.write("<footer>\n");
		bw.write("<p> Powered by <a href=\"https://www.coindesk.com/price\">CoinDesk</a> </p>\n");
		bw.write("</footer>\n");
		bw.write("<script src=\"https://code.jquery.com/jquery-3.2.1.slim.min.js\" integrity=\"sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN\" crossorigin=\"anonymous\"></script>\r\n" + 
				"<script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.11.0/umd/popper.min.js\" integrity=\"sha384-b/U6ypiBEHpOf/4+1nzFpr53nxSS+GLCkfwBdFNTxtclqqenISfwAzpKaMNFNmj4\" crossorigin=\"anonymous\"></script>\r\n" + 
				"<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta/js/bootstrap.min.js\" integrity=\"sha384-h0AbiXch4ZDo7tp9hKZ4TsHbi047NrKGLO3SEJAg45jXxnGIfYzk4Si90RDIqNm1\" crossorigin=\"anonymous\"></script>\n");
		bw.write("</body>\n");
		bw.write("</html>\n");
		
		bw.close();
		
		Desktop.getDesktop().browse(resultFile.toURI());
		
	}
	
	
	public void displayResults(ArrayList<Double> value_in_USD,ArrayList<Double> value_in_INR, boolean flag_USD, boolean flag_INR,
			String last_updated_time, String disclaimer, ArrayList<String> dates_for_previous_7_wks) throws IOException, URISyntaxException {
		
			//Create and open a new HTML File to show the results
			
			File resultFile = new File("Bitcoin_Value_Trends.html");
			BufferedWriter bw = new BufferedWriter(new FileWriter(resultFile));
			
			bw.write("<html>");
			
			bw.write("<head>");
			bw.write("<script type=\"text/javascript\" src = \"https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.7.0/Chart.bundle.min.js\"></script>\n");
			bw.write("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta/css/bootstrap.min.css\" integrity=\"sha384-/Y6pD6FV/Vv2HJnA6t+vslU6fwYXjCFtcEpHbNJ0lyAFsXTsjBbfaDjzALeQsN6M\" crossorigin=\"anonymous\">\n");
			
			bw.write("<style>\n"
				+ "#test {\n"
				+		"margin: 0px auto;\n"
				+	"}\n"
				+ "</style>\n"
		    );
		
			bw.write("</head>");
			
			bw.write("<body>\n");
			
			bw.write("<center>Last updated at "+last_updated_time+"<br>\n");
			
			
			
			File readMeFile = new File("BiPC-Res/Readme.html");
			
			if(!readMeFile.exists()) {
				
				// Unpacking start : Unpack Readme.html file 
				
				new File("BiPC-Res").mkdir();
				
				InputStream io = getClass().getResourceAsStream("/WEB-INF/Readme.html");
				
				FileOutputStream fos = new FileOutputStream("BiPC-Res/Readme.html");
				byte[] buf = new byte[256];
			    int read = 0;
			    while ((read = io.read(buf)) > 0) {
			      fos.write(buf, 0, read);
			    }
			    
			    if(fos != null)
			    	fos.close();
			    
			  //Unpacking end
			
			}
		    
			bw.write("<a href=\""+readMeFile.getAbsolutePath()+"\" class=\"btn btn-info\" role=\"button\">README</a></center>\n");
			bw.write("<br>\n");
			bw.write("<span class=\"badge badge-pill badge-primary\"><h4 >Bitcoin's Current value (USD) : $ "+value_in_USD.get(7) +"</h4></span>"
					+ "<span class=\"badge badge-pill badge-primary float-right m-2\" ><h4>Bitcoin's Current value (INR) : Rs. "+value_in_INR.get(7) +"</h4></span>\n");
			
			bw.write("<div style=\"width:75%;\" id = \"test\">\n");
			bw.write("<canvas id=\"myChart\"></canvas>\n");
			bw.write("</div>\n");
			bw.write("<script>\n");
			
			bw.write("var ctx = document.getElementById(\"myChart\");\n"
					+"var myChart = new Chart(ctx, {\n"
					+"type:'line',\n"
					+ "data: {\n"
					+ "labels: [" + '"'+ dates_for_previous_7_wks.get(7)+'"'+","+ '"'+ dates_for_previous_7_wks.get(6)+'"'+","+ '"'+ dates_for_previous_7_wks.get(5)+'"'+","+ '"'+ dates_for_previous_7_wks.get(4)+'"'+","+ '"'+ dates_for_previous_7_wks.get(3)+'"'+","+ '"'+ dates_for_previous_7_wks.get(2)+'"'+","+ '"'+ dates_for_previous_7_wks.get(1)+'"'+","+ '"'+ dates_for_previous_7_wks.get(0)+'"'+"],\n"
					+" datasets: [\n"
					+ "{ \n"
					+" label: \"Bitcoin Rates in USD\",\n"
					+" yAxisID: 'A',\n"
					+" borderColor: \"rgb(255, 0, 0)\",\n"
					+" data: ["
					+ value_in_USD.get(0)+","
							+ value_in_USD.get(1)+","
									+ value_in_USD.get(2)+","
											+ value_in_USD.get(3)+","
													+ value_in_USD.get(4)+","
															+ value_in_USD.get(5)+","
																	+ value_in_USD.get(6)+","
																			+ value_in_USD.get(7)
					+"],\n"
					+"fill:false,\n"
					+" },{\n"
					+" label: \"Bitcoin Rates in INR\",\n"
					+" yAxisID: 'B',\n"
					+" borderColor: \"rgb(0, 0, 255)\",\n"
					+" data: ["
					+ value_in_INR.get(0)+","
							+ value_in_INR.get(1)+","
									+ value_in_INR.get(2)+","
											+ value_in_INR.get(3)+","
													+ value_in_INR.get(4)+","
															+ value_in_INR.get(5)+","
																	+ value_in_INR.get(6)+","
																			+ value_in_INR.get(7)
					+"],\n"
					+"fill:false,\n"
					+" }\n"
					+"]\n"
					+"},\n"
					+"options: {\n"
					
					+"legend: {\n"
			        +"    labels: {\n"
			        +"        fontColor: \"green\",\n"
			        +"        fontSize: 18\n"
			        +"   }\n"
			        +"},\n"
					
				    +"   scales: {\n"
					+" xAxes: [{\n"
					+"			scaleLabel: {\n" 
					+ "				display: true,\n" 
					+ "     		labelString: 'Dates from the past 7 weeks and the current week',\n"
					
					+"				fontSize:18,\n"
					+ "				fontColor:'#444'\n"
					
					+"      	}\n"
					+" }],\n"
					
					+"     yAxes: [{\n"
					+"        id: 'A',\n"
					+"        type: 'linear',\n"
					+"        position: 'left',\n"
					+"			scaleLabel: {\n" 
					+ "				display: true,\n" 
					+ "     		labelString: 'Value of Bitcoin in USD ($)',\n"
					
					+"				fontSize:18,\n"
					+ "				fontColor:'#339'\n"
					
					
					+"      	}\n"
					
					+"      }, {\n"
					+"        id: 'B',\n"
					+"        type: 'linear',\n"
					+"        position: 'right',\n"
					+"			scaleLabel: {\n" 
					+ "			display: true,\n" 
					+ "     	labelString: 'Value of Bitcoin in INR (Rs.)',\n"
					
					+"				fontSize:18,\n"
					+ "				fontColor:'#930'\n"
					
					+"      	}\n"
					+"      }]\n"
					+"    }\n"
					+"  }\n"
					+"});\n"
					);
			bw.write("</script>\n");
			bw.write("<br>Disclaimer: "+disclaimer+"<br>\n");
			bw.write("<br>\n");
			
			bw.write("<script src=\"https://code.jquery.com/jquery-3.2.1.slim.min.js\" integrity=\"sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN\" crossorigin=\"anonymous\"></script>\r\n" + 
					"<script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.11.0/umd/popper.min.js\" integrity=\"sha384-b/U6ypiBEHpOf/4+1nzFpr53nxSS+GLCkfwBdFNTxtclqqenISfwAzpKaMNFNmj4\" crossorigin=\"anonymous\"></script>\r\n" + 
					"<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta/js/bootstrap.min.js\" integrity=\"sha384-h0AbiXch4ZDo7tp9hKZ4TsHbi047NrKGLO3SEJAg45jXxnGIfYzk4Si90RDIqNm1\" crossorigin=\"anonymous\"></script>\n");
			bw.write("</body>\n");
			bw.write("<footer>\n");
			bw.write("<p> <center> Powered by <a href=\"https://www.coindesk.com/price\">CoinDesk</a></center></p>\n");
			bw.write("</footer>\n");
			bw.write("</html>\n");
			
			bw.close();
			
			Desktop.getDesktop().browse(resultFile.toURI());
	}
}
