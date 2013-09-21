package org.fife.rsta.ui.demo;

import java.awt.event.*;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.text.BadLocationException;

import org.fife.rsta.ui.CollapsibleSectionPanel;
import org.fife.rsta.ui.GoToDialog;
import org.fife.rsta.ui.search.FindDialog;
import org.fife.rsta.ui.search.ReplaceDialog;
import org.fife.rsta.ui.search.ReplaceToolBar;
import org.fife.rsta.ui.search.SearchEvent;
import org.fife.rsta.ui.search.SearchListener;
import org.fife.rsta.ui.search.FindToolBar;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SearchContext;
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
public class RSTAUIDemoApp extends JFrame implements SearchListener {

	private CollapsibleSectionPanel cp;
	private RSyntaxTextArea textArea;
	private FindDialog findDialog;
	private ReplaceDialog replaceDialog;
	private FindToolBar findToolBar;
	private ReplaceToolBar replaceToolBar;


	public RSTAUIDemoApp() {

		initSearchDialogs();

		cp = new CollapsibleSectionPanel();
		setContentPane(cp);

		setJMenuBar(createMenuBar());
		
		textArea = new RSyntaxTextArea(25, 60);
		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
		RTextScrollPane sp = new RTextScrollPane(textArea);
		cp.add(sp);

		setTitle("RSTAUI Demo Application");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);

	}


	private void addItem(Action a, ButtonGroup bg, JMenu menu) {
		JRadioButtonMenuItem item = new JRadioButtonMenuItem(a);
		bg.add(item);
		menu.add(item);
	}


	private JMenuBar createMenuBar() {

		JMenuBar mb = new JMenuBar();
		JMenu menu = new JMenu("Search");
		menu.add(new JMenuItem(new ShowFindDialogAction()));
		menu.add(new JMenuItem(new ShowReplaceDialogAction()));
		menu.add(new JMenuItem(new GoToLineAction()));
		menu.addSeparator();

		int ctrl = getToolkit().getMenuShortcutKeyMask();
		int shift = InputEvent.SHIFT_MASK;
		KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_F, ctrl|shift);
		Action a = cp.addBottomComponent(ks, findToolBar);
		a.putValue(Action.NAME, "Show Find Search Bar");
		menu.add(new JMenuItem(a));
		ks = KeyStroke.getKeyStroke(KeyEvent.VK_H, ctrl|shift);
		a = cp.addBottomComponent(ks, replaceToolBar);
		a.putValue(Action.NAME, "Toggle Bottom Component 2");
		menu.add(new JMenuItem(a));

		mb.add(menu);

		menu = new JMenu("LookAndFeel");
		ButtonGroup bg = new ButtonGroup();
		LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();
		for (int i=0; i<infos.length; i++) {
			addItem(new LookAndFeelAction(infos[i]), bg, menu);
		}
		mb.add(menu);

		return mb;

	}


	/**
	 * Creates our Find and Replace dialogs.
	 */
	public void initSearchDialogs() {

		findDialog = new FindDialog(this, this);
		replaceDialog = new ReplaceDialog(this, this);

		// This ties the properties of the two dialogs together (match case,
		// regex, etc.).
		SearchContext context = findDialog.getSearchContext();
		replaceDialog.setSearchContext(context);

		// Create tool bars and tie their search contexts together also.
		findToolBar = new FindToolBar(this);
		findToolBar.setSearchContext(context);
		replaceToolBar = new ReplaceToolBar(this);
		replaceToolBar.setSearchContext(context);

	}


	/**
	 * Listens for events from our search dialogs and actually does the dirty
	 * work.
	 */
	public void searchEvent(SearchEvent e) {

		SearchEvent.Type type = e.getType();
		SearchContext context = e.getSearchContext();

		switch (type) {
			case MARK_ALL:
				SearchEngine.markAll(textArea, context);
				break;
			case FIND:
				if (!SearchEngine.find(textArea, context)) {
					UIManager.getLookAndFeel().provideErrorFeedback(textArea);
				}
				break;
			case REPLACE:
				if (!SearchEngine.replace(textArea, context)) {
					UIManager.getLookAndFeel().provideErrorFeedback(textArea);
				}
				break;
			case REPLACE_ALL:
				int count = SearchEngine.replaceAll(textArea, context);
				JOptionPane.showMessageDialog(null, count
						+ " occurrences replaced.");
				break;
		}

	}


	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceGraphiteAquaLookAndFeel");
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


	private class LookAndFeelAction extends AbstractAction {

		private LookAndFeelInfo info;

		public LookAndFeelAction(LookAndFeelInfo info) {
			putValue(NAME, info.getName());
			this.info = info;
		}

		public void actionPerformed(ActionEvent e) {
			try {
				UIManager.setLookAndFeel(info.getClassName());
				SwingUtilities.updateComponentTreeUI(RSTAUIDemoApp.this);
				if (findDialog!=null) {
					SwingUtilities.updateComponentTreeUI(findDialog);
					SwingUtilities.updateComponentTreeUI(replaceDialog);
				}
			} catch (RuntimeException re) {
				throw re; // FindBugs
			} catch (Exception ex) {
				ex.printStackTrace();
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