# Code Review
### Reviewed: Ruben Gerritse
### Reviewed by: Rasyan Ahmed

 Names: names of variables, methods and classes are mostly consistant and describe their function. level: 4

 Headers: Only one of the 6 classes have header comments. level: 1

 Comments: most methods have a single line of comment above them and the more complex methods have additional comments. level: 3

 layouts: the layouts are consistant and follow convention. level: 4

 format : the code is well formatted. level: 4

 flow: the code is readable and doesnt appear to contain many duplicate code. both films and add classes contain asynctasks with some duplicate code, these could be combined into a single class. Altough that would of had its own downsides. ultimitly this is design choice. level : 4

 idiom:  if else statements and other control structures are appropriate, reusing of library functionality was not possible. level :4

 Decomposition: some methods have become a bit too long which decreases readability such as the oncreate method of the films class. setting up the recyclerView could of been a different method for example. level : 3

 modularization : modules are mostly simple and it is clear what their function is. level : 4
