package org.fife.rsta.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link SizeGripIcon}.
 */
class SizeGripIconTest {

	private SizeGripIcon icon;

	@BeforeEach
	void setUp() {
		icon = new SizeGripIcon();
	}

	@Test
	void testGetIconHeight() {
		assertEquals(16, icon.getIconHeight());
	}

	@Test
	void testGetIconWidth() {
		assertEquals(16, icon.getIconWidth());
	}

	@Test
	void testPaintIcon_leftToRight() {
		JPanel panel = new JPanel();
		panel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		panel.setSize(20, 20);
		Graphics g = TestUtil.createTestGraphics();
		assertDoesNotThrow(() -> icon.paintIcon(panel, g, 0, 0));
	}

	@Test
	void testPaintIcon_rightToLeft() {
		JPanel panel = new JPanel();
		panel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		panel.setSize(20, 20);
		Graphics g = TestUtil.createTestGraphics();
		assertDoesNotThrow(() -> icon.paintIcon(panel, g, 0, 0));
	}
}
