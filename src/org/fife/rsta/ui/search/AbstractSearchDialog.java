/*
 * 04/08/2004
 *
 * AbstractSearchDialog.java - Base class for all search dialogs
 * (find, replace, etc.).
 * This library is distributed under a modified BSD license.  See the included
 * RSyntaxTextArea.License.txt file for details.
 */
package org.fife.rsta.ui.search;

import java.awt.Frame;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;

import org.fife.rsta.ui.EscapableDialog;
import org.fife.rsta.ui.MaxWidthComboBox;
import org.fife.rsta.ui.UIUtil;


/**
 * Base class for all search dialogs (find, replace, find in files, etc.).
 * This class is not useful on its own; you should use either FindDialog
 * or ReplaceDialog, or extend this class to create your own search
 * dialog.
 *
 * @author Robert Futrell
 * @version 0.1
 */
public class AbstractSearchDialog extends EscapableDialog
							implements ActionListener {

	private static final long serialVersionUID = 1L;

	public static final String MATCH_CASE_PROPERTY		= "SearchDialog.MatchCase";
	public static final String MATCH_WHOLE_WORD_PROPERTY	= "SearchDialog.MatchWholeWord";
	public static final String USE_REG_EX_PROPERTY		= "SearchDialog.UseRegularExpressions";

	protected SearchDialogSearchContext context;

	// Conditions check boxes and the panel they go in.
	// This should be added in the actual layout of the search dialog.
	protected JCheckBox caseCheckBox;
	protected JCheckBox wholeWordCheckBox;
	protected JCheckBox regExpCheckBox;
	protected JPanel searchConditionsPanel;

	/**
	 * The image to use beside a text component when content assist is
	 * available.
	 */
	private static Image contentAssistImage;

	/**
	 * The combo box where the user enters the text for which to search.
	 */
	protected JComboBox findTextCombo;

	// Miscellaneous other stuff.
	protected JButton cancelButton;

	protected static final ResourceBundle msg = ResourceBundle.
			getBundle("org.fife.ui.rsyntaxtextarea.search.Search");


	/**
	 * Constructor.  Does initializing for parts common to all search
	 * dialogs.
	 *
	 * @param owner The window that owns this search dialog.
	 */
	public AbstractSearchDialog(Frame owner) {

		super(owner);

		// The user should set a shared instance between all subclass
		// instances, but to be safe we set individual ones.
		context = new SearchDialogSearchContext();

		// Make a panel containing the option check boxes.
		searchConditionsPanel = new JPanel();
		searchConditionsPanel.setLayout(new BoxLayout(
						searchConditionsPanel, BoxLayout.Y_AXIS));
		caseCheckBox = createCheckBox(msg, "MatchCase");
		searchConditionsPanel.add(caseCheckBox);
		wholeWordCheckBox = createCheckBox(msg, "WholeWord");
		searchConditionsPanel.add(wholeWordCheckBox);
		regExpCheckBox = createCheckBox(msg, "RegEx");
		searchConditionsPanel.add(regExpCheckBox);

		// Initialize any text fields.
		findTextCombo = createSearchComboBox(false);

		// Initialize other stuff.
		cancelButton = new JButton(getString("Cancel"));
		cancelButton.setMnemonic((int)getString("CancelMnemonic").charAt(0));
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(this);

	}


	/**
	 * Listens for actions in this search dialog.
	 */
	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();

		// They check/uncheck the "Match Case" checkbox on the Find dialog.
		if (command.equals("FlipMatchCase")) {
			boolean matchCase = caseCheckBox.isSelected();
			context.setMatchCase(matchCase);
			firePropertyChange(MATCH_CASE_PROPERTY, !matchCase, matchCase);
		}

		// They check/uncheck the "Whole word" checkbox on the Find dialog.
		else if (command.equals("FlipWholeWord")) {
			boolean wholeWord = wholeWordCheckBox.isSelected();
			context.setWholeWord(wholeWord);
			firePropertyChange(MATCH_WHOLE_WORD_PROPERTY, !wholeWord, wholeWord);
		}

		// They check/uncheck the "Regular expression" checkbox.
		else if (command.equals("FlipRegEx")) {
			boolean useRegEx = regExpCheckBox.isSelected();
			context.setRegularExpression(useRegEx);
			handleRegExCheckBoxClicked();
			firePropertyChange(USE_REG_EX_PROPERTY, !useRegEx, useRegEx);
		}

		// If they press the "Cancel" button.
		else if (command.equals("Cancel")) {
			setVisible(false);
		}

	}


	private JCheckBox createCheckBox(ResourceBundle msg, String keyRoot) {
		JCheckBox cb = new JCheckBox(msg.getString(keyRoot));
		cb.setMnemonic((int)msg.getString(keyRoot + "Mnemonic").charAt(0));
		cb.setActionCommand("Flip" + keyRoot);
		cb.addActionListener(this);
		return cb;
	}


	/**
	 * Returns a combo box suitable for a "search in" or "replace with"
	 * field. Subclasses can override to provide combo boxes with enhanced
	 * functionality.
	 *
	 * @param replace Whether this is a "replace" combo box (as opposed to a
	 *        "find" combo box).  This affects what content assistance they
	 *        receive.
	 * @return The combo box.
	 */
	protected MaxWidthComboBox createSearchComboBox(boolean replace) {
		MaxWidthComboBox combo = new RegexAwareComboBox(replace);
		UIUtil.fixComboOrientation(combo);
		return combo;
	}


	/**
	 * Returns a titled border for panels on search dialogs.
	 *
	 * @param title The title for the border.
	 * @return The border.
	 */
	protected Border createTitledBorder(String title) {
		if (title!=null && title.charAt(title.length()-1)!=':')
			title += ":";
		return BorderFactory.createTitledBorder(title);
	}


	protected void escapePressed() {
		if (findTextCombo instanceof RegexAwareComboBox) {
			RegexAwareComboBox racb = (RegexAwareComboBox)findTextCombo;
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
	 * Makes the "Find text" field active.
	 */
	protected void focusFindTextField() {
		JTextComponent textField = getTextComponent(findTextCombo);
		textField.requestFocusInWindow();
		textField.selectAll();
	}


	/**
	 * Returns the text on the Cancel button.
	 *
	 * @return The text on the Cancel button.
	 * @see #setCancelButtonText
	 */
	public final String getCancelButtonText() {
		return cancelButton.getText();
	}


	/**
	 * Returns the image to display beside text components when content assist
	 * is available.
	 *
	 * @return The image to use.
	 */
	public static Image getContentAssistImage() {
		if (contentAssistImage==null) {
			URL url = AbstractSearchDialog.class.getResource("lightbulb.png");
			try {
				contentAssistImage = ImageIO.read(url);
			} catch (IOException ioe) { // Never happens
				ioe.printStackTrace();
			}
		}
		return contentAssistImage;
	}


	/**
	 * Returns the text for the "Match Case" check box.
	 *
	 * @return The text for the "Match Case" check box.
	 * @see #setMatchCaseCheckboxText
	 */
	public final String getMatchCaseCheckboxText() {
		return caseCheckBox.getText();
	}


	/**
	 * Returns the text for the "Regular Expression" check box.
	 *
	 * @return The text for the "Regular Expression" check box.
	 * @see #setRegularExpressionCheckboxText
	 */
	public final String getRegularExpressionCheckboxText() {
		return regExpCheckBox.getText();
	}


	/**
	 * Returns the search context used by this dialog.
	 *
	 * @return The search context.
	 * @see #setSearchContext(SearchDialogSearchContext)
	 */
	public SearchDialogSearchContext getSearchContext() {
		return context;
	}


	/**
	 * Returns the text to search for.
	 *
	 * @return The text the user wants to search for.
	 */
	public String getSearchString() {
		return (String)findTextCombo.getSelectedItem();
	}


	/**
	 * Returns the <code>Strings</code> contained in the "Find what" combo
	 * box.
	 *
	 * @return A <code>java.util.Vector</code> of strings found in the "Find
	 *         what" combo box.  If that combo box is empty, than a
	 *         zero-length <code>Vector</code> is returned.
	 */
	public Vector getSearchStrings() {

		// First, ensure that the item in the combo box editor is indeed in the combo box.
		int selectedIndex = findTextCombo.getSelectedIndex();
		if (selectedIndex==-1) {
			findTextCombo.addItem(getSearchString());
		}

		// If they just searched for an item that's already in the list other than
		// the first, move it to the first position.
		else if (selectedIndex>0) {
			Object item = findTextCombo.getSelectedItem();
			findTextCombo.removeItem(item);
			findTextCombo.insertItemAt(item, 0);
			findTextCombo.setSelectedIndex(0);
		}


		int itemCount = findTextCombo.getItemCount();
		Vector vector = new Vector(itemCount);
		for (int i=0; i<itemCount; i++)
			vector.add(findTextCombo.getItemAt(i));
		return vector;

	}


	public static String getString(String key) {
		return msg.getString(key);
	}


	/**
	 * Returns the text editor component for the specified combo box.
	 *
	 * @param combo The combo box.
	 * @return The text component.
	 */
	protected static JTextComponent getTextComponent(JComboBox combo) {
		return (JTextComponent)combo.getEditor().getEditorComponent();
	}


	/**
	 * Returns the text for the "Whole Word" check box.
	 *
	 * @return The text for the "Whole Word" check box.
	 * @see #setWholeWordCheckboxText
	 */
	public final String getWholeWordCheckboxText() {
		return wholeWordCheckBox.getText();
	}


	/**
	 * Called when the regex checkbox is clicked.  Subclasses can override
	 * to add custom behavior, but should call the super implementation.
	 */
	protected void handleRegExCheckBoxClicked() {

		handleToggleButtons();

		// "Content assist" support
		boolean b = regExpCheckBox.isSelected();
		// Always true except when debugging
		if (findTextCombo instanceof RegexAwareComboBox) {
			RegexAwareComboBox racb = (RegexAwareComboBox)findTextCombo;
			racb.setAutoCompleteEnabled(b);
		}

	}


	/**
	 * Returns whether any action-related buttons (Find Next, Replace, etc.)
	 * should be enabled.  Subclasses can call this method when the "Find What"
	 * or "Replace With" text fields are modified.  They can then
	 * enable/disable any components as appropriate.
	 *
	 * @return Whether the buttons should be enabled.
	 */
	protected EnableResult handleToggleButtons() {

		//String text = getSearchString();
		JTextComponent tc = getTextComponent(findTextCombo);
		String text = tc.getText();
		if (text.length()==0) {
			return new EnableResult(false, null);
		}
		if (regExpCheckBox.isSelected()) {
			try {
				Pattern.compile(text);
			} catch (PatternSyntaxException pse) {
				return new EnableResult(false, pse.getMessage());
			}
		}
		return new EnableResult(true, null);
	}


	/**
	 * This method allows us to check if the current JRE is 1.4 or 1.5.
	 * This is used to workaround some Java bugs, for example, pre 1.6,
	 * JComboBoxes would "swallow" enter key presses in them when their
	 * content changed.  This causes the user to have to press Enter twice
	 * when entering text to search for in a "Find" dialog, so instead we
	 * detect if a JRE is old enough to have this behavior and, if so,
	 * programmitcally press the Find button.
	 *
	 * @return Whether this is a 1.4 or 1.5 JRE.
	 */
	protected static boolean isPreJava6JRE() {
		// We only support 1.4+, so no need to check 1.3, etc.
		String version = System.getProperty("java.specification.version");
		return version.startsWith("1.5") || version.startsWith("1.4");
	}


	/**
	 * Returns whether the characters on either side of
	 * <code>substr(searchIn,startPos,startPos+searchStringLength)</code>
	 * are whitespace.  While this isn't the best definition of "whole word",
	 * it's the one we're going to use for now.
	 */
	public static final boolean isWholeWord(CharSequence searchIn,
											int offset, int len) {

		boolean wsBefore, wsAfter;

		try {
			wsBefore = Character.isWhitespace(searchIn.charAt(offset - 1));
		} catch (IndexOutOfBoundsException e) { wsBefore = true; }
		try {
			wsAfter  = Character.isWhitespace(searchIn.charAt(offset + len));
		} catch (IndexOutOfBoundsException e) { wsAfter = true; }

		return wsBefore && wsAfter;

	}


	/**
	 * Refreshes UI elements to be in sync with the (probably shared) search
	 * context.  Subclasses can override to synchronize added UI components.
	 */
	protected void refreshUIFromContext() {
		this.caseCheckBox.setSelected(context.getMatchCase());
		this.regExpCheckBox.setSelected(context.isRegularExpression());
		this.wholeWordCheckBox.setSelected(context.getWholeWord());
	}


	/**
	 * Overridden to ensure the "Find text" field gets focused.
	 */
	public void requestFocus() {
		super.requestFocus();
		focusFindTextField();
	}


	/**
	 * Sets the text on the Cancel button.
	 *
	 * @param text The text for the Cancel button.
	 * @see #getCancelButtonText
	 */
	public final void setCancelButtonText(String text) {
		cancelButton.setText(text);
	}


	/**
	 * Sets the text for the "Match Case" check box.
	 *
	 * @param text The text for the "Match Case" check box.
	 * @see #getMatchCaseCheckboxText
	 */
	public final void setMatchCaseCheckboxText(String text) {
		caseCheckBox.setText(text);
	}


	/**
	 * Sets the text for the "Regular Expression" check box.
	 *
	 * @param text The text for the "Regular Expression" check box.
	 * @see #getRegularExpressionCheckboxText
	 */
	public final void setRegularExpressionCheckboxText(String text) {
		regExpCheckBox.setText(text);
	}


	/**
	 * Sets the search context for this dialog.  You'll usually want to call
	 * this method for all search dialogs and give them the same search
	 * context, so that their options (match case, etc.) stay in sync with one
	 * another.
	 *
	 * @param context The new search context.  This cannot be <code>null</code>.
	 * @see #getSearchContext()
	 */
	public void setSearchContext(SearchDialogSearchContext context) {
		this.context = context;
		refreshUIFromContext();
	}


	/**
	 * Sets the <code>java.lang.String</code> to search for.
	 *
	 * @param newSearchString The <code>java.lang.String</code> to put into
	 *        the search field.
	 */
	public void setSearchString(String newSearchString) {
		findTextCombo.addItem(newSearchString);
		findTextCombo.setSelectedIndex(0);
	}


	/**
	 * {@inheritDoc}
	 */
	public void setVisible(boolean visible) {

		// Make sure content assist is enabled (regex check box might have
		// been checked in a different search dialog).
		if (visible) {
			refreshUIFromContext();
			boolean regexEnabled = regExpCheckBox.isSelected();
			// Always true except when debugging.  findTextCombo done in parent
			if (findTextCombo instanceof RegexAwareComboBox) {
				RegexAwareComboBox racb = (RegexAwareComboBox)findTextCombo;
				racb.setAutoCompleteEnabled(regexEnabled);
			}
		}

		super.setVisible(visible);

	}


	/**
	 * Sets the text for the "Whole Word" check box.
	 *
	 * @param text The text for the "Whole Word" check box.
	 * @see #getWholeWordCheckboxText
	 */
	public final void setWholeWordCheckboxText(String text) {
		wholeWordCheckBox.setText(text);
	}


	/**
	 * Returns the result of whether the "action" buttons such as "Find"
	 * and "Replace" should be enabled.
	 *
	 * @author Robert Futrell
	 */
	protected static class EnableResult {

		private boolean enable;
		private String tooltip;

		public EnableResult(boolean enable, String tooltip) {
			this.enable = enable;
			this.tooltip = tooltip;
		}

		public boolean getEnable() {
			return enable;
		}

		public String getToolTip() {
			return tooltip;
		}

		public void setEnable(boolean enable) {
			this.enable = enable;
		}

	}


}