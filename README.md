This is a library for adding the following dialogs to an application using RSyntaxTextArea as an editor:

* Find Dialog
* Replace Dialog
* Find and Replace tool bars (a la Sublime Text)
* Go to Line Dialog
* Text File Properties Dialog (requires use of TextEditorPane)

Searching support is fully featured - regex searches, match case, whole word, searching forward and backward, mark all occurrences.  Note that the actual searching functionality is handled in the RSyntaxTextArea project itself (see the SearchEngine class); this library just wraps that functionality in a UI.

RSTAUI is available under a [modified BSD license](https://github.com/bobbylight/RSTAUI/blob/master/src/main/dist/RSTAUI.License.txt).  For more information, visit [http://bobbylight.github.io/RSyntaxTextArea/](http://bobbylight.github.io/RSyntaxTextArea/).

# Example Usage

A very simple example can be found in the [RSTAUIDemoApp class](https://github.com/bobbylight/RSTAUI/blob/master/src/main/java/org/fife/rsta/ui/demo/RSTAUIDemoApp.java) included in this project.

# Sister Projects

* [RSyntaxTextArea](https://github.com/bobbylight/RSyntaxTextArea) provides syntax highlighting, code folding, and many other features out-of-the-box.
* [AutoComplete](https://github.com/bobbylight/AutoComplete) - Adds code completion to RSyntaxTextArea (or any other JTextComponent).
* [RSTALanguageSupport](https://github.com/bobbylight/RSTALanguageSupport) - Code completion for RSTA for the following languages: Java, JavaScript, HTML, PHP, JSP, Perl, C, Unix Shell.  Built on both RSTA and AutoComplete.
* [SpellChecker](https://github.com/bobbylight/SpellChecker) - Adds squiggle-underline spell checking to RSyntaxTextArea.

# Getting Help

* Add an issue on GitHub
* Ask in the [project forum](http://fifesoft.com/forum/)
* Check the project's [home page](http://bobbylight.github.io/RSyntaxTextArea/)

