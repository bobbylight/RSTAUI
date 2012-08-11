package org.fife.ui.rsyntaxtextarea.search;

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