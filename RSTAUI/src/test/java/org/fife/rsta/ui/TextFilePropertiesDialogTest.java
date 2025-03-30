package org.fife.rsta.ui;

import org.fife.ui.rsyntaxtextarea.FileLocation;
import org.fife.ui.rsyntaxtextarea.TextEditorPane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link TextFilePropertiesDialog}.
 */
@ExtendWith(SwingRunnerExtension.class)
class TextFilePropertiesDialogTest {

	private TextFilePropertiesDialog dialog;
	private TextEditorPane textArea;

	private static String createContent(int size) {
		StringBuilder sb = new StringBuilder();
		sb.append("a".repeat(Math.max(0, size)));
		return sb.toString();
	}

	@BeforeEach
	void setUp() {
		textArea = new TextEditorPane();
		dialog = new TextFilePropertiesDialog((Frame) null, textArea);
	}

	@Test
	void testConstructor_dialog_nonNull() {
		JDialog parent = new JDialog();
		assertDoesNotThrow(() -> new TextFilePropertiesDialog(parent, textArea));
	}

	@Test
	void testConstructor_dialog_null() {
		assertDoesNotThrow(() -> new TextFilePropertiesDialog((Dialog) null, textArea));
	}

	@Test
	void testConstructor_frame_nonNull() {
		JFrame parent = new JFrame();
		assertDoesNotThrow(() -> new TextFilePropertiesDialog(parent, textArea));
	}

	@Test
	void testConstructor_frame_null() {
		assertDoesNotThrow(() -> new TextFilePropertiesDialog((Frame) null, textArea));
	}

	@Test
	void testConstructor_readOnlyEditor() {
		textArea.setReadOnly(true);
		assertDoesNotThrow(() -> new TextFilePropertiesDialog((Frame) null, textArea));
	}

	@Test
	void testConstructor_rtl() {
		Locale origDefault = Locale.getDefault();
		try {
			Locale.setDefault(new Locale("ar", "SA"));
			assertDoesNotThrow(() -> new TextFilePropertiesDialog((Frame) null, textArea));
		} finally {
			Locale.setDefault(origDefault);
		}
	}

	@Test
	void testConstructor_contentInTextArea() {
		textArea.setText("one two 333 fff%$$#$\nfive six");
		assertDoesNotThrow(() -> new TextFilePropertiesDialog((Frame) null, textArea));
	}

	@Test
	void testConstructor_lastSaveOrLoadTime() throws IOException{
		Path path = Files.createTempFile("test", ".txt");
		String content = createContent(4096);
		Files.writeString(path, content, StandardCharsets.UTF_8);
		path.toFile().deleteOnExit();
		textArea.load(FileLocation.create(path.toFile()));
		textArea.save();
		assertDoesNotThrow(() -> new TextFilePropertiesDialog((Frame) null, textArea));
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 1024, 1024 * 1024 })
	void testConstructor_filePassedIn_variousSizes(int size) throws IOException {
		Path path = Files.createTempFile("test", ".txt");
		String content = createContent(size);
		Files.writeString(path, content, StandardCharsets.UTF_8);
		path.toFile().deleteOnExit();
		textArea.load(FileLocation.create(path.toFile()));
		assertDoesNotThrow(() -> new TextFilePropertiesDialog((Frame) null, textArea));
	}

	@Test
	void testActionPerformed_cancelButton() {
		dialog.actionPerformed(new ActionEvent("source", ActionEvent.ACTION_PERFORMED, "CancelButton"));
		assertFalse(dialog.isVisible());
	}

	@Test
	void testActionPerformed_encodingComboBox() {
		assertFalse(dialog.isCloseable());
		dialog.actionPerformed(new ActionEvent("source", ActionEvent.ACTION_PERFORMED, "encodingCombo"));
		assertTrue(dialog.isCloseable());
	}

	@Test
	void testActionPerformed_okButton_everythingChanged() {
		assertFalse(dialog.isCloseable());
		dialog.setSelectedLineTerminator("\r");
		dialog.setEncoding(StandardCharsets.UTF_16.name());
		assertTrue(dialog.isCloseable());
		dialog.actionPerformed(new ActionEvent("source", ActionEvent.ACTION_PERFORMED, "OKButton"));
		assertFalse(dialog.isVisible());
	}

	@Test
	void testActionPerformed_okButton_nothingChanged() {
		dialog.actionPerformed(new ActionEvent("source", ActionEvent.ACTION_PERFORMED, "OKButton"));
		assertFalse(dialog.isVisible());
	}

	@Test
	void testActionPerformed_terminatorComboBox() {
		assertFalse(dialog.isCloseable());
		dialog.actionPerformed(new ActionEvent("source", ActionEvent.ACTION_PERFORMED, "TerminatorComboBox"));
		assertTrue(dialog.isCloseable());
	}

	@Test
	void testCreateButtonFooter() {
		JButton okButton = new JButton("OK");
		JButton cancelButton = new JButton("Cancel");
		Container buttonPanel = dialog.createButtonFooter(okButton, cancelButton);
		assertNotNull(buttonPanel);
		assertEquals(2, ((JPanel) buttonPanel.getComponent(0)).getComponentCount());
	}

	@Test
	void testSetVisible_false() {
		dialog.setVisible(false);
		assertFalse(dialog.isVisible());
	}
}
