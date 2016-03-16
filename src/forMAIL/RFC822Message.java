package forMAIL;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;
import javax.mail.util.SharedByteArrayInputStream;

public class RFC822Message implements eMessage{
	
	private String from;
	private String subj;
	private Date date;
	private ArrayList<Part> parts;
	private StringBuilder HTML = new StringBuilder();
	
	public RFC822Message(String f, String s, Date date, ArrayList<Part> array){
		this.date = date;
		this.from = f;
		this.subj = s;
		this.parts = array;
		CreateContent();
	}

	private void CreateContent() {
		try {
			for (Part part : parts) {
				if (part.isMimeType("text/plain")){
					HTML.append(part.getContent().toString());
				}
				if (part.isMimeType("message/delivery-status")){
					SharedByteArrayInputStream stream = (SharedByteArrayInputStream)part.getContent();
					byte[] buffer = new byte[100];
					stream.read(buffer);
				}
				if (part.isMimeType("message/rfc822")){
					MimeMessage rfc = (MimeMessage)part.getContent();
					HTML.append("<hr>");
					HTML.append("<strong>------------Вложеное сообщение------------</strong>");
					HTML.append("<br>");
					HTML.append("<strong>Тема: </strong>"+rfc.getSubject());
					HTML.append("<br>");
					HTML.append("<strong>От: </strong>"+rfc.getFrom()[0]);
					HTML.append("<br>");
					HTML.append("<strong>Дата: </strong>"+rfc.getSentDate());
					HTML.append("<br>");
					HTML.append("<strong>Кому: </strong>"+rfc.getAllRecipients()[0].toString());
					HTML.append("<br>");
					HTML.append("<strong>Текст сообщения: </strong>"+rfc.getContent().toString());
				}
			}
		}
		catch (MessagingException | IOException me){
			
		}
	}

	public String getContent() {
		return HTML.toString();
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

	public boolean isRelated() {
		return false;
	}
}