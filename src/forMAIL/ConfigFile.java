package forMAIL;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.swing.JOptionPane;

public class ConfigFile{
	private int port;
	private String host;
	private boolean auth;
	private String socketFactory;
	private File fileSource;
	private HashMap<String, String> properties = new HashMap<String, String>();
	
	ConfigFile(int port, String host, boolean auth, String socketFactory, File source) {
		this.port = port;
		this.host = host;
		this.auth = auth;
		this.socketFactory = socketFactory;
		this.fileSource = source;
	}
	
	public synchronized void saveConfigFile(){
		try (PrintWriter pw = new PrintWriter(new FileWriter(fileSource))){
			pw.println("port = "+port);
			pw.println("host = "+host);
			pw.println("auth = "+auth);
			pw.println("socketFactory = "+socketFactory);
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public synchronized void loadConfigFile(){
		try (BufferedReader br = new BufferedReader(new FileReader(fileSource))) {
			String line;
			String allVal = "";
			while ((line = br.readLine()) != null) {
				if (line.length()!=0) allVal += (line+"\n"); 
			}
			String[] val = allVal.split("\n");
			for (int i = 0; i < val.length; i++) {
				properties.put(val[i].substring(0,(val[i].indexOf('=') - 1)), val[i].substring((val[i].indexOf('=') + 2), val[i].length()));
			}
			port = Integer.parseInt(properties.get("port"));
			host = properties.get("host");
			auth = properties.get("auth") != null;
			socketFactory = properties.get("socketFactory");
			JOptionPane.showMessageDialog(null, "Порт - "+port+"\nХост - "+host+"\nsmtp.auth - "+auth+"\nsocketFactory.class - "+socketFactory);
			
		} 
		catch (FileNotFoundException ex) {
			JOptionPane.showMessageDialog(null, "Файл не найден!","Ошибка",JOptionPane.ERROR_MESSAGE);
		} 
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
