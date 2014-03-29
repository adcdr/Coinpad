import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.EventListenerList;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


public class DateTimeSelector implements FocusListener{
	private static final int DAY = 1;
	private static final int MONTH = 2;
	private static final int YEAR = 3;
	private static final int HOUR = 4;
	private static final int MINUTE = 5;
	
	private static final Color SELECTED_COLOR = new Color(0x3399FF); 
	
	public JPanel frame;
	DateTime dateTime, minDateTime, maxDateTime;
	DateTimeFormatter dateTimeFormatter;
	JTextArea  dayTextArea, monthTextArea, yearTextArea, hourTextArea, minuteTextArea;
	JLabel separator1Label, separator2Label, separator3Label, separator4Label;
	private String actionCommand;
	protected EventListenerList dateTimeListenerList = new EventListenerList();
	boolean notSaved = false;
	
	public DateTimeSelector(){
		minDateTime = null;
		maxDateTime = null;
		
        frame = new JPanel();
        
        FlowLayout layout = new FlowLayout();
        layout.setHgap(0);
        frame.setLayout(layout);
        
		dayTextArea = new JTextArea();
		dayTextArea.setFont(dayTextArea.getFont().deriveFont(Font.BOLD));
		dayTextArea.setEditable(false);
		dayTextArea.setBackground(frame.getBackground());
		dayTextArea.addFocusListener(this);
		dayTextArea.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e){
				processKeyPress(DAY, e);
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
			}
		});
		frame.add(dayTextArea);
		
		separator1Label = new JLabel("/");
		separator1Label.setBackground(frame.getBackground());
		separator1Label.setOpaque(true);
		frame.add(separator1Label);
		
		monthTextArea = new JTextArea();
		monthTextArea.setFont(dayTextArea.getFont().deriveFont(Font.BOLD));
		monthTextArea.setEditable(false);
		monthTextArea.setBackground(frame.getBackground());
		monthTextArea.addFocusListener(this);
		monthTextArea.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e){
				processKeyPress(MONTH, e);
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
			}
		});
		frame.add(monthTextArea);
		
		separator2Label = new JLabel("/");
		separator2Label.setBackground(frame.getBackground());
		separator2Label.setOpaque(true);
		frame.add(separator2Label);
		
		yearTextArea = new JTextArea();
		yearTextArea.setFont(dayTextArea.getFont().deriveFont(Font.BOLD));
		yearTextArea.setEditable(false);
		yearTextArea.setBackground(frame.getBackground());
		yearTextArea.addFocusListener(this);
		yearTextArea.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e){
				processKeyPress(YEAR, e);
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
			}
		});
		frame.add(yearTextArea);
		
		separator3Label = new JLabel("  ");
		separator3Label.setBackground(frame.getBackground());
		separator3Label.setOpaque(true);
		frame.add(separator3Label);
		
		hourTextArea = new JTextArea();
		hourTextArea.setFont(dayTextArea.getFont().deriveFont(Font.BOLD));
		hourTextArea.setEditable(false);
		hourTextArea.setBackground(frame.getBackground());
		hourTextArea.addFocusListener(this);
		hourTextArea.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e){
				processKeyPress(HOUR, e);
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
			}
		});
		frame.add(hourTextArea);
		
		separator4Label = new JLabel(":");
		separator4Label.setBackground(frame.getBackground());
		separator4Label.setOpaque(true);
		frame.add(separator4Label);
		
		minuteTextArea = new JTextArea();
		minuteTextArea.setFont(dayTextArea.getFont().deriveFont(Font.BOLD));
		minuteTextArea.setEditable(false);
		minuteTextArea.setBackground(frame.getBackground());
		minuteTextArea.addFocusListener(this);
		minuteTextArea.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e){
				processKeyPress(MINUTE, e);
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
			}
		});
		frame.add(minuteTextArea);
		
		setDateTime(DateTime.now());
	}
	
	public void setDateTime(DateTime dateTime){
		if (notSaved){
			dayTextArea.setForeground(Color.red);
			separator1Label.setForeground(Color.red);
			monthTextArea.setForeground(Color.red);
			separator2Label.setForeground(Color.red);
			yearTextArea.setForeground(Color.red);
			separator3Label.setForeground(Color.red);
			hourTextArea.setForeground(Color.red);
			separator4Label.setForeground(Color.red);
			minuteTextArea.setForeground(Color.red);
		}
		else {
			dayTextArea.setForeground(Color.black);
			separator1Label.setForeground(Color.black);
			monthTextArea.setForeground(Color.black);
			separator2Label.setForeground(Color.black);
			yearTextArea.setForeground(Color.black);
			separator3Label.setForeground(Color.black);
			hourTextArea.setForeground(Color.black);
			separator4Label.setForeground(Color.black);
			minuteTextArea.setForeground(Color.black);
		}
		
		this.dateTime = dateTime;
		this.dateTime.minusSeconds(this.dateTime.getSecondOfMinute());
		
		dateTimeFormatter = DateTimeFormat.forPattern("dd");
	    dayTextArea.setText(dateTimeFormatter.print(dateTime));
	    
	    dateTimeFormatter = DateTimeFormat.forPattern("MM");
		monthTextArea.setText(dateTimeFormatter.print(dateTime));
		
		dateTimeFormatter = DateTimeFormat.forPattern("YYYY");
		yearTextArea.setText(dateTimeFormatter.print(dateTime));
		
		dateTimeFormatter = DateTimeFormat.forPattern("HH");
		hourTextArea.setText(dateTimeFormatter.print(dateTime));
		
		dateTimeFormatter = DateTimeFormat.forPattern("mm");
		minuteTextArea.setText(dateTimeFormatter.print(dateTime));
	}
	
	public DateTime getDateTime(){
		return this.dateTime;
	}

	public void processOnFocus(int source){
		select(source);
	}
	
	public void setMinimum(DateTime minDateTime){
		this.minDateTime = minDateTime;
	}
	
	public void setMaximum(DateTime maxDateTime){
		this.maxDateTime = maxDateTime;
	}
	
	public void processKeyPress(int source, KeyEvent e){
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
			notSaved = true;
			
			setDateTime(DateTime.now());
			
			return;
		}
		else if (e.getKeyCode() == KeyEvent.VK_ENTER){
			notSaved = false;
			
			setDateTime(this.dateTime);
			
			select(-1);
			
			fireEvent(new DateTimeSelectorEvent(this, "return"));
			
			return;
		}
		else if (source == DAY){
			if (e.getKeyCode() == KeyEvent.VK_RIGHT){
				select(MONTH);
			}
			else if (e.getKeyCode() == KeyEvent.VK_LEFT){
				select(MINUTE);
			}
			else if (e.getKeyCode() == KeyEvent.VK_UP){
				if (maxDateTime == null || !this.dateTime.plusDays(1).isAfter(maxDateTime))
					if (minDateTime == null || !this.dateTime.plusDays(1).isBefore(minDateTime))
						this.dateTime = this.dateTime.plusDays(1);
			}
			else if (e.getKeyCode() == KeyEvent.VK_DOWN){				
				if (maxDateTime == null || !this.dateTime.minusDays(1).isAfter(maxDateTime))
					if (minDateTime == null || !this.dateTime.minusDays(1).isBefore(minDateTime))
					this.dateTime = this.dateTime.minusDays(1);
			}
		}
		else if (source == MONTH){
			if (e.getKeyCode() == KeyEvent.VK_RIGHT){
				select(YEAR);
			}
			else if (e.getKeyCode() == KeyEvent.VK_LEFT){
				select(DAY);
			}
			else if (e.getKeyCode() == KeyEvent.VK_UP){
				if (maxDateTime == null || !this.dateTime.plusMonths(1).isAfter(maxDateTime))
					if (minDateTime == null || !this.dateTime.plusMonths(1).isBefore(minDateTime)){
						this.dateTime = this.dateTime.plusMonths(1);
						
						//make sure day field is not larger than number of days in month
						int daysInMonth = this.dateTime.dayOfMonth().getMaximumValue();
						int dayTextAreaDays = Integer.parseInt(dayTextArea.getText());
						
						if (dayTextAreaDays > daysInMonth){
							this.dateTime = this.dateTime.withDayOfMonth(daysInMonth);
						}
				}
			}
			else if (e.getKeyCode() == KeyEvent.VK_DOWN){
				if (maxDateTime == null || !this.dateTime.minusMonths(1).isAfter(maxDateTime))
					if (minDateTime == null || !this.dateTime.minusMonths(1).isBefore(minDateTime)){
						this.dateTime = this.dateTime.minusMonths(1);
						
						//make sure day field is not larger than number of days in month
						int daysInMonth = this.dateTime.dayOfMonth().getMaximumValue();
						int dayTextAreaDays = Integer.parseInt(dayTextArea.getText());
						
						if (dayTextAreaDays > daysInMonth){
							this.dateTime = this.dateTime.withDayOfMonth(daysInMonth);
						}
				}
			}
		}
		else if (source == YEAR){
			if (e.getKeyCode() == KeyEvent.VK_RIGHT){
				select(HOUR);
			}
			else if (e.getKeyCode() == KeyEvent.VK_LEFT){
				select(MONTH);
			}
			else if (e.getKeyCode() == KeyEvent.VK_UP){
				if (maxDateTime == null || !this.dateTime.plusYears(1).isAfter(maxDateTime))
					if (minDateTime == null || !this.dateTime.plusYears(1).isBefore(minDateTime)){
						this.dateTime = this.dateTime.plusYears(1);
						
						//make sure day field is not larger than number of days in month
						int daysInMonth = this.dateTime.dayOfMonth().getMaximumValue();
						int dayTextAreaDays = Integer.parseInt(dayTextArea.getText());
						
						if (dayTextAreaDays > daysInMonth){
							this.dateTime = this.dateTime.withDayOfMonth(daysInMonth);
						}
				}
			}
			else if (e.getKeyCode() == KeyEvent.VK_DOWN){
				if (maxDateTime == null || !this.dateTime.minusYears(1).isAfter(maxDateTime))
					if (minDateTime == null || !this.dateTime.minusYears(1).isBefore(minDateTime)){
						this.dateTime = this.dateTime.minusYears(1);
						
						//make sure day field is not larger than number of days in month
						int daysInMonth = this.dateTime.dayOfMonth().getMaximumValue();
						int dayTextAreaDays = Integer.parseInt(dayTextArea.getText());
						
						if (dayTextAreaDays > daysInMonth){
							this.dateTime = this.dateTime.withDayOfMonth(daysInMonth);
						}
				}
			}
		}
		else if (source == HOUR){
			if (e.getKeyCode() == KeyEvent.VK_RIGHT){
				select(MINUTE);
			}
			else if (e.getKeyCode() == KeyEvent.VK_LEFT){
				select(YEAR);
			}
			else if (e.getKeyCode() == KeyEvent.VK_UP){
				if (maxDateTime == null || !this.dateTime.plusHours(1).isAfter(maxDateTime))
					if (minDateTime == null || !this.dateTime.plusHours(1).isBefore(minDateTime))
					this.dateTime = this.dateTime.plusHours(1);
			}
			else if (e.getKeyCode() == KeyEvent.VK_DOWN){
				if (maxDateTime == null || !this.dateTime.minusHours(1).isAfter(maxDateTime))
					if (minDateTime == null || !this.dateTime.minusHours(1).isBefore(minDateTime))
					this.dateTime = this.dateTime.minusHours(1);
			}
		}
		else if (source == MINUTE){
			if (e.getKeyCode() == KeyEvent.VK_RIGHT){
				select(DAY);
			}
			else if (e.getKeyCode() == KeyEvent.VK_LEFT){
				select(HOUR);
			}
			else if (e.getKeyCode() == KeyEvent.VK_UP){
				if (maxDateTime == null || !this.dateTime.plusMinutes(1).isAfter(maxDateTime))
					if (minDateTime == null || !this.dateTime.plusMinutes(1).isBefore(minDateTime))
					this.dateTime = this.dateTime.plusMinutes(1);
			}
			else if (e.getKeyCode() == KeyEvent.VK_DOWN){
				if (maxDateTime == null || !this.dateTime.minusMinutes(1).isAfter(maxDateTime))
					if (minDateTime == null || !this.dateTime.minusMinutes(1).isBefore(minDateTime))
					this.dateTime = this.dateTime.minusMinutes(1);
			}
		}
		
		if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN){
			notSaved = true;
			setDateTime(this.dateTime);
			fireEvent(new DateTimeSelectorEvent(this, "changed"));
		}
	}
	
	public void select(int selector){		
		switch (selector){
			case 1:
				dayTextArea.setBackground(SELECTED_COLOR);
				dayTextArea.requestFocus(true);
				break;
			case 2:
				monthTextArea.setBackground(SELECTED_COLOR);
				monthTextArea.requestFocus(true);
				break;
			case 3:
				yearTextArea.setBackground(SELECTED_COLOR);
				yearTextArea.requestFocus(true);
				break;
			case 4:
				hourTextArea.setBackground(SELECTED_COLOR);
				hourTextArea.requestFocus(true);
				break;
			case 5:
				minuteTextArea.setBackground(SELECTED_COLOR);
				minuteTextArea.requestFocus(true);
				break;
			default:
				frame.requestFocus(true);
				break;
		}
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
		if (dayTextArea.hasFocus())
			processOnFocus(DAY);
		else if (monthTextArea.hasFocus())
			processOnFocus(MONTH);
		else if (yearTextArea.hasFocus())
			processOnFocus(YEAR);
		else if (hourTextArea.hasFocus())
			processOnFocus(HOUR);
		else if (minuteTextArea.hasFocus())
			processOnFocus(MINUTE);
		
	}

	@Override
	public void focusLost(FocusEvent e){
		JComponent component = (JComponent) e.getSource();
		
		component.setBackground(frame.getBackground());
		
		fireEvent(new DateTimeSelectorEvent(this, "lost_focus"));
	}
}