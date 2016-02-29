# INTRO

Hi Gem and Company.  Here, you will find an example Java 8 applications that will hopefully demo my Dev chops in some depth, and more importantly, provide some late night giggles (particularly for Thomas).  I've spent about 3-4 total hours on it since learning Quasar took a bit of reading.

# The Starving Philosophers

This is an illustration of the classic resource locking problem that plagues distributed systems when nodes contend for limited resources.  A good writeup can be found [here](https://practicingruby.com/articles/gentle-intro-to-actor-based-concurrency) and [here](http://adit.io/posts/2013-05-11-The-Dining-Philosophers-Problem-With-Ron-Swanson.html), where I've shameless taken the data modeling.  Originally formulated by [Djkistra](https://en.wikipedia.org/wiki/Dining_philosophers_problem), it serves to illustrate problems that occur within distributed systems and techniques for solving them. 

In summary, while the classic mutex approach can help resolve the dead lock, it can actually be more elegantly solved by applying an Actor framework.  It simplifies the hairy implementation that would otherwise be hard to debug using Java's concurrent framework.  For this example, I've chosen to use [Quasars](http://docs.paralleluniverse.co/quasar/) which provides asynchronous tools such as light-weight threads (fibers), Dataflow, and Actors for implementing distributed systems.

To minimize boilerplate related to data access, logging and other common patterns, I've also prolifically applied [lombok](https://projectlombok.org/) annotations when appropriate.

# Abstractions

[Philsophers]() represent client nodes that contend for system resources.
[Waiter]() is the arbitrator that ensure the "missing 1 fork" race condition doesn't happen.
[Forks]() is the contended resource.
[Table]() describe the structure and relationship between philosophers and their forks.

# Building and running it

The build and runtime are all mechanized via [Gradle](www.gradle.org).  To run the sample application, checkout the repo:

    $ git checkout https://github.com/armadillabs/GemJava.git
    
...and run the the gradle wrapper that has been included as part of the project:

    $ ./gradlew run
    
Any transitive dependencies will be resolved and locally cached, assuming that you have internet connection.

There may be a couple times when things will just **BORK**, just Ctrl-C and run a couple times until no exceptions are thrown :).

This is a work-in-progress.

# What worked, and what didn't

In order to simulate the stochastic time intervals when the clients (Philosopher actors) messages the supervisor (Waiter actor), I've introduced [random pauses](https://docs.oracle.com/javase/7/docs/api/java/lang/Object.html#wait%28long%29) in client actors' threads.  A couple unexpected bugs that are yet to be solved with actors:

1. Since Actors do not currently allow for synchronized operations - or blocking calls - the sleep methods can only be used in the context of true JVM threads.
2. Better monitoring and testing need to be put in place to make sure actors stay alive.  There's a race condition that keeps killing the waiter, a mystery that remains to be solved.
3. Curiously enough, classic Java [Locks](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/locks/ReentrantLock.html), being synchronized, doesn't error out when used with Quasar.

# Example output...when it works
```
Philips-MacBook-Air:GemJava Philip$ ./gradlew run
:compileJava
Note: /Users/Philip/github/GemJava/src/main/java/co/gem/philosopher/Waiter.java uses unchecked or unsafe operations.
Note: Recompile with -Xlint:unchecked for details.
:processResources UP-TO-DATE
:classes
:run
objc[6743]: Class JavaLaunchHelper is implemented in both /Library/Java/JavaVirtualMachines/jdk1.8.0_74.jdk/Contents/Home/bin/java and /Library/Java/JavaVirtualMachines/jdk1.8.0_74.jdk/Contents/Home/jre/lib/libinstrument.dylib. One of the two will be used. Which one is undefined.
SLF4J: Class path contains multiple SLF4J bindings.
SLF4J: Found binding in [jar:file:/Users/Philip/.gradle/caches/modules-2/files-2.1/org.slf4j/slf4j-simple/1.7.16/f0cacc3d21e1027c82006a7a6f6cf2cad86d2a4f/slf4j-simple-1.7.16.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: Found binding in [jar:file:/Users/Philip/.gradle/caches/modules-2/files-2.1/org.apache.logging.log4j/log4j-slf4j-impl/2.0/435f7a4b8c59a70bcbdbdf964a0db10fc81eff6f/log4j-slf4j-impl-2.0.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.
SLF4J: Actual binding is of type [org.slf4j.impl.SimpleLoggerFactory]
[quasar] ERROR: Unable to instrument class co/gem/philosopher/Philosopher
co.paralleluniverse.fibers.instrument.UnableToInstrumentException: Unable to instrument co/gem/philosopher/Philosopher#think()V because of blocking call to java/lang/Thread#sleep(J)V
	at co.paralleluniverse.fibers.instrument.InstrumentMethod.possiblyWarnAboutBlocking(InstrumentMethod.java:280)
	at co.paralleluniverse.fibers.instrument.InstrumentMethod.callsSuspendables(InstrumentMethod.java:170)
	at co.paralleluniverse.fibers.instrument.InstrumentClass.visitEnd(InstrumentClass.java:261)
	at co.paralleluniverse.asm.ClassReader.accept(Unknown Source)
	at co.paralleluniverse.asm.ClassReader.accept(Unknown Source)
	at co.paralleluniverse.fibers.instrument.QuasarInstrumentor.instrumentClass(QuasarInstrumentor.java:113)
	at co.paralleluniverse.fibers.instrument.QuasarInstrumentor.instrumentClass(QuasarInstrumentor.java:87)
	at co.paralleluniverse.fibers.instrument.JavaAgent$Transformer.transform(JavaAgent.java:185)
	at sun.instrument.TransformerManager.transform(TransformerManager.java:188)
	at sun.instrument.InstrumentationImpl.transform(InstrumentationImpl.java:428)
	at java.lang.ClassLoader.defineClass1(Native Method)
	at java.lang.ClassLoader.defineClass(ClassLoader.java:760)
	at java.security.SecureClassLoader.defineClass(SecureClassLoader.java:142)
	at java.net.URLClassLoader.defineClass(URLClassLoader.java:467)
	at java.net.URLClassLoader.access$100(URLClassLoader.java:73)
	at java.net.URLClassLoader$1.run(URLClassLoader.java:368)
	at java.net.URLClassLoader$1.run(URLClassLoader.java:362)
	at java.security.AccessController.doPrivileged(Native Method)
	at java.net.URLClassLoader.findClass(URLClassLoader.java:361)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:424)
	at sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:331)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:357)
	at co.gem.philosopher.Driver.main(Driver.java:22)
[quasar] ERROR: while transforming co/gem/philosopher/Philosopher: Unable to instrument co/gem/philosopher/Philosopher#think()V because of blocking call to java/lang/Thread#sleep(J)V
co.paralleluniverse.fibers.instrument.UnableToInstrumentException: Unable to instrument co/gem/philosopher/Philosopher#think()V because of blocking call to java/lang/Thread#sleep(J)V
	at co.paralleluniverse.fibers.instrument.InstrumentMethod.possiblyWarnAboutBlocking(InstrumentMethod.java:280)
	at co.paralleluniverse.fibers.instrument.InstrumentMethod.callsSuspendables(InstrumentMethod.java:170)
	at co.paralleluniverse.fibers.instrument.InstrumentClass.visitEnd(InstrumentClass.java:261)
	at co.paralleluniverse.asm.ClassReader.accept(Unknown Source)
	at co.paralleluniverse.asm.ClassReader.accept(Unknown Source)
	at co.paralleluniverse.fibers.instrument.QuasarInstrumentor.instrumentClass(QuasarInstrumentor.java:113)
	at co.paralleluniverse.fibers.instrument.QuasarInstrumentor.instrumentClass(QuasarInstrumentor.java:87)
	at co.paralleluniverse.fibers.instrument.JavaAgent$Transformer.transform(JavaAgent.java:185)
	at sun.instrument.TransformerManager.transform(TransformerManager.java:188)
	at sun.instrument.InstrumentationImpl.transform(InstrumentationImpl.java:428)
	at java.lang.ClassLoader.defineClass1(Native Method)
	at java.lang.ClassLoader.defineClass(ClassLoader.java:760)
	at java.security.SecureClassLoader.defineClass(SecureClassLoader.java:142)
	at java.net.URLClassLoader.defineClass(URLClassLoader.java:467)
	at java.net.URLClassLoader.access$100(URLClassLoader.java:73)
	at java.net.URLClassLoader$1.run(URLClassLoader.java:368)
	at java.net.URLClassLoader$1.run(URLClassLoader.java:362)
	at java.security.AccessController.doPrivileged(Native Method)
	at java.net.URLClassLoader.findClass(URLClassLoader.java:361)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:424)
	at sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:331)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:357)
	at co.gem.philosopher.Driver.main(Driver.java:22)
[main] INFO co.gem.philosopher.Philosopher - NIETCHE has awakened.
[main] INFO co.gem.philosopher.Philosopher - NIETCHE is thinking.
[main] INFO co.gem.philosopher.Philosopher - NIETCHE requests to eat.
[main] INFO co.gem.philosopher.Philosopher - ARISTOTLE has awakened.
[main] INFO co.gem.philosopher.Philosopher - ARISTOTLE is thinking.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=NIETCHE, table=Table(forkArrayList=[co.gem.philosopher.Fork@cb7cc94, co.gem.philosopher.Fork@3597f745, co.gem.philosopher.Fork@3af0e65a], seats=3), waiter=Waiter(eaters=[]), position=0, lFork=co.gem.philosopher.Fork@3af0e65a, rFork=co.gem.philosopher.Fork@cb7cc94, rn=java.util.Random@2c340d05, count=null), state=1)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter serving NIETCHE
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE got hold of his left fork.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE got hold of his right fork.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE is eating.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE is done eating.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE is thinking.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=NIETCHE, table=Table(forkArrayList=[co.gem.philosopher.Fork@cb7cc94, co.gem.philosopher.Fork@3597f745, co.gem.philosopher.Fork@3af0e65a], seats=3), waiter=Waiter(eaters=[NIETCHE]), position=0, lFork=co.gem.philosopher.Fork@3af0e65a, rFork=co.gem.philosopher.Fork@cb7cc94, rn=java.util.Random@2c340d05, count=null), state=2)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter cleaning up NIETCHE
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE requests to eat.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=NIETCHE, table=Table(forkArrayList=[co.gem.philosopher.Fork@cb7cc94, co.gem.philosopher.Fork@3597f745, co.gem.philosopher.Fork@3af0e65a], seats=3), waiter=Waiter(eaters=[]), position=0, lFork=co.gem.philosopher.Fork@3af0e65a, rFork=co.gem.philosopher.Fork@cb7cc94, rn=java.util.Random@2c340d05, count=null), state=1)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter serving NIETCHE
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE got hold of his left fork.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE got hold of his right fork.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE is eating.
[main] INFO co.gem.philosopher.Philosopher - ARISTOTLE requests to eat.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=ARISTOTLE, table=Table(forkArrayList=[co.gem.philosopher.Fork@cb7cc94, co.gem.philosopher.Fork@3597f745, co.gem.philosopher.Fork@3af0e65a], seats=3), waiter=Waiter(eaters=[NIETCHE]), position=1, lFork=co.gem.philosopher.Fork@cb7cc94, rFork=co.gem.philosopher.Fork@3597f745, rn=java.util.Random@290da0c9, count=null), state=1)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter serving ARISTOTLE
[main] INFO co.gem.philosopher.Philosopher - SOCRATES has awakened.
[main] INFO co.gem.philosopher.Philosopher - SOCRATES is thinking.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE is done eating.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE got hold of his left fork.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE is thinking.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE got hold of his right fork.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE is eating.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=NIETCHE, table=Table(forkArrayList=[co.gem.philosopher.Fork@cb7cc94, co.gem.philosopher.Fork@3597f745, co.gem.philosopher.Fork@3af0e65a], seats=3), waiter=Waiter(eaters=[ARISTOTLE, NIETCHE]), position=0, lFork=co.gem.philosopher.Fork@3af0e65a, rFork=co.gem.philosopher.Fork@cb7cc94, rn=java.util.Random@2c340d05, count=null), state=2)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter cleaning up NIETCHE
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE is done eating.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE is thinking.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=ARISTOTLE, table=Table(forkArrayList=[co.gem.philosopher.Fork@cb7cc94, co.gem.philosopher.Fork@3597f745, co.gem.philosopher.Fork@3af0e65a], seats=3), waiter=Waiter(eaters=[ARISTOTLE]), position=1, lFork=co.gem.philosopher.Fork@cb7cc94, rFork=co.gem.philosopher.Fork@3597f745, rn=java.util.Random@290da0c9, count=null), state=2)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter cleaning up ARISTOTLE
[main] INFO co.gem.philosopher.Philosopher - SOCRATES requests to eat.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=SOCRATES, table=Table(forkArrayList=[co.gem.philosopher.Fork@cb7cc94, co.gem.philosopher.Fork@3597f745, co.gem.philosopher.Fork@3af0e65a], seats=3), waiter=Waiter(eaters=[]), position=2, lFork=co.gem.philosopher.Fork@3597f745, rFork=co.gem.philosopher.Fork@3af0e65a, rn=java.util.Random@16804967, count=null), state=1)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter serving SOCRATES
[SOCRATES] INFO co.gem.philosopher.Philosopher - SOCRATES got hold of his left fork.
[SOCRATES] INFO co.gem.philosopher.Philosopher - SOCRATES got hold of his right fork.
[SOCRATES] INFO co.gem.philosopher.Philosopher - SOCRATES is eating.
[SOCRATES] INFO co.gem.philosopher.Philosopher - SOCRATES is done eating.
[SOCRATES] INFO co.gem.philosopher.Philosopher - SOCRATES is thinking.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=SOCRATES, table=Table(forkArrayList=[co.gem.philosopher.Fork@cb7cc94, co.gem.philosopher.Fork@3597f745, co.gem.philosopher.Fork@3af0e65a], seats=3), waiter=Waiter(eaters=[SOCRATES]), position=2, lFork=co.gem.philosopher.Fork@3597f745, rFork=co.gem.philosopher.Fork@3af0e65a, rn=java.util.Random@16804967, count=null), state=2)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter cleaning up SOCRATES
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE requests to eat.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=NIETCHE, table=Table(forkArrayList=[co.gem.philosopher.Fork@cb7cc94, co.gem.philosopher.Fork@3597f745, co.gem.philosopher.Fork@3af0e65a], seats=3), waiter=Waiter(eaters=[]), position=0, lFork=co.gem.philosopher.Fork@3af0e65a, rFork=co.gem.philosopher.Fork@cb7cc94, rn=java.util.Random@2c340d05, count=null), state=1)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter serving NIETCHE
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE got hold of his left fork.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE got hold of his right fork.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE is eating.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE requests to eat.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=ARISTOTLE, table=Table(forkArrayList=[co.gem.philosopher.Fork@cb7cc94, co.gem.philosopher.Fork@3597f745, co.gem.philosopher.Fork@3af0e65a], seats=3), waiter=Waiter(eaters=[NIETCHE]), position=1, lFork=co.gem.philosopher.Fork@cb7cc94, rFork=co.gem.philosopher.Fork@3597f745, rn=java.util.Random@290da0c9, count=null), state=1)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter serving ARISTOTLE
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE is done eating.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE got hold of his left fork.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE got hold of his right fork.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE is eating.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=NIETCHE, table=Table(forkArrayList=[co.gem.philosopher.Fork@cb7cc94, co.gem.philosopher.Fork@3597f745, co.gem.philosopher.Fork@3af0e65a], seats=3), waiter=Waiter(eaters=[ARISTOTLE, NIETCHE]), position=0, lFork=co.gem.philosopher.Fork@3af0e65a, rFork=co.gem.philosopher.Fork@cb7cc94, rn=java.util.Random@2c340d05, count=null), state=2)
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE is thinking.
[Thread-1] INFO co.gem.philosopher.Waiter - waiter cleaning up NIETCHE
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE is done eating.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE is thinking.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=ARISTOTLE, table=Table(forkArrayList=[co.gem.philosopher.Fork@cb7cc94, co.gem.philosopher.Fork@3597f745, co.gem.philosopher.Fork@3af0e65a], seats=3), waiter=Waiter(eaters=[ARISTOTLE]), position=1, lFork=co.gem.philosopher.Fork@cb7cc94, rFork=co.gem.philosopher.Fork@3597f745, rn=java.util.Random@290da0c9, count=null), state=2)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter cleaning up ARISTOTLE
[SOCRATES] INFO co.gem.philosopher.Philosopher - SOCRATES requests to eat.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=SOCRATES, table=Table(forkArrayList=[co.gem.philosopher.Fork@cb7cc94, co.gem.philosopher.Fork@3597f745, co.gem.philosopher.Fork@3af0e65a], seats=3), waiter=Waiter(eaters=[]), position=2, lFork=co.gem.philosopher.Fork@3597f745, rFork=co.gem.philosopher.Fork@3af0e65a, rn=java.util.Random@16804967, count=null), state=1)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter serving SOCRATES
[SOCRATES] INFO co.gem.philosopher.Philosopher - SOCRATES got hold of his left fork.
[SOCRATES] INFO co.gem.philosopher.Philosopher - SOCRATES got hold of his right fork.
[SOCRATES] INFO co.gem.philosopher.Philosopher - SOCRATES is eating.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE requests to eat.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=ARISTOTLE, table=Table(forkArrayList=[co.gem.philosopher.Fork@cb7cc94, co.gem.philosopher.Fork@3597f745, co.gem.philosopher.Fork@3af0e65a], seats=3), waiter=Waiter(eaters=[SOCRATES]), position=1, lFork=co.gem.philosopher.Fork@cb7cc94, rFork=co.gem.philosopher.Fork@3597f745, rn=java.util.Random@290da0c9, count=null), state=1)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter serving ARISTOTLE
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE got hold of his left fork.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE requests to eat.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=NIETCHE, table=Table(forkArrayList=[co.gem.philosopher.Fork@cb7cc94, co.gem.philosopher.Fork@3597f745, co.gem.philosopher.Fork@3af0e65a], seats=3), waiter=Waiter(eaters=[ARISTOTLE, SOCRATES]), position=0, lFork=co.gem.philosopher.Fork@3af0e65a, rFork=co.gem.philosopher.Fork@cb7cc94, rn=java.util.Random@2c340d05, count=null), state=1)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter serving NIETCHE
[SOCRATES] INFO co.gem.philosopher.Philosopher - SOCRATES is done eating.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE got hold of his left fork.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE got hold of his right fork.
[SOCRATES] INFO co.gem.philosopher.Philosopher - SOCRATES is thinking.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE is eating.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=SOCRATES, table=Table(forkArrayList=[co.gem.philosopher.Fork@cb7cc94, co.gem.philosopher.Fork@3597f745, co.gem.philosopher.Fork@3af0e65a], seats=3), waiter=Waiter(eaters=[ARISTOTLE, SOCRATES, NIETCHE]), position=2, lFork=co.gem.philosopher.Fork@3597f745, rFork=co.gem.philosopher.Fork@3af0e65a, rn=java.util.Random@16804967, count=null), state=2)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter cleaning up SOCRATES
[SOCRATES] INFO co.gem.philosopher.Philosopher - SOCRATES requests to eat.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=SOCRATES, table=Table(forkArrayList=[co.gem.philosopher.Fork@cb7cc94, co.gem.philosopher.Fork@3597f745, co.gem.philosopher.Fork@3af0e65a], seats=3), waiter=Waiter(eaters=[ARISTOTLE, NIETCHE]), position=2, lFork=co.gem.philosopher.Fork@3597f745, rFork=co.gem.philosopher.Fork@3af0e65a, rn=java.util.Random@16804967, count=null), state=1)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter serving SOCRATES
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE is done eating.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE got hold of his right fork.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE is thinking.
[SOCRATES] INFO co.gem.philosopher.Philosopher - SOCRATES got hold of his left fork.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=ARISTOTLE, table=Table(forkArrayList=[co.gem.philosopher.Fork@cb7cc94, co.gem.philosopher.Fork@3597f745, co.gem.philosopher.Fork@3af0e65a], seats=3), waiter=Waiter(eaters=[ARISTOTLE, SOCRATES, NIETCHE]), position=1, lFork=co.gem.philosopher.Fork@cb7cc94, rFork=co.gem.philosopher.Fork@3597f745, rn=java.util.Random@290da0c9, count=null), state=2)
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE is eating.
[Thread-1] INFO co.gem.philosopher.Waiter - waiter cleaning up ARISTOTLE
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE is done eating.
[SOCRATES] INFO co.gem.philosopher.Philosopher - SOCRATES got hold of his right fork.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE is thinking.
[SOCRATES] INFO co.gem.philosopher.Philosopher - SOCRATES is eating.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=NIETCHE, table=Table(forkArrayList=[co.gem.philosopher.Fork@cb7cc94, co.gem.philosopher.Fork@3597f745, co.gem.philosopher.Fork@3af0e65a], seats=3), waiter=Waiter(eaters=[SOCRATES, NIETCHE]), position=0, lFork=co.gem.philosopher.Fork@3af0e65a, rFork=co.gem.philosopher.Fork@cb7cc94, rn=java.util.Random@2c340d05, count=null), state=2)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter cleaning up NIETCHE
[SOCRATES] INFO co.gem.philosopher.Philosopher - SOCRATES is done eating.
[SOCRATES] INFO co.gem.philosopher.Philosopher - SOCRATES is thinking.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=SOCRATES, table=Table(forkArrayList=[co.gem.philosopher.Fork@cb7cc94, co.gem.philosopher.Fork@3597f745, co.gem.philosopher.Fork@3af0e65a], seats=3), waiter=Waiter(eaters=[SOCRATES]), position=2, lFork=co.gem.philosopher.Fork@3597f745, rFork=co.gem.philosopher.Fork@3af0e65a, rn=java.util.Random@16804967, count=null), state=2)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter cleaning up SOCRATES
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE requests to eat.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=NIETCHE, table=Table(forkArrayList=[co.gem.philosopher.Fork@cb7cc94, co.gem.philosopher.Fork@3597f745, co.gem.philosopher.Fork@3af0e65a], seats=3), waiter=Waiter(eaters=[]), position=0, lFork=co.gem.philosopher.Fork@3af0e65a, rFork=co.gem.philosopher.Fork@cb7cc94, rn=java.util.Random@2c340d05, count=null), state=1)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter serving NIETCHE
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE got hold of his left fork.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE got hold of his right fork.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE is eating.
[SOCRATES] INFO co.gem.philosopher.Philosopher - SOCRATES requests to eat.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=SOCRATES, table=Table(forkArrayList=[co.gem.philosopher.Fork@cb7cc94, co.gem.philosopher.Fork@3597f745, co.gem.philosopher.Fork@3af0e65a], seats=3), waiter=Waiter(eaters=[NIETCHE]), position=2, lFork=co.gem.philosopher.Fork@3597f745, rFork=co.gem.philosopher.Fork@3af0e65a, rn=java.util.Random@16804967, count=null), state=1)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter serving SOCRATES
[SOCRATES] INFO co.gem.philosopher.Philosopher - SOCRATES got hold of his left fork.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE requests to eat.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=ARISTOTLE, table=Table(forkArrayList=[co.gem.philosopher.Fork@cb7cc94, co.gem.philosopher.Fork@3597f745, co.gem.philosopher.Fork@3af0e65a], seats=3), waiter=Waiter(eaters=[SOCRATES, NIETCHE]), position=1, lFork=co.gem.philosopher.Fork@cb7cc94, rFork=co.gem.philosopher.Fork@3597f745, rn=java.util.Random@290da0c9, count=null), state=1)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter serving ARISTOTLE
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE is done eating.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE got hold of his left fork.
[SOCRATES] INFO co.gem.philosopher.Philosopher - SOCRATES got hold of his right fork.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE is thinking.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=NIETCHE, table=Table(forkArrayList=[co.gem.philosopher.Fork@cb7cc94, co.gem.philosopher.Fork@3597f745, co.gem.philosopher.Fork@3af0e65a], seats=3), waiter=Waiter(eaters=[ARISTOTLE, SOCRATES, NIETCHE]), position=0, lFork=co.gem.philosopher.Fork@3af0e65a, rFork=co.gem.philosopher.Fork@cb7cc94, rn=java.util.Random@2c340d05, count=null), state=2)
[SOCRATES] INFO co.gem.philosopher.Philosopher - SOCRATES is eating.
[Thread-1] INFO co.gem.philosopher.Waiter - waiter cleaning up NIETCHE
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE got hold of his right fork.
[SOCRATES] INFO co.gem.philosopher.Philosopher - SOCRATES is done eating.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE is eating.
[SOCRATES] INFO co.gem.philosopher.Philosopher - SOCRATES is thinking.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=SOCRATES, table=Table(forkArrayList=[co.gem.philosopher.Fork@cb7cc94, co.gem.philosopher.Fork@3597f745, co.gem.philosopher.Fork@3af0e65a], seats=3), waiter=Waiter(eaters=[ARISTOTLE, SOCRATES]), position=2, lFork=co.gem.philosopher.Fork@3597f745, rFork=co.gem.philosopher.Fork@3af0e65a, rn=java.util.Random@16804967, count=null), state=2)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter cleaning up SOCRATES
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE is done eating.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE is thinking.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=ARISTOTLE, table=Table(forkArrayList=[co.gem.philosopher.Fork@cb7cc94, co.gem.philosopher.Fork@3597f745, co.gem.philosopher.Fork@3af0e65a], seats=3), waiter=Waiter(eaters=[ARISTOTLE]), position=1, lFork=co.gem.philosopher.Fork@cb7cc94, rFork=co.gem.philosopher.Fork@3597f745, rn=java.util.Random@290da0c9, count=null), state=2)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter cleaning up ARISTOTLE
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE requests to eat.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=ARISTOTLE, table=Table(forkArrayList=[co.gem.philosopher.Fork@cb7cc94, co.gem.philosopher.Fork@3597f745, co.gem.philosopher.Fork@3af0e65a], seats=3), waiter=Waiter(eaters=[]), position=1, lFork=co.gem.philosopher.Fork@cb7cc94, rFork=co.gem.philosopher.Fork@3597f745, rn=java.util.Random@290da0c9, count=null), state=1)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter serving ARISTOTLE
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE got hold of his left fork.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE got hold of his right fork.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE is eating.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE requests to eat.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=NIETCHE, table=Table(forkArrayList=[co.gem.philosopher.Fork@cb7cc94, co.gem.philosopher.Fork@3597f745, co.gem.philosopher.Fork@3af0e65a], seats=3), waiter=Waiter(eaters=[ARISTOTLE]), position=0, lFork=co.gem.philosopher.Fork@3af0e65a, rFork=co.gem.philosopher.Fork@cb7cc94, rn=java.util.Random@2c340d05, count=null), state=1)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter serving NIETCHE
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE got hold of his left fork.
[SOCRATES] INFO co.gem.philosopher.Philosopher - SOCRATES requests to eat.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=SOCRATES, table=Table(forkArrayList=[co.gem.philosopher.Fork@cb7cc94, co.gem.philosopher.Fork@3597f745, co.gem.philosopher.Fork@3af0e65a], seats=3), waiter=Waiter(eaters=[ARISTOTLE, NIETCHE]), position=2, lFork=co.gem.philosopher.Fork@3597f745, rFork=co.gem.philosopher.Fork@3af0e65a, rn=java.util.Random@16804967, count=null), state=1)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter serving SOCRATES
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE is done eating.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE got hold of his right fork.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE is thinking.
[SOCRATES] INFO co.gem.philosopher.Philosopher - SOCRATES got hold of his left fork.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE is eating.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=ARISTOTLE, table=Table(forkArrayList=[co.gem.philosopher.Fork@cb7cc94, co.gem.philosopher.Fork@3597f745, co.gem.philosopher.Fork@3af0e65a], seats=3), waiter=Waiter(eaters=[ARISTOTLE, SOCRATES, NIETCHE]), position=1, lFork=co.gem.philosopher.Fork@cb7cc94, rFork=co.gem.philosopher.Fork@3597f745, rn=java.util.Random@290da0c9, count=null), state=2)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter cleaning up ARISTOTLE
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE requests to eat.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=ARISTOTLE, table=Table(forkArrayList=[co.gem.philosopher.Fork@cb7cc94, co.gem.philosopher.Fork@3597f745, co.gem.philosopher.Fork@3af0e65a], seats=3), waiter=Waiter(eaters=[SOCRATES, NIETCHE]), position=1, lFork=co.gem.philosopher.Fork@cb7cc94, rFork=co.gem.philosopher.Fork@3597f745, rn=java.util.Random@290da0c9, count=null), state=1)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter serving ARISTOTLE
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE is done eating.
[SOCRATES] INFO co.gem.philosopher.Philosopher - SOCRATES got hold of his right fork.
[SOCRATES] INFO co.gem.philosopher.Philosopher - SOCRATES is eating.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE got hold of his left fork.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=NIETCHE, table=Table(forkArrayList=[co.gem.philosopher.Fork@cb7cc94, co.gem.philosopher.Fork@3597f745, co.gem.philosopher.Fork@3af0e65a], seats=3), waiter=Waiter(eaters=[ARISTOTLE, SOCRATES, NIETCHE]), position=0, lFork=co.gem.philosopher.Fork@3af0e65a, rFork=co.gem.philosopher.Fork@cb7cc94, rn=java.util.Random@2c340d05, count=null), state=2)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter cleaning up NIETCHE
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE is thinking.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE requests to eat.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=NIETCHE, table=Table(forkArrayList=[co.gem.philosopher.Fork@cb7cc94, co.gem.philosopher.Fork@3597f745, co.gem.philosopher.Fork@3af0e65a], seats=3), waiter=Waiter(eaters=[ARISTOTLE, SOCRATES]), position=0, lFork=co.gem.philosopher.Fork@3af0e65a, rFork=co.gem.philosopher.Fork@cb7cc94, rn=java.util.Random@2c340d05, count=null), state=1)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter serving NIETCHE
[SOCRATES] INFO co.gem.philosopher.Philosopher - SOCRATES is done eating.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE got hold of his left fork.
[SOCRATES] INFO co.gem.philosopher.Philosopher - SOCRATES is thinking.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE got hold of his right fork.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE is eating.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=SOCRATES, table=Table(forkArrayList=[co.gem.philosopher.Fork@cb7cc94, co.gem.philosopher.Fork@3597f745, co.gem.philosopher.Fork@3af0e65a], seats=3), waiter=Waiter(eaters=[ARISTOTLE, SOCRATES, NIETCHE]), position=2, lFork=co.gem.philosopher.Fork@3597f745, rFork=co.gem.philosopher.Fork@3af0e65a, rn=java.util.Random@16804967, count=null), state=2)
```

# ...and when it doesn't

```
[Thread-1] INFO co.gem.philosopher.Waiter - waiter serving ARISTOTLE
Exception in thread "Thread-1" java.lang.IllegalStateException: Actor has not been started
	at co.paralleluniverse.actors.Actor.ref(Actor.java:357)
	at co.gem.philosopher.Waiter.doRun(Waiter.java:39)
	at co.gem.philosopher.Waiter.doRun(Waiter.java:17)
	at co.paralleluniverse.actors.Actor.run0(Actor.java:691)
	at co.paralleluniverse.actors.ActorRunner.run(ActorRunner.java:51)
	at co.paralleluniverse.strands.Strand$SuspendableCallableRunnable.run(Strand.java:845)
	at java.lang.Thread.run(Thread.java:745)
```
