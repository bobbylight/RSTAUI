/*
 * Licensed under a modified BSD license.
 * See the included license file for details.
 */
package org.fife.rsta.ui;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.List;


/**
 * Unit tests for the {@code UIUtil} class.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class UIUtilTest {

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

	/**
	 * A dummy subclass used for unit testing purposes.
	 */
	private static class SubclassedJLabel extends JLabel {
	}
}
