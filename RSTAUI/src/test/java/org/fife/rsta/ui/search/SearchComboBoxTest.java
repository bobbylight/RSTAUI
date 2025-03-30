package org.fife.rsta.ui.search;

import org.fife.rsta.ui.SwingRunnerExtension;
import org.fife.rsta.ui.UIUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

/**
 * Unit tests for the {@code SearchComboBox} class.
 */
@ExtendWith(SwingRunnerExtension.class)
class SearchComboBoxTest {

	@Test
	void testAddItem() {
		SearchComboBox comboBox = new SearchComboBox(null, false);
		Assertions.assertEquals(0, comboBox.getItemCount());
		comboBox.addItem("foo");
		Assertions.assertEquals(1, comboBox.getItemCount());
	}

	@Test
	void testAddItem_existingItem_moveToTopOfList() {
		SearchComboBox comboBox = new SearchComboBox(null, false);
		comboBox.addItem("111");
		comboBox.addItem("222");
		comboBox.addItem("333");
		comboBox.addItem("222");
		List<String> items = comboBox.getSearchStrings();
		Assertions.assertEquals(3, items.size());
		Assertions.assertEquals("222", comboBox.getSelectedItem());
		Assertions.assertEquals("222", items.get(0));
	}

	@Test
	void testAddItem_existingItem_firstItem_orderNotChanged() {
		SearchComboBox comboBox = new SearchComboBox(null, false);
		comboBox.addItem("111");
		comboBox.addItem("222");
		comboBox.addItem("333");
		comboBox.addItem("111");
		List<String> items = comboBox.getSearchStrings();
		Assertions.assertEquals(3, items.size());
		Assertions.assertEquals("111", comboBox.getSelectedItem());
		Assertions.assertEquals("111", items.get(0));
	}

	@Test
	void testGetSearchStrings_noSelectedIndex_addsTextInTextComponent() {
		SearchComboBox comboBox = new SearchComboBox(null, false);
		UIUtil.getTextComponent(comboBox).setText("foo");
		List<String> items = comboBox.getSearchStrings();
		Assertions.assertEquals(1, items.size());
		Assertions.assertEquals("foo", items.get(0));
	}

	@Test
	void testGetSearchStrings_selectedIndexInMiddle_movedtoTop() {
		SearchComboBox comboBox = new SearchComboBox(null, false);
		comboBox.addItem("111");
		comboBox.addItem("222");
		comboBox.addItem("333");
		comboBox.setSelectedIndex(1);
		List<String> items = comboBox.getSearchStrings();
		Assertions.assertEquals(3, items.size());
		Assertions.assertEquals("222", items.get(0));
		Assertions.assertEquals("333", items.get(1));
		Assertions.assertEquals("111", items.get(2));
	}
}
