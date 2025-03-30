package org.fife.rsta.ui.search;

import org.fife.ui.rtextarea.SearchContext;
import org.fife.rsta.ui.search.SearchEvent.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link SearchEvent}.
 */
public class SearchEventTest {

	private SearchContext context;
	private SearchEvent event;

	@BeforeEach
	void setUp() {
		context = new SearchContext();
	}

	@Test
	void testConstructor() {
		event = new SearchEvent(this, Type.FIND, context);
		assertNotNull(event);
	}

	@Test
	void testGetSearchContext() {
		event = new SearchEvent(this, Type.FIND, context);
		assertEquals(context, event.getSearchContext());
	}

	@Test
	void testGetType() {
		event = new SearchEvent(this, Type.FIND, context);
		assertEquals(Type.FIND, event.getType());
	}

	@Test
	void testTypeValues() {
		Type[] types = Type.values();
		assertEquals(4, types.length);
		assertEquals(Type.MARK_ALL, types[0]);
		assertEquals(Type.FIND, types[1]);
		assertEquals(Type.REPLACE, types[2]);
		assertEquals(Type.REPLACE_ALL, types[3]);
	}
}
