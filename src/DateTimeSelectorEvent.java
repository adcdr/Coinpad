import java.util.EventObject;


public class DateTimeSelectorEvent extends EventObject{
	private String message;
	
	public DateTimeSelectorEvent(Object source, String message){
		super(source);
		
		this.message = message;
	}
	
	public String getMessage(){
		return message;
	}
	
	public String getActionCommand(){
		if (source.getClass() == DateTimeSelector.class)
			return ((DateTimeSelector)source).getActionCommand();
		
		return "";
	}
}