package org.fife.rsta.ui;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link GoToDialog}.
 */
@ExtendWith(SwingRunnerExtension.class)
public class GoToDialogTest {

	@Test
	void testConstructor_dialog_nonNull() {
		JDialog parent = new JDialog();
		Assertions.assertDoesNotThrow(() -> new GoToDialog(parent));
	}

	@Test
	void testConstructor_dialog_null() {
		Assertions.assertDoesNotThrow(() -> new GoToDialog((Dialog) null));
	}

	@Test
	void testConstructor_frame_nonNull() {
		JFrame parent = new JFrame();
		Assertions.assertDoesNotThrow(() -> new GoToDialog(parent));
	}

	@Test
	void testConstructor_frame_null() {
		Assertions.assertDoesNotThrow(() -> new GoToDialog((Frame) null));
	}

	@Test
	void testActionPerformed_cancelButton() {
		GoToDialog dialog = new GoToDialog((Frame) null);
		dialog.clickCancelButton();
		Assertions.assertFalse(dialog.isVisible());
	}

	@Test
	void testActionPerformed_okButton() {
		GoToDialog dialog = new GoToDialog((Frame) null);
		dialog.setLineNumber(1);
		dialog.clickOkButton();
		Assertions.assertEquals(1, dialog.getLineNumber());
	}

	@Test
	void testDisplayInvalidLineNumberMessage() {
		try (MockedStatic<JOptionPane> utils = Mockito.mockStatic(JOptionPane.class)) {
			GoToDialog dialog = new GoToDialog((Frame) null);
			dialog.displayInvalidLineNumberMessage();
			utils.verify(() -> JOptionPane.showMessageDialog(Mockito.any(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyInt()), Mockito.times(1));
		}
	}

	@Test
	void testEscapePressed_hidesDialog() {
		GoToDialog dialog = new GoToDialog((Frame) null);
		dialog.escapePressed();
		Assertions.assertEquals(-1, dialog.getLineNumber());
	}

	@Test
	void testGetSetMaxLineNumberAllowed() {
		GoToDialog dialog = new GoToDialog((Frame) null);
		dialog.setMaxLineNumberAllowed(100);
		Assertions.assertEquals(100, dialog.getMaxLineNumberAllowed());
	}

	@Test
	void testGetSetErrorDialogTitle() {
		GoToDialog dialog = new GoToDialog((Frame) null);
		dialog.setErrorDialogTitle("title");
		Assertions.assertEquals("title", dialog.getErrorDialogTitle());
	}

	@Test
	void testSetVisible_false() {
		GoToDialog dialog = new GoToDialog((Frame) null);
		dialog.setVisible(false);
		assertFalse(dialog.isVisible());
	}
}
