package forMAIL;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Checker {
	
	private ArrayList<eMessage> messagesForUser = new ArrayList<eMessage>();
	private Integer port = new Integer(465);
	
	public ArrayList<eMessage> checkEMAIL(final String login, final String password, final String host) {
		Thread thread = new Thread(new Runnable() {
			public void run() {
					try {
						System.out.println("[checker is RUN]");
						Properties properties = System.getProperties();
						properties.put("mail.smtp.host", host);
						properties.put("mail.smtp.socketFactory.port", port);
						properties.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
						properties.put("mail.smtp.auth", "true");
						properties.put("mail.smtp.port", port);
	
						Session emailSession = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
									protected PasswordAuthentication getPasswordAuthentication() {
										return new PasswordAuthentication(login,password);
									}
								});
	
						Store emailStore = emailSession.getStore("pop3s");
						
						try { emailStore.connect(host, login, password); }
						catch (javax.mail.AuthenticationFailedException afe) { System.out.println("Ошыбка AuthenticationFailedException"); }
	
						Folder emailFolder = emailStore.getFolder("INBOX");
						emailFolder.open(Folder.READ_ONLY);
	
						Message[] tempMessagesFromSMTP = emailFolder.getMessages();
						ArrayList<eMessage> tempMessagesForUser = new ArrayList<eMessage>(tempMessagesFromSMTP.length);
	
						for (int i = 0; i < tempMessagesFromSMTP.length; i++) {
							if (tempMessagesFromSMTP[i].isMimeType("text/*")) {
								tempMessagesForUser.add(new SimpleMessage(
										tempMessagesFromSMTP[i].getFrom()[0].toString(),
										tempMessagesFromSMTP[i].getSubject(),
										tempMessagesFromSMTP[i].getSentDate(),
										tempMessagesFromSMTP[i].getContent().toString()));
							} else if (tempMessagesFromSMTP[i].isMimeType("multipart/*")) {
								if (tempMessagesFromSMTP[i].isMimeType("multipart/report")){
									Multipart multiPart = (Multipart) tempMessagesFromSMTP[i].getContent();
									ArrayList<Part> parts = new ArrayList<Part>();
									for (int j = 0; j < multiPart.getCount(); j++) {
										parts.add(multiPart.getBodyPart(j));
									}
									tempMessagesForUser.add(new RFC822Message(
											tempMessagesFromSMTP[i].getFrom()[0].toString(),
											tempMessagesFromSMTP[i].getSubject(),
											tempMessagesFromSMTP[i].getSentDate(),
											parts)
									);
								}
								else {
									Multipart multiPart = (Multipart) tempMessagesFromSMTP[i].getContent();
									ArrayList<Part> parts = new ArrayList<Part>();
									for (int j = 0; j < multiPart.getCount(); j++) {
										parts.add(multiPart.getBodyPart(j));
									}
									tempMessagesForUser.add(new MultiMessage(
											tempMessagesFromSMTP[i].getFrom()[0].toString(),
											tempMessagesFromSMTP[i].getSubject(),
											tempMessagesFromSMTP[i].getSentDate(),
											parts)
									);
								}
							}
						}
	
						emailFolder.close(false);
						emailStore.close();
	
						System.out.println("messagesFromSMTP count = " + tempMessagesFromSMTP.length);
						System.out.println("messagesForUser count = " + tempMessagesForUser.size());
	
						messagesForUser = tempMessagesForUser;
					} 
					catch (MessagingException me) {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								JOptionPane.showMessageDialog(null, "Неправильно вказаный пароль!", "Ошибка", JOptionPane.ERROR_MESSAGE);
							}
						});
						me.printStackTrace();
					} 
					catch (IOException ex) {
						ex.printStackTrace();
					}
				}
		});
		
		thread.start();
		
		try {
			thread.join();
		} 
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			e.printStackTrace();
		}
		
		return messagesForUser;
	}
}
