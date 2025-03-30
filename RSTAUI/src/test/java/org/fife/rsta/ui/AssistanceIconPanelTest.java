package org.fife.rsta.ui;

import org.fife.ui.autocomplete.EmptyIcon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link AssistanceIconPanel}.
 */
@ExtendWith(SwingRunnerExtension.class)
class AssistanceIconPanelTest {

	private AssistanceIconPanel panel;
	private JComponent component;

	@BeforeEach
	void setUp() {
		component = new JTextField();
		panel = new AssistanceIconPanel(component);
	}

	@Test
	void testConstructor_noArgs() {
		assertDoesNotThrow(() -> new AssistanceIconPanel(null));
	}

	@Test
	void testConstructor_withIconWidth() {
		assertDoesNotThrow(() -> new AssistanceIconPanel(component, 16));
	}

	@Test
	void testGetSetAssistanceEnabled() {
		Image img = new ImageIcon(new byte[0]).getImage();
		panel.setAssistanceEnabled(img);
		assertNotNull(panel.getIcon());
		assertEquals(AssistanceIconPanel.getAssistanceAvailableText(), panel.getToolTipText());

		panel.setAssistanceEnabled(null);
		assertInstanceOf(EmptyIcon.class, panel.getIcon());
		assertNull(panel.getToolTipText());
	}

	@Test
	void testPropertyChange() {
		// Set the icon to a non-null value
		Image img = new ImageIcon(new byte[0]).getImage();
		PropertyChangeEvent event = new PropertyChangeEvent(component, ContentAssistable.ASSISTANCE_IMAGE, null, img);
		panel.propertyChange(event);
		assertNotNull(panel.getIcon());

		// Back to bull results in an EmptyIcon
		event = new PropertyChangeEvent(component, ContentAssistable.ASSISTANCE_IMAGE, img, null);
		panel.propertyChange(event);
		assertInstanceOf(EmptyIcon.class, panel.getIcon());
	}

	@Test
	void testUpdateUI_notComboBox() {
		assertDoesNotThrow(panel::updateUI);
	}

	@Test
	void testUpdateUI_comboBox() {
		JComboBox<String> comboBox = new JComboBox<>();
		AssistanceIconPanel panel = new AssistanceIconPanel(comboBox);
		assertDoesNotThrow(panel::updateUI);
	}
}
