package forMAIL;
import java.util.Properties;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;

public class MessageEraser implements Runnable {
	
	private String user;
	private String password;
	private String pop3Host;
	private int index;

	MessageEraser(String u, String p, int i){
		this.user=u;
		this.password=p;
		this.index=i;
		if (user.contains("yahoo")) this.pop3Host = "pop.mail.yahoo.com";
		else this.pop3Host = "pop."+user.substring(user.indexOf('@')+1,user.length());
	}
	
	public void run() {
		try {
			Properties properties = new Properties();
			properties.put("mail.store.protocol", "pop3");
			properties.put("mail.pop3s.host", pop3Host);
			properties.put("mail.pop3s.port", "995");
			properties.put("mail.pop3.starttls.enable", "true");
			
			Session emailSession = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(user,password);
				}
			});

			Store store = emailSession.getStore("pop3s");
			store.connect(pop3Host, user, password);

			Folder emailFolder = store.getFolder("INBOX");
			emailFolder.open(Folder.READ_WRITE);

			Message[] messages = emailFolder.getMessages();
			System.out.println("messages.length---" + messages.length);
			
			if (index > 0) {
				emailFolder.getMessages()[index].setFlag(Flags.Flag.DELETED, true);
				System.out.println("DELETE Message with Subj: " + emailFolder.getMessages()[index].getSubject());
			}
			else System.out.println("<--Index is = "+index+"-->");
			
			emailFolder.close(true);
			store.close();
			System.out.println("All Done");
		} 
		catch (MessagingException e) {
			e.printStackTrace();
		}
	}
}
