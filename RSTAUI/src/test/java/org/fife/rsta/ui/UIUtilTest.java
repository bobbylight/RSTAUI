/*
 * Licensed under a modified BSD license.
 * See the included license file for details.
 */
package org.fife.rsta.ui;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.mockito.Mockito.doReturn;


/**
 * Unit tests for the {@code UIUtil} class.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class UIUtilTest {

	private static final String VALID_URL_STRING = "https://google.com";
	private static final URI VALID_URI = URI.create(VALID_URL_STRING);

	@Test
	void testBrowse_stringArg_null() {
		Assertions.assertFalse(UIUtil.browse((String)null));
	}

	@Test
	void testBrowse_stringArg_invalidUrl_desktopSupported() {
		try (MockedStatic<Desktop> utils = Mockito.mockStatic(Desktop.class)) {
			utils.when(Desktop::isDesktopSupported).thenReturn(true);
			Desktop mockDesktop = Mockito.mock(Desktop.class);
			utils.when(Desktop::getDesktop).thenReturn(mockDesktop);
			doReturn(true).when(mockDesktop).isSupported(Desktop.Action.BROWSE);
			Assertions.assertFalse(UIUtil.browse("xxx\\yyy"));
		}
	}

	@Test
	void testBrowse_stringArg_validUrl_desktopSupported_browseActionSupported() {
		try (MockedStatic<Desktop> utils = Mockito.mockStatic(Desktop.class)) {
			utils.when(Desktop::isDesktopSupported).thenReturn(true);
			Desktop mockDesktop = Mockito.mock(Desktop.class);
			utils.when(Desktop::getDesktop).thenReturn(mockDesktop);
			doReturn(true).when(mockDesktop).isSupported(Desktop.Action.BROWSE);
			Assertions.assertTrue(UIUtil.browse(VALID_URL_STRING));
		}
	}

	@Test
	void testBrowse_stringArg_validUrl_desktopSupported_browseActionUnsupported() {
		try (MockedStatic<Desktop> utils = Mockito.mockStatic(Desktop.class)) {
			utils.when(Desktop::isDesktopSupported).thenReturn(true);
			Desktop mockDesktop = Mockito.mock(Desktop.class);
			utils.when(Desktop::getDesktop).thenReturn(mockDesktop);
			doReturn(false).when(mockDesktop).isSupported(Desktop.Action.BROWSE);
			Assertions.assertFalse(UIUtil.browse(VALID_URL_STRING));
		}
	}

	@Test
	void testBrowse_stringArg_validUrl_desktopUnsupported() {
		try (MockedStatic<Desktop> utils = Mockito.mockStatic(Desktop.class)) {
			utils.when(Desktop::isDesktopSupported).thenReturn(false);
			Assertions.assertFalse(UIUtil.browse(VALID_URL_STRING));
		}
	}

	@Test
	void testBrowse_uriArg_null() {
		Assertions.assertFalse(UIUtil.browse((URI)null));
	}

	@Test
	void testBrowse_uriArg_validUrl_desktopSupported_browseActionSupported() {
		try (MockedStatic<Desktop> utils = Mockito.mockStatic(Desktop.class)) {
			utils.when(Desktop::isDesktopSupported).thenReturn(true);
			Desktop mockDesktop = Mockito.mock(Desktop.class);
			utils.when(Desktop::getDesktop).thenReturn(mockDesktop);
			doReturn(true).when(mockDesktop).isSupported(Desktop.Action.BROWSE);
			Assertions.assertTrue(UIUtil.browse(VALID_URI));
		}
	}

	@Test
	void testBrowse_uriArg_validUrl_desktopSupported_browseActionUnsupported() {
		try (MockedStatic<Desktop> utils = Mockito.mockStatic(Desktop.class)) {
			utils.when(Desktop::isDesktopSupported).thenReturn(true);
			Desktop mockDesktop = Mockito.mock(Desktop.class);
			utils.when(Desktop::getDesktop).thenReturn(mockDesktop);
			doReturn(false).when(mockDesktop).isSupported(Desktop.Action.BROWSE);
			Assertions.assertFalse(UIUtil.browse(VALID_URI));
		}
	}

	@Test
	void testBrowse_uriArg_validUrl_desktopUnsupported() {
		try (MockedStatic<Desktop> utils = Mockito.mockStatic(Desktop.class)) {
			utils.when(Desktop::isDesktopSupported).thenReturn(false);
			Assertions.assertFalse(UIUtil.browse(VALID_URI));
		}
	}

	@Test
	void testFixComboOrientation_ltr() {
		Locale defaultLocale = Locale.getDefault();
		try {
			Locale.setDefault(Locale.ENGLISH);

			JComboBox<String> combo = new JComboBox<>();
			((Component)combo.getRenderer()).setComponentOrientation(
				ComponentOrientation.RIGHT_TO_LEFT);
			UIUtil.fixComboOrientation(combo);
			Assertions.assertEquals(ComponentOrientation.LEFT_TO_RIGHT,
				((Component)combo.getRenderer()).getComponentOrientation());
		} finally {
			Locale.setDefault(defaultLocale);
		}
	}

	@Test
	void testFixComboOrientation_rtl() {
		Locale defaultLocale = Locale.getDefault();
		try {
			Locale.setDefault(Locale.forLanguageTag("ar"));

			JComboBox<String> combo = new JComboBox<>();
			((Component)combo.getRenderer()).setComponentOrientation(
				ComponentOrientation.LEFT_TO_RIGHT);
			UIUtil.fixComboOrientation(combo);
			Assertions.assertEquals(ComponentOrientation.RIGHT_TO_LEFT,
				((Component)combo.getRenderer()).getComponentOrientation());
		} finally {
			Locale.setDefault(defaultLocale);
		}
	}

	@Test
	void testFixComboOrientation_renderNotComponent() {
		Locale defaultLocale = Locale.getDefault();
		try {
			Locale.setDefault(Locale.forLanguageTag("ar"));
			JComboBox<String> combo = new JComboBox<>();
			combo.setRenderer((list, value, index, selected, hasFocus) -> new JLabel());
			Assertions.assertDoesNotThrow(() -> UIUtil.fixComboOrientation(combo));
		} finally {
			Locale.setDefault(defaultLocale);
		}
	}

	@Test
	void testGetDescendantsOfType_topLevelMatch() {
		JLabel label = new JLabel();
		List<JLabel> result = UIUtil.getDescendantsOfType(label, JLabel.class);
		Assertions.assertEquals(1, result.size());
		Assertions.assertEquals(label, result.get(0));
	}

	@Test
	void testGetDescendantsOfType_nestedMatch() {
		JPanel panel = new JPanel();
		JLabel label = new JLabel();
		panel.add(label);
		List<JLabel> result = UIUtil.getDescendantsOfType(panel, JLabel.class);
		Assertions.assertEquals(1, result.size());
		Assertions.assertEquals(label, result.get(0));
	}

	@Test
	void testGetDescendantsOfType_nestedTwoDeepMatch() {
		JPanel panel = new JPanel();
		JPanel nestedPanel = new JPanel();
		panel.add(nestedPanel);
		JLabel label = new JLabel();
		nestedPanel.add(label);
		List<JLabel> result = UIUtil.getDescendantsOfType(panel, JLabel.class);
		Assertions.assertEquals(1, result.size());
		Assertions.assertEquals(label, result.get(0));
	}

	@Test
	void testGetDescendantsOfType_subclass() {
		JPanel panel = new JPanel();
		SubclassedJLabel label = new SubclassedJLabel();
		panel.add(label);
		List<JLabel> result = UIUtil.getDescendantsOfType(panel, JLabel.class);
		Assertions.assertEquals(1, result.size());
		Assertions.assertEquals(label, result.get(0));
	}

	@Test
	void testGetDescendantsOfType_multiple() {
		JPanel panel = new JPanel();
		JPanel nestedPanel = new JPanel();
		panel.add(nestedPanel);
		JLabel label = new JLabel();
		nestedPanel.add(label);
		SubclassedJLabel label2 = new SubclassedJLabel();
		panel.add(label2);
		List<JLabel> result = UIUtil.getDescendantsOfType(panel, JLabel.class);
		Assertions.assertEquals(2, result.size());
		Assertions.assertEquals(label2, result.get(0));
		Assertions.assertEquals(label, result.get(1));
	}

	@Test
	void testGetEmpty5Border() {
		Border border = UIUtil.getEmpty5Border();
		Assertions.assertInstanceOf(EmptyBorder.class, border);
	}

	@Test
	void testGetErrorTextForeground_lightText() {
		Color orig = UIManager.getColor("TextField.foreground");
		try {
			UIManager.put("TextField.foreground", Color.WHITE);
			Assertions.assertEquals(new Color(255, 160, 160), UIUtil.getErrorTextForeground());
		} finally {
			UIManager.put("TextField.foreground", orig);
		}
	}

	@Test
	void testGetErrorTextForeground_darkText() {
		Color orig = UIManager.getColor("TextField.foreground");
		try {
			UIManager.put("TextField.foreground", Color.BLACK);
			Assertions.assertEquals(Color.RED, UIUtil.getErrorTextForeground());
		} finally {
			UIManager.put("TextField.foreground", orig);
		}
	}

	@Test
	void testGetErrorTextForeground_textWithOneDarkerRGBValue() {
		Color orig = UIManager.getColor("TextField.foreground");
		try {
			UIManager.put("TextField.foreground", new Color(255, 255, 159));
			Assertions.assertEquals(Color.RED, UIUtil.getErrorTextForeground());

			UIManager.put("TextField.foreground", new Color(255, 159, 255));
			Assertions.assertEquals(Color.RED, UIUtil.getErrorTextForeground());

			UIManager.put("TextField.foreground", new Color(159, 255, 255));
			Assertions.assertEquals(Color.RED, UIUtil.getErrorTextForeground());
		} finally {
			UIManager.put("TextField.foreground", orig);
		}
	}

	@Test
	void testGetMnemonic_keyFound_error_nonStringValue() {
		ResourceBundle bundle = Mockito.mock(ResourceBundle.class);
		doReturn(true).when(bundle).containsKey(Mockito.anyString());
		doReturn(new Object()).when(bundle).getObject("Some.Mnemonic");
		Assertions.assertEquals(0, UIUtil.getMnemonic(bundle, "Some.Mnemonic"));
	}

	@Test
	void testGetMnemonic_keyFound_happyPath() {
		ResourceBundle bundle = ResourceBundle.getBundle("org.fife.rsta.ui.GoToDialog");
		Assertions.assertEquals('L', UIUtil.getMnemonic(bundle, "LineNumber.Mnemonic"));
	}

	@Test
	void testGetMnemonic_keyNotFound() {
		ResourceBundle bundle = ResourceBundle.getBundle("org.fife.rsta.ui.GoToDialog");
		Assertions.assertEquals(0, UIUtil.getMnemonic(bundle, "bogus"));
	}

	@Test
	void testGetTextComponent() {
		JComboBox<String> combo = new JComboBox<>();
		Assertions.assertNotNull(UIUtil.getTextComponent(combo));
	}

	@Test
	void testMakeSpringCompactGrid_error_notSpringLayout() {
		JPanel panel = new JPanel();
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			UIUtil.makeSpringCompactGrid(panel, 2, 2, 0, 0, 0, 0);
		});
	}

	@Test
	void testMakeSpringCompactGrid_happyPath_components() {
		JPanel panel = new JPanel(new SpringLayout());
		for (int i = 0; i < 6; i++) {
			panel.add(new JLabel());
		}
		Assertions.assertDoesNotThrow(() -> {
			UIUtil.makeSpringCompactGrid(panel, 2, 2, 0, 0, 0, 0);
		});
	}

	@Test
	void testNewButton() {
		ResourceBundle msg = Mockito.mock(ResourceBundle.class);
		doReturn(true).when(msg).containsKey(Mockito.anyString());
		Assertions.assertInstanceOf(JButton.class, UIUtil.newButton(msg, "Some.Text"));
	}

	@Test
	void testNewLabel_labelFor() {
		ResourceBundle msg = Mockito.mock(ResourceBundle.class);
		doReturn(true).when(msg).containsKey(Mockito.anyString());
		JTextField labelFor = new JTextField();
		Assertions.assertInstanceOf(JLabel.class, UIUtil.newLabel(msg, "Some.Text", labelFor));
	}

	@Test
	void testNewLabel_noLabelFor() {
		ResourceBundle msg = Mockito.mock(ResourceBundle.class);
		doReturn(true).when(msg).containsKey(Mockito.anyString());
		Assertions.assertInstanceOf(JLabel.class, UIUtil.newLabel(msg, "Some.Text", null));
	}

	/**
	 * A dummy subclass used for unit testing purposes.
	 */
	private static class SubclassedJLabel extends JLabel {
	}
}
