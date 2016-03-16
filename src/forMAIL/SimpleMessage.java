package forMAIL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SimpleMessage implements eMessage{
	
	private String from;
	private String text;
	private Date date;
	private String subj;

	SimpleMessage(String f, String s, Date date, String t){
		this.date = date;
		this.from=f;
		this.text=t;
		this.subj=s;
	}
	
	public String getContent() {
		return text;
	}

	public String getFrom() {
		if (from.contains("<")) return from.substring(from.indexOf('<')+1,from.indexOf('>'));
		else return from;
	}

	public String getSubject() {
		return subj;
	}
	
	public String getDate(){
		try { return new SimpleDateFormat().format(date); }
		catch (java.lang.NullPointerException npe) { return new SimpleDateFormat().format(Calendar.getInstance().getTime()).toString();	}
	}

	public boolean isRelated() {
		return false;
	}
}