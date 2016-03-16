package forMAIL;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class СontactsBook extends JFrame {
	
	private static ArrayList<Contact> contactsArray = new ArrayList<Contact>();
	private static JTable table;
	private static JFrame parent;
	
	@SuppressWarnings("static-access")
	СontactsBook(ArrayList<Contact> arr){
		
		parent = this;
		this.contactsArray = arr;
		setTitle("Контакты");
		setSize(900, 400);
		setLocation(((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2) - (getWidth()/2), 
					((int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2) - (getHeight()/2));
		setLayout(null);
		
		ContactsBookModel cbm = new ContactsBookModel(contactsArray);
		table = new JTable(cbm);
		table.getColumnModel().getColumn(0).setPreferredWidth(170);
		table.getColumnModel().getColumn(1).setPreferredWidth(130);
		table.getColumnModel().getColumn(2).setPreferredWidth(70);
		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);
		scrollPane.setBounds(20, 20, getWidth() - table.getWidth() - 55, getHeight() - table.getHeight()-75);
		
		final JPopupMenu menu = new JPopupMenu();
		JMenuItem delete = new JMenuItem("Удалить");
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				contactsArray.remove(table.getSelectedRow());
				table.repaint();
				table.updateUI();
			}
		});
		
		JMenuItem change = new JMenuItem("Редактировать");
		change.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JDialog addContactToBookDialog = new JDialog(parent,"Редактировать контакт", true);
				JLabel newAcLoginLabel = new JLabel("Почтовый ящик");
				newAcLoginLabel.setBounds(30, 30, 100, 18);
				final JFormattedTextField newAcMail = new JFormattedTextField();
				newAcMail.setBounds(30, 50, 150, 18);
				newAcMail.setText(contactsArray.get(table.getSelectedRow()).getMail());
				JLabel newAcNameLabel = new JLabel("Имя");
				newAcNameLabel.setBounds(30, 70, 100, 18);
				final JFormattedTextField newAcName = new JFormattedTextField();
				newAcName.setBounds(30, 90, 150, 18);
				newAcName.setText(contactsArray.get(table.getSelectedRow()).getName());
				
				JLabel newAcPhoneLabel = new JLabel("Телефон");
				newAcPhoneLabel.setBounds(30, 110, 100, 18);
				final JFormattedTextField newAcPhone = new JFormattedTextField();
				newAcPhone.setBounds(30, 130, 150, 18);
				newAcPhone.setText(contactsArray.get(table.getSelectedRow()).getTelephone());
				
				JLabel newAcPsevdoLabel = new JLabel("Псевдоним");
				newAcPsevdoLabel.setBounds(30, 150, 100, 18);
				final JFormattedTextField newAcPsevdo = new JFormattedTextField();
				newAcPsevdo.setBounds(30, 170, 150, 18);
				newAcPsevdo.setText(contactsArray.get(table.getSelectedRow()).getPsevdo());
				
				JButton addButton = new JButton("Редактировать");
				addButton.setBounds(30, 210, 150, 18);
				addButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						contactsArray.remove(table.getSelectedRow());
						addContact(new Contact(newAcMail.getText(), newAcName.getText(), newAcPhone.getText(), newAcPsevdo.getText()));
						addContactToBookDialog.setVisible(false);
						table.repaint();
						table.updateUI();
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
		
		menu.add(delete);
		menu.add(change);
		
		table.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me) {
				if (me.isPopupTrigger()){
					menu.show(me.getComponent(), me.getX(), me.getY());
				}
			}
			public void mouseReleased(MouseEvent me) {
				if (me.isPopupTrigger()){
					menu.show(me.getComponent(), me.getX(), me.getY());
					int column = table.columnAtPoint(me.getPoint());
					int row = table.rowAtPoint(me.getPoint());
					table.setColumnSelectionInterval(column, column);
					table.setRowSelectionInterval(row, row);
				}
			}
		});
	}
	
	public static void addContact(Contact c){
		contactsArray.add(c);
	}
	
	public static ArrayList<Contact> getArray(){
		return contactsArray;
	}
}

class Contact implements Serializable{
	
	private static final long serialVersionUID = -1986708510967998223L;
	private String mail;
	private String name;
	private String telephne;
	private String psevdo;
	
	public Contact(String m, String n, String t, String p){
		this.mail = m;
		this.name = n;
		this.telephne = t;
		this.psevdo = p;
	}
	
	public String getMail(){
		return mail;
	}
	public String getName(){
		return name;
	}
	public String getTelephone(){
		return telephne;
	}
	public String getPsevdo(){
		return psevdo;
	}
}

@SuppressWarnings("serial")
class ContactsBookModel extends AbstractTableModel{
	
	private ArrayList<Contact> array;
	
	ContactsBookModel(ArrayList<Contact> arr){
		super();
		this.array = arr;
	}
	
	public int getRowCount() {
		return array.size();
	}

	public int getColumnCount() {
		return 4;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return array.get(rowIndex).getMail();
		case 1:
			return array.get(rowIndex).getName();
		case 2:
			return array.get(rowIndex).getTelephone();
		case 3:
			return array.get(rowIndex).getPsevdo();
		default:
			return null;
		}
	}
	
	public String getColumnName(int c) {
		switch (c) {
		case 0:
			return "Адрес";
		case 1:
			return "Имя";
		case 2:
			return "Телефон";
		case 3:
			return "Псевдоним";
		default:
			return "";
		}
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
}