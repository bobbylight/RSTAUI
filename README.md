# RSTAUI
This is a library for adding the following dialogs to an application using `RSyntaxTextArea` as an
editor:

* Find Dialog
* Replace Dialog
* Find and Replace tool bars (a la Sublime Text)
* Go to Line Dialog
* Text File Properties Dialog (requires use of `TextEditorPane`)

Searching support is fully featured - regex searches, match case, whole word, searching forward and
backward, mark all occurrences.  Note that the actual searching functionality is handled in the
`RSyntaxTextArea` project itself (see its `SearchEngine` class); this library just wraps that
functionality in a UI.

Available in the [Maven Central repository](https://search.maven.org/search?q=rstaui%20jar) (`com.fifesoft:rstaui:XXX`).
SNAPSHOT builds of the in-development, unreleased version are hosted on [Sonatype](https://oss.sonatype.org/content/repositories/snapshots/com/fifesoft/rstaui/).

RSTAUI is available under a [modified BSD license](https://github.com/bobbylight/RSTAUI/blob/master/RSTAUI/src/main/dist/RSTAUI.License.txt).
For more information, visit [http://bobbylight.github.io/RSyntaxTextArea/](http://bobbylight.github.io/RSyntaxTextArea/).

## Compiling

RSTAUI is built using Gradle.  To compile the source, run all tests, and build the distribution jar,
simply change into the project directory and run:

    gradlew build --warning-mode all

## Example Usage

A very simple example can be found in the
[RSTAUIDemoApp class](https://github.com/bobbylight/RSTAUI/blob/master/RSTAUIDemo/src/main/java/org/fife/rsta/ui/demo/RSTAUIDemoApp.java)
included in this project.

## Sister Projects

* [RSyntaxTextArea](https://github.com/bobbylight/RSyntaxTextArea) provides syntax highlighting, code folding, and many other features out-of-the-box.
* [AutoComplete](https://github.com/bobbylight/AutoComplete) - Adds code completion to RSyntaxTextArea (or any other JTextComponent).
* [RSTALanguageSupport](https://github.com/bobbylight/RSTALanguageSupport) - Code completion for RSTA for the following languages: Java, JavaScript, HTML, PHP, JSP, Perl, C, Unix Shell.
  Built on both RSTA and AutoComplete.
* [SpellChecker](https://github.com/bobbylight/SpellChecker) - Adds squiggle-underline spell checking to RSyntaxTextArea.

## Getting Help

* Add an issue on GitHub
* Check the project's [home page](http://bobbylight.github.io/RSyntaxTextArea/)
