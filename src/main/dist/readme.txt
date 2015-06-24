RSTAUI Readme
-------------
Please contact me if you are using RSyntaxTextArea in your project!  I like
to know when people are finding it useful.  Please send mail to:
robert -at- fifesoft dot com.


* About RSTAUI

  This is a library for adding the following dialogs to an application using
  RSyntaxTextArea as an editor:
  
     * Find Dialog
     * Replace Dialog
     * Go to Line Dialog
     * Text File Properties Dialog (requires use of TextEditorPane)
  
  Searching support is fully featured - regex searches, match case, whole word,
  searching forward and backward, mark all occurrences.  Note that the actual
  searching functionality is handled in the RSyntaxTextArea project (see the
  SearchEngine class); this library just wraps that functionality in a UI.

* License

  RSTAUI is licensed under a modified BSD license.  Please see the included
  RSTAUI.License.txt file.

* Compiling

  RSTAUI is built using Gradle.  To compile the source, run all tests, and
  build the distribution jar, you must check out RSTAUI and its sister projects,
  RSyntaxTextArea and AutoComplete, side by side.  Then use Gradle to build:
  
     cd RSyntaxTextArea
     gradlew build
     cd ../AutoComplete
     gradlew build
     cd ../RSTAUI
     gradlew build

* Example Usage

  Compile and run the demo class included with the source distribution:
  
     org.fife.rsta.ui.demo.RSTAUIDemoApp
  
  It is a simple example of using the Find, Replace, and Go To Line dialogs.
  It currently does not demo the "Mark All" functionality of searching, or
  the Text File Properties dialog.
      
* Feedback

  I hope you find RSyntaxTextArea useful.  Bug reports, feature requests, and
  just general questions are always welcome.  Ways you can submit feedback:
  
    * http://forum.fifesoft.com (preferred)
         Has a forum for RSyntaxTextArea and related projects, where you can
         ask questions and get feedback quickly.

    * https://github.com/bobbylight/RSTAUI
         Add a bug or enhancement request, peruse the Wiki, etc.

* Thanks
  
  Icons in this package (such as lightbulb.png) come from Eclipse and are
  licensed under the EPL (http://www.eclipse.org/legal/epl-v10.html).
  
  Translations:
     
     * Arabic:                 Mawaheb, Linostar
     * Chinese:                Terrance, peter_barnes, Sunquan, sonyichi, zvest
     * Chinese (Traditional):  kin Por Fok, liou xiao
     * Dutch:                  Roel, Sebastiaan, lovepuppy
     * French:                 PivWan
     * German:                 Domenic, bikerpete
     * Hungarian:              Zityi, flatron
     * Indonesian:             azis, Sonny
     * Italian:                Luca, stepagweb
     * Japanese:               Josh, izumi, tomoM
     * Korean:                 Changkyoon, sbrownii
     * Polish:                 Chris, Maciej Mrug
     * Portuguese (Brazil):    Pat, Marcos Parmeggiani, Leandro
     * Russian:                Nadiya, Vladimir
     * Spanish:                Leonardo, phrodo, daloporhecho
     * Turkish:                Cahit, Burak
