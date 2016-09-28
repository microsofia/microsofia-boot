Most of Java developers use Maven to compile. It handles at compile time all the jar hell dependencies, but once we go to the production things are different. Microsofia-boot tries to solve this gap by having the output of the development phase directly usable at production phase.

#How does it work ?

##At development phase
As usual create your Maven project with the correct dependencies, and containing the main that you want to execute at production. At release time, deploy the needed artifacts using Maven into a repository.

##At production phase
Using a settings file that looks like Maven one, configure microsofia-boot to have the correct repository and the root dependency of the project containing the main that you want to run. Only microsofia-boot jar is needed to run your main.
And THAT's IT! 

#Behind the scenes
Microsofia-boot uses Eclipse Aether in order to download from the repository all needed artifacts with their dependencies, build a graph of classloaders that mimicks the artificats dependencies graph, and then load your class and invoke its main.
