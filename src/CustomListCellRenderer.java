import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class CustomListCellRenderer extends JLabel implements ListCellRenderer {
	Map<String, String> coinColorCodeMap;
	protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
	protected static Border focusBorder = new LineBorder(new Color(0x6382BF));

    public CustomListCellRenderer(Map<String, String> coinColorCodeMap) {
    	this.coinColorCodeMap = coinColorCodeMap;
    	
        setOpaque(true);
    }
    
    public void refreshColorMap(Map<String, String> coinColorCodeMap){
    	this.coinColorCodeMap = coinColorCodeMap;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, 
    		boolean cellHasFocus) {
    	Font font = new Font("helvetica", Font.BOLD, 12);

    	if (value.toString().startsWith("¬")){
    		Map  attributes = font.getAttributes();
    		attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
    		
    		font = new Font(attributes);
    		
    		setFont(font);
    		
    		setForeground(Color.GRAY);
    		
    		setText(value.toString().substring(1));
    	}
    	else{
    		font = new Font("helvetica", Font.BOLD, 12);
    	
    		setFont(font);
    		
    		setForeground(Color.black);
    		
    		setText(value.toString());
    	}

    	//if a coin
    	if (value.toString().length() == 3) { 
	        //set color of coin
	        Color coinColor = Color.black;
	        
	        if (coinColorCodeMap.containsKey(value.toString())){
	        	coinColor = Color.decode((String)coinColorCodeMap.get(value.toString())); 
	        }
	        
	        setForeground(coinColor);
    	}
	        
        //set color of background if selected
        if (isSelected)
        	setBackground(new Color(184,207,229));
        else
        	setBackground(Color.white);
        
        //set border if focused
        setBorder(cellHasFocus ? focusBorder : noFocusBorder);


        return this;
    }
}