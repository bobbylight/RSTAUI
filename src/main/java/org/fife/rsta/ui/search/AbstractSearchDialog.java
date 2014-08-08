/*
 * 04/08/2004
 *
 * AbstractSearchDialog.java - Base class for all search dialogs
 * (find, replace, etc.).
 * This library is distributed under a modified BSD license.  See the included
 * RSTAUI.License.txt file for details.
 */
package org.fife.rsta.ui.search;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;

import org.fife.rsta.ui.EscapableDialog;
import org.fife.rsta.ui.UIUtil;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rtextarea.SearchContext;


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

	protected SearchContext context;
	private SearchContextListener contextListener;

	// Conditions check boxes and the panel they go in.
	// This should be added in the actual layout of the search dialog.
	protected JCheckBox caseCheckBox;
	protected JCheckBox wholeWordCheckBox;
	protected JCheckBox regexCheckBox;
	protected JPanel searchConditionsPanel;

	/**
	 * The image to use beside a text component when content assist is
	 * available.
	 */
	private static Image contentAssistImage;

	/**
	 * The combo box where the user enters the text for which to search.
	 */
	protected SearchComboBox findTextCombo;

	// Miscellaneous other stuff.
	protected JButton cancelButton;

	private static final ResourceBundle msg = ResourceBundle.
			getBundle("org.fife.rsta.ui.search.Search");


	/**
	 * Constructor.  Does initializing for parts common to all search
	 * dialogs.
	 *
	 * @param owner The dialog that owns this search dialog.
	 */
	public AbstractSearchDialog(Dialog owner) {
		super(owner);
		init();
	}


	/**
	 * Constructor.  Does initializing for parts common to all search
	 * dialogs.
	 *
	 * @param owner The window that owns this search dialog.
	 */
	public AbstractSearchDialog(Frame owner) {
		super(owner);
		init();
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
		}

		// They check/uncheck the "Whole word" checkbox on the Find dialog.
		else if (command.equals("FlipWholeWord")) {
			boolean wholeWord = wholeWordCheckBox.isSelected();
			context.setWholeWord(wholeWord);
		}

		// They check/uncheck the "Regular expression" checkbox.
		else if (command.equals("FlipRegEx")) {
			boolean useRegEx = regexCheckBox.isSelected();
			context.setRegularExpression(useRegEx);
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
	 * Returns the default search context to use for this dialog.  Applications
	 * that create new subclasses of this class can provide customized
	 * search contexts here.
	 *
	 * @return The default search context.
	 */
	protected SearchContext createDefaultSearchContext() {
		return new SearchContext();
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


	@Override
	protected void escapePressed() {
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
		if (findTextCombo.hideAutoCompletePopups()) {
			return;
		}
		super.escapePressed();
	}


	/**
	 * Makes the "Find text" field active.
	 */
	protected void focusFindTextField() {
		JTextComponent textField = UIUtil.getTextComponent(findTextCombo);
		textField.requestFocusInWindow();
		textField.selectAll();
	}


	protected ResourceBundle getBundle() {
		return msg;
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
		return regexCheckBox.getText();
	}


	/**
	 * Returns the search context used by this dialog.
	 *
	 * @return The search context.
	 * @see #setSearchContext(SearchContext)
	 */
	public SearchContext getSearchContext() {
		return context;
	}


	/**
	 * Returns the text to search for.
	 *
	 * @return The text the user wants to search for.
	 */
	public String getSearchString() {
		return findTextCombo.getSelectedString();
	}


	public static String getString(String key) {
		return msg.getString(key);
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
	 * Called when the regex checkbox is clicked (or its value is modified
	 * via a change to the search context).  Subclasses can override
	 * to add custom behavior, but should call the super implementation.
	 */
	protected void handleRegExCheckBoxClicked() {
		handleToggleButtons();
		// "Content assist" support
		boolean b = regexCheckBox.isSelected();
		findTextCombo.setAutoCompleteEnabled(b);
	}


	/**
	 * Called whenever a property in the search context is modified.
	 * Subclasses should override if they listen for additional properties.
	 *
	 * @param e The property change event fired.
	 */
	protected void handleSearchContextPropertyChanged(PropertyChangeEvent e) {

		// A property changed on the context itself.
		String prop = e.getPropertyName();

		if (SearchContext.PROPERTY_MATCH_CASE.equals(prop)) {
			boolean newValue = ((Boolean)e.getNewValue()).booleanValue();
			caseCheckBox.setSelected(newValue);
		}
		else if (SearchContext.PROPERTY_MATCH_WHOLE_WORD.equals(prop)) {
			boolean newValue = ((Boolean)e.getNewValue()).booleanValue();
			wholeWordCheckBox.setSelected(newValue);
		}
		//else if (SearchContext.PROPERTY_SEARCH_FORWARD.equals(prop)) {
		//	boolean newValue = ((Boolean)e.getNewValue()).booleanValue();
		//	...
		//}
		//else if (SearchContext.PROPERTY_SELECTION_ONLY.equals(prop)) {
		//	boolean newValue = ((Boolean)e.getNewValue()).booleanValue();
		//	...
		//}
		else if (SearchContext.PROPERTY_USE_REGEX.equals(prop)) {
			boolean newValue = ((Boolean)e.getNewValue()).booleanValue();
			regexCheckBox.setSelected(newValue);
			handleRegExCheckBoxClicked();
		}
		else if (SearchContext.PROPERTY_SEARCH_FOR.equals(prop)) {
			String newValue = (String)e.getNewValue();
			String oldValue = getSearchString();
			// Prevents IllegalStateExceptions
			if (!newValue.equals(oldValue)) {
				setSearchString(newValue);
			}
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
	protected FindReplaceButtonsEnableResult handleToggleButtons() {

		//String text = getSearchString();
		JTextComponent tc = UIUtil.getTextComponent(findTextCombo);
		String text = tc.getText();
		if (text.length()==0) {
			return new FindReplaceButtonsEnableResult(false, null);
		}
		if (regexCheckBox.isSelected()) {
			try {
				Pattern.compile(text);
			} catch (PatternSyntaxException pse) {
				return new FindReplaceButtonsEnableResult(false, pse.getMessage());
			}
		}
		return new FindReplaceButtonsEnableResult(true, null);
	}


	private void init() {

		// The user should set a shared instance between all subclass
		// instances, but to be safe we set individual ones.
		contextListener = new SearchContextListener();
		setSearchContext(createDefaultSearchContext());

		// Make a panel containing the option check boxes.
		searchConditionsPanel = new JPanel();
		searchConditionsPanel.setLayout(new BoxLayout(
						searchConditionsPanel, BoxLayout.Y_AXIS));
		caseCheckBox = createCheckBox(msg, "MatchCase");
		searchConditionsPanel.add(caseCheckBox);
		wholeWordCheckBox = createCheckBox(msg, "WholeWord");
		searchConditionsPanel.add(wholeWordCheckBox);
		regexCheckBox = createCheckBox(msg, "RegEx");
		searchConditionsPanel.add(regexCheckBox);

		// Initialize any text fields.
		findTextCombo = new SearchComboBox(null, false);

		// Initialize other stuff.
		cancelButton = new JButton(getString("Cancel"));
		//cancelButton.setMnemonic((int)getString("CancelMnemonic").charAt(0));
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(this);

	}


	protected boolean matchesSearchFor(String text) {
		if (text==null || text.length()==0) {
			return false;
		}
		String searchFor = findTextCombo.getSelectedString();
		if (searchFor!=null && searchFor.length()>0) {
			boolean matchCase = caseCheckBox.isSelected();
			if (regexCheckBox.isSelected()) {
				int flags = Pattern.MULTILINE; // '^' and '$' are done per line.
				flags = RSyntaxUtilities.getPatternFlags(matchCase, flags);
				Pattern pattern = null;
				try {
					pattern = Pattern.compile(searchFor, flags);
				} catch (PatternSyntaxException pse) {
					pse.printStackTrace(); // Never happens
					return false;
				}
				return pattern.matcher(text).matches();
			}
			else {
				if (matchCase) {
					return searchFor.equals(text);
				}
				return searchFor.equalsIgnoreCase(text);
			}
		}
		return false;
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
	 * Initializes the UI in this tool bar from a search context.  This is
	 * called whenever a new search context is installed on this tool bar
	 * (which should practically be never).
	 */
	protected void refreshUIFromContext() {
		if (this.caseCheckBox==null) {
			return; // First time through, UI not realized yet
		}
		this.caseCheckBox.setSelected(context.getMatchCase());
		this.regexCheckBox.setSelected(context.isRegularExpression());
		this.wholeWordCheckBox.setSelected(context.getWholeWord());
	}


	/**
	 * Overridden to ensure the "Find text" field gets focused.
	 */
	@Override
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
		regexCheckBox.setText(text);
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
	public void setSearchContext(SearchContext context) {
		if (this.context!=null) {
			this.context.removePropertyChangeListener(contextListener);
		}
		this.context = context;
		this.context.addPropertyChangeListener(contextListener);
		refreshUIFromContext();
	}


	/**
	 * Sets the <code>java.lang.String</code> to search for.
	 *
	 * @param newSearchString The <code>tring</code> to put into
	 *        the search field.
	 */
	public void setSearchString(String newSearchString) {
		findTextCombo.addItem(newSearchString);
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
	 * Listens for properties changing in the search context.
	 */
	private class SearchContextListener implements PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent e) {
			handleSearchContextPropertyChanged(e);
		}

	}


}