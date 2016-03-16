package forMAIL;
public interface eMessage {
	String getContent();
	String getFrom();
	String getSubject();
	String getDate();
	boolean isRelated();
}
