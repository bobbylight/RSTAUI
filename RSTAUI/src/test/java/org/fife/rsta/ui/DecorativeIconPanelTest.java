package org.fife.rsta.ui;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link DecorativeIconPanel}.
 */
@ExtendWith(SwingRunnerExtension.class)
class DecorativeIconPanelTest {

	private DecorativeIconPanel panel;

	@Test
	void testConstructor_noArgs() {
		assertDoesNotThrow(() -> new DecorativeIconPanel());
	}

	@Test
	void testConstructor_withIconWidth() {
		assertDoesNotThrow(() -> new DecorativeIconPanel(16));
	}

	@Test
	void testGetSetIcon() {
		panel = new DecorativeIconPanel(16);
		assertNotNull(panel.getIcon());
		Icon icon = new ImageIcon(new byte[0]);
		panel.setIcon(icon);
		assertEquals(icon, panel.getIcon());
	}

	@Test
	void testGetSetShowIcon() {
		panel = new DecorativeIconPanel(16);
		assertFalse(panel.getShowIcon());
		panel.setShowIcon(true);
		assertTrue(panel.getShowIcon());
	}

	@Test
	void testGetToolTipText() {
		panel = new DecorativeIconPanel(16);
		assertNull(panel.getToolTipText());
		String tip = "Test Tip";
		panel.setToolTipText(tip);
		assertEquals(tip, panel.getToolTipText());
	}

	@Test
	void testPaintChildren_notShowingChildren() {
		panel = new DecorativeIconPanel(16);
		panel.setShowIcon(false);
		panel.paintChildren(TestUtil.createTestGraphics());
	}

	@Test
	void testPaintChildren_showingChildren() {
		panel = new DecorativeIconPanel(16);
		panel.setShowIcon(true);
		panel.paintChildren(TestUtil.createTestGraphics());
	}

	@Test
	void testSetIcon_null_usesDefaultIcon() {
		panel = new DecorativeIconPanel(16);
		panel.setIcon(null);
		assertNotNull(panel.getIcon());
	}

	@Test
	void testSetIconWidth_sameWidth() {
		panel = new DecorativeIconPanel(16);
		panel.setIconWidth(16);
		assertEquals(16, panel.getIcon().getIconWidth());
	}

	@Test
	void testSetIconWidth_differentWidth() {
		panel = new DecorativeIconPanel(16);
		panel.setIconWidth(18);
		assertEquals(18, panel.getIcon().getIconWidth());
	}
}
