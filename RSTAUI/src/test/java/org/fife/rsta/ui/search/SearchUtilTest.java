package org.fife.rsta.ui.search;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link SearchUtil}.
 */
public class SearchUtilTest {

	@Test
	void testGetToolTip_noError() {
		FindReplaceButtonsEnableResult result = new FindReplaceButtonsEnableResult(true, null);
		assertNull(SearchUtil.getToolTip(result));
	}

	@Test
	void testGetToolTip_withError() {
		FindReplaceButtonsEnableResult result = new FindReplaceButtonsEnableResult(true, "Error");
		assertEquals("Error", SearchUtil.getToolTip(result));
	}

	@Test
	void testGetToolTip_withErrorContainingNewline() {
		FindReplaceButtonsEnableResult result = new FindReplaceButtonsEnableResult(true, "Foo\nbar");
		assertEquals("<html><b>Foo</b><br><pre>bar", SearchUtil.getToolTip(result));
	}
}
