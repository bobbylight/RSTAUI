package org.fife.rsta.ui.search;

import org.fife.rsta.ui.SwingRunnerExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link FindDialog}.
 */
@ExtendWith(SwingRunnerExtension.class)
public class FindDialogTest {

	private FindDialog dialog;
	private TestSearchListener listener;

	@BeforeEach
	void setUp() {
		listener = new TestSearchListener();
	}

	@Test
	void testConstructor_dialog_nonNull() {
		assertDoesNotThrow(() -> new FindDialog(new JDialog(), listener));
	}

	@Test
	void testConstructor_dialog_nonNull_rtl() {
		Locale orig = Locale.getDefault();
		try {
			Locale.setDefault(Locale.forLanguageTag("ar"));
			assertDoesNotThrow(() -> new FindDialog(new JDialog(), listener));
		} finally {
			Locale.setDefault(orig);
		}
	}

	@Test
	void testConstructor_dialog_null() {
		assertDoesNotThrow(() -> new FindDialog((Dialog)null, listener));
	}

	@Test
	void testConstructor_frame_nonNull() {
		assertDoesNotThrow(() -> new FindDialog(new JFrame(), listener));
	}

	@Test
	void testConstructor_frame_null() {
		assertDoesNotThrow(() -> new FindDialog((Frame)null, listener));
	}

	@Test
	void testGetSetDownRadioButtonText() {
		dialog = new FindDialog(new JDialog(), listener);
		assertEquals("Down", dialog.getDownRadioButtonText());
		dialog.setDownRadioButtonText("xxx");
		assertEquals("xxx", dialog.getDownRadioButtonText());
	}

	@Test
	void testGetSetFindButtonText() {
		dialog = new FindDialog(new JDialog(), listener);
		assertEquals("Find", dialog.getFindButtonText());
		dialog.setFindButtonText("xxx");
		assertEquals("xxx", dialog.getFindButtonText());
	}

	@Test
	void testGetSetFindWhatLabelText() {
		dialog = new FindDialog(new JDialog(), listener);
		assertEquals("Find what:   ", dialog.getFindWhatLabelText());
		dialog.setFindWhatLabelText("xxx");
		assertEquals("xxx", dialog.getFindWhatLabelText());
	}

	@Test
	void testGetSetSearchButtonsBorderText() {
		dialog = new FindDialog(new JDialog(), listener);
		assertEquals("Direction:", dialog.getSearchButtonsBorderText());
		dialog.setSearchButtonsBorderText("xxx");
		assertEquals("xxx", dialog.getSearchButtonsBorderText());
	}

	@Test
	void testGetSetUpRadioButtonText() {
		dialog = new FindDialog(new JDialog(), listener);
		assertEquals("Up", dialog.getUpRadioButtonText());
		dialog.setUpRadioButtonText("xxx");
		assertEquals("xxx", dialog.getUpRadioButtonText());
	}

	@Test
	void testHandleToggleButtons_plainText_empty() {
		dialog = new FindDialog(new JDialog(), listener);
		dialog.setSearchString("");
		FindReplaceButtonsEnableResult result = dialog.handleToggleButtons();
		assertFalse(result.getEnable());
	}

	@Test
	void testHandleToggleButtons_plainText_nonEmpty() {
		dialog = new FindDialog(new JDialog(), listener);
		dialog.setSearchString("foo");
		FindReplaceButtonsEnableResult result = dialog.handleToggleButtons();
		assertTrue(result.getEnable());
	}

	@Test
	void testHandleToggleButtons_regex_invalid() {
		dialog = new FindDialog(new JDialog(), listener);
		dialog.setSearchString("invalid(regex");
		dialog.getSearchContext().setRegularExpression(true);
		FindReplaceButtonsEnableResult result = dialog.handleToggleButtons();
		assertFalse(result.getEnable());
	}

	@Test
	void testHandleToggleButtons_regex_valid() {
		dialog = new FindDialog(new JDialog(), listener);
		dialog.setSearchString("valid(regex)");
		dialog.getSearchContext().setRegularExpression(true);
		FindReplaceButtonsEnableResult result = dialog.handleToggleButtons();
		assertTrue(result.getEnable());
	}

	@Test
	void testMatchesSearchFor_emptySearchFor() {
		dialog = new FindDialog(new JDialog(), listener);
		dialog.getSearchContext().setSearchFor("");
		assertFalse(dialog.matchesSearchFor("xxx"));
	}

	@Test
	void testMatchesSearchFor_emptyString() {
		dialog = new FindDialog(new JDialog(), listener);
		dialog.getSearchContext().setSearchFor("foo");
		assertFalse(dialog.matchesSearchFor(""));
	}

	@Test
	void testMatchesSearchFor_null() {
		dialog = new FindDialog(new JDialog(), listener);
		dialog.getSearchContext().setSearchFor("foo");
		assertFalse(dialog.matchesSearchFor(null));
	}

	@Test
	void testMatchesSearchFor_plainText_ignoreCase_match() {
		dialog = new FindDialog(new JDialog(), listener);
		dialog.getSearchContext().setSearchFor("foo");
		assertTrue(dialog.matchesSearchFor("foo"));
	}

	@Test
	void testMatchesSearchFor_plainText_ignoreCase_noMatch() {
		dialog = new FindDialog(new JDialog(), listener);
		dialog.getSearchContext().setSearchFor("foo");
		assertFalse(dialog.matchesSearchFor("fob"));
	}

	@Test
	void testMatchesSearchFor_plainText_matchCase_match() {
		dialog = new FindDialog(new JDialog(), listener);
		dialog.getSearchContext().setSearchFor("foo");
		dialog.getSearchContext().setMatchCase(true);
		assertTrue(dialog.matchesSearchFor("foo"));
	}

	@Test
	void testMatchesSearchFor_plainText_match_noMatch() {
		dialog = new FindDialog(new JDialog(), listener);
		dialog.getSearchContext().setSearchFor("foo");
		dialog.getSearchContext().setMatchCase(true);
		assertFalse(dialog.matchesSearchFor("foO"));
	}

	@Test
	void testMatchesSearchFor_regex_match() {
		dialog = new FindDialog(new JDialog(), listener);
		dialog.getSearchContext().setSearchFor("fo[ox]");
		dialog.getSearchContext().setRegularExpression(true);
		assertTrue(dialog.matchesSearchFor("fox"));
	}

	@Test
	void testMatchesSearchFor_regex_invalidRegex() {
		dialog = new FindDialog(new JDialog(), listener);
		dialog.getSearchContext().setSearchFor("fo[ox");
		dialog.getSearchContext().setRegularExpression(true);
		assertFalse(dialog.matchesSearchFor("fob"));
	}

	@Test
	void testMatchesSearchFor_regex_noMatch() {
		dialog = new FindDialog(new JDialog(), listener);
		dialog.getSearchContext().setSearchFor("fo[ox]");
		dialog.getSearchContext().setRegularExpression(true);
		assertFalse(dialog.matchesSearchFor("fob"));
	}

	@Test
	void testSearchContextUpdated_matchCase() {
		dialog = new FindDialog(new JDialog(), listener);
		assertDoesNotThrow(() -> dialog.getSearchContext().setMatchCase(true));
	}

	@Test
	void testSearchContextUpdated_regex() {
		dialog = new FindDialog(new JDialog(), listener);
		assertDoesNotThrow(() -> dialog.getSearchContext().setRegularExpression(true));
	}

	@Test
	void testSearchContextUpdated_searchFor_nonNull() {
		dialog = new FindDialog(new JDialog(), listener);
		assertEquals("", dialog.getSearchString());
		dialog.getSearchContext().setSearchFor("newText");
		assertEquals("newText", dialog.getSearchString());
	}

	@Test
	void testSearchContextUpdated_searchFor_null() {
		dialog = new FindDialog(new JDialog(), listener);
		assertEquals("", dialog.getSearchString());
		dialog.getSearchContext().setSearchFor(null);
		assertEquals("", dialog.getSearchString());
	}

	@Test
	void testSearchContextUpdated_wholeWord() {
		dialog = new FindDialog(new JDialog(), listener);
		assertDoesNotThrow(() -> dialog.getSearchContext().setWholeWord(true));
	}

	@Test
	void testSearchContextUpdated_wrapSearch() {
		dialog = new FindDialog(new JDialog(), listener);
		assertDoesNotThrow(() -> dialog.getSearchContext().setSearchWrap(true));
	}

	@Test
	void testSetVisible_false() {
		dialog = new FindDialog(new JDialog(), listener);
		dialog.setVisible(false);
		assertFalse(dialog.isVisible());
	}

	@Test
	@Disabled("Modal must be visible for this methot to be called")
	void testUpdateUI() {
		dialog = new FindDialog(new JDialog(), listener);
		dialog.updateUI();
	}

	private static class TestSearchListener implements SearchListener {

		private List<SearchEvent> events;

		TestSearchListener() {
			events = new ArrayList<>();
		}

		@Override
		public void searchEvent(SearchEvent e) {
			events.add(e);
		}

		@Override
		public String getSelectedText() {
			return "";
		}
	}
}
