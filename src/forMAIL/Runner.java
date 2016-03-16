package forMAIL;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.sun.mail.util.MailConnectException;

@SuppressWarnings("serial")
public class Runner extends JFrame{
	private JFormattedTextField login = new JFormattedTextField();
	private JPasswordField password = new JPasswordField();
	private JButton authorizationButton;
	private MainFrame mainWindow;
	private String loginEmail;
	private String passwordEmail;
	private String hostPOP;
	private Integer port = new Integer(465);
	private String socketFactory = "javax.net.ssl.SSLSocketFactory";
	private String auth = "true";

	public Runner() {
		final Runner parent = this;
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } 
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
				UnsupportedLookAndFeelException e1) { e1.printStackTrace(); }
		
		setTitle("Авторизация");
		setSize(350, 200);
		setLocation(((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2) - (parent.getWidth()/2), 
					((int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2) - (parent.getHeight()/2));
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);

		JPanel panelRunner = new JPanel();
		panelRunner.setBounds(15, 15, 315, 80);
		add(panelRunner);
		panelRunner.setLayout(new GridLayout(4, 1));
		panelRunner.add(new JLabel("Логин"));
		login.setText("l.yaruk1993@gmail.com");
		login.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode()==9){
					password.requestFocus();
				}
				if (e.getKeyCode()==10){
					authorizationButton.doClick();
				}
			}
		});
		panelRunner.add(login);
		panelRunner.add(new JLabel("Пароль"));
		password.setEchoChar('*');
		password.setText("14111993qw");
		password.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode()==10){
					authorizationButton.doClick();
				}
			}
		});
		panelRunner.add(password);
		panelRunner.setVisible(true);

		authorizationButton = new JButton("Войти");
		authorizationButton.setBounds(15, 130, 100, 25);
		authorizationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loginEmail = login.getText();
				Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
				Matcher matcher = pattern.matcher(loginEmail);
				if (matcher.matches()){
					passwordEmail = String.valueOf(password.getPassword());
					if (!(loginEmail.contains("yahoo"))) hostPOP = "pop."+loginEmail.substring((loginEmail.indexOf('@')+1), loginEmail.length());
					else hostPOP = "pop.mail.yahoo.com";
					System.out.println("hostPOP = "+hostPOP);
	
					Properties properties = System.getProperties();
					properties.put("mail.smtp.host", hostPOP);
					properties.put("mail.smtp.socketFactory.port", port);
					properties.put("mail.smtp.socketFactory.class", socketFactory);
					properties.put("mail.smtp.auth", auth);
					properties.put("mail.smtp.port", port);
					Session emailSession = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
								protected PasswordAuthentication getPasswordAuthentication() {
									return new PasswordAuthentication(loginEmail, passwordEmail);
								}
							});
					Store emailStore;
					Folder emailFolder;
					try {
						emailStore = emailSession.getStore("pop3s");
						emailStore.connect(hostPOP, loginEmail, passwordEmail);
						emailFolder = emailStore.getFolder("INBOX");
						emailFolder.open(Folder.READ_ONLY);
						setVisible(false);
						System.out.println("[runner is nonVisible]");
						new Thread(new Runnable() {
							public void run() {
								mainWindow = new MainFrame(loginEmail, passwordEmail, hostPOP);
								mainWindow.setVisible(true);
								System.out.println("[checker is visible]");
							}
						}).start();
					} 
					catch (MailConnectException mce) {
						JOptionPane.showMessageDialog(null, "Проблема с подключением!", "Ошибка",JOptionPane.ERROR_MESSAGE);
					} 
					catch (MessagingException me) {
						JOptionPane.showMessageDialog(null, "Неправильно вказаный пароль!", "Ошибка",JOptionPane.ERROR_MESSAGE);
					}
				}
				else JOptionPane.showMessageDialog(null, "Адрес электронной почты должен соответствовать шаблону\n\"user@domain.com\"", "Ошибка",JOptionPane.ERROR_MESSAGE);
			}
		});
		add(authorizationButton);

		JButton parametrLoadButton = new JButton("Конфиг.");
		parametrLoadButton.setBounds(120, 130, 100, 25);
		parametrLoadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					public void run() {
						JFileChooser jfc = new JFileChooser("D:\\sTudy\\Java\\wSpace\\kyrsuch\\config");
						int APPROVE = jfc.showDialog(parent, "Choose");
						if (APPROVE == JFileChooser.CANCEL_OPTION) 
							JOptionPane.showMessageDialog(null, "Файл не выбран!", "Ошибка!", JOptionPane.ERROR_MESSAGE);
						else{
							ConfigFile configer = new ConfigFile(port, hostPOP, true, "javax.net.ssl.SSLSocketFactory", jfc.getSelectedFile());
							configer.loadConfigFile();
						}
					}
				}).start();
			}
		});
		add(parametrLoadButton);
		
		addWindowListener(new WindowAdapter(){
			public void windowOpened(WindowEvent e){
				//authorizationButton.doClick();
			}
		});
	}

	public static void main(String arsg[]) {
		Runner run = new Runner();
		run.setVisible(true);
	}
}