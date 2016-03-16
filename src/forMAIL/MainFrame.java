package forMAIL;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class MainFrame extends JFrame{
	
	private String loginEmailFirst;
	private String passwordEmailFirst;
	private String hostPOPFirst;
	
	public volatile int port = new Integer(465);
	
	private BrowserPanel browser;
	private JPopupMenu MessageMenu = new JPopupMenu();
	private СontactsBook contacts;
	private JTabbedPane tabbledPane = new JTabbedPane();
	
	private HashMap<String, JTable> tablesHash = new HashMap<String, JTable>();
	private HashMap<String, ArrayList<eMessage>> acountsMessageHash = new HashMap<String, ArrayList<eMessage>>();
	private HashMap<String, String> passwordsHash = new HashMap<String, String>();
	
	@SuppressWarnings("unchecked")
	public MainFrame(String login, String password, String host) {
		
		this.loginEmailFirst = login;
		this.passwordEmailFirst = password;
		this.hostPOPFirst = host;
		final JFrame parent = this;
		
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("temp.dat"))){
			contacts = new СontactsBook((ArrayList<Contact>) ois.readObject());
		} 
		catch (ClassNotFoundException | IOException e2) { System.out.println("ContactsBook Not Read From File"); }
		
		passwordsHash.put(loginEmailFirst, passwordEmailFirst);
		
		setTitle("Мои сообщения");
		setSize(1100, 550);
		setLocation(170, 110);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		
		ArrayList<eMessage> tempArray = new Checker().checkEMAIL(loginEmailFirst, passwordEmailFirst, hostPOPFirst);
		acountsMessageHash.put(loginEmailFirst.toLowerCase(), tempArray);
		
		//----------Контекстное меню---Начало------
		JMenuItem deleteMessage = new JMenuItem("Удалить сообщение");
		deleteMessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteMessage();
			}
		});
		
		JMenuItem addAccount = new JMenuItem("Записать контакт");
		addAccount.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JDialog addContactToBookDialog = new JDialog(parent,"Добавить новый контакт", true);
				
				JLabel newAcLoginLabel = new JLabel("Почтовый ящик");
				newAcLoginLabel.setBounds(30, 30, 100, 18);
				final JFormattedTextField newAcMail = new JFormattedTextField();
				newAcMail.setBounds(30, 50, 150, 18);
				newAcMail.setText((tablesHash.get(tabbledPane.getTitleAt(tabbledPane.getSelectedIndex())).getModel().getValueAt(
						tablesHash.get(tabbledPane.getTitleAt(tabbledPane.getSelectedIndex())).getSelectedRow(), 1).toString()));
				JLabel newAcNameLabel = new JLabel("Имя");
				newAcNameLabel.setBounds(30, 70, 100, 18);
				final JFormattedTextField newAcName = new JFormattedTextField();
				newAcName.setBounds(30, 90, 150, 18);
				
				JLabel newAcPhoneLabel = new JLabel("Телефон");
				newAcPhoneLabel.setBounds(30, 110, 100, 18);
				final JFormattedTextField newAcPhone = new JFormattedTextField();
				newAcPhone.setBounds(30, 130, 150, 18);
				
				JLabel newAcPsevdoLabel = new JLabel("Псевдоним");
				newAcPsevdoLabel.setBounds(30, 150, 100, 18);
				final JFormattedTextField newAcPsevdo = new JFormattedTextField();
				newAcPsevdo.setBounds(30, 170, 150, 18);
				
				JButton addButton = new JButton("Добавить");
				addButton.setBounds(30, 210, 150, 18);
				addButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						СontactsBook.addContact(new Contact(newAcMail.getText(), newAcName.getText(), newAcPhone.getText(), newAcPsevdo.getText()));
						addContactToBookDialog.setVisible(false);
					}
				});
				
				addContactToBookDialog.setLayout(null);
				addContactToBookDialog.add(newAcLoginLabel);
				addContactToBookDialog.add(newAcMail);
				addContactToBookDialog.add(newAcNameLabel);
				addContactToBookDialog.add(newAcName);
				addContactToBookDialog.add(newAcPhoneLabel);
				addContactToBookDialog.add(newAcPhone);
				addContactToBookDialog.add(newAcPsevdoLabel);
				addContactToBookDialog.add(newAcPsevdo);
				addContactToBookDialog.add(addButton);
				
				addContactToBookDialog.setSize(500, 300);
				addContactToBookDialog.setLocation(((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2) - (addContactToBookDialog.getWidth()/2), 
												   ((int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2) - (addContactToBookDialog.getHeight()/2));
				addContactToBookDialog.setVisible(true);
			}
		});
		
		MessageMenu.add(deleteMessage);
		MessageMenu.add(addAccount);
		//----------Контекстное меню------Конец------

		browser = new BrowserPanel(null);
		add(browser);
		
		if (acountsMessageHash.get(loginEmailFirst).size() == 0) {
			browser.reloadContent("Ящик пуст :(\nСообщений нет");
			new Thread(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(null, "Новых сообщений нет:(","Уведомление", JOptionPane.INFORMATION_MESSAGE);
					parent.requestFocus();
				}
			}).start();
		}

		ListMessageTableModel lmtm = new ListMessageTableModel(acountsMessageHash.get(loginEmailFirst));
		final JTable dataTable = new JTable(lmtm);
		tablesHash.put(loginEmailFirst, dataTable);
		
		dataTable.setColumnSelectionAllowed(false);
		dataTable.setRowSelectionAllowed(true);
		dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		dataTable.getColumnModel().getColumn(0).setPreferredWidth(170);
		dataTable.getColumnModel().getColumn(1).setPreferredWidth(130);
		
		dataTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {
				if (dataTable.getSelectedRow() != -1) {
					String content = acountsMessageHash.get(tabbledPane.getTitleAt(tabbledPane.getSelectedIndex())).get(dataTable.getSelectedRow()).getContent();
					if (acountsMessageHash.get(tabbledPane.getTitleAt(tabbledPane.getSelectedIndex())).get(dataTable.getSelectedRow()).isRelated())
						browser.reloadURL(content);
					else
						browser.reloadContent(content);
				}
			};
		});
		
		final JScrollPane scrollPaneForListMessages = new JScrollPane(dataTable);
		tabbledPane.add(loginEmailFirst, scrollPaneForListMessages);
		tabbledPane.setVisible(true);
		add(tabbledPane);

		final JButton editNewMessageButton = new JButton("Написать сообщение");
		editNewMessageButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Sender sender = new Sender(loginEmailFirst, passwordEmailFirst);
				sender.setVisible(true);
			}
		});
		add(editNewMessageButton);

		final JButton parametrSaveButton = new JButton("Сохранить конфиг.");
		parametrSaveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					public void run() {
						JFileChooser jfc = new JFileChooser("D:\\sTudy\\Java\\wSpace\\kyrsuch\\config");
						int APPROVE = jfc.showDialog(parent, "Choose");
						if (APPROVE == JFileChooser.CANCEL_OPTION) 
							JOptionPane.showMessageDialog(null, "Файл не выбран!", "Ошибка!", JOptionPane.INFORMATION_MESSAGE);
						else {
							ConfigFile configer = new ConfigFile(port, hostPOPFirst, true,"javax.net.ssl.SSLSocketFactory", jfc.getSelectedFile());
							configer.saveConfigFile();
							JOptionPane.showMessageDialog(null, "Конфигурация записана в "+jfc.getSelectedFile().toString());
						}
					}
				}).start();
			}
		});
		add(parametrSaveButton);

		final JButton refreshButton = new JButton("Обновить список");
		refreshButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateMailBox();
			}
		});
		add(refreshButton);

		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				browser.setBounds(450, 18, getWidth() - 480, getHeight() - 190);
				
				scrollPaneForListMessages.setBounds(18, 18, 420, getHeight() - 95);
				tabbledPane.setBounds(12, 12, 420, getHeight() - 90);
				
				editNewMessageButton.setBounds(getWidth() - 190, getHeight() - 160, 160, 25);
				parametrSaveButton.setBounds(getWidth() - 190, getHeight() - 130, 160, 25);
				refreshButton.setBounds(getWidth() - 190, getHeight() - 100, 160, 25);
				scrollPaneForListMessages.updateUI();
			}
		});
		
		JMenuBar menu = new JMenuBar();
		JMenu account = new JMenu("Аккаунт");
		JMenu properties = new JMenu("Параметры");
		
		JMenuItem newAccount = new JMenuItem("Добавить аккаунт");
		newAccount.setToolTipText("Подключение еще одного почтового ящика");
		
		JMenuItem sendMessage = new JMenuItem("Написать сообщение");
		sendMessage.setToolTipText("Написать и отправить сообщение");
		
		JMenuItem exit = new JMenuItem("Выход");
		exit.setToolTipText("Выход из приложения");
		
		JMenuItem programProp = new JMenuItem("Настройки");
		programProp.setToolTipText("Настройка приложения");
		
		JMenuItem addressBook = new JMenuItem("Контакты");
		addressBook.setToolTipText("Контакты");
		
		sendMessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Sender sender = new Sender(loginEmailFirst, passwordEmailFirst);
				sender.setVisible(true);
			}
		});
		
		newAccount.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JLabel newLoginLabel = new JLabel("Логин");
				JLabel newPassLabel = new JLabel("Пароль");
				final JFormattedTextField newLoginTextArea = new JFormattedTextField();
				newLoginTextArea.setText("forMailSend096@yandex.ru");
				final JPasswordField newPassArea = new JPasswordField();
				newPassArea.setText("14111993qw_");
				final JButton addAcButton = new JButton("Добавить");
				
				final JDialog addAcDialog = new JDialog(parent,"Добавить новый почтовый ящик", false);
				addAcDialog.setSize(315, 153);
				addAcDialog.setLayout(null);
				addAcDialog.add(newLoginLabel);
				addAcDialog.add(newLoginTextArea);
				addAcDialog.add(newPassLabel);
				addAcDialog.add(newPassArea);
				addAcDialog.add(addAcButton);
				addAcDialog.setVisible(true);
				addAcDialog.setLocation(((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2) - (addAcDialog.getWidth()/2), 
										((int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2) - (addAcDialog.getHeight()/2));
				newLoginLabel.setBounds(15, 15, 40, 15);
				newLoginTextArea.setBounds(75, 15, 210, 17);
				newPassLabel.setBounds(15, 45, 60, 15);
				newPassArea.setBounds(75, 45, 210, 16);
				addAcButton.setBounds(addAcDialog.getWidth() - 130, addAcDialog.getHeight() - 77, 100, 25);
				
				newLoginTextArea.addKeyListener(new KeyAdapter() {
					public void keyPressed(KeyEvent e) {
						if (e.getKeyCode()==10){
							addAcButton.doClick();
						}
					}
				});
				
				addAcButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						final String login = newLoginTextArea.getText().toLowerCase();
						Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
						Matcher matcher = pattern.matcher(login);
						if (matcher.matches()){
							if (!(acountsMessageHash.containsKey(login))){
								String password = String.valueOf(newPassArea.getPassword());
								passwordsHash.put(login, password);
								String host;
								if (newLoginTextArea.getText().contains("yahoo")) host = "pop.mail.yahoo.com";
								else host = "pop." + newLoginTextArea.getText().substring((newLoginTextArea.getText().indexOf('@')+1), newLoginTextArea.getText().length());
								ArrayList<eMessage> tempArray = new Checker().checkEMAIL(login, password, host);
								if (tempArray != null){
									acountsMessageHash.put(login, tempArray);
									ListMessageTableModel tempLMTM = new ListMessageTableModel(acountsMessageHash.get(login));
									final JTable tempDataTable = new JTable(tempLMTM);
									tablesHash.put(login, tempDataTable);
									JScrollPane tempPanel = new JScrollPane(tempDataTable);
									tempPanel.setBounds(18, 18, 420, getHeight() - 95);
									tempDataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
									tempDataTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
									tempDataTable.getColumnModel().getColumn(0).setPreferredWidth(170);
									tempDataTable.getColumnModel().getColumn(1).setPreferredWidth(130);
									tempDataTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
										public void valueChanged(ListSelectionEvent e) {
											if ((acountsMessageHash.get(tabbledPane.getTitleAt(tabbledPane.getSelectedIndex())).size() >= 0) && 
												(tablesHash.get(login).getSelectedRow() >= 0)){
												String content = acountsMessageHash.get(tabbledPane.getTitleAt(tabbledPane.getSelectedIndex())).get(
													tablesHash.get(login).getSelectedRow()).getContent();
												browser.reloadContent(content);
											}
											else if ((acountsMessageHash.get(tabbledPane.getTitleAt(tabbledPane.getSelectedIndex())).size() >= 0) && 
													(tablesHash.get(login).getSelectedRow() == -1)){
												browser.reloadContent(null);
											} 
											else browser.reloadContent("Ящик пуст :(\nСообщений нет");
										};
									});
									tempDataTable.addMouseListener(new MenuMouseAdapter(MessageMenu));
									tabbledPane.add(login, tempPanel);
									addAcDialog.dispose();
								}
							}
							else JOptionPane.showMessageDialog(null, login+" уже добавлен","Уведомление", JOptionPane.INFORMATION_MESSAGE);
						}
						else JOptionPane.showMessageDialog(null, "Адрес электронной почты должен "
								+ "соответствовать шаблону\n\"user@domain.com\"","Ошибка",JOptionPane.ERROR_MESSAGE);
					}
				});
				
				addWindowListener(new WindowAdapter() {
					public void windowClosed(WindowEvent e){
						addAcDialog.dispose();
					}
				});
			}
		});
		ImageIcon addIcon = new ImageIcon("icon\\add.png");
		newAccount.setIcon(addIcon);
		
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		ImageIcon exitIcon = new ImageIcon("icon\\exit.png");
		exit.setIcon(exitIcon);
		
		programProp.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Configuration config = new Configuration((MainFrame)parent);
				config.setVisible(true);
			}
		});
		ImageIcon propIcon = new ImageIcon("icon\\prop.png");
		programProp.setIcon(propIcon);
		
		addressBook.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				contacts.setVisible(true);
			}
		});
		ImageIcon addressIcon = new ImageIcon("icon\\address.png");
		addressBook.setIcon(addressIcon);
		
		ImageIcon sendMessageIcon = new ImageIcon("icon\\send.png");
		sendMessage.setIcon(sendMessageIcon);
		
		account.add(newAccount);
		account.add(sendMessage);
		account.addSeparator();
		account.add(exit);
		
		properties.add(programProp);
		properties.add(addressBook);
		
		menu.add(account);
		menu.add(properties);
		parent.setJMenuBar(menu);
		
		dataTable.addMouseListener(new MenuMouseAdapter(MessageMenu));
		
		tabbledPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if ((acountsMessageHash.get(tabbledPane.getTitleAt(tabbledPane.getSelectedIndex())).size() > 0) && 
					(tablesHash.get(tabbledPane.getTitleAt(tabbledPane.getSelectedIndex())).getSelectedRow() >= 0)){
					String content = acountsMessageHash.get(tabbledPane.getTitleAt(tabbledPane.getSelectedIndex())).get(
							tablesHash.get(tabbledPane.getTitleAt(tabbledPane.getSelectedIndex())).getSelectedRow()).getContent();
					browser.reloadContent(content);
				}
				else if (acountsMessageHash.get(tabbledPane.getTitleAt(tabbledPane.getSelectedIndex())).size() > 0) {
					browser.reloadContent(acountsMessageHash.get(tabbledPane.getTitleAt(tabbledPane.getSelectedIndex())).get(0).getContent());
				}
				else browser.reloadContent("Ящик пуст :(\nСообщений нет");
			}
		});
		
		addWindowListener(new WindowAdapter() {
			@SuppressWarnings("static-access")
			public void windowClosing(WindowEvent e) {
				
				try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("temp.dat"))) { oos.writeObject(contacts.getArray()); }
				catch (IOException e1) { e1.printStackTrace(); }
				
				Path dir = Paths.get("D:\\sTudy\\Java\\wSpace\\kyrsuch\\htmlForMultipartRelated");
				try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.{html}")) {
					for (Path entry: stream) { Files.delete(entry); }
				}
				catch (IOException e1) { e1.printStackTrace(); }
			}
		});
	}
	
	private void deleteMessage(){
		Runnable delete = new MessageEraser(tabbledPane.getTitleAt(tabbledPane.getSelectedIndex()), 
				passwordsHash.get(tabbledPane.getTitleAt(tabbledPane.getSelectedIndex())),
				tablesHash.get(tabbledPane.getTitleAt(tabbledPane.getSelectedIndex())).getSelectedRow());
		Thread t = new Thread(delete);
		t.start();
		acountsMessageHash.get(tabbledPane.getTitleAt(tabbledPane.getSelectedIndex())).remove(
				tablesHash.get(tabbledPane.getTitleAt(tabbledPane.getSelectedIndex())).getSelectedRow());
		tablesHash.get(tabbledPane.getTitleAt(tabbledPane.getSelectedIndex())).repaint();
		tablesHash.get(tabbledPane.getTitleAt(tabbledPane.getSelectedIndex())).updateUI();
		browser.reloadContent(null);
	}
	
	private void updateMailBox(){
		final String[] accounts = new String[acountsMessageHash.keySet().size()];
		for (int i=0;i<accounts.length;i++){
			accounts[i] = (String)acountsMessageHash.keySet().toArray()[i];
			final String tLogin = accounts[i];
			new Thread(new Runnable() {
				public void run() {
					acountsMessageHash.remove(tLogin);
					
					String host;
					if (tLogin.contains("yahoo")) host = "pop.mail.yahoo.com";
					else host = "pop." + tLogin.substring((tLogin.indexOf('@')+1), tLogin.length());
					
					ArrayList<eMessage> tempArray = new Checker().checkEMAIL(tLogin, passwordsHash.get(tLogin), host);
					acountsMessageHash.put(tLogin, tempArray);
					
					ListMessageTableModel lmtm = new ListMessageTableModel(acountsMessageHash.get(tLogin));
					lmtm.fireTableDataChanged();
					tablesHash.get(tLogin).setModel(lmtm);
				}
			},"Thread_"+(i+1)).start();
		}
	}
}

class MenuMouseAdapter extends MouseAdapter{
	private JPopupMenu OnMessageMenu;
	
	public MenuMouseAdapter(JPopupMenu omm){
		super();
		this.OnMessageMenu = omm;
	}
	
	public void mousePressed(MouseEvent me) {
		if (me.isPopupTrigger()){
			OnMessageMenu.show(me.getComponent(), me.getX(), me.getY());
		}
	}
	
	public void mouseReleased(MouseEvent me) {
		if (me.isPopupTrigger()){
			OnMessageMenu.show(me.getComponent(), me.getX(), me.getY());
			JTable table = (JTable)me.getComponent();
			int column = table.columnAtPoint(me.getPoint());
			int row = table.rowAtPoint(me.getPoint());
			table.setColumnSelectionInterval(column, column);
			table.setRowSelectionInterval(row, row);
		}
	}
}

@SuppressWarnings("serial")
class ListMessageTableModel extends AbstractTableModel {

	private ArrayList<eMessage> data;

	public ListMessageTableModel(ArrayList<eMessage> array) {
		super();
		this.data = array;
	}

	public int getRowCount() {
		return data.size();
	}

	public int getColumnCount() {
		return 3;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return data.get(rowIndex).getSubject();
		case 1:
			return data.get(rowIndex).getFrom();
		case 2:
			return data.get(rowIndex).getDate();
		default:
			return null;
		}
	}

	public String getColumnName(int c) {
		String result = "";
		switch (c) {
		case 0:
			result = "Тема";
			break;
		case 1:
			result = "От";
			break;
		case 2:
			result = "Дата";
			break;
		}
		return result;
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
}

@SuppressWarnings("serial")
class Configuration extends JDialog{
	private AtomicIntegerFieldUpdater<MainFrame> portNew = AtomicIntegerFieldUpdater.newUpdater(MainFrame.class, "port");
	
	public Configuration(final MainFrame mainF){
		final JDialog parent = this;
		setModal(true);
		setTitle("Настройки");
		setSize(900, 400);
		setLocation(((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2) - (getWidth()/2), 
					((int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2) - (getHeight()/2));
		setResizable(false);
		setLayout(null);
		
		JLabel portLabel = new JLabel("Порт");
		portLabel.setBounds(20, 19, 50, 18);
		add(portLabel);
		
		final JTextArea portTextArea = new JTextArea();
		portTextArea.setBounds(70, 20, 60, 18);
		portTextArea.setText("465");
		add(portTextArea);
		
		JButton apply = new JButton("Применить");
		apply.setBounds(getWidth() - 170, getHeight() - 60, 150, 20);
		apply.addActionListener(new ActionListener() {
			@SuppressWarnings("static-access")
			public void actionPerformed(ActionEvent e) {
				portNew.set(mainF, (int)Integer.valueOf(portTextArea.getText()));
				portNew.newUpdater(MainFrame.class, "port");
				parent.setVisible(false);
			}
		});
		add(apply);
	}
}

@SuppressWarnings("serial")
class BrowserPanel extends JFXPanel{
	private WebEngine engine;
	private WebView view;
	private Scene scene;
	
	public BrowserPanel(final String url){
		super();
		
		Platform.runLater(new Runnable() {
			public void run() {
				view = new WebView();
				engine = view.getEngine();
				scene = new Scene(view);
				setScene(scene);
				
				engine.loadContent(url);
			}
		});
	}
	
	public void reloadContent(final String reload){
		Platform.runLater(new Runnable() {
			public void run() {
				engine.loadContent(reload);
			}
		});
	}
	
	public void reloadURL(final String URL){
		Platform.runLater(new Runnable() {
			public void run() {
				engine.load(URL);
			}
		});
	}
}