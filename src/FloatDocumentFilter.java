import javax.swing.text.*;

public class FloatDocumentFilter extends DocumentFilter
{
    public void insertString(DocumentFilter.FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException
    {
        StringBuilder sb = new StringBuilder();
        sb.append(fb.getDocument().getText(0, fb.getDocument().getLength()));
        sb.insert(offset, text);
        
        if(!containsOnlyNumbers(sb.toString())) 
        	return;
        
        fb.insertString(offset, text, attr);
    }
    
    public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attr) throws BadLocationException
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append(fb.getDocument().getText(0, fb.getDocument().getLength()));
        sb.replace(offset, offset + length, text);
        
        if(!containsOnlyNumbers(sb.toString())) 
        	return;
        
        fb.replace(offset, length, text, attr);
    }
    
    public boolean containsOnlyNumbers(String text)
    {       
    	boolean isMatch = true;
    	
    	try {
    		double d = Double.parseDouble(text);
        } catch(NumberFormatException e) {
        	return false;
        }
    	
        return isMatch;
    }
}