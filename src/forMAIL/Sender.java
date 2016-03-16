package forMAIL;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class Sender extends JFrame {

	private String loginEmail;
	private String passwordEmail;
	private Integer port = new Integer(465);
	private String hostSMTP;

	JFormattedTextField receiverArea = new JFormattedTextField();
	JTextArea textOfMessageArea = new JTextArea();
	JFormattedTextField subjArea = new JFormattedTextField();
	JButton sendMessage;

	public Sender(String logined, String acPassword) {
		
		final Sender parent = this;
		this.loginEmail = logined;
		this.passwordEmail = acPassword;
		
		setTitle("Новое сообщение");
		setSize(900, 400);
		setLocation(((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2) - (parent.getWidth()/2), 
					((int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2) - (parent.getHeight()/2));
		setResizable(false);

		setLayout(null);

		JLabel lab1 = new JLabel("Получатель");
		lab1.setBounds(20, 17, 150, 15);
		add(lab1);
		receiverArea.setBounds(20, 35, 150, 20);
		add(receiverArea);
		receiverArea.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode()==9){
					subjArea.requestFocus();
				}
			}
		});

		JLabel subjlabel = new JLabel("Тема сообщения");
		subjlabel.setBounds(20, 57, 150, 15);
		add(subjlabel);
		subjArea.setBounds(20, 73, 150, 20);
		subjArea.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode()==9){
					textOfMessageArea.requestFocus();
				}
			}
		});
		add(subjArea);

		JLabel textlabel = new JLabel("Текст сообщения");
		textlabel.setBounds(20, 101, 150, 15);
		textOfMessageArea.setFont(new Font("Courier", Font.PLAIN, 14));
		add(textlabel);

		JScrollPane sp = new JScrollPane(textOfMessageArea);
		sp.setBounds(20, 120, parent.getWidth() - 45, parent.getHeight() - 200);
		add(sp);

		sendMessage = new JButton("Отправить");
		sendMessage.setBounds(parent.getWidth() - 510, parent.getHeight() - 70, 160, 25);
		sendMessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ((receiverArea.getText().length() == 0) || textOfMessageArea.getText().length() == 0)
					JOptionPane.showMessageDialog(null, "Поля должны быть заполнены!", "Увидомление", JOptionPane.INFORMATION_MESSAGE);
				else sendEMessage();
			}
		});
		add(sendMessage);

		JButton save = new JButton("Сохранить");
		save.setBounds(parent.getWidth() - 345, parent.getHeight() - 70, 160, 25);
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fchoose = new JFileChooser("D:\\sTudy\\Java\\wSpace\\kyrsuch\\messages");
				int APPROVE = fchoose.showDialog(parent, "Choose");
				if (APPROVE == JFileChooser.CANCEL_OPTION) 
					JOptionPane.showMessageDialog(null, "Файл не выбран!", "Ошибка!", JOptionPane.ERROR_MESSAGE);
				else {
					char[] buffer = new char[textOfMessageArea.getText().length()];
					textOfMessageArea.getText().getChars(0,textOfMessageArea.getText().length(), buffer, 0);
					try (FileWriter fw = new FileWriter(fchoose.getSelectedFile())) {
						for (int i = 0; i < buffer.length; i++) {
							fw.write(buffer[i]);
						}
						JOptionPane.showMessageDialog(null, "Текст сохранен в "+fchoose.getSelectedFile().toString(), "Увидомление", JOptionPane.INFORMATION_MESSAGE);
					} 
					catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		add(save);

		JButton load = new JButton("Загрузить");
		load.setBounds(parent.getWidth() - 180, parent.getHeight() - 70, 160, 25);
		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fchoose = new JFileChooser("D:\\sTudy\\Java\\wSpace\\kyrsuch\\messages");
				int APPROVE = fchoose.showDialog(parent, "Choose");
				if (APPROVE == JFileChooser.CANCEL_OPTION) 
					JOptionPane.showMessageDialog(null, "Файл не выбран!", "Ошибка!", JOptionPane.ERROR_MESSAGE);
				else {
					try (BufferedReader fr = new BufferedReader(new FileReader(fchoose.getSelectedFile()))) {
						String line;
						String allText = "";
						while ((line = fr.readLine()) != null) {
							allText += line + "\n";
						}
						textOfMessageArea.setText(allText);
						JOptionPane.showMessageDialog(null, "Текст загружен из "+fchoose.getSelectedFile().toString(), "Увидомление", JOptionPane.INFORMATION_MESSAGE);
					}
					catch (FileNotFoundException ex) {
						JOptionPane.showMessageDialog(null, "Файл не найден!");
					} 
					catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		add(load);
	}

	public void sendEMessage() {		
		if (!(loginEmail.contains("yahoo"))) hostSMTP = "smtp."+loginEmail.substring((loginEmail.indexOf('@')+1), loginEmail.length());
		else hostSMTP = "smtp.mail.yahoo.com";

		Properties properties = System.getProperties();
		properties.put("mail.smtp.host", hostSMTP);
		properties.put("mail.smtp.socketFactory.port", port);
		properties.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.port", port);
		Session emailSession = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(loginEmail, passwordEmail);
					}
				});
		try {
			Message message = new MimeMessage(emailSession);
			message.setFrom(new InternetAddress(loginEmail));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiverArea.getText()));
			message.setSubject(subjArea.getText());
			message.setText(textOfMessageArea.getText());
			Transport.send(message);
			JOptionPane.showMessageDialog(null, "Сообщение отправлено");
		} 
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}