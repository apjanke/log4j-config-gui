TODO for log4j1-config-gui
=============================

##  ISSUES

* In the Appender editor, changes to the filter list take effect right away (instead of waiting for OK to be pressed), and cause the appender editor GUI to reload from the underlying appender object, causing unsaved property changes to be lost. Same with the appender list in the Logger editor, I think.

##  TODO

* Add Cmd-W for close; other hotkey support
* Friendlier validation and error handling for numeric-valued text fields
* Extensible menu options for appender editors
 * To support those with "activateOptions()" or other methods that aren't property setters
* When resizing the Logger editor window, the Appenders widget should expand to fill horizontally, and the controls panel should stay left-aligned.
* The JTextFields and JLabels in controls for ThingEditor widgets are slightly misaligned (on macOS); the JLabels show up a few pixels left of where the JTextFields and JCheckBoxes start.
* Right-click in the empty space of a Filters or Appenders list should still bring up a context menu.
* Editor text fields for loggers and appenders should expand horizontally with window resize, to accomodate large fields for file names.
* Log4j 2.x support
