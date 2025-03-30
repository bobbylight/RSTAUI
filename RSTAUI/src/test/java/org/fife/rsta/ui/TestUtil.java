package org.fife.rsta.ui;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Utility methods for testing.
 */
public final class TestUtil {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private TestUtil() {
	}

	public static Graphics2D createTestGraphics() {
		return createTestGraphics(80, 80);
	}


	public static Graphics2D createTestGraphics(int width, int height) {
		Graphics2D g = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB).
			createGraphics();
		g.setClip(0, 0, width, height);
		return g;
	}
}
