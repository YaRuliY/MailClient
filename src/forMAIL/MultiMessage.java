package forMAIL;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;

import javax.imageio.ImageIO;
import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.mail.util.BASE64DecoderStream;

public class MultiMessage implements eMessage{

	private String from;
	private String subj;
	private Date date;
	private ArrayList<Part> parts;
	private String HTML = "";
	private boolean isRelated = false;

	MultiMessage(String f, String s, Date date, ArrayList<Part> array) {
		this.date = date;
		this.from = f;
		this.subj = s;
		this.parts = array;
		CreateContent();
	}

	public String getContent() {
		return HTML;
	}

	public String getFrom() {
		if (from.contains("<")) return from.substring(from.indexOf('<') + 1, from.indexOf('>'));
		else return from;
	}

	public String getSubject() {
		return subj;
	}

	public String getDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat();
		return dateFormat.format(date);
	}
	
	private void CreateContent() {
		try {
			for (Part PartOfMessage : parts) {
				if (PartOfMessage.isMimeType("text/html")) { 
					HTML = PartOfMessage.getContent().toString(); 
				}
				else if (PartOfMessage.isMimeType("multipart/related")) {
					System.out.println("Related Part Found");
						Multipart multiPart = (Multipart) PartOfMessage.getContent();
						for (int j = 0; j < multiPart.getCount(); j++) {
							if (multiPart.getBodyPart(j).isMimeType("text/html")) {
								HTML = multiPart.getBodyPart(j).getContent().toString();
							}
							if (multiPart.getBodyPart(j).isMimeType("image/*")){
								@SuppressWarnings("unchecked")
								Enumeration<Header> enumm =  multiPart.getBodyPart(j).getAllHeaders();
								while(enumm.hasMoreElements()){
									Header Header = enumm.nextElement();
									if (Header.getName().equals("Content-ID")) {
										String imageType = multiPart.getBodyPart(j).getContentType();
										String format = "."+imageType.substring(imageType.indexOf('/')+1,imageType.indexOf(';'));
										String name = Header.getValue().substring(Header.getValue().indexOf('<') + 1,Header.getValue().indexOf('>'));
										
										HTML = HTML.replace(name, (name + format));
										
										BASE64DecoderStream DownLoadStream = (BASE64DecoderStream)multiPart.getBodyPart(j).getContent();
										BufferedImage image = ImageIO.read(DownLoadStream);
										FileOutputStream fos = new FileOutputStream("htmlForMultipartRelated\\image\\"+name+format);
										JPEGImageEncoder ImageEncoder= JPEGCodec.createJPEGEncoder(fos);
										ImageEncoder.encode(image);
										fos.close();
									}
								}
								HTML = HTML.replace("src=\"cid:", "src=\"image/");
								HTML = HTML.replace("background=\"cid:", "background=\"image/");
								if (j == multiPart.getCount()-1) 
									HTML = HTML.replace("<html>", "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" "
											+ "\"http://www.w3.org/TR/html4/loose.dtd\">\n<html>");
								BufferedWriter bufferedWriter = new BufferedWriter(
										new OutputStreamWriter(new FileOutputStream(
												new File("D:\\sTudy\\Java\\wSpace\\kyrsuch\\htmlForMultipartRelated", PartOfMessage.hashCode()+".html")), "UTF8"));
								bufferedWriter.append(HTML);
								bufferedWriter.flush();
								bufferedWriter.close();
								isRelated = true;
							}
						}
					HTML = "file:///D:\\sTudy\\Java\\wSpace\\kyrsuch\\htmlForMultipartRelated\\"+PartOfMessage.hashCode()+".html";
				}
				else if (PartOfMessage.isMimeType("multipart/alternative")){
					Multipart multiPart = (Multipart) PartOfMessage.getContent();
					for (int j = 0; j < multiPart.getCount(); j++) {
						if (multiPart.getBodyPart(j).isMimeType("text/html")) { HTML = multiPart.getBodyPart(j).getContent().toString(); }
					}
				}
			}
		} 
		catch (MessagingException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isRelated(){
		return isRelated;
	}
}
