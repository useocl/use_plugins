package org.tzi.use.plugin.filmstrip.gui;

import java.awt.FontMetrics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 * An extended JLabel to display filenames. The advantage is the changed usage
 * of the ellipsis, which is placed in a way so the filename at the end of the
 * string is not cut.
 */
public class FilePathLabel extends JLabel implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;
	private static final String TEXTCHANGE_PROPERTY = "TEXTCHANGE";
	
	private String fullText = "";
	
	public FilePathLabel(){
		addPropertyChangeListener(this);
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				updateText();
			}
			
			@Override
			public void componentShown(ComponentEvent e) {
				updateText();
			}
		});
	}
	
	@Override
	public void setText(String text) {
		String oldText = fullText;
		fullText = text;
		firePropertyChange(TEXTCHANGE_PROPERTY, oldText, text);
	}
	
	private void updateText(){
		setToolTipText(null);
		FontMetrics fm = getFontMetrics(getFont());
		int stringWidth = SwingUtilities.computeStringWidth(fm, fullText);
		String t = fullText;
		int totalWidth = getSize().width;
		
		if(totalWidth < stringWidth){
			final String ellipsis = "...";
			int width = SwingUtilities.computeStringWidth(fm, ellipsis);
			
			int chars = fullText.length();
			while(chars > 0 && (width += fm.charWidth(fullText.charAt(chars-1))) < totalWidth){
				chars--;
			}
			
			t = ellipsis + fullText.substring(chars);
			setToolTipText(fullText);
		}
		super.setText(t);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(TEXTCHANGE_PROPERTY)){
			updateText();
		}
	}
	
}