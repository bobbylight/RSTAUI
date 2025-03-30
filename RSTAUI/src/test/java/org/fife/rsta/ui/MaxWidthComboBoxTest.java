package org.fife.rsta.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link MaxWidthComboBox}.
 */
@ExtendWith(SwingRunnerExtension.class)
class MaxWidthComboBoxTest {

	private MaxWidthComboBox<String> comboBox;

	@Test
	void testConstructor_withMaxWidth() {
		comboBox = new MaxWidthComboBox<>(100);
		assertEquals(100, comboBox.getMaximumSize().width);
	}

	@Test
	void testConstructor_withModelAndMaxWidth() {
		ComboBoxModel<String> model = new DefaultComboBoxModel<>(new String[]{"item1", "item2"});
		MaxWidthComboBox<String> comboBox = new MaxWidthComboBox<>(model, 100);
		assertNotNull(comboBox);
		assertEquals(100, comboBox.getMaximumSize().width);
		assertEquals(2, comboBox.getItemCount());
	}

	@Test
	void testGetMaximumSize() {
		comboBox = new MaxWidthComboBox<>(100);
		Dimension maxSize = comboBox.getMaximumSize();
		assertEquals(100, maxSize.width);
	}

	@Test
	void testGetMinimumSize() {
		comboBox = new MaxWidthComboBox<>(100);
		Dimension minSize = comboBox.getMinimumSize();
		assertNotEquals(0, minSize.width);
	}

	@Test
	void testGetPreferredSize() {
		comboBox = new MaxWidthComboBox<>(100);
		Dimension prefSize = comboBox.getPreferredSize();
		assertNotEquals(00, prefSize.width);
	}
}
