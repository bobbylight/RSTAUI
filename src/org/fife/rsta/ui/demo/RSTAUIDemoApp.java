package org.fife.rsta.ui.demo;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.BadLocationException;

import org.fife.rsta.ui.GoToDialog;
import org.fife.rsta.ui.search.FindDialog;
import org.fife.rsta.ui.search.ReplaceDialog;
import org.fife.rsta.ui.search.SearchDialogSearchContext;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SearchEngine;


/**
 * An application that demonstrates use of the RSTAUI project.  Please don't
 * take this as good application design; it's just a simple example.<p>
 *
 * Unlike the library itself, this class is public domain.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class RSTAUIDemoApp extends JFrame implements ActionListener {

	private RSyntaxTextArea textArea;
	private FindDialog findDialog;
	private ReplaceDialog replaceDialog;


	public RSTAUIDemoApp() {

		initSearchDialogs();

		setJMenuBar(createMenuBar());
		JPanel cp = new JPanel(new BorderLayout());
		setContentPane(cp);

		textArea = new RSyntaxTextArea(25, 60);
		RTextScrollPane sp = new RTextScrollPane(textArea);
		cp.add(sp);

		setTitle("RSTAUI Demo Application");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);

	}


	private JMenuBar createMenuBar() {
		JMenuBar mb = new JMenuBar();
		JMenu menu = new JMenu("Search");
		menu.add(new JMenuItem(new ShowFindDialogAction()));
		menu.add(new JMenuItem(new ShowReplaceDialogAction()));
		menu.add(new JMenuItem(new GoToLineAction()));
		mb.add(menu);
		return mb;
	}


	/**
	 * Creates our Find and Replace dialogs.
	 */
	public void initSearchDialogs() {

		findDialog = new FindDialog(this, this);
		replaceDialog = new ReplaceDialog(this, this);

		// This ties the properties of the two dialogs together (match
		// case, regex, etc.).
		replaceDialog.setSearchContext(findDialog.getSearchContext());

	}


	/**
	 * Listens for events from our search dialogs and actually does the dirty
	 * work.
	 */
	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();
		SearchDialogSearchContext context = findDialog.getSearchContext();

		if (FindDialog.ACTION_FIND.equals(command)) {
			if (!SearchEngine.find(textArea, context)) {
				UIManager.getLookAndFeel().provideErrorFeedback(textArea);
			}
		}
		else if (ReplaceDialog.ACTION_REPLACE.equals(command)) {
			if (!SearchEngine.replace(textArea, context)) {
				UIManager.getLookAndFeel().provideErrorFeedback(textArea);
			}
		}
		else if (ReplaceDialog.ACTION_REPLACE_ALL.equals(command)) {
			int count = SearchEngine.replaceAll(textArea, context);
			JOptionPane.showMessageDialog(null, count
					+ " occurrences replaced.");
		}

	}


	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					e.printStackTrace();
				}
				new RSTAUIDemoApp().setVisible(true);
			}
		});
	}


	private class GoToLineAction extends AbstractAction {

		public GoToLineAction() {
			super("Go To Line...");
			int c = getToolkit().getMenuShortcutKeyMask();
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_L, c));
		}

		public void actionPerformed(ActionEvent e) {
			if (findDialog.isVisible()) {
				findDialog.setVisible(false);
			}
			if (replaceDialog.isVisible()) {
				replaceDialog.setVisible(false);
			}
			GoToDialog dialog = new GoToDialog(RSTAUIDemoApp.this);
			dialog.setMaxLineNumberAllowed(textArea.getLineCount());
			dialog.setVisible(true);
			int line = dialog.getLineNumber();
			if (line>0) {
				try {
					textArea.setCaretPosition(textArea.getLineStartOffset(line-1));
				} catch (BadLocationException ble) { // Never happens
					UIManager.getLookAndFeel().provideErrorFeedback(textArea);
					ble.printStackTrace();
				}
			}
		}

	}


	private class ShowFindDialogAction extends AbstractAction {
		
		public ShowFindDialogAction() {
			super("Find...");
			int c = getToolkit().getMenuShortcutKeyMask();
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, c));
		}

		public void actionPerformed(ActionEvent e) {
			if (replaceDialog.isVisible()) {
				replaceDialog.setVisible(false);
			}
			findDialog.setVisible(true);
		}

	}


	private class ShowReplaceDialogAction extends AbstractAction {
		
		public ShowReplaceDialogAction() {
			super("Replace...");
			int c = getToolkit().getMenuShortcutKeyMask();
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_H, c));
		}

		public void actionPerformed(ActionEvent e) {
			if (findDialog.isVisible()) {
				findDialog.setVisible(false);
			}
			replaceDialog.setVisible(true);
		}

	}


}