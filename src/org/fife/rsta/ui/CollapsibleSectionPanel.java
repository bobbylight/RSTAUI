/*
 * 09/20/2013
 *
 * CollapsibleSectionPanel - A panel that can show or hide its contents.
 * 
 * This library is distributed under a modified BSD license.  See the included
 * RSyntaxTextArea.License.txt file for details.
 */
package org.fife.rsta.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;


/**
 * A panel that can show or hide contents anchored to its bottom via a
 * shortcut.  Those contents "slide" in, since today's applications are
 * all about fancy smancy animations.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class CollapsibleSectionPanel extends JPanel {

	private Map<KeyStroke, BottomComponentInfo> bottomComponentInfos;
	private BottomComponentInfo currentBci;

	private Timer timer;
	private int tick;
	private final int totalTicks = 20;
	private boolean down;


	public CollapsibleSectionPanel() {
		super(new BorderLayout());
		bottomComponentInfos = new HashMap<KeyStroke, BottomComponentInfo>();
		installKeystrokes();
	}


	public Action addBottomComponent(KeyStroke ks, JComponent comp) {
		BottomComponentInfo bci = new BottomComponentInfo(comp);
		bottomComponentInfos.put(ks, bci);
		InputMap im= getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		im.put(ks, ks);
		Action action = new ShowBottomComponentAction(ks, bci);
		getActionMap().put(ks, action);
		return action;
	}


	private void createTimer() {
		timer = new Timer(10, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tick++;
				if (tick==totalTicks) {
					timer.stop();
					timer = null;
					tick = 0;
					Dimension finalSize = down ?
							new Dimension(0, 0) : currentBci.getRealPreferredSize();
					currentBci.component.setPreferredSize(finalSize);
					if (down) {
						remove(currentBci.component);
						currentBci = null;
					}
					else {
						// We assume here that the component has some focusable
						// child we want to play with
						currentBci.component.requestFocusInWindow();
					}
				}
				else {
					float proportion = !down ? (((float)tick)/totalTicks) : (1f- (((float)tick)/totalTicks));
					Dimension size = new Dimension(currentBci.getRealPreferredSize());
					size.height = (int)(size.height*proportion);
					currentBci.component.setPreferredSize(size);
				}
				revalidate();
				repaint();
			}
		});
		timer.setRepeats(true);
	}


	/**
	 * Hides the currently displayed "bottom" component with a slide-out
	 * animation.
	 *
	 * @see #showBottomComponent(BottomComponentInfo)
	 */
	private void hideBottomComponent() {

		if (currentBci==null) {
			return;
		}

		if (timer!=null) {
			timer.stop();
			tick = totalTicks - tick;
		}
		down = true;

		createTimer();
		timer.start();

	}


	/**
	 * Installs standard keystrokes for this component.
	 */
	private void installKeystrokes() {

		InputMap im = getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		ActionMap am = getActionMap();

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "onEscape");
		am.put("onEscape", new HideBottomComponentAction());

	}


	/**
	 * Displays a new "bottom" component.  If a component is currently
	 * displayed at the "bottom," it is hidden.
	 *
	 * @param bci The new bottom component.
	 * @see #hideBottomComponent()
	 */
	private void showBottomComponent(final BottomComponentInfo bci) {

		if (bci.equals(currentBci)) {
			currentBci.component.requestFocusInWindow();
			return;
		}

		// Remove currently displayed bottom component
		if (currentBci!=null) {
			remove(currentBci.component);
		}
		currentBci = bci;
		add(currentBci.component, BorderLayout.SOUTH);

		if (timer!=null) {
			timer.stop();
		}
		tick = 0;
		down = false;

		// Animate display of new bottom component.
		createTimer();
		timer.start();

	}


	@Override
	public void updateUI() {
		super.updateUI();
		if (bottomComponentInfos!=null) {
			for (BottomComponentInfo info : bottomComponentInfos.values()) {
				if (!info.component.isDisplayable()) {
					SwingUtilities.updateComponentTreeUI(info.component);
				}
				info.uiUpdated();
			}
		}
	}


	private static class BottomComponentInfo {

		private JComponent component;
		private Dimension _preferredSize;

		public BottomComponentInfo(JComponent component) {
			this.component = component;
		}

		public Dimension getRealPreferredSize() {
			if (_preferredSize==null) {
				_preferredSize = component.getPreferredSize();
			}
			return _preferredSize;
		}

		private void uiUpdated() {
			// Remove explicit size previously set
			component.setPreferredSize(null);
		}

	}


	private class HideBottomComponentAction extends AbstractAction {

		public void actionPerformed(ActionEvent e) {
			hideBottomComponent();
		}

	}


	private class ShowBottomComponentAction extends AbstractAction {

		private BottomComponentInfo bci;

		public ShowBottomComponentAction(KeyStroke ks, BottomComponentInfo bci){
			putValue(ACCELERATOR_KEY, ks);
			this.bci = bci;
		}

		public void actionPerformed(ActionEvent e) {
			showBottomComponent(bci);
		}

	}


}