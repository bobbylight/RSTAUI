package org.fife.rsta.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.MouseInputAdapter;

import org.fife.ui.rsyntaxtextarea.DefaultTokenPainter;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxView;
//import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenPainter;


public class DocumentMap extends JPanel {

	private RSyntaxTextArea textArea;
	private int fontSize;
	private Listener textAreaListener;
	private int width;

	private Color activeRangeColor;
	private int activeRangeY0;
	private int activeRangeY1;
	private Timer activeRangeHideTimer;
	private float activeRangeAlpha;
	private HideActiveRangeEvent activeRangeHideEvent;


	public DocumentMap(RSyntaxTextArea textArea) {
		textAreaListener = new Listener();
		setTextArea(textArea);
		setFontSize(4);
		setWidth(80);
		setActiveRangeColor(Color.LIGHT_GRAY);
		activeRangeHideEvent = new HideActiveRangeEvent();
		activeRangeHideTimer = new Timer(20, activeRangeHideEvent);
		new DocMapListener(this);
	}


	public Color getActiveRangeColor() {
		return activeRangeColor;
	}


	@Override
	public Dimension getPreferredSize() {
		Dimension size = super.getPreferredSize();
		size.width = width;
		return size;
	}


	@Override
	protected void paintComponent(Graphics g) {

		Graphics2D g2d = (Graphics2D)g;
		int w = getWidth();
		int h = getHeight();
		int lineHeight = textArea.getLineHeight();
		TokenPainter p = new DefaultTokenPainter();
		SyntaxView view = new SyntaxView(textArea.getDocument().getDefaultRootElement()) {
			@Override
			public Container getContainer() {
				return textArea;
			}
		};

		g.setColor(textArea.getBackground());
		g.fillRect(0, 0, w, h);

		if (activeRangeY0>-1) {
			Color base = getActiveRangeColor();
			System.out.println(activeRangeAlpha);
			Color c = new Color(base.getRed(), base.getGreen(), base.getBlue(),
					(int)(activeRangeAlpha*255));
			g.setColor(c);
			int activeRangeH = activeRangeY1 - activeRangeY0;
			g.fillRect(0,activeRangeY0, getWidth(),activeRangeH);
		}

		g2d.scale(0.15, 0.15);
/*
		int y = lineHeight;
		for (int i=0; i<textArea.getLineCount(); i++) {

			Token t = textArea.getTokenListForLine(i);
			float x = 0;

			while (t!=null && t.isPaintable() && x<w) {
				x = p.paint(t, g2d, x, y, textArea, view);
				t = t.getNextToken();
			}

			y += lineHeight;

		}
*/
		view.paint(g2d, getVisibleRect());
	}


	private void repaintDocumentMap() {
		repaint();
	}


	public void setActiveRangeColor(Color color) {
		if (color==null) {
			throw new IllegalArgumentException("activeRangeColor cannot be null");
		}
		if (!color.equals(activeRangeColor)) {
			this.activeRangeColor = color;
			repaint();
		}
	}


	public void setFontSize(int size) {
		if (size!=this.fontSize) {
			this.fontSize = size;
			repaint();
		}
	}


	public void setTextArea(RSyntaxTextArea textArea) {
		if (textArea==null) {
			throw new IllegalArgumentException("textArea cannot be null");
		}
		if (this.textArea!=null) {
			textAreaListener.uninstall(this.textArea);
		}
		this.textArea = textArea;
		textAreaListener.install(this.textArea);
		repaint();
	}


	public void setWidth(int width) {
		if (width>=0 && width!=this.width) {
			this.width = width;
			revalidate();
			repaint();
		}
	}


	private class DocMapListener extends MouseInputAdapter {

		private DocMapListener(DocumentMap docMap) {
			docMap.addMouseListener(this);
			docMap.addMouseMotionListener(this);
		}

		private void hideActiveLineRange() {
			activeRangeHideEvent.reset();
			activeRangeHideTimer.restart();
			repaint();
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			float relY = ((float)e.getY()) / getHeight();
			int textAreaH = textArea.getHeight();
			Rectangle visibleRect = textArea.getVisibleRect();
			int y = (int)(textAreaH*relY) - visibleRect.height/2;
			Rectangle r = new Rectangle(0,y, getWidth(),y+visibleRect.height);
			textArea.scrollRectToVisible(r);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			updateActiveLineRange(e);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			hideActiveLineRange();
		}

		private void updateActiveLineRange(MouseEvent e) {

			Rectangle visibleRect = textArea.getVisibleRect();
			int y0 = visibleRect.y;
			int y1 = y0 + visibleRect.height;
			int textAreaH = textArea.getHeight();

			activeRangeHideTimer.stop();
			int docMapH = getHeight();
			activeRangeAlpha = 1f;
			activeRangeY0 = (int)(((float)y0/textAreaH) * docMapH);
			activeRangeY1 = (int)(((float)y1/textAreaH) * docMapH);

			repaint();

		}

	}


	private class HideActiveRangeEvent implements ActionListener {

		private static final long DURATION_MS = 500;

		private long endTime;

		public HideActiveRangeEvent() {
		}

		public void actionPerformed(ActionEvent e) {
			long diff = endTime - System.currentTimeMillis();
			if (diff<=0) {
				activeRangeY0 = activeRangeY1 = -1;
				activeRangeHideTimer.stop();
			}
			else {
				activeRangeAlpha = ((diff)*1f) / DURATION_MS;
				activeRangeAlpha = Math.max(0, Math.min(activeRangeAlpha, 1));
				repaint(0, activeRangeY0, getWidth(), activeRangeY1);
			}
		}

		public void reset() {
			endTime = System.currentTimeMillis() + DURATION_MS;
		}

	}


	private class Listener implements DocumentListener, CaretListener {

		public void caretUpdate(CaretEvent e) {
			repaintDocumentMap();
		}

		public void changedUpdate(DocumentEvent e) {
			// Do nothing
		}

		private void handleDocumentEvent(DocumentEvent e) {
			
		}

		public void insertUpdate(DocumentEvent e) {
			handleDocumentEvent(e);
		}

		public void install(RSyntaxTextArea textArea) {
			textArea.addCaretListener(this);
		}

		public void removeUpdate(DocumentEvent e) {
			handleDocumentEvent(e);
		}

		public void uninstall(RSyntaxTextArea textArea) {
			textArea.removeCaretListener(this);
		}

	}


}