/*
 * 09/20/2013
 *
 * FindReplaceButtonsEnableResult - Whether "find" and "replace" buttons
 * should be enabled.
 * 
 * This library is distributed under a modified BSD license.  See the included
 * RSyntaxTextArea.License.txt file for details.
 */
package org.fife.rsta.ui.search;

/**
 * Returns the result of whether the "action" buttons such as "Find"
 * and "Replace" should be enabled.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class FindReplaceButtonsEnableResult {

	private boolean enable;
	private String tooltip;

	public FindReplaceButtonsEnableResult(boolean enable, String tooltip) {
		this.enable = enable;
		this.tooltip = tooltip;
	}

	public boolean getEnable() {
		return enable;
	}

	public String getToolTip() {
		return tooltip;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

}