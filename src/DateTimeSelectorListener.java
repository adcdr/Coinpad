import java.util.EventListener;

public interface DateTimeSelectorListener extends EventListener {
	public void dateTimeSelectorEventOccurred(DateTimeSelectorEvent evt);
}