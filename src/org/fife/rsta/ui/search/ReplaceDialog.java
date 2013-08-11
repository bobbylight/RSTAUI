/*
 * 11/14/2003
 *
 * ReplaceDialog.java - Dialog for replacing text in a GUI.
 * This library is distributed under a modified BSD license.  See the included
 * RSTAUI.License.txt file for details.
 */
package org.fife.rsta.ui.search;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import org.fife.rsta.ui.AssistanceIconPanel;
import org.fife.rsta.ui.MaxWidthComboBox;
import org.fife.rsta.ui.ResizableFrameContentPane;
import org.fife.rsta.ui.UIUtil;
import org.fife.ui.rtextarea.SearchEngine;


/**
 * A "Replace" dialog similar to those found in most Windows text editing
 * applications.  Contains many search options, including:<br>
 * <ul>
 *   <li>Match Case
 *   <li>Match Whole Word
 *   <li>Use Regular Expressions
 *   <li>Search Forwards or Backwards
 * </ul>
 * The dialog also remembers your previous several selections in a combo box.
 * <p>An application can use a <code>ReplaceDialog</code> as follows.  It is suggested
 * that you create an <code>Action</code> or something similar to facilitate
 * "bringing up" the Replace dialog.  Have the main application contain an object
 * that implements <code>ActionListener</code>.  This object will receive the
 * following action events from the Replace dialog:
 * <ul>
 *   <li>{@link AbstractFindReplaceDialog#ACTION_FIND ACTION_FIND} action when
 *       the user clicks the "Find" button.
 *   <li>{@link AbstractFindReplaceDialog#ACTION_REPLACE ACTION_REPLACE} action
 *       when the user clicks the "Replace" button.
 *   <li>{@link AbstractFindReplaceDialog#ACTION_REPLACE_ALL ACTION_REPLACE_ALL}
 *       action when the user clicks the "Replace All" button.
 * </ul>
 * The application can then call i.e.
 * {@link SearchEngine#find(javax.swing.JTextArea, org.fife.ui.rtextarea.SearchContext) SearchEngine.find()}
 * or
 * {@link SearchEngine#replace(org.fife.ui.rtextarea.RTextArea, org.fife.ui.rtextarea.SearchContext) SearchEngine.replace()}
 * to actually execute the search.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class ReplaceDialog extends AbstractFindReplaceDialog {

	private static final long serialVersionUID = 1L;

	private JButton replaceButton;
	private JButton replaceAllButton;
	private JLabel replaceFieldLabel;

	private MaxWidthComboBox replaceWithCombo;

	// This helps us work around the "bug" where JComboBox eats the first Enter
	// press.
	private String lastSearchString;
	private String lastReplaceString;


	/**
	 * Creates a new <code>ReplaceDialog</code>.
	 *
	 * @param owner The main window that owns this dialog.
	 * @param listener The component that listens for
	 *        {@link AbstractFindReplaceDialog#ACTION_FIND ACTION_FIND},
	 *        {@link AbstractFindReplaceDialog#ACTION_REPLACE ACTION_REPLACE},
	 *        and
	 *        {@link AbstractFindReplaceDialog#ACTION_REPLACE_ALL ACTION_REPLACE_ALL}
	 *        actions.
	 */
	public ReplaceDialog(Dialog owner, ActionListener listener) {
		super(owner);
		init(listener);
	}


	/**
	 * Creates a new <code>ReplaceDialog</code>.
	 *
	 * @param owner The main window that owns this dialog.
	 * @param listener The component that listens for
	 *        {@link AbstractFindReplaceDialog#ACTION_FIND ACTION_FIND},
	 *        {@link AbstractFindReplaceDialog#ACTION_REPLACE ACTION_REPLACE},
	 *        and
	 *        {@link AbstractFindReplaceDialog#ACTION_REPLACE_ALL ACTION_REPLACE_ALL}
	 *        actions.
	 */
	public ReplaceDialog(Frame owner, ActionListener listener) {
		super(owner);
		init(listener);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();

		if (ACTION_REPLACE.equals(command) ||
				ACTION_REPLACE_ALL.equals(command)) {

			context.setSearchFor(getSearchString());
			context.setReplaceWith((String)replaceWithCombo.getSelectedItem());

			findTextCombo.addItem(getTextComponent(findTextCombo).getText());

			// If they just searched for an item that's already in the list
			// other than the first, move it to the first position.
			if (findTextCombo.getSelectedIndex()>0) {
				Object item = findTextCombo.getSelectedItem();
				findTextCombo.removeItem(item);
				findTextCombo.insertItemAt(item, 0);
				findTextCombo.setSelectedIndex(0);
			}

			String replaceText = getTextComponent(replaceWithCombo).getText();
			if (!replaceText.equals(""))
				replaceWithCombo.addItem(replaceText);

			// If they just searched for an item that's already in the list
			// other than the first, move it to the first position.
			if (replaceWithCombo.getSelectedIndex()>0) {
				Object item = replaceWithCombo.getSelectedItem();
				replaceWithCombo.removeItem(item);
				replaceWithCombo.insertItemAt(item, 0);
				replaceWithCombo.setSelectedIndex(0);
			}

			fireActionPerformed(e); // Let parent application know

		}

		else {
			super.actionPerformed(e);
		}

	}


	@Override
	protected void escapePressed() {
		if (replaceWithCombo instanceof RegexAwareComboBox) {
			RegexAwareComboBox racb = (RegexAwareComboBox)replaceWithCombo;
			// Workaround for the strange behavior (Java bug?) that sometimes
			// the Escape keypress "gets through" from the AutoComplete's
			// registered key Actions, and gets to this EscapableDialog, which
			// hides the entire dialog.  Reproduce by doing the following:
			//   1. In an empty find field, press Ctrl+Space
			//   2. Type "\\".
			//   3. Press Escape.
			// The entire dialog will hide, instead of the completion popup.
			// Further, bringing the Find dialog back up, the completion popup
			// will still be visible.
			if (racb.hideAutoCompletePopups()) {
				return;
			}
		}
		super.escapePressed();
	}


	/**
	 * Returns the text on the "Replace" button.
	 *
	 * @return The text on the Replace button.
	 * @see #setReplaceButtonText
	 */
	public final String getReplaceButtonText() {
		return replaceButton.getText();
	}


	/**
	 * Returns the text on the "Replace All" button.
	 *
	 * @return The text on the Replace All button.
	 * @see #setReplaceAllButtonText
	 */
	public final String getReplaceAllButtonText() {
		return replaceAllButton.getText();
	}


	/**
	 * Returns the <code>java.lang.String</code> to replace with.
	 *
	 * @return The <code>java.lang.String</code> the user wants to replace
	 *         the text to find with.
	 */
	public String getReplaceString() {
		String text = (String)replaceWithCombo.getSelectedItem();
		if (text==null) { // possible from JComboBox
			text = "";
		}
		return text;
	}


	/**
	 * Returns the label on the "Replace with" text field.
	 *
	 * @return The text on the "Replace with" text field.
	 * @see #setReplaceWithLabelText
	 */
	public final String getReplaceWithLabelText() {
		return replaceFieldLabel.getText();
	}


	/**
	 * Called when the regex checkbox is clicked.  Subclasses can override
	 * to add custom behavior, but should call the super implementation.
	 */
	@Override
	protected void handleRegExCheckBoxClicked() {

		super.handleRegExCheckBoxClicked();

		// "Content assist" support
		boolean b = regExpCheckBox.isSelected();
		// Always true except when debugging.  findTextCombo done in parent
		if (replaceWithCombo instanceof RegexAwareComboBox) {
			RegexAwareComboBox racb = (RegexAwareComboBox)replaceWithCombo;
			racb.setAutoCompleteEnabled(b);
		}

	}


	@Override
	protected EnableResult handleToggleButtons() {
		EnableResult er = super.handleToggleButtons();
		replaceButton.setEnabled(er.getEnable());
		replaceAllButton.setEnabled(er.getEnable());
		return er;
	}


	/**
	 * Does replace dialog-specific initialization stuff.
	 *
	 * @param listener The component that listens for
	 *        {@link AbstractFindReplaceDialog#ACTION_FIND ACTION_FIND},
	 *        {@link AbstractFindReplaceDialog#ACTION_REPLACE ACTION_REPLACE},
	 *        and
	 *        {@link AbstractFindReplaceDialog#ACTION_REPLACE_ALL ACTION_REPLACE_ALL}
	 *        actions.
	 */
	private void init(ActionListener listener) {

		ComponentOrientation orientation = ComponentOrientation.
									getOrientation(getLocale());

		// Create a panel for the "Find what" and "Replace with" text fields.
		JPanel searchPanel = new JPanel(new SpringLayout());

		// Create listeners for the combo boxes.
		ReplaceFocusAdapter replaceFocusAdapter = new ReplaceFocusAdapter();
		ReplaceKeyListener replaceKeyListener = new ReplaceKeyListener();
		ReplaceDocumentListener replaceDocumentListener = new ReplaceDocumentListener();

		// Create the "Find what" text field.
		JTextComponent textField = getTextComponent(findTextCombo);
		textField.addFocusListener(replaceFocusAdapter);
		textField.addKeyListener(replaceKeyListener);
		textField.getDocument().addDocumentListener(replaceDocumentListener);

		// Create the "Replace with" text field.
		replaceWithCombo = createSearchComboBox(true);
		textField = getTextComponent(replaceWithCombo);
		textField.addFocusListener(replaceFocusAdapter);
		textField.addKeyListener(replaceKeyListener);
		textField.getDocument().addDocumentListener(replaceDocumentListener);

		// Create the "Replace with" label.
		replaceFieldLabel = UIUtil.newLabel(getBundle(), "ReplaceWith",
				replaceWithCombo);

		JPanel temp = new JPanel(new BorderLayout());
		temp.add(findTextCombo);
		AssistanceIconPanel aip = new AssistanceIconPanel(findTextCombo);
		temp.add(aip, BorderLayout.LINE_START);
		JPanel temp2 = new JPanel(new BorderLayout());
		temp2.add(replaceWithCombo);
		AssistanceIconPanel aip2 = new AssistanceIconPanel(replaceWithCombo);
		temp2.add(aip2, BorderLayout.LINE_START);

		// Orient things properly.
		if (orientation.isLeftToRight()) {
			searchPanel.add(findFieldLabel);
			searchPanel.add(temp);
			searchPanel.add(replaceFieldLabel);
			searchPanel.add(temp2);
		}
		else {
			searchPanel.add(temp);
			searchPanel.add(findFieldLabel);
			searchPanel.add(temp2);
			searchPanel.add(replaceFieldLabel);
		}

		UIUtil.makeSpringCompactGrid(searchPanel, 2, 2,	//rows, cols
											0,0,		//initX, initY
											6, 6);	//xPad, yPad

		// Make a panel containing the inherited search direction radio
		// buttons and the inherited search options.
		JPanel bottomPanel = new JPanel(new BorderLayout());
		temp = new JPanel(new BorderLayout());
		bottomPanel.setBorder(UIUtil.getEmpty5Border());
		temp.add(searchConditionsPanel, BorderLayout.LINE_START);
		temp.add(dirPanel);
		bottomPanel.add(temp, BorderLayout.LINE_START);

		// Now, make a panel containing all the above stuff.
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.add(searchPanel);
		leftPanel.add(bottomPanel);

		// Make a panel containing the action buttons.
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(4,1, 5,5));
		ResourceBundle msg = getBundle();
		replaceButton = UIUtil.newButton(msg, "Replace");
		replaceButton.setActionCommand(ACTION_REPLACE);
		replaceButton.addActionListener(this);
		replaceButton.setEnabled(false);
		replaceButton.setIcon(null);
		replaceButton.setToolTipText(null);
		replaceAllButton = UIUtil.newButton(msg, "ReplaceAll");
		replaceAllButton.setActionCommand(ACTION_REPLACE_ALL);
		replaceAllButton.addActionListener(this);
		replaceAllButton.setEnabled(false);
		replaceAllButton.setIcon(null);
		replaceAllButton.setToolTipText(null);
		buttonPanel.add(findNextButton);
		buttonPanel.add(replaceButton);
		buttonPanel.add(replaceAllButton);
		buttonPanel.add(cancelButton);		// Defined in superclass.
		JPanel rightPanel = new JPanel(new BorderLayout());
		rightPanel.add(buttonPanel, BorderLayout.NORTH);

		// Put it all together!
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setBorder(BorderFactory.createEmptyBorder(5,5,0,5));
		contentPane.add(leftPanel);
		contentPane.add(rightPanel, BorderLayout.LINE_END);
		temp = new ResizableFrameContentPane(new BorderLayout());
		temp.add(contentPane, BorderLayout.NORTH);
		setContentPane(temp);
		getRootPane().setDefaultButton(findNextButton);
		setTitle(getString("ReplaceDialogTitle"));
		setResizable(true);
		pack();
		setLocationRelativeTo(getParent());

		setSearchContext(new SearchDialogSearchContext());
		addActionListener(listener);

		applyComponentOrientation(orientation);

	}


	/**
	 * Sets the text on the "Replace" button.
	 *
	 * @param text The text for the Replace button.
	 * @see #getReplaceButtonText
	 */
	public final void setReplaceButtonText(String text) {
		replaceButton.setText(text);
	}


	/**
	 * Sets the text on the "Replace All" button.
	 *
	 * @param text The text for the Replace All button.
	 * @see #getReplaceAllButtonText
	 */
	public final void setReplaceAllButtonText(String text) {
		replaceAllButton.setText(text);
	}


	/**
	 * Sets the label on the "Replace with" text field.
	 *
	 * @param text The text for the "Replace with" text field's label.
	 * @see #getReplaceWithLabelText
	 */
	public final void setReplaceWithLabelText(String text) {
		replaceFieldLabel.setText(text);
	}


	/**
	 * Sets the <code>java.lang.String</code> to replace with
	 *
	 * @param newReplaceString The <code>java.lang.String</code> to put into
	 *        the replace field.
	 */
	public void setReplaceString(String newReplaceString) {
		replaceWithCombo.addItem(newReplaceString);
		replaceWithCombo.setSelectedIndex(0);
	}


	/**
	 * Overrides <code>JDialog</code>'s <code>setVisible</code> method; decides
	 * whether or not buttons are enabled.
	 *
	 * @param visible Whether or not the dialog should be visible.
	 */
	@Override
	public void setVisible(boolean visible) {

		if (visible) {

			// Make sure content assist is enabled (regex check box might have
			// been checked in a different search dialog).
			if (visible) {
				boolean regexEnabled = regExpCheckBox.isSelected();
				// Always true except when debugging.  findTextCombo done in parent
				if (replaceWithCombo instanceof RegexAwareComboBox) {
					RegexAwareComboBox racb = (RegexAwareComboBox)replaceWithCombo;
					racb.setAutoCompleteEnabled(regexEnabled);
				}
			}

			String selectedItem = (String)findTextCombo.getSelectedItem();
			if (selectedItem==null) {
				findNextButton.setEnabled(false);
				replaceButton.setEnabled(false);
				replaceAllButton.setEnabled(false);
			}
			else {
				handleToggleButtons();
			}

			super.setVisible(true);

			// Make the "Find" text field active.
			JTextComponent textField = getTextComponent(findTextCombo);
			textField.requestFocusInWindow();
			textField.selectAll();

		}

		else {
			super.setVisible(false);
		}

	}


	/**
	 * Called whenever the user changes the Look and Feel, etc.
	 * This is overridden so we can reinstate the listeners that are evidently
	 * lost on the JTextField portion of our combo box.
	*/
	public void updateUI() {

		// Create listeners for the combo boxes.
		ReplaceFocusAdapter replaceFocusAdapter = new ReplaceFocusAdapter();
		ReplaceKeyListener replaceKeyListener = new ReplaceKeyListener();
		ReplaceDocumentListener replaceDocumentListener = new ReplaceDocumentListener();

		// Fix the Find What combo box's listeners.
		JTextComponent textField = getTextComponent(findTextCombo);
		textField.addFocusListener(replaceFocusAdapter);
		textField.addKeyListener(replaceKeyListener);
		textField.getDocument().addDocumentListener(replaceDocumentListener);

		// Fix the Replace With combo box's listeners.
		textField = getTextComponent(replaceWithCombo);
		textField.addFocusListener(replaceFocusAdapter);
		textField.addKeyListener(replaceKeyListener);
		textField.getDocument().addDocumentListener(replaceDocumentListener);

	}


	/**
	 * Listens for changes in the text field (find search field).
	 */
	private class ReplaceDocumentListener implements DocumentListener {

		public void insertUpdate(DocumentEvent e) {
			JTextComponent findWhatTextField = getTextComponent(findTextCombo);
			if (e.getDocument().equals(findWhatTextField.getDocument())) {
				handleToggleButtons();
			}
		}

		public void removeUpdate(DocumentEvent e) {
			JTextComponent findWhatTextField = getTextComponent(findTextCombo);
			if (e.getDocument().equals(findWhatTextField.getDocument()) && e.getDocument().getLength()==0) {
				findNextButton.setEnabled(false);
				replaceButton.setEnabled(false);
				replaceAllButton.setEnabled(false);
			}
			else {
				handleToggleButtons();
			}
		}

		public void changedUpdate(DocumentEvent e) {
		}

	}


	/**
	 * Listens for the text fields gaining focus.
	 */
	private class ReplaceFocusAdapter extends FocusAdapter {

		@Override
		public void focusGained(FocusEvent e) {

			JTextComponent textField = (JTextComponent)e.getSource();
			textField.selectAll();

			if (textField==getTextComponent(findTextCombo)) {
				// Remember what it originally was, in case they tabbed out.
				lastSearchString = (String)findTextCombo.getSelectedItem();
			}
			else { // if (textField==getTextComponent(replaceWithComboBox)).
				// Remember what it originally was, in case they tabbed out.
				lastReplaceString = (String)replaceWithCombo.getSelectedItem();
			}

		}

	}


	/**
	 * Listens for key presses in the replace dialog.
	 */
	private class ReplaceKeyListener extends KeyAdapter {

		@Override
		public void keyReleased(KeyEvent e) {

			// This is an ugly hack to get around JComboBox's insistence on
			// eating the first Enter keypress it receives when it has focus.
			if (e.getKeyCode()==KeyEvent.VK_ENTER && isPreJava6JRE()) {
				if (e.getSource()==getTextComponent(findTextCombo)) {
					String replaceString = (String)replaceWithCombo.getSelectedItem();
					lastReplaceString = replaceString;	// Just in case it changed too.
					String searchString = (String)findTextCombo.getSelectedItem();
					if (!searchString.equals(lastSearchString)) {
						findNextButton.doClick(0);
						lastSearchString = searchString;
						getTextComponent(findTextCombo).selectAll();
					}
				}
				else { // if (e.getSource()==getTextComponent(replaceWithComboBox)) {
					String searchString = (String)findTextCombo.getSelectedItem();
					lastSearchString = searchString;	// Just in case it changed too.
					String replaceString = (String)replaceWithCombo.getSelectedItem();
					if (!replaceString.equals(lastReplaceString)) {
						findNextButton.doClick(0);
						lastReplaceString = replaceString;
						getTextComponent(replaceWithCombo).selectAll();
					}
				}
			}

		}

	}


}