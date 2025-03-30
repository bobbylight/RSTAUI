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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link FindToolBar}.
 */
@ExtendWith(SwingRunnerExtension.class)
class FindToolBarTest {

	private FindToolBar toolBar;
	private TestSearchListener listener;

	@BeforeEach
	void setUp() {
		listener = new TestSearchListener();
		toolBar = new FindToolBar(listener);
	}

	@Test
	void testConstructor() {
		assertNotNull(toolBar);
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
	void testDoMarkAll_delay() {
		toolBar.doMarkAll(true);
		assertEquals(0, listener.events.size());;
	}

	@Test
	void testDoMarkAll_noDelay() {
		toolBar.doMarkAll(false);
		assertEquals(1, listener.events.size());;
	}

	@Test
	void testDoSearch_backward() {
		toolBar.doSearch(false);
	}

	@Test
	void testDoSearch_forward() {
		toolBar.doSearch(true);
	}

	@Test
	void testGetSetReplaceText() {
		assertNull(toolBar.getReplaceText());
		toolBar.setReplaceText("Replace text");
		// Ignored since this is a FindToolBar (no replace combo)
		assertNull(toolBar.getReplaceText());
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
	void testHandleToggleButtons_plainText_empty() {
		toolBar.getSearchContext().setSearchFor("");
		FindReplaceButtonsEnableResult result = toolBar.handleToggleButtons();
		assertFalse(result.getEnable());
	}

	@Test
	void testHandleToggleButtons_plainText_nonEmpty() {
		toolBar.getSearchContext().setSearchFor("foo");
		FindReplaceButtonsEnableResult result = toolBar.handleToggleButtons();
		assertTrue(result.getEnable());
	}

	@Test
	void testHandleToggleButtons_regex_invalid() {
		toolBar.getSearchContext().setSearchFor("invalid(regex");
		toolBar.getSearchContext().setRegularExpression(true);
		FindReplaceButtonsEnableResult result = toolBar.handleToggleButtons();
		assertFalse(result.getEnable());
	}

	@Test
	void testHandleToggleButtons_regex_valid() {
		toolBar.getSearchContext().setSearchFor("valid(regex)");
		toolBar.getSearchContext().setRegularExpression(true);
		FindReplaceButtonsEnableResult result = toolBar.handleToggleButtons();
		assertTrue(result.getEnable());
	}

	@Test
	void testSetContentAssistImage() {
		Image img = new ImageIcon(new byte[0]).getImage();
		toolBar.setContentAssistImage(img);
		assertNotNull(toolBar.findCombo.getContentAssistImage());
	}

	@Test
	void testHandleRegExCheckBoxClicked() {
		toolBar.handleRegExCheckBoxClicked();
	}

	@Test
	void testRequestFocusInWindow() {
		assertDoesNotThrow(() -> toolBar.requestFocusInWindow());
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
	void testSearchContextUpdated_markAll() {
		assertDoesNotThrow(() -> toolBar.getSearchContext().setMarkAll(false));
	}

	@Test
	void testSearchContextUpdated_matchCase() {
		assertDoesNotThrow(() -> toolBar.getSearchContext().setMatchCase(true));
	}

	@Test
	void testSearchContextUpdated_regex() {
		assertDoesNotThrow(() -> toolBar.getSearchContext().setRegularExpression(true));
	}

	@Test
	void testSearchContextUpdated_replaceWith() {
		assertDoesNotThrow(() -> toolBar.getSearchContext().setReplaceWith("newText"));
	}

	@Test
	void testSearchContextUpdated_wholeWord() {
		assertDoesNotThrow(() -> toolBar.getSearchContext().setWholeWord(true));
	}

	@Test
	void testSearchContextUpdated_wrapSearch() {
		assertDoesNotThrow(() -> toolBar.getSearchContext().setSearchWrap(true));
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
