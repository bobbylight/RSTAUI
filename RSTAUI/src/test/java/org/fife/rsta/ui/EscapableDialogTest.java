package org.fife.rsta.ui;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link EscapableDialog}.
 */
@ExtendWith(SwingRunnerExtension.class)
public class EscapableDialogTest {

	@Test
	void testConstructor_zeroArg() {
		assertDoesNotThrow(() -> new EscapableDialog() {});
	}

	@Test
	void testConstructor_dialog_nonNull() {
		Dialog parent = new JDialog();
		assertDoesNotThrow(() -> new EscapableDialog(parent) {});
	}

	@Test
	void testConstructor_dialog_null() {
		assertDoesNotThrow(() -> new EscapableDialog((Dialog) null) {});
	}

	@Test
	void testConstructor_frame_nonNull() {
		Frame parent = new JFrame();
		assertDoesNotThrow(() -> new EscapableDialog(parent) {});
	}

	@Test
	void testConstructor_frame_null() {
		assertDoesNotThrow(() -> new EscapableDialog((Frame) null) {});
	}

	@Test
	void testConstructor_2arg_modal_dialog() {
		Dialog parent = new JDialog();
		EscapableDialog dialog = new EscapableDialog(parent, true) {};
		assertTrue(dialog.isModal());
	}

	@Test
	void testConstructor_2arg_modal_frame() {
		Frame parent = new JFrame();
		EscapableDialog dialog = new EscapableDialog(parent, true) {};
		assertTrue(dialog.isModal());
	}

	@Test
	void testConstructor_2arg_frame_dialog() {
		Frame parent = new JFrame();
		EscapableDialog dialog = new EscapableDialog(parent, "title") {};
		assertFalse(dialog.isModal());
		assertEquals("title", dialog.getTitle());
	}

	@Test
	void testConstructor_2arg_title_dialog() {
		Dialog parent = new JDialog();
		EscapableDialog dialog = new EscapableDialog(parent, "title") {};
		assertFalse(dialog.isModal());
		assertEquals("title", dialog.getTitle());
	}

	@Test
	void testConstructor_3arg_modal_dialog() {
		Dialog parent = new JDialog();
		EscapableDialog dialog = new EscapableDialog(parent, "title", true) {};
		assertTrue(dialog.isModal());
		assertEquals("title", dialog.getTitle());
	}

	@Test
	void testConstructor_3arg_modal_frame() {
		Frame parent = new JFrame();
		EscapableDialog dialog = new EscapableDialog(parent, "title", true) {};
		assertTrue(dialog.isModal());
		assertEquals("title", dialog.getTitle());
	}

	@Test
	void testEscapePressed_hidesDialog() {
		EscapableDialog dialog = new EscapableDialog((Frame) null) {};
		dialog.escapePressed();
		assertFalse(dialog.isVisible());
	}

	@Test
	void testSetEscapeClosesDialog_true() {
		EscapableDialog dialog = new EscapableDialog((Frame) null) {};
		dialog.setEscapeClosesDialog(true);
		JRootPane rootPane = dialog.getRootPane();
		InputMap im = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap am = rootPane.getActionMap();
		KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		assertNotNull(im.get(ks));
		assertNotNull(am.get("OnEsc"));
	}

	@Test
	void testSetEscapeClosesDialog_false() {
		EscapableDialog dialog = new EscapableDialog((Frame) null) {};
		dialog.setEscapeClosesDialog(false);
		JRootPane rootPane = dialog.getRootPane();
		InputMap im = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap am = rootPane.getActionMap();
		KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		assertNull(im.get(ks));
		assertNull(am.get("OnEsc"));
	}
}
