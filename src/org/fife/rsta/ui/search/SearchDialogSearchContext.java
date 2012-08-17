/*
 * 08/12/2012
 *
 * SearchDialogSearchContext.java - Search context with extra information
 * pertinent to Find and Replace dialogs.
 * This library is distributed under a modified BSD license.  See the included
 * RSyntaxTextArea.License.txt file for details.
 */
package org.fife.rsta.ui.search;

import org.fife.ui.rtextarea.SearchContext;


/**
 * Search context for find and replace dialogs.  Remembers more information
 * that is important to keep such dialogs in synch with each other.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class SearchDialogSearchContext extends SearchContext {

	private boolean markAll;


	public boolean getMarkAll() {
		return markAll;
	}


	public void setMarkAll(boolean markAll) {
		this.markAll = markAll;
	}


}