log4j1-config-guide Coding Style Guide
========================================

#  Guidelines

* Standard Java naming conventions
* IntelliJ inspection-clean
 * Except for "duplicated code" inspection
* Autoformat all code using IntelliJ default formatting
* Use early returns instead of deeply-nested if/else
* Prefer short statements and lots of named local variables
* Use Yoda conditions
* Yep, trailing commas in array initializers
* Spaces between operators and operands
* Be boring
* Capitalize the "W" in "Hello, World!"

##  IntelliJ inspection-clean

Code should be 100% free of IntelliJ inspection warnings when used with IntelliJ's default inspection settings. This means that any warning suppressions need to be done in source code or the project definition. Prefer changing code idioms to suppressing inspection warnings.

Fixing an inspection justifies a commit.

I'm currently using IntelliJ 2017.3.

Don't expend much effort avoiding the "duplicated code" notification. Just consider that advisory.

##  Be boring

Avoid Reflection unless you really need it.

Just slog through and do things the normal Swing way.