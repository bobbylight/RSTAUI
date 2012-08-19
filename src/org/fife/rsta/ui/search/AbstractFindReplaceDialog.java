/*
 * 04/08/2004
 *
 * AbstractFindReplaceSearchDialog.java - Base class for FindDialog and
 * ReplaceDialog.
 * This library is distributed under a modified BSD license.  See the included
 * RSyntaxTextArea.License.txt file for details.
 */
package org.fife.rsta.ui.search;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.UIManager;
import javax.swing.event.EventListenerList;
import javax.swing.text.JTextComponent;

import org.fife.rsta.ui.UIUtil;


/**
 * This is the base class for {@link FindDialog} and {@link ReplaceDialog}. It
 * is basically all of the features common to the two dialogs that weren't
 * taken care of in {@link AbstractSearchDialog}.
 *
 * @author Robert Futrell
 * @version 0.1
 */
public abstract class AbstractFindReplaceDialog extends AbstractSearchDialog
										implements ActionListener {

	/**
	 * The name of the action triggered when the "Find Next" button is clicked.
	 */
	public static final String ACTION_FIND = "FindNext";

	/**
	 * The name of the action triggered when the "Replace" button is clicked.
	 */
	public static final String ACTION_REPLACE = "Replace";

	/**
	 * The name of the action triggered when "Replace All" is clicked.
	 */
	public static final String ACTION_REPLACE_ALL = "ReplaceAll";

	/**
	 * Property fired when the user toggles the "Mark All" check box.
	 */
	public static final String MARK_ALL_PROPERTY		= "SearchDialog.MarkAll";

	/**
	 * Property fired when the user toggles the search direction radio buttons.
	 */
	public static final String SEARCH_DOWNWARD_PROPERTY	= "SearchDialog.SearchDownward";

	// The radio buttons for changing the search direction.
	protected JRadioButton upButton;
	protected JRadioButton downButton;
	protected JPanel dirPanel;
	private String dirPanelTitle;
	protected JLabel findFieldLabel;
	protected JButton findNextButton;

	/**
	 * The "mark all" check box.
	 */
	protected JCheckBox markAllCheckBox;

	/**
	 * Folks listening for events in this dialog.
	 */
    private EventListenerList listenerList;

	/**
	 * Constructor.  Does initializing for parts common to
	 * <code>FindDialog</code> and <code>ReplaceDialog</code> that isn't
	 * taken care of in <code>AbstractSearchDialog</code>'s constructor.
	 *
	 * @param owner The window that owns this search dialog.
	 */
	public AbstractFindReplaceDialog(Frame owner) {

		super(owner);
		listenerList = new EventListenerList();

		// Make a panel containing the "search up/down" radio buttons.
		dirPanel = new JPanel();
		dirPanel.setLayout(new BoxLayout(dirPanel, BoxLayout.LINE_AXIS));
		setSearchButtonsBorderText(getString("Direction"));
		ButtonGroup bg = new ButtonGroup();
		upButton = new JRadioButton(getString("Up"), false);
		upButton.setMnemonic((int)getString("UpMnemonic").charAt(0));
		downButton = new JRadioButton(getString("Down"), true);
		downButton.setMnemonic((int)getString("DownMnemonic").charAt(0));
		upButton.setActionCommand("UpRadioButtonClicked");
		upButton.addActionListener(this);
		downButton.setActionCommand("DownRadioButtonClicked");
		downButton.addActionListener(this);
		bg.add(upButton);
		bg.add(downButton);
		dirPanel.add(upButton);
		dirPanel.add(downButton);

		// Initialize the "mark all" button.
		markAllCheckBox = new JCheckBox(getString("MarkAll"));
		markAllCheckBox.setMnemonic((int)getString("MarkAllMnemonic").charAt(0));
		markAllCheckBox.setActionCommand("MarkAll");
		markAllCheckBox.addActionListener(this);

		// Rearrange the search conditions panel.
		searchConditionsPanel.removeAll();
		searchConditionsPanel.setLayout(new BorderLayout());
		JPanel temp = new JPanel();
		temp.setLayout(new BoxLayout(temp, BoxLayout.PAGE_AXIS));
		temp.add(caseCheckBox);
		temp.add(wholeWordCheckBox);
		searchConditionsPanel.add(temp, BorderLayout.LINE_START);
		temp = new JPanel();
		temp.setLayout(new BoxLayout(temp, BoxLayout.PAGE_AXIS));
		temp.add(regExpCheckBox);
		temp.add(markAllCheckBox);
		searchConditionsPanel.add(temp, BorderLayout.LINE_END);

		// Create the "Find what" label.
		findFieldLabel = createLabel("FindWhat", findTextCombo);

		// Create a "Find Next" button.
		findNextButton = UIUtil.createButton(getBundle(), "Find",
				"FindMnemonic");
		findNextButton.setActionCommand(ACTION_FIND);
		findNextButton.addActionListener(this);
		findNextButton.setDefaultCapable(true);
		findNextButton.setEnabled(false);	// Initially, nothing to look for.

	}


	/**
	 * Listens for action events in this dialog.
	 *
	 * @param e The event that occurred.
	 */
	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();

		if ("UpRadioButtonClicked".equals(command)) {
			context.setSearchForward(false);
			firePropertyChange(SEARCH_DOWNWARD_PROPERTY, true, false);
		}

		else if ("DownRadioButtonClicked".equals(command)) {
			context.setSearchForward(true);
			firePropertyChange(SEARCH_DOWNWARD_PROPERTY, false, true);
		}

		else if ("MarkAll".equals(command)) {
			boolean checked = markAllCheckBox.isSelected();
			context.setMarkAll(checked);
			firePropertyChange(MARK_ALL_PROPERTY, !checked, checked);
		}

		else if (ACTION_FIND.equals(command)) {

			// Add the item to the combo box's list, if it isn't already there.
			findTextCombo.addItem(getTextComponent(findTextCombo).getText());
			context.setSearchFor(getSearchString());

			// If they just searched for an item that's already in the list
			// other than the first, move it to the first position.
			if (findTextCombo.getSelectedIndex()>0) {
				Object item = findTextCombo.getSelectedItem();
				findTextCombo.removeItem(item);
				findTextCombo.insertItemAt(item, 0);
				findTextCombo.setSelectedIndex(0);
			}

			fireActionPerformed(e); // Let parent application know

		}

		else {
			super.actionPerformed(e);
		}

	}


	/**
	 * Adds an <code>ActionListener</code> to this dialog.  This listener will
	 * be notified when find or replace operations are triggered.  For
	 * example, for a Replace dialog, a listener will receive notification
	 * when the user clicks "Find", "Replace", or "Replace All".
	 *
	 * @param l The listener to add.
	 * @see #removeActionListener(ActionListener)
	 */
	public void addActionListener(ActionListener l) {
		listenerList.add(ActionListener.class, l);
	}


	/**
	 * Changes the action listener from one component to another.
	 *
	 * @param fromPanel The old <code>ActionListener</code> to remove.
	 * @param toPanel The new <code>ActionListener</code> to add as an action
	 *        listener.
	 */
	public void changeActionListener(ActionListener fromPanel,
								ActionListener toPanel) {
		this.removeActionListener(fromPanel);
		this.addActionListener(toPanel);
	}


	/**
	 * Returns a label for a component.
	 *
	 * @param key The root key into the resource bundle.
	 * @param comp The component this will be a label for.
	 * @return The label.
	 */
	protected JLabel createLabel(String key, JComponent comp) {
		JLabel label = new JLabel(getString(key));
		int mnemonic = getString(key + "Mnemonic").charAt(0);
		label.setDisplayedMnemonic(mnemonic);
		label.setLabelFor(comp);
		return label;
	}


	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * <code>event</code> parameter.
	 * 
	 * @param event The <code>ActionEvent</code> object coming from a
	 *        child component.
	 */
	protected void fireActionPerformed(ActionEvent event) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		ActionEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ActionListener.class) {
				// Lazily create the event:
				if (e == null) {
					String command = event.getActionCommand();
					e = new ActionEvent(this,
							ActionEvent.ACTION_PERFORMED, command,
							event.getWhen(), event.getModifiers());
				}
				((ActionListener)listeners[i+1]).actionPerformed(e);
			}
		}
	}


	/**
	 * Returns the text for the "Down" radio button.
	 *
	 * @return The text for the "Down" radio button.
	 * @see #setDownRadioButtonText
	 */
	public final String getDownRadioButtonText() {
		return downButton.getText();
	}


	/**
	 * Returns the text on the "Find" button.
	 *
	 * @return The text on the Find button.
	 * @see #setFindButtonText
	 */
	public final String getFindButtonText() {
		return findNextButton.getText();
	}


	/**
	 * Returns the label on the "Find what" text field.
	 *
	 * @return The text on the "Find what" text field.
	 * @see #setFindWhatLabelText
	 */
	public final String getFindWhatLabelText() {
		return findFieldLabel.getText();
	}


	/**
	 * Returns the text for the search direction's radio buttons' border.
	 *
	 * @return The text for the search radio buttons' border.
	 * @see #setSearchButtonsBorderText
	 */
	public final String getSearchButtonsBorderText() {
		return dirPanelTitle;
	}


	/**
	 * Returns the text for the "Up" radio button.
	 *
	 * @return The text for the "Up" radio button.
	 * @see #setUpRadioButtonText
	 */
	public final String getUpRadioButtonText() {
		return upButton.getText();
	}


	protected EnableResult handleToggleButtons() {

		EnableResult er = super.handleToggleButtons();
		boolean enable = er.getEnable();

		findNextButton.setEnabled(enable);

		// setBackground doesn't show up with XP Look and Feel!
		//findTextComboBox.setBackground(enable ?
		//		UIManager.getColor("ComboBox.background") : Color.PINK);
		JTextComponent tc = getTextComponent(findTextCombo);
		tc.setForeground(enable ? UIManager.getColor("TextField.foreground") :
									Color.RED);

		String tooltip = er.getToolTip();
		if (tooltip!=null && tooltip.indexOf('\n')>-1) {
			tooltip = tooltip.replaceFirst("\\\n", "</b><br><pre>");
			tooltip = "<html><b>" + tooltip;
		}
		tc.setToolTipText(tooltip); // Always set, even if null

		return er;

	}


	/**
	 * Overridden to initialize UI elements specific to this subclass.
	 */
	protected void refreshUIFromContext() {
		super.refreshUIFromContext();
		markAllCheckBox.setSelected(context.getMarkAll());
		boolean searchForward = context.getSearchForward();
		upButton.setSelected(!searchForward);
		downButton.setSelected(searchForward);
	}


	/**
	 * Removes an <code>ActionListener</code> from this dialog.
	 *
	 * @param l The listener to remove
	 * @see #addActionListener(ActionListener)
	 */
	public void removeActionListener(ActionListener l) {
		listenerList.remove(ActionListener.class, l);
	}


	/**
	 * Sets the text label for the "Down" radio button.
	 *
	 * @param text The new text label for the "Down" radio button.
	 * @see #getDownRadioButtonText
	 */
	public void setDownRadioButtonText(String text) {
		downButton.setText(text);
	}


	/**
	 * Sets the text on the "Find" button.
	 *
	 * @param text The text for the Find button.
	 * @see #getFindButtonText
	 */
	public final void setFindButtonText(String text) {
		findNextButton.setText(text);
	}


	/**
	 * Sets the label on the "Find what" text field.
	 *
	 * @param text The text for the "Find what" text field's label.
	 * @see #getFindWhatLabelText
	 */
	public void setFindWhatLabelText(String text) {
		findFieldLabel.setText(text);
	}


	/**
	 * Sets the text for the search direction's radio buttons' border.
	 *
	 * @param text The text for the search radio buttons' border.
	 * @see #getSearchButtonsBorderText
	 */
	public final void setSearchButtonsBorderText(String text) {
		dirPanelTitle = text;
		dirPanel.setBorder(createTitledBorder(dirPanelTitle));
	}


	/**
	 * Sets the text label for the "Up" radio button.
	 *
	 * @param text The new text label for the "Up" radio button.
	 * @see #getUpRadioButtonText
	 */
	public void setUpRadioButtonText(String text) {
		upButton.setText(text);
	}


}