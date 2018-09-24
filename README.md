# Trailing space trimmer

This JAVA application allows you to trim trailing spaces and empty tabs over a whole project.
It's able to get rid of all trailing spaces for all code files in a directory and its subdirectories, so you could work on a whole repository at once.

All programming languages are supported, you can input a comma-separated list of code-file extensions into the app's GUI, so that all matching files are included in the operation.

Definition of GUI option 1 ("Remove trailing spaces/tabs"): 
- remove whitespaces at the end of a line consisting of empty character spaces (no loss of indent or style)
- actual tabs/spaces content in empty lines (it won't delete the empty lines itself, only bring it back to 0 bytes)

[Example] See below image for the effects of this option:

[img] https://i.imgur.com/7jLMMlk.png

Definition of GUI option 2 ("Remove excessive empty lines"):
- Particular locations with more than 3 empty lines, will be brought back to 2 lines. Such lines are often separator spaces between code/function blocks, and grow inconsistent over the lifespan of a project.

[Example] See below image for the effects of this option:

[img] https://i.imgur.com/cu46f7E.png


The stable, multi-threaded approach and progress bar, should allow you to work on an high amount of files simultaneously and large repositories.

This is freeware and free to use for private and commercial purposes, change the license of, (re)distribute, compile and modify on private and corporate level. There are no restrictions.

GUI interface:

https://i.imgur.com/QL6asxH.png

Processing the 1100 files in above example GUI image, took just under 3 seconds of execution time.



It should speak for itself, but if you have any specific demands, you can simply edit the source and recompile the app.
Also feel free to submit pull requests!