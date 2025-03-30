package org.fife.rsta.ui;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ResizableFrameContentPane}.
 */
@ExtendWith(SwingRunnerExtension.class)
class ResizableFrameContentPaneTest {

	@Test
	void testConstructor_noArgs() {
		assertDoesNotThrow(() -> new ResizableFrameContentPane());
	}

	@Test
	void testConstructor_withLayout() {
		LayoutManager layout = new BorderLayout();
		ResizableFrameContentPane paneWithLayout = new ResizableFrameContentPane(layout);
		assertNotNull(paneWithLayout);
		assertEquals(layout, paneWithLayout.getLayout());
	}

	@Test
	void testPaint() {
		ResizableFrameContentPane pane = new ResizableFrameContentPane();
		Graphics g = TestUtil.createTestGraphics();
		assertDoesNotThrow(() -> pane.paint(g));
	}
}
