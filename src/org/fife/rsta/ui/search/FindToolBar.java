/*
 * 09/20/2013
 *
 * FindToolBar - A tool bar for "find" operations in text areas.
 * 
 * This library is distributed under a modified BSD license.  See the included
 * RSyntaxTextArea.License.txt file for details.
 */
package org.fife.rsta.ui.search;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import org.fife.rsta.ui.AssistanceIconPanel;
import org.fife.rsta.ui.UIUtil;
import org.fife.ui.rtextarea.SearchContext;


/**
 * A toolbar for search operations in a text editor application.  This provides
 * a more seamless experience than using a Find or Replace dialog.
 *
 * @author Robert Futrell
 * @version 0.5
 * @see FindDialog
 */
public class FindToolBar extends JPanel {

	private SearchContext context;
	protected ToolBarListener listener;
	protected FindFieldListener findFieldListener;
	protected SearchComboBox findCombo;
	protected SearchComboBox replaceCombo;
	protected JButton findButton;
	protected JButton findPrevButton;
	protected JCheckBox matchCaseCheckBox;
	protected JCheckBox wholeWordCheckBox;
	protected JCheckBox regexCheckBox;
	protected JCheckBox markAllCheckBox;
	private JLabel infoLabel;
	private String textNotFound;
	private Timer markAllTimer;

	/**
	 * Flag to prevent double-modification of SearchContext when e.g. a
	 * FindToolBar and ReplaceToolBar share the same SearchContext.
	 */
	private boolean settingFindTextFromEvent;

	protected static final ResourceBundle msg = ResourceBundle.getBundle(
			"org.fife.rsta.ui.search.SearchToolBar");

	/**
	 * Creates the tool bar.
	 *
	 * @param listener An entity listening for search events.
	 */
	public FindToolBar(SearchListener listener) {

		markAllTimer = new Timer(250, new MarkAllEventNotifier());
		markAllTimer.setRepeats(false);

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
		addSearchListener(listener);
		this.listener = new ToolBarListener();

		// The user should set a shared instance between all subclass
		// instances, but to be safe we set individual ones.
		setSearchContext(new SearchContext());

		ComponentOrientation orientation = ComponentOrientation.
									getOrientation(getLocale());

		add(Box.createHorizontalStrut(5));

		add(createFieldPanel());

		Box rest = new Box(BoxLayout.LINE_AXIS);
		add(rest, BorderLayout.LINE_END);

		rest.add(Box.createHorizontalStrut(5));
		rest.add(createButtonPanel());
		rest.add(Box.createHorizontalStrut(15));

		infoLabel = new JLabel("");
		textNotFound = msg.getString("TextNotFound");
		rest.add(infoLabel);

		rest.add(Box.createHorizontalGlue());

		// Get ready to go.
		applyComponentOrientation(orientation);

	}


	/**
	 * Adds a {@link SearchListener} to this tool bar.  This listener will
	 * be notified when find or replace operations are triggered.
	 *
	 * @param l The listener to add.
	 * @see #removeSearchListener(SearchListener)
	 */
	public void addSearchListener(SearchListener l) {
		listenerList.add(SearchListener.class, l);
	}


	protected Container createButtonPanel() {

		Box panel = new Box(BoxLayout.LINE_AXIS);
		createFindButtons();

		//JPanel bp = new JPanel(new GridLayout(1,2, 5,0));
		//bp.add(findButton); bp.add(findPrevButton);
		JPanel filler = new JPanel(new BorderLayout());
		filler.setBorder(BorderFactory.createEmptyBorder());
		filler.add(findButton);//bp);
		panel.add(filler);
		panel.add(Box.createHorizontalStrut(5));

		matchCaseCheckBox = createCB("MatchCase");
		panel.add(matchCaseCheckBox);

		regexCheckBox = createCB("Regex");
		panel.add(regexCheckBox);

		wholeWordCheckBox = createCB("WholeWord");
		panel.add(wholeWordCheckBox);

		markAllCheckBox = createCB("MarkAll");
		panel.add(markAllCheckBox);

		return panel;

	}


	protected JCheckBox createCB(String key) {
		JCheckBox cb = new JCheckBox(msg.getString(key));
		cb.addActionListener(listener);
		cb.addFocusListener(listener);
		return cb;
	}


	/**
	 * Wraps the specified component in a panel with a leading "content assist
	 * available" icon in front of it.
	 *
	 * @param comp The component with content assist.
	 * @return The wrapper panel.
	 */
	protected Container createContentAssistablePanel(JComponent comp) {
		JPanel temp = new JPanel(new BorderLayout());
		temp.add(comp);
		AssistanceIconPanel aip = new AssistanceIconPanel(comp);
		temp.add(aip, BorderLayout.LINE_START);
		return temp;
	}


	protected Container createFieldPanel() {

		findFieldListener = new FindFieldListener();
		JPanel temp = new JPanel(new BorderLayout());

//		JLabel label = new JLabel(msg.getString("FindWhat"));
//		temp.add(label, BorderLayout.LINE_START);

		findCombo = new SearchComboBox(false);
		JTextComponent findField = UIUtil.getTextComponent(findCombo);
		findFieldListener.install(findField);
		temp.add(createContentAssistablePanel(findCombo));

		return temp;
	}


	protected void createFindButtons() {

		findPrevButton = new JButton(msg.getString("FindPrev"));
		findPrevButton.setActionCommand("FindPrevious");
		findPrevButton.addActionListener(listener);
		findPrevButton.setEnabled(false);

		findButton = new JButton(msg.getString("FindNext")) {
			@Override
			public Dimension getPreferredSize() {
				return findPrevButton.getPreferredSize(); // Always bigger
			}
		};
		findButton.setToolTipText(msg.getString("FindNext.ToolTip"));
		findButton.setActionCommand("FindNext");
		findButton.addActionListener(listener);
		findButton.setEnabled(false);

	}


	/**
	 * Forces a "mark all" event to be sent out, if "mark all" is enabled.
	 *
	 * @param delay If the delay should be honored.
	 */
	protected void doMarkAll(boolean delay) {
		if (context.getMarkAll() && !settingFindTextFromEvent) {
			if (delay) {
				markAllTimer.restart();
			}
			else {
				fireMarkAllEvent();
			}
		}
	}


	void doSearch(boolean forward) {
		if (forward) {
			findButton.doClick(0);
		}
		else {
			findPrevButton.doClick(0);
		}
	}


	/**
	 * Fires a "mark all" search event.
	 */
	private void fireMarkAllEvent() {
		SearchEvent se = new SearchEvent(this, SearchEvent.Type.MARK_ALL,
				context);
		fireSearchEvent(se);
	}


	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * <code>event</code> parameter.
	 * 
	 * @param event The <code>ActionEvent</code> object coming from a
	 *        child component.
	 */
	protected void fireSearchEvent(SearchEvent e) {
		// Process the listeners last to first, notifying
		// those that are interested in this event
		SearchListener[] listeners = listenerList.
								getListeners(SearchListener.class);
		int count = listeners==null ? 0 : listeners.length;
		for (int i=count-1; i>=0; i--) {
			listeners[i].searchEvent(e);
		}
	}


	protected String getFindText() {
		return UIUtil.getTextComponent(findCombo).getText();
	}


	/**
	 * Returns the delay between when the user types and when a "mark all"
	 * event is fired (assuming "mark all" is enabled), in milliseconds.
	 *
	 * @return The delay.
	 * @see #setMarkAllDelay(int)
	 */
	public int getMarkAllDelay() {
		return markAllTimer.getInitialDelay();
	}


	protected String getReplaceText() {
		if (replaceCombo==null) {
			return null;
		}
		return UIUtil.getTextComponent(replaceCombo).getText();
	}


	/**
	 * Returns the search context for this tool bar.
	 *
	 * @return The search context.
	 * @see #setSearchContext(SearchContext)
	 */
	public SearchContext getSearchContext() {
		return context;
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
		findCombo.setAutoCompleteEnabled(b);
	}


	/**
	 * Creates a search event object and notifies all registered listeners.
	 */
	private void handleSearchAction(ActionEvent e) {

		SearchEvent.Type type = null;
		boolean forward = true;
		String action = e.getActionCommand();
		int allowedModifiers =
				InputEvent.CTRL_DOWN_MASK|InputEvent.SHIFT_DOWN_MASK;

		if ("FindNext".equals(action)) {
			type = SearchEvent.Type.FIND;
			int mods = e.getModifiers();
			forward = (mods&allowedModifiers)==0;
			// Add the item to the combo box's list, if it isn't already there.
			JTextComponent tc = UIUtil.getTextComponent(findCombo);
			findCombo.addItem(tc.getText());
		}
		else if ("FindPrevious".equals(action)) {
			type = SearchEvent.Type.FIND;
			forward = false;
			// Add the item to the combo box's list, if it isn't already there.
			JTextComponent tc = UIUtil.getTextComponent(findCombo);
			findCombo.addItem(tc.getText());
		}
		else if ("Replace".equals(action)) {
			type = SearchEvent.Type.REPLACE;
			int mods = e.getModifiers();
			forward = (mods&allowedModifiers)==0;
			// Add the item to the combo box's list, if it isn't already there.
			JTextComponent tc = UIUtil.getTextComponent(findCombo);
			findCombo.addItem(tc.getText());
			tc = UIUtil.getTextComponent(replaceCombo);
			replaceCombo.addItem(tc.getText());
		}
		else if ("ReplaceAll".equals(action)) {
			type = SearchEvent.Type.REPLACE_ALL;
			// Add the item to the combo box's list, if it isn't already there.
			JTextComponent tc = UIUtil.getTextComponent(findCombo);
			findCombo.addItem(tc.getText());
			tc = UIUtil.getTextComponent(replaceCombo);
			replaceCombo.addItem(tc.getText());
		}

		context.setSearchFor(getFindText());
		if (replaceCombo!=null) {
			context.setReplaceWith(replaceCombo.getSelectedString());
		}

		// Use a different context to prevent modifying forward/backward
		// property.  While this would likely never be noticed by folks, as
		// they'd have to be using both the Find/Replace dialogs and these
		// tool bars, it still would bug me.
		SearchContext context2 = context.clone();
		context2.setSearchForward(forward);

		SearchEvent se = new SearchEvent(this, type, context2);
		fireSearchEvent(se);
		
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

		FindReplaceButtonsEnableResult result =
				new FindReplaceButtonsEnableResult(true, null);

		String text = getFindText();
		if (text.length()==0) {
			result = new FindReplaceButtonsEnableResult(false, null);
		}
		else if (regexCheckBox.isSelected()) {
			try {
				Pattern.compile(text);
			} catch (PatternSyntaxException pse) {
				result = new FindReplaceButtonsEnableResult(false,
						pse.getMessage());
			}
		}

		boolean enable = result.getEnable();
		findButton.setEnabled(enable);
		findPrevButton.setEnabled(enable);

		// setBackground doesn't show up with XP Look and Feel!
		//findTextComboBox.setBackground(enable ?
		//		UIManager.getColor("ComboBox.background") : Color.PINK);
		JTextComponent tc = UIUtil.getTextComponent(findCombo);
		tc.setForeground(enable ? UIManager.getColor("TextField.foreground") :
									Color.RED);

		String tooltip = SearchUtil.getToolTip(result);
		tc.setToolTipText(tooltip); // Always set, even if null

		return result;

	}


	/**
	 * Initializes the UI in this tool bar from a search context.  This is
	 * called whenever a new search context is installed on this tool bar
	 * (which should practically be never).
	 */
	protected void initUIFromContext() {
		if (findCombo==null) { // First time through, stuff not initialized yet
			return;
		}
		setFindText(context.getSearchFor());
		if (replaceCombo!=null) {
			setReplaceText(context.getReplaceWith());
		}
		matchCaseCheckBox.setSelected(context.getMatchCase());
		wholeWordCheckBox.setSelected(context.getWholeWord());
		regexCheckBox.setSelected(context.isRegularExpression());
	}


	/**
	 * Removes a {@link SearchListener} from this tool bar.
	 *
	 * @param l The listener to remove
	 * @see #addSearchListener(SearchListener)
	 */
	public void removeSearchListener(SearchListener l) {
		listenerList.remove(SearchListener.class, l);
	}


	/**
	 * Makes the find field on this toolbar request focus.  If it is already
	 * focused, its text is selected.
	 */
	@Override
	public boolean requestFocusInWindow() {
		JTextComponent findField = UIUtil.getTextComponent(findCombo);
		findField.selectAll();
		return findField.requestFocusInWindow();
	}


	protected void setFindText(String text) {
		UIUtil.getTextComponent(findCombo).setText(text);
		//findCombo.setSelectedItem(text);
	}


	/**
	 * Sets the delay between when the user types and when a "mark all"
	 * event is fired (assuming "mark all" is enabled), in milliseconds.
	 *
	 * @param millis The new delay.  This should be &gt;= zero.
	 * @see #getMarkAllDelay()
	 */
	public void setMarkAllDelay(int millis) {
		markAllTimer.setInitialDelay(millis);
	}


	protected void setReplaceText(String text) {
		if (replaceCombo!=null) {
			UIUtil.getTextComponent(replaceCombo).setText(text);
			//replaceCombo.setSelectedItem(text);
		}
	}


	/**
	 * Sets the search context for this tool bar.  You'll usually want to call
	 * this method for all tool bars and give them the same search context,
	 * so that their options (match case, etc.) stay in sync with one another.
	 *
	 * @param context The new search context.  This cannot be <code>null</code>.
	 * @see #getSearchContext()
	 */
	public void setSearchContext(SearchContext context) {
		if (this.context!=null) {
			this.context.removePropertyChangeListener(listener);
		}
		this.context = context;
		this.context.addPropertyChangeListener(listener);
		initUIFromContext();
	}


	/**
	 * Listens for events in this tool bar.  Keeps the UI in sync with the
	 * search context and vice versa.
	 */
	private class ToolBarListener extends FocusAdapter
			implements ActionListener, PropertyChangeListener {

		public void actionPerformed(ActionEvent e) {

			Object source = e.getSource();

			if (source==matchCaseCheckBox) {
				context.setMatchCase(matchCaseCheckBox.isSelected());
			}
			else if (source==wholeWordCheckBox) {
				context.setWholeWord(wholeWordCheckBox.isSelected());
			}
			else if (source==regexCheckBox) {
				context.setRegularExpression(regexCheckBox.isSelected());
			}
			else if (source==markAllCheckBox) {
				context.setMarkAll(markAllCheckBox.isSelected());
			}
			else {
				handleSearchAction(e);
			}

		}

		@Override
		public void focusGained(FocusEvent e) {
			if (e.getSource() instanceof JCheckBox) { // Always true
				Component opposite = e.getOppositeComponent();
				if (opposite instanceof JTextField) {
					// From Find or Replace field - don't select all, just keep
					// focus in the text field itself.
					findFieldListener.selectAll = false;
					opposite.requestFocusInWindow();
				}
				else {
					// From anywhere else in the app - focus and select all
					findCombo.requestFocusInWindow();
				}
			}
		}

		public void propertyChange(PropertyChangeEvent e) {

			// A property changed on the context itself.
			String prop = e.getPropertyName();

			if (SearchContext.PROPERTY_MATCH_CASE.equals(prop)) {
				boolean newValue = ((Boolean)e.getNewValue()).booleanValue();
				matchCaseCheckBox.setSelected(newValue);
				if (markAllCheckBox.isSelected()) {
					doMarkAll(false);
				}
			}
			else if (SearchContext.PROPERTY_MATCH_WHOLE_WORD.equals(prop)) {
				boolean newValue = ((Boolean)e.getNewValue()).booleanValue();
				wholeWordCheckBox.setSelected(newValue);
				if (markAllCheckBox.isSelected()) {
					doMarkAll(false);
				}
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
				if (markAllCheckBox.isSelected()) {
					doMarkAll(false);
				}
			}
			else if (SearchContext.PROPERTY_MARK_ALL.equals(prop)) {
				boolean newValue = ((Boolean)e.getNewValue()).booleanValue();
				markAllCheckBox.setSelected(newValue);
				if (markAllCheckBox.isSelected()) {
					doMarkAll(false);
				}
			}
			else if (SearchContext.PROPERTY_SEARCH_FOR.equals(prop)) {
				String newValue = (String)e.getNewValue();
				String oldValue = getFindText();
				// Prevents IllegalStateExceptions
				if (!newValue.equals(oldValue)) {
					settingFindTextFromEvent = true;
					setFindText(newValue);
					settingFindTextFromEvent = false;
				}
			}
			else if (SearchContext.PROPERTY_REPLACE_WITH.equals(prop)) {
				String newValue = (String)e.getNewValue();
				String oldValue = getReplaceText();
				// Prevents IllegalStateExceptions
				if (!newValue.equals(oldValue)) {
					setReplaceText(newValue);
				}
			}

		}

	}


	/**
	 * Listens for events in the Find (and Replace, in the subclass) search
	 * field.
	 */
	protected class FindFieldListener extends KeyAdapter
					implements DocumentListener, FocusListener {

		protected boolean selectAll;

		public void changedUpdate(DocumentEvent e) {
		}

		public void focusGained(FocusEvent e) {
			if (selectAll) {
				UIUtil.getTextComponent(findCombo).selectAll();
			}
			selectAll = true;
		}

		public void focusLost(FocusEvent e) {
		}

		protected void handleDocumentEvent(DocumentEvent e) {
			handleToggleButtons();
			if (context.getMarkAll() && !settingFindTextFromEvent) {
				JTextComponent findField = UIUtil.getTextComponent(findCombo);
				// Don't re-fire "mark all" events for "replace" text edits
				if (e.getDocument()==findField.getDocument()) {
					context.setSearchFor(findField.getText());
					doMarkAll(true);
				}
			}
		}

		public void insertUpdate(DocumentEvent e) {
			handleDocumentEvent(e);
		}

		public void install(JTextComponent field) {
			field.getDocument().addDocumentListener(this);
			field.addKeyListener(this);
			field.addFocusListener(this);
		}

		@Override
		public void keyTyped(KeyEvent e) {
			if (e.getKeyChar()=='\n') {
				int mod = e.getModifiers();
				int ctrlShift = InputEvent.CTRL_MASK|InputEvent.SHIFT_MASK;
				boolean forward = (mod&ctrlShift) == 0;
				doSearch(forward);
			}
		}

		public void removeUpdate(DocumentEvent e) {
			handleDocumentEvent(e);
		}

	}


	/**
	 * Called when the user edits the "Find" field's contents, after a slight
	 * delay.  Fires a "mark all" search event for applications that want to
	 * display "mark all" results on the fly.
	 */
	private class MarkAllEventNotifier implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			fireMarkAllEvent();
		}

	}

}