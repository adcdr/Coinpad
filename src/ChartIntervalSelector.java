import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;
import javax.swing.text.DocumentFilter;
import javax.swing.text.*;


public class ChartIntervalSelector implements FocusListener{
	private static final Color SELECTED_COLOR = new Color(0x3399FF); 
	
	public JPanel frame;
	int interval = 1;
	MaskFormatter textFieldFormatter;
	public JFormattedTextField formattedTextField;
	private String actionCommand;
	protected EventListenerList dateTimeListenerList = new EventListenerList();
	boolean notSaved = false;
	
	public ChartIntervalSelector(){
        frame = new JPanel();
        
        FlowLayout layout = new FlowLayout();
        layout.setHgap(0);
        frame.setLayout(layout);
        
        formattedTextField = new JFormattedTextField();
        
        DocumentFilter onlyNumberFilter = new IntervalDocumentFilter();
        ((AbstractDocument)formattedTextField.getDocument()).setDocumentFilter(onlyNumberFilter);
        
        formattedTextField.setText(Integer.toString(interval));
        formattedTextField.setFont(formattedTextField.getFont().deriveFont(Font.BOLD));
        formattedTextField.setBorder(null);
        formattedTextField.setPreferredSize(new Dimension(16, 23));
        formattedTextField.setBackground(frame.getBackground());
        formattedTextField.addFocusListener(this);
        formattedTextField.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e){
				processKeyPress(e);
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
			}
		});
		
		frame.add(formattedTextField);
	}
	
	public int getInterval(){
		return interval;
	}

	public void processOnFocus(){
		formattedTextField.setBackground(Color.red);
	}
	
	public void processKeyPress(KeyEvent e){
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
			formattedTextField.setText("" + interval);
		}
		else if (e.getKeyCode() == KeyEvent.VK_ENTER){
			if (formattedTextField.getText().trim().isEmpty() 
					|| Integer.valueOf(formattedTextField.getText()) == 0){
				
				formattedTextField.setText(String.valueOf(interval));
			}
			else{
				interval = Integer.parseInt(formattedTextField.getText());
				formattedTextField.setText(String.valueOf(Integer.parseInt(formattedTextField.getText())));
			}
			
			notSaved = false;
			
			formattedTextField.setForeground(Color.black);
			
			fireEvent(new DateTimeSelectorEvent(this, "return"));
			
			frame.requestFocus(true);
			
			return;
		}
		else if (e.getKeyCode() == KeyEvent.VK_UP){
			if (interval < 99){
				interval++;
				
				formattedTextField.setText("" + interval);
			}
		}
		else if (e.getKeyCode() == KeyEvent.VK_DOWN){
			if (interval > 1)
				interval--;
			
			formattedTextField.setText("" + interval);
		}
		
		if (!formattedTextField.getText().trim().isEmpty())
			try { 
				int userInput = Integer.parseInt(formattedTextField.getText().toString().trim());
				
				if (userInput > 0)
					interval = userInput;
					
		    } catch(NumberFormatException ex) { 
		    	formattedTextField.setText(String.valueOf(interval));
		        System.out.println("ChartIntervalSelector - processKeyPress");; 
		    }
		
		
		notSaved = true;
		formattedTextField.setForeground(Color.red);
		fireEvent(new DateTimeSelectorEvent(this, "changed"));
	}
	
	public void setIntervals(int intervals){
		this.interval = intervals;
		
		formattedTextField.setText(String.valueOf(interval));
	}
	
	public void setActionCommand(String actionCommand){
		this.actionCommand = actionCommand;
	}
	
	public String getActionCommand(){
		return actionCommand;
	}
	
	public void addDateTimeListener(DateTimeSelectorListener listener) {
		dateTimeListenerList.add(DateTimeSelectorListener.class, listener);
	}

	public void removeDateTimeListener(DateTimeSelectorListener listener) {
		dateTimeListenerList.remove(DateTimeSelectorListener.class, listener);
	}
	
	private void fireEvent(DateTimeSelectorEvent e) {
		Object[] listeners = dateTimeListenerList.getListenerList();
		
		// Each listener occupies two elements - the listener class and the listener instance
		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == DateTimeSelectorListener.class) {
				((DateTimeSelectorListener) listeners[i + 1]).dateTimeSelectorEventOccurred(e);
			}
		}
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		formattedTextField.setBackground(SELECTED_COLOR);
		formattedTextField.setCaretPosition(formattedTextField.getText().length());
	}

	@Override
	public void focusLost(FocusEvent e){
		if (formattedTextField.getText().trim().isEmpty() 
				|| Integer.valueOf(formattedTextField.getText()) == 0){
			notSaved = false;
			
			formattedTextField.setForeground(Color.black);
			formattedTextField.setText(String.valueOf(interval));
		}
		
		formattedTextField.setBackground(frame.getBackground());
	}
}