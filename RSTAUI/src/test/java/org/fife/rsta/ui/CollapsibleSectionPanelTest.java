package org.fife.rsta.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.swing.*;
import java.awt.event.KeyEvent;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link CollapsibleSectionPanel}.
 */
@ExtendWith(SwingRunnerExtension.class)
class CollapsibleSectionPanelTest {

	private CollapsibleSectionPanel panel;

	@BeforeEach
	void setUp() {
		panel = new CollapsibleSectionPanel();
	}

	@Test
	void testConstructor_noArgs() {
		assertDoesNotThrow(() -> new CollapsibleSectionPanel());
	}

	@Test
	void testConstructor_withAnimate() {
		assertDoesNotThrow(() -> new CollapsibleSectionPanel(true));
		assertDoesNotThrow(() -> new CollapsibleSectionPanel(false));
	}

	@Test
	void testAddBottomComponent() {
		JComponent comp = new JPanel();
		panel.addBottomComponent(comp);
		assertNull(panel.getDisplayedBottomComponent());
		panel.setAnimate(false);
		panel.showBottomComponent(comp);
		assertEquals(comp, panel.getDisplayedBottomComponent());
	}

	@Test
	void testAddBottomComponent_withKeyStroke() {
		JComponent comp = new JPanel();
		KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0);
		Action action = panel.addBottomComponent(ks, comp);
		assertNotNull(action);
		assertNull(panel.getDisplayedBottomComponent());
		panel.setAnimate(false);
		panel.showBottomComponent(comp);
		assertNotNull(panel.getDisplayedBottomComponent());
	}

	@Test
	void testGetSetAnimationTime() {
		panel.setAnimationTime(200);
		assertEquals(200 / 10, panel.getTotalTicks());
	}

	@Test
	void testGetDisplayedBottomComponent_animateFalse() {
		assertNull(panel.getDisplayedBottomComponent());
		JComponent comp = new JPanel();
		panel.addBottomComponent(comp);
		panel.setAnimate(false);
		panel.showBottomComponent(comp);
		assertEquals(comp, panel.getDisplayedBottomComponent());
	}

	@Test
	void testHideBottomComponent_animateFalse() {
		JComponent comp = new JPanel();
		panel.addBottomComponent(comp);
		panel.setAnimate(false);
		panel.showBottomComponent(comp);
		panel.hideBottomComponent();
		assertNull(panel.getDisplayedBottomComponent());
	}

	@Test
	void testShowBottomComponent_animateFalse() {
		JComponent comp = new JPanel();
		panel.addBottomComponent(comp);
		panel.setAnimate(false);
		panel.showBottomComponent(comp);
		assertEquals(comp, panel.getDisplayedBottomComponent());
	}

	@Test
	void testShowBottomComponent_animateFalse_alreadyShown() {
		JComponent comp = new JPanel();
		panel.addBottomComponent(comp);
		panel.setAnimate(false);
		panel.showBottomComponent(comp);
		panel.showBottomComponent(comp);
	}

	@Test
	void testShowBottomComponent_animateFalse_differentComponent() {
		JComponent comp = new JPanel();
		JPanel comp2 = new JPanel();
		panel.addBottomComponent(comp);
		panel.addBottomComponent(comp2);
		panel.setAnimate(false);
		panel.showBottomComponent(comp);
		panel.showBottomComponent(comp2);
	}

	@Test
	void testUpdateUI_withBottomComponentInfos() {
		JComponent comp = new JPanel();
		panel.addBottomComponent(comp);
		panel.updateUI();
	}
}
