/*
 * 09/08/2005
 *
 * UIUtil.java - Utility methods for org.fife.ui classes.
 * This library is distributed under a modified BSD license.  See the included
 * RSyntaxTextArea.License.txt file for details.
 */
package org.fife.rsta.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.UIResource;


/**
 * Utility methods for <code>org.fife.ui</code> GUI components.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class UIUtil {

	private static boolean desktopCreationAttempted;
	private static Object desktop;
	private static final Object LOCK_DESKTOP_CREATION = new Object();


	/*
	 * -1 => Not yet determined, 0 => no, 1 => yes.
	 */
	private static int nonOpaqueTabbedPaneComponents = -1;

	/**
	 * A very common border that can be shared across many components.
	 */
	private static final Border EMPTY_5_BORDER		=
							BorderFactory.createEmptyBorder(5,5,5,5);


	/**
	 * Private constructor so we cannot instantiate this class.
	 */
	private UIUtil() {
	}


	/**
	 * Attempts to open a web browser to the specified URI.
	 *
	 * @param uri The URI to open.  If this is <code>null</code>, nothing
	          happens and this method returns <code>false</code>.
	 * @return Whether the operation was successful.  This will be
	 *         <code>false</code> on JRE's older than 1.6.
	 * @see #browse(URI)
	 */
	public static boolean browse(String uri) {
		if (uri==null) {
			return false;
		}
		try {
			return browse(new URI(uri));
		} catch (URISyntaxException e) {
			return false;
		}
	}


	/**
	 * Attempts to open a web browser to the specified URI.
	 *
	 * @param uri The URI to open.  If this is <code>null</code>, nothing
	          happens and this method returns <code>false</code>.
	 * @return Whether the operation was successful.  This will be
	 *         <code>false</code> on JRE's older than 1.6.
	 * @see #browse(String)
	 */
	public static boolean browse(URI uri) {

		boolean success = false;

		if (uri!=null) {
			Object desktop = getDesktop();
			if (desktop!=null) {
				try {
					Method m = desktop.getClass().getDeclaredMethod(
								"browse", new Class[] { URI.class });
					m.invoke(desktop, new Object[] { uri });
					success = true;
				} catch (RuntimeException re) {
					throw re; // Keep FindBugs happy
				} catch (Exception e) {
					// Ignore, just return "false" below.
				}
			}
		}

		return success;

	}


	/**
	 * Returns a button with the specified text and mnemonic.
	 *
	 * @param bundle The resource bundle in which to get the int.
	 * @param textKey The key into the bundle containing the string text value.
	 * @param mnemonicKey The key into the bundle containing a single-char
	 *        <code>String</code> value for the mnemonic.
	 * @return The button.
	 */
	public static final JButton createButton(ResourceBundle bundle,
								String textKey, String mnemonicKey) {
		JButton b = new JButton(bundle.getString(textKey));
		b.setMnemonic((int)bundle.getString(mnemonicKey).charAt(0));
		return b;
	}


	/**
	 * Returns an <code>JLabel</code> with the specified text and mnemonic.
	 *
	 * @param msg The resource bundle in which to get the int.
	 * @param textKey The key into the bundle containing the string text value.
	 * @param mnemonicKey The key into the bundle containing a single-char
	 *        <code>String</code> value for the mnemonic.
	 * @return The <code>JLabel</code>.
	 */
	public static final JLabel createLabel(ResourceBundle msg,
								String textKey, String mnemonicKey) {
		JLabel label = new JLabel(msg.getString(textKey));
		Object mnemonic = msg.getObject(mnemonicKey);
		if (mnemonic instanceof String)
			label.setDisplayedMnemonic((int)((String)mnemonic).charAt(0));
		return label;
	}


	/**
	 * Returns an <code>JRadioButton</code> with the specified text and
	 * mnemonic.
	 *
	 * @param bundle The resource bundle in which to get the int.
	 * @param textKey The key into the bundle containing the string text value.
	 * @param mnemonicKey The key into the bundle containing a single-char
	 *        <code>String</code> value for the mnemonic.
	 * @return The <code>JRadioButton</code>.
	 */
	public static final JRadioButton createRadioButton(ResourceBundle bundle,
								String textKey, String mnemonicKey) {
		JRadioButton radio = new JRadioButton(bundle.getString(textKey));
		radio.setMnemonic((int)bundle.getString(mnemonicKey).charAt(0));
		return radio;
	}


	/**
	 * Returns a button to add to a panel in a tabbed pane.  This method
	 * checks system properties to determine the operating system this JVM is
	 * running in; if it is determined that this OS paints its tabbed panes
	 * in a special way (such as the gradient tabbed panes in Windows XP),
	 * then the button returned is not opaque.  Otherwise, a regular (opaque)
	 * button is returned.
	 *
	 * @return A button to add to a <code>JTabbedPane</code>.
	 * @see #createTabbedPanePanel
	 */
	public static JButton createTabbedPaneButton(String text) {
		JButton button = new JButton(text);
		if (getUseNonOpaqueTabbedPaneComponents())
			button.setOpaque(false);
		return button;
	}


	/**
	 * Returns an opaque panel so we get the cool gradient effect on Windows
	 * XP and Vista.
	 *
	 * @return A panel to add to a <code>JTabbedPane</code>.
	 * @see #createTabbedPaneButton(String)
	 */
	public static JPanel createTabbedPanePanel() {
		JPanel panel = new JPanel();
		if (getUseNonOpaqueTabbedPaneComponents())
			panel.setOpaque(false);
		return panel;
	}


	/**
	 * Returns an opaque panel so we get the cool gradient effect on Windows
	 * XP and Vista.
	 *
	 * @param layout The layout for the panel.
	 * @return A panel to add to a <code>JTabbedPane</code>.
	 * @see #createTabbedPaneButton(String)
	 */
	public static JPanel createTabbedPanePanel(LayoutManager layout) {
		JPanel panel = new JPanel(layout);
		if (getUseNonOpaqueTabbedPaneComponents())
			panel.setOpaque(false);
		return panel;
	}


	/**
	 * Derives a color from another color by linearly shifting its blue, green,
	 * and blue values.
	 *
	 * @param orig The original color.
	 * @param darker The amount by which to decrease its r, g, and b values.
	 *        Note that you can use negative values for making a color
	 *        component "brighter."  If this makes any of the three values
	 *        less than zero, zero is used for that component value; similarly,
	 *        if it makes any value greater than 255, 255 is used for that
	 *        component's value.
	 */
	public static final Color deriveColor(Color orig, int darker) {

		int red = orig.getRed()-darker;
		int green = orig.getGreen()-darker;
		int blue = orig.getBlue()-darker;

		if (red<0) red=0; else if (red>255) red=255;
		if (green<0) green=0; else if (green>255) green=255;
		if (blue<0) blue=0; else if (blue>255) blue=255;

		return new Color(red, green, blue);

	}


	/**
	 * Expands all nodes in the specified tree.
	 *
	 * @param tree The tree.
	 */
	public static void expandAllNodes(final JTree tree) {
		// Do separately for nested panels.
		int j=0;
		while (j<tree.getRowCount()) {
			tree.expandRow(j++);
		}
	}


	/**
	 * Fixes the orientation of the renderer of a combo box.  I can't believe
	 * Swing standard LaFs don't handle this on their own.
	 *
	 * @param combo The combo box.
	 */
	public static void fixComboOrientation(JComboBox combo) {
		ListCellRenderer r = combo.getRenderer();
		if (r instanceof Component) {
			ComponentOrientation o = ComponentOrientation.
							getOrientation(Locale.getDefault());
			((Component)r).setComponentOrientation(o);
		}
	}


	/**
	 * Used by makeSpringCompactGrid.  This is ripped off directly from
	 * <code>SpringUtilities.java</code> in the Sun Java Tutorial.
	 *
	 * @param parent The container whose layout must be an instance of
	 *        <code>SpringLayout</code>.
	 * @return The spring constraints for the specified component contained
	 *         in <code>parent</code>.
	 */
	private static final SpringLayout.Constraints getConstraintsForCell(
										int row, int col,
										Container parent, int cols) {
		SpringLayout layout = (SpringLayout) parent.getLayout();
		Component c = parent.getComponent(row * cols + col);
		return layout.getConstraints(c);
	}


	/**
	 * Returns the singleton <code>java.awt.Desktop</code> instance, or
	 * <code>null</code> if it is unsupported on this platform (or the JRE
	 * is older than 1.6).
	 *
	 * @return The desktop, as an {@link Object}.
	 */
	private static Object getDesktop() {

		synchronized (LOCK_DESKTOP_CREATION) {

			if (!desktopCreationAttempted) {

				desktopCreationAttempted = true;

				try {
					Class desktopClazz = Class.forName("java.awt.Desktop");
					Method m = desktopClazz.
						getDeclaredMethod("isDesktopSupported", null);

					boolean supported = ((Boolean)m.invoke(null, null)).
												booleanValue();
					if (supported) {
						m = desktopClazz.getDeclaredMethod("getDesktop", null);
						desktop = m.invoke(null, null);
					}

				} catch (RuntimeException re) {
					throw re; // Keep FindBugs happy
				} catch (Exception e) {
					// Ignore; keeps desktop as null.
				}

			}

		}

		return desktop;

	}


	/**
	 * Returns an empty border of width 5 on all sides.  Since this is a
	 * very common border in GUI's, the border returned is a singleton.
	 *
	 * @return The border.
	 */
	public static Border getEmpty5Border() {
		return EMPTY_5_BORDER;
	}


	/**
	 * Returns a <code>String</code> of the form "#xxxxxx" good for use
	 * in HTML, representing the given color.
	 *
	 * @param color The color to get a string for.
	 * @return The HTML form of the color.  If <code>color</code> is
	 *         <code>null</code>, <code>#000000</code> is returned.
	 */
	public static final String getHTMLFormatForColor(Color color) {
		if (color==null) {
			return "#000000";
		}
		String hexRed = Integer.toHexString(color.getRed());
		if (hexRed.length()==1)
			hexRed = "0" + hexRed;
		String hexGreen = Integer.toHexString(color.getGreen());
		if (hexGreen.length()==1)
			hexGreen = "0" + hexGreen;
		String hexBlue = Integer.toHexString(color.getBlue());
		if (hexBlue.length()==1)
			hexBlue = "0" + hexBlue;
		return "#" + hexRed + hexGreen + hexBlue;
	}


	/**
	 * Returns whether or not this operating system should use non-opaque
	 * components in tabbed panes to show off, for example, a gradient effect.
	 *
	 * @return Whether or not non-opaque components should be used in tabbed
	 *         panes.
	 */
	static synchronized boolean getUseNonOpaqueTabbedPaneComponents() {

		if (nonOpaqueTabbedPaneComponents==-1) {

			// Check for Windows XP.
			String osname = System.getProperty("os.name");
			if (osname.toLowerCase().indexOf("windows")>-1) {
				String osver = System.getProperty("os.version");
				boolean isXPorVista = osver.startsWith("5.1") ||
								osver.startsWith("6.0");
				nonOpaqueTabbedPaneComponents = isXPorVista ? 1 : 0;
			}
			else {
				nonOpaqueTabbedPaneComponents = 0;
			}

		}

		return nonOpaqueTabbedPaneComponents==1 ? true : false;

	}


	/**
	 * Tweaks certain LookAndFeels (i.e., Windows XP) to look just a tad more
	 * like the native Look.
	 */
	public static void installOsSpecificLafTweaks() {

		String lafName = UIManager.getLookAndFeel().getName();
		String os = System.getProperty("os.name");

		// XP has insets between the edge of popup menus and the selection.
		if ("Windows XP".equals(os) && "Windows".equals(lafName)) {

			Border insetsBorder = BorderFactory.createEmptyBorder(2, 3, 2, 3);

			String key = "PopupMenu.border";
			Border origBorder = UIManager.getBorder(key);
			UIResource res = new BorderUIResource.CompoundBorderUIResource(
										origBorder, insetsBorder);
			//UIManager.put(key, res);
			UIManager.getLookAndFeelDefaults().put(key, res);

		}

	}


	/**
	 * This method is ripped off from <code>SpringUtilities.java</code> found
	 * on Sun's Java Tutorial pages.  It takes a component whose layout is
	 * <code>SpringLayout</code> and organizes the components it contains into
	 * a nice grid.
	 * Aligns the first <code>rows</code> * <code>cols</code> components of
	 * <code>parent</code> in a grid. Each component in a column is as wide as
	 * the maximum preferred width of the components in that column; height is
	 * similarly determined for each row.  The parent is made just big enough
	 * to fit them all.
	 *
	 * @param parent The container whose layout is <code>SpringLayout</code>.
	 * @param rows The number of rows of components to make in the container.
	 * @param cols The umber of columns of components to make.
	 * @param initialX The x-location to start the grid at.
	 * @param initialY The y-location to start the grid at.
	 * @param xPad The x-padding between cells.
	 * @param yPad The y-padding between cells.
	 */
	public static final void makeSpringCompactGrid(Container parent, int rows,
								int cols, int initialX, int initialY,
								int xPad, int yPad) {

		SpringLayout layout;
		try {
			layout = (SpringLayout)parent.getLayout();
		} catch (ClassCastException cce) {
			System.err.println("The first argument to makeCompactGrid " +
							"must use SpringLayout.");
			return;
		}

		//Align all cells in each column and make them the same width.
		Spring x = Spring.constant(initialX);
		for (int c = 0; c < cols; c++) {
			Spring width = Spring.constant(0);
			for (int r = 0; r < rows; r++) {
				width = Spring.max(width,
						getConstraintsForCell(
									r, c, parent, cols).getWidth());
			}
			for (int r = 0; r < rows; r++) {
				SpringLayout.Constraints constraints =
							getConstraintsForCell(r, c, parent, cols);
				constraints.setX(x);
				constraints.setWidth(width);
			}
			x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)));
		}

		//Align all cells in each row and make them the same height.
		Spring y = Spring.constant(initialY);
		for (int r = 0; r < rows; r++) {
			Spring height = Spring.constant(0);
			for (int c = 0; c < cols; c++) {
				height = Spring.max(height,
					getConstraintsForCell(r, c, parent, cols).getHeight());
			}
			for (int c = 0; c < cols; c++) {
				SpringLayout.Constraints constraints =
							getConstraintsForCell(r, c, parent, cols);
				constraints.setY(y);
				constraints.setHeight(height);
			}
			y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));
		}

		//Set the parent's size.
		SpringLayout.Constraints pCons = layout.getConstraints(parent);
		pCons.setConstraint(SpringLayout.SOUTH, y);
		pCons.setConstraint(SpringLayout.EAST, x);

	}


	/**
	 * Sets the accessible description on the specified component.
	 *
	 * @param comp The component on which to set the accessible description.
	 * @param msg A resource bundle from which to get the description.
	 * @param key The key for the description in the resource bundle.
	 */
	public static void setDescription(JComponent comp, ResourceBundle msg,
								String key) {
		comp.getAccessibleContext().setAccessibleDescription(
											msg.getString(key));
	}


	/**
	 * Sets the rendering hints on a graphics object to those closest to the
	 * system's desktop values.<p>
	 * 
	 * See <a href="http://download.oracle.com/javase/6/docs/api/java/awt/doc-files/DesktopProperties.html">AWT
	 * Desktop Properties</a> for more information.
	 *
	 * @param g2d The graphics context.
	 * @return The old rendering hints.
	 */
	public static Map setNativeRenderingHints(Graphics2D g2d) {

		Map old = g2d.getRenderingHints();

		// Try to use the rendering hint set that is "native".
		Map hints = (Map)Toolkit.getDefaultToolkit().
						getDesktopProperty("awt.font.desktophints");
		if (hints!=null) {
			g2d.addRenderingHints(hints);
		}

		return old;

	}


}