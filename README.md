# Trailing space trimmer

This JAVA application allows you to trim trailing spaces and empty tabs over a whole project.
It's able to get rid of all trailing spaces for all code files in a directory and its subdirectories, so you could work on a whole repository at once.

All programming languages are supported, you can input a comma-separated list of code-file extensions into the app's GUI, so that all matching files are included in the operation.

Definition of GUI option 1 ("Remove trailing spaces/tabs"): 
- empty whitespaces at the end of a line consisting of characters (only the very end of it, no loss of indent)
- actual tabs/spaces content in empty lines (it won't delete the empty lines itself, only bring it back to 0 bytes)

[Example] See below image for the effects of this option:

[img] https://i.imgur.com/i35spWx.png

Definition of GUI option 2 ("Remove empty lines"):
- delete empty lines by completely removing them. This will most often be separator lines between blocks of code, added by the developer's preference over the lifespan of a project.

[Example] See below image for the effects of this option:

[img] https://i.imgur.com/KGGnhz8.png


The stable, multi-threaded approach and progress bar, should allow you to work on an high amount of files simultaneously and large repositories.

This is freeware and free to use, distribute, compilate and modify on private and corporate level. No further updates will be commited by myself, although you're free to submit Pull requests.

GUI interface:

https://i.imgur.com/N5dEWo3.png

Processing the 1100 files in above example GUI image, costed just under 3 seconds.