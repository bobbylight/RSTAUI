package org.fife.rsta.ui.search;

import org.fife.rsta.ui.SwingRunnerExtension;
import org.fife.ui.rtextarea.SearchContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ReplaceDialog}.
 */
@ExtendWith(SwingRunnerExtension.class)
class ReplaceDialogTest {

	private ReplaceDialog dialog;
	private TestSearchListener listener;

	@BeforeEach
	void setUp() {
		listener = new TestSearchListener();
	}

	@Test
	void testConstructor_dialog_nonNull() {
		assertDoesNotThrow(() -> new ReplaceDialog(new JDialog(), listener));
	}

	@Test
	void testConstructor_dialog_nonNull_rtl() {
		Locale orig = Locale.getDefault();
		try {
			Locale.setDefault(Locale.forLanguageTag("ar"));
			assertDoesNotThrow(() -> new ReplaceDialog(new JDialog(), listener));
		} finally {
			Locale.setDefault(orig);
		}
	}

	@Test
	void testConstructor_dialog_null() {
		assertDoesNotThrow(() -> new ReplaceDialog((Dialog)null, listener));
	}

	@Test
	void testConstructor_frame_nonNull() {
		assertDoesNotThrow(() -> new ReplaceDialog(new JFrame(), listener));
	}

	@Test
	void testConstructor_frame_null() {
		assertDoesNotThrow(() -> new ReplaceDialog((Frame)null, listener));
	}

	@Test
	void testActionPerformed_find() {
		dialog = new ReplaceDialog(new JDialog(), listener);
		dialog.setSearchString("searchFor");
		ActionEvent e = new ActionEvent(dialog, 0, SearchEvent.Type.FIND.name());
		dialog.actionPerformed(e);
		assertEquals(1, listener.events.size());
		SearchEvent se = listener.events.get(0);
		assertEquals(SearchEvent.Type.FIND, se.getType());
		SearchContext sc = se.getSearchContext();
		assertEquals("searchFor", sc.getSearchFor());
	}

	@Test
	void testActionPerformed_replace_noReplaceText() {
		dialog = new ReplaceDialog(new JDialog(), listener);
		dialog.setSearchString("searchFor");
		ActionEvent e = new ActionEvent(dialog, 0, SearchEvent.Type.REPLACE.name());
		dialog.actionPerformed(e);
		assertEquals(1, listener.events.size());
		SearchEvent se = listener.events.get(0);
		assertEquals(SearchEvent.Type.REPLACE, se.getType());
		SearchContext sc = se.getSearchContext();
		assertEquals("searchFor", sc.getSearchFor());
		assertEquals("", sc.getReplaceWith());
	}

	@Test
	void testActionPerformed_replace_withReplaceText() {
		dialog = new ReplaceDialog(new JDialog(), listener);
		dialog.setSearchContext(new SearchContext());
		dialog.setSearchString("searchFor");
		dialog.setReplaceString("newText");
		ActionEvent e = new ActionEvent(dialog, 0, SearchEvent.Type.REPLACE.name());
		dialog.actionPerformed(e);
		assertEquals(1, listener.events.size());
		SearchEvent se = listener.events.get(0);
		assertEquals(SearchEvent.Type.REPLACE, se.getType());
		SearchContext sc = se.getSearchContext();
		assertEquals("searchFor", sc.getSearchFor());
		assertEquals("newText", sc.getReplaceWith());
	}

	@Test
	void testActionPerformed_replace_withReplaceText_highlightsNextMatch() {
		dialog = new ReplaceDialog(new JDialog(), listener);
		dialog.setSearchContext(new SearchContext());
		dialog.setSearchString("searchFor");
		dialog.setReplaceString("newText");
		ActionEvent e = new ActionEvent(dialog, 0, SearchEvent.Type.REPLACE.name());
		listener.setSelectedText("searchFor");
		dialog.actionPerformed(e);
	}

	@Test
	void testActionPerformed_replaceAll_noReplaceText() {
		dialog = new ReplaceDialog(new JDialog(), listener);
		dialog.setSearchString("searchFor");
		ActionEvent e = new ActionEvent(dialog, 0, SearchEvent.Type.REPLACE_ALL.name());
		dialog.actionPerformed(e);
		assertEquals(1, listener.events.size());
		SearchEvent se = listener.events.get(0);
		assertEquals(SearchEvent.Type.REPLACE_ALL, se.getType());
		SearchContext sc = se.getSearchContext();
		assertEquals("searchFor", sc.getSearchFor());
		assertEquals("", sc.getReplaceWith());
	}

	@Test
	void testActionPerformed_replace_withReplaceAllText() {
		dialog = new ReplaceDialog(new JDialog(), listener);
		dialog.setSearchString("searchFor");
		dialog.setReplaceString("newText");
		ActionEvent e = new ActionEvent(dialog, 0, SearchEvent.Type.REPLACE_ALL.name());
		dialog.actionPerformed(e);
		assertEquals(1, listener.events.size());
		SearchEvent se = listener.events.get(0);
		assertEquals(SearchEvent.Type.REPLACE_ALL, se.getType());
		SearchContext sc = se.getSearchContext();
		assertEquals("searchFor", sc.getSearchFor());
		assertEquals("newText", sc.getReplaceWith());
	}

	@Test
	void testEscapePressed_hidesDialog() {
		dialog = new ReplaceDialog(new JDialog(), listener);
		dialog.escapePressed();
		assertFalse(dialog.isVisible());
	}

	@Test
	void testGetContentAssistImage() {
		// Note getter and setter are *not* symmetric!
		assertNotNull(ReplaceDialog.getContentAssistImage());
	}

	@Test
	void testGetReplaceString_neverReturnsNull() {
		dialog = new ReplaceDialog(new JDialog(), listener);
		assertNotNull(dialog.getReplaceString());
	}

	@Test
	void testGetSetDownRadioButtonText() {
		dialog = new ReplaceDialog(new JDialog(), listener);
		assertEquals("Down", dialog.getDownRadioButtonText());
		dialog.setDownRadioButtonText("xxx");
		assertEquals("xxx", dialog.getDownRadioButtonText());
	}

	@Test
	void testGetSetFindButtonText() {
		dialog = new ReplaceDialog(new JDialog(), listener);
		assertEquals("Find", dialog.getFindButtonText());
		dialog.setFindButtonText("xxx");
		assertEquals("xxx", dialog.getFindButtonText());
	}

	@Test
	void testGetSetFindWhatLabelText() {
		dialog = new ReplaceDialog(new JDialog(), listener);
		assertEquals("Find what:   ", dialog.getFindWhatLabelText());
		dialog.setFindWhatLabelText("xxx");
		assertEquals("xxx", dialog.getFindWhatLabelText());
	}

	@Test
	void testGetSetReplaceAllButtonText() {
		dialog = new ReplaceDialog(new JDialog(), listener);
		assertEquals("Replace All", dialog.getReplaceAllButtonText());
		dialog.setReplaceAllButtonText("xxx");
		assertEquals("xxx", dialog.getReplaceAllButtonText());
	}

	@Test
	void testGetSetReplaceButtonText() {
		dialog = new ReplaceDialog(new JDialog(), listener);
		assertEquals("Replace", dialog.getReplaceButtonText());
		dialog.setReplaceButtonText("xxx");
		assertEquals("xxx", dialog.getReplaceButtonText());
	}

	@Test
	void testGetSetReplaceString() {
		dialog = new ReplaceDialog(new JDialog(), listener);
		assertEquals("", dialog.getReplaceString());
		dialog.setReplaceString("xxx");
		assertEquals("xxx", dialog.getReplaceString());
	}

	@Test
	void testGetSetSearchButtonsBorderText() {
		dialog = new ReplaceDialog(new JDialog(), listener);
		assertEquals("Direction:", dialog.getSearchButtonsBorderText());
		dialog.setSearchButtonsBorderText("xxx");
		assertEquals("xxx", dialog.getSearchButtonsBorderText());
	}

	@Test
	void testGetSetUpRadioButtonText() {
		dialog = new ReplaceDialog(new JDialog(), listener);
		assertEquals("Up", dialog.getUpRadioButtonText());
		dialog.setUpRadioButtonText("xxx");
		assertEquals("xxx", dialog.getUpRadioButtonText());
	}

	@Test
	void testSetContentAssistImage() {
		// Note getter and setter are *not* symmetric!
		dialog = new ReplaceDialog(new JDialog(), listener);
		dialog.setContentAssistImage(new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB));
	}

	@Test
	void testGetSetReplaceWithLabelText() {
		dialog = new ReplaceDialog(new JDialog(), listener);
		assertEquals("Replace with:", dialog.getReplaceWithLabelText());
		dialog.setReplaceWithLabelText("xxx");
		assertEquals("xxx", dialog.getReplaceWithLabelText());
	}

	@Test
	void testHandleRegExCheckBoxClicked() {
		dialog = new ReplaceDialog(new JDialog(), listener);
		assertDoesNotThrow(() -> dialog.handleRegExCheckBoxClicked());
	}

	@Test
	void testSearchContextUpdated_replaceWith_nonNull() {
		dialog = new ReplaceDialog(new JDialog(), listener);
		assertEquals("", dialog.getReplaceString());
		dialog.getSearchContext().setReplaceWith("newText");
		assertEquals("newText", dialog.getReplaceString());
	}

	@Test
	void testSearchContextUpdated_replaceWith_null() {
		dialog = new ReplaceDialog(new JDialog(), listener);
		assertEquals("", dialog.getReplaceString());
		dialog.getSearchContext().setReplaceWith(null);
		assertEquals("", dialog.getReplaceString());
	}

	@Test
	void testSearchContextUpdated_searchFor_nonNull() {
		dialog = new ReplaceDialog(new JDialog(), listener);
		assertEquals("", dialog.getSearchString());
		dialog.getSearchContext().setSearchFor("newText");
		assertEquals("newText", dialog.getSearchString());
	}

	@Test
	void testSearchContextUpdated_searchFor_null() {
		dialog = new ReplaceDialog(new JDialog(), listener);
		assertEquals("", dialog.getSearchString());
		dialog.getSearchContext().setSearchFor(null);
		assertEquals("", dialog.getSearchString());
	}

	@Test
	void testSetVisible_false() {
		dialog = new ReplaceDialog(new JDialog(), listener);
		dialog.setVisible(false);
		assertFalse(dialog.isVisible());
	}

	@Test
	void testUpdateUI() {
		dialog = new ReplaceDialog(new JDialog(), listener);
		dialog.updateUI();
	}

	private static class TestSearchListener implements SearchListener {

		private List<SearchEvent> events;
		private String selectedText;

		TestSearchListener() {
			events = new ArrayList<>();
			selectedText = "";
		}

		@Override
		public String getSelectedText() {
			return selectedText;
		}

		@Override
		public void searchEvent(SearchEvent e) {
			events.add(e);
		}

		public void setSelectedText(String selectedText) {
			this.selectedText = selectedText;
		}
	}
}
