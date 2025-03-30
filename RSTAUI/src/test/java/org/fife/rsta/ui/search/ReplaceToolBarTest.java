package org.fife.rsta.ui.search;

import org.fife.rsta.ui.SwingRunnerExtension;
import org.fife.ui.rtextarea.SearchContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ReplaceToolBar}.
 */
@ExtendWith(SwingRunnerExtension.class)
class ReplaceToolBarTest {

	private ReplaceToolBar toolBar;
	private TestSearchListener listener;

	@BeforeEach
	void setUp() {
		listener = new TestSearchListener();
		toolBar = new ReplaceToolBar(listener);
	}

	@Test
	void testConstructor() {
		assertNotNull(toolBar);
	}

	@Test
	void testConstructor_rtl() {
		Locale orig = Locale.getDefault();
		try {
			Locale.setDefault(Locale.forLanguageTag("ar"));
			assertDoesNotThrow(() -> new ReplaceToolBar(listener));
		} finally {
			Locale.setDefault(orig);
		}
	}

	@Test
	void testAddNotify() {
		toolBar.addNotify();
	}

	@Test
	void testAddSearchListener() {
		SearchListener newListener = new TestSearchListener();
		toolBar.addSearchListener(newListener);
	}

	@Test
	void testRemoveSearchListener() {
		toolBar.removeSearchListener(listener);
	}

	@Test
	void testGetSetMarkAllDelay() {
		toolBar.setMarkAllDelay(500);
		assertEquals(500, toolBar.getMarkAllDelay());
	}

	@Test
	void testGetSetSearchContext() {
		SearchContext context = new SearchContext();
		toolBar.setSearchContext(context);
		assertEquals(context, toolBar.getSearchContext());
	}

	@Test
	void testGetSetReplaceText() {
		assertEquals("", toolBar.getReplaceText());
		toolBar.setReplaceText("Replace text");
		assertEquals("Replace text", toolBar.getReplaceText());
	}

	@Test
	void testHandleSearchAction_findNext() {
		ActionEvent event = new ActionEvent(toolBar.findButton, ActionEvent.ACTION_PERFORMED, "FindNext");
		toolBar.handleSearchAction(event);
		assertEquals(SearchEvent.Type.FIND, listener.events.get(0).getType());
	}

	@Test
	void testHandleSearchAction_findPrevious() {
		ActionEvent event = new ActionEvent(toolBar.findButton, ActionEvent.ACTION_PERFORMED, "FindPrevious");
		toolBar.handleSearchAction(event);
		assertEquals(SearchEvent.Type.FIND, listener.events.get(0).getType());
	}

	@Test
	void testHandleSearchAction_replace() {
		ActionEvent event = new ActionEvent(toolBar.findButton, ActionEvent.ACTION_PERFORMED, "Replace");
		toolBar.handleSearchAction(event);
		assertEquals(SearchEvent.Type.REPLACE, listener.events.get(0).getType());
	}

	@Test
	void testHandleSearchAction_replaceAll() {
		ActionEvent event = new ActionEvent(toolBar.findButton, ActionEvent.ACTION_PERFORMED, "ReplaceAll");
		toolBar.handleSearchAction(event);
		assertEquals(SearchEvent.Type.REPLACE_ALL, listener.events.get(0).getType());
	}

	@Test
	void testSetContentAssistImage() {
		Image img = new ImageIcon(new byte[0]).getImage();
		toolBar.setContentAssistImage(img);
		assertNotNull(toolBar.replaceCombo.getContentAssistImage());
	}

	@Test
	void testRequestFocusInWindow() {
		assertDoesNotThrow(() -> toolBar.requestFocusInWindow());
	}

	@Test
	void testHandleRegExCheckBoxClicked() {
		toolBar.regexCheckBox.setSelected(true);
		toolBar.handleRegExCheckBoxClicked();
		assertTrue(toolBar.regexCheckBox.isSelected());
	}

	@Test
	void testHandleSearchAction() {
		ActionEvent event = new ActionEvent(toolBar.findButton, ActionEvent.ACTION_PERFORMED, "FindNext");
		toolBar.handleSearchAction(event);
		assertEquals(SearchEvent.Type.FIND, listener.events.get(0).getType());
	}

	@Test
	void testMatchesSearchFor_emptySearchFor() {
		toolBar.getSearchContext().setSearchFor("");
		assertFalse(toolBar.matchesSearchFor("xxx"));
	}

	@Test
	void testMatchesSearchFor_emptyString() {
		toolBar.getSearchContext().setSearchFor("foo");
		assertFalse(toolBar.matchesSearchFor(""));
	}

	@Test
	void testMatchesSearchFor_null() {
		toolBar.getSearchContext().setSearchFor("foo");
		assertFalse(toolBar.matchesSearchFor(null));
	}

	@Test
	void testMatchesSearchFor_plainText_ignoreCase_match() {
		toolBar.getSearchContext().setSearchFor("foo");
		assertTrue(toolBar.matchesSearchFor("foo"));
	}

	@Test
	void testMatchesSearchFor_plainText_ignoreCase_noMatch() {
		toolBar.getSearchContext().setSearchFor("foo");
		assertFalse(toolBar.matchesSearchFor("fob"));
	}

	@Test
	void testMatchesSearchFor_plainText_matchCase_match() {
		toolBar.getSearchContext().setSearchFor("foo");
		toolBar.getSearchContext().setMatchCase(true);
		assertTrue(toolBar.matchesSearchFor("foo"));
	}

	@Test
	void testMatchesSearchFor_plainText_match_noMatch() {
		toolBar.getSearchContext().setSearchFor("foo");
		toolBar.getSearchContext().setMatchCase(true);
		assertFalse(toolBar.matchesSearchFor("foO"));
	}

	@Test
	void testMatchesSearchFor_regex_match() {
		toolBar.getSearchContext().setSearchFor("fo[ox]");
		toolBar.getSearchContext().setRegularExpression(true);
		assertTrue(toolBar.matchesSearchFor("fox"));
	}

	@Test
	void testMatchesSearchFor_regex_invalidRegex() {
		toolBar.getSearchContext().setSearchFor("fo[ox");
		toolBar.getSearchContext().setRegularExpression(true);
		assertFalse(toolBar.matchesSearchFor("fob"));
	}

	@Test
	void testMatchesSearchFor_regex_noMatch() {
		toolBar.getSearchContext().setSearchFor("fo[ox]");
		toolBar.getSearchContext().setRegularExpression(true);
		assertFalse(toolBar.matchesSearchFor("fob"));
	}

	private static class TestSearchListener implements SearchListener {

		private List<SearchEvent> events = new ArrayList<>();

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
