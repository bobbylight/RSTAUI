package org.fife.rsta.ui;

import org.junit.jupiter.api.Test;

import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link RComboBoxModel}.
 */
public class RComboBoxModelTest {

	@Test
	public void testConstructor_noArgs() {
		RComboBoxModel<String> model = new RComboBoxModel<>();
		assertEquals(8, model.getMaxNumElements());
	}

	@Test
	public void testConstructor_withArray() {
		String[] items = {"item1", "item2"};
		RComboBoxModel<String> model = new RComboBoxModel<>(items);
		assertEquals(2, model.getSize());
		assertEquals("item1", model.getElementAt(0));
		assertEquals("item2", model.getElementAt(1));
	}

	@Test
	public void testConstructor_withArray_overDefaultSize() {
		String[] items = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};
		RComboBoxModel<String> model = new RComboBoxModel<>(items);
		assertEquals(8, model.getSize());
		for (int i = 0; i < 8; i++) {
			assertEquals(Integer.toString(i + 1), model.getElementAt(i));
		}
	}

	@Test
	public void testConstructor_withVector() {
		Vector<String> items = new Vector<>();
		items.add("item1");
		items.add("item2");
		RComboBoxModel<String> modelWithVector = new RComboBoxModel<>(items);
		assertEquals(2, modelWithVector.getSize());
		assertEquals("item1", modelWithVector.getElementAt(0));
		assertEquals("item2", modelWithVector.getElementAt(1));
	}

	@Test
	public void testConstructor_withVector_overDefaultSize() {
		Vector<String> items = new Vector<>();
		for (int i = 1; i <= 9; i++) {
			items.add(Integer.toString(i));
		}
		RComboBoxModel<String> model = new RComboBoxModel<>(items);
		assertEquals(8, model.getSize());
		for (int i = 0; i < 8; i++) {
			assertEquals(Integer.toString(i + 1), model.getElementAt(i));
		}
	}

	@Test
	public void testAddElement() {
		RComboBoxModel<String> model = new RComboBoxModel<>();
		model.addElement("item1");
		assertEquals(1, model.getSize());
		assertEquals("item1", model.getElementAt(0));
	}

	@Test
	public void testAddElement_noDuplicates() {
		RComboBoxModel<String> model = new RComboBoxModel<>();
		model.addElement("item1");
		model.addElement("item1");
		assertEquals(1, model.getSize());
	}

	@Test
	public void testAddElement_movesToTop() {
		RComboBoxModel<String> model = new RComboBoxModel<>();
		model.addElement("item1");
		model.addElement("item2");
		model.addElement("item3");
		assertEquals(3, model.getSize());
		assertEquals("item3", model.getElementAt(0));
		assertEquals("item2", model.getElementAt(1));
		assertEquals("item1", model.getElementAt(2));
	}

	@Test
	public void testGetMaxNumElements() {
		RComboBoxModel<String> model = new RComboBoxModel<>();
		assertEquals(8, model.getMaxNumElements());
		model.setMaxNumElements(5);
		assertEquals(5, model.getMaxNumElements());
	}

	@Test
	public void testInsertElementAt_newElement_newPosition() {
		RComboBoxModel<String> model = new RComboBoxModel<>();
		model.addElement("item1");
		model.insertElementAt("item2", 0);
		assertEquals(2, model.getSize());
		assertEquals("item2", model.getElementAt(0));
		assertEquals("item1", model.getElementAt(1));
	}

	@Test
	public void testInsertElementAt_insertingSameElementIntoSamePosition() {
		RComboBoxModel<String> model = new RComboBoxModel<>(new String[] { "item1", "item2" });
		model.insertElementAt("item1", 0);
		assertEquals(2, model.getSize());
		assertEquals("item1", model.getElementAt(0));
		assertEquals("item2", model.getElementAt(1));
	}

	@Test
	public void testInsertElementAt_insertingExistingElementIntoNewPosition() {
		RComboBoxModel<String> model = new RComboBoxModel<>(new String[] { "item1", "item2" });
		model.insertElementAt("item2", 0);
		assertEquals(2, model.getSize());
		assertEquals("item2", model.getElementAt(0));
		assertEquals("item1", model.getElementAt(1));
	}

	@Test
	public void testSetMaxNumElements() {
		RComboBoxModel<String> model = new RComboBoxModel<>();
		model.setMaxNumElements(1);
		model.addElement("item1");
		model.addElement("item2");
		assertEquals(1, model.getSize());
		assertEquals("item2", model.getElementAt(0));
	}

	@Test
	public void testSetMaxNumElements_negativeValue() {
		RComboBoxModel<String> model = new RComboBoxModel<>();
		model.setMaxNumElements(-1);
		assertEquals(4, model.getMaxNumElements());
	}
}
