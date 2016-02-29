# README

Hi Gem and Company.  Here, you will find an example Java 8 applications that will hopefully demo my Dev chops in some depth, and more importantly, provide some late night giggles.

The build and runtime are all mechanized via [Gradle](www.gradle.org).  To run the sample application, checkout the repo:

    $ git checkout https://github.com/armadillabs/GemJava.git
    
...and run the the gradle wrapper that has been included as part of the project:

    $ ./gradlew run
    
Any transitive dependencies will be resolved and locally cached, assuming that you have internet connection.

# The Starving Philosopher Eaten by Fibers

This is an illustration of the classic resource locking problem that plagues distributed systems when nodes contend for limited resources.  A good writeup can be found [here](https://practicingruby.com/articles/gentle-intro-to-actor-based-concurrency) and [here](http://adit.io/posts/2013-05-11-The-Dining-Philosophers-Problem-With-Ron-Swanson.html), where I've shameless taken the data modeling.  Originally formulated by [Djkistra](https://en.wikipedia.org/wiki/Dining_philosophers_problem), it serves to illustrate problems that occur within distributed systems and techniques for solving them. 

In summary, while the classic mutex approach can help resolve the dead lock, it can actually be more elegantly solved by applying an Actor framework.  It simplifies the hairy implementation that would otherwise be hard to debug using Java's concurrent framework.  For this example, I've chosen to use [Quasars](http://docs.paralleluniverse.co/quasar/) which provides asynchronous tools such as light-weight threads (fibers), Dataflow, and Actors for implementing distributed systems.

# What worked, and what doesn't

In order to simulate the stochastic time intervals when the clients (Philosopher actors) messages the supervisor (Waiter actor), I've introduced [random pauses](https://docs.oracle.com/javase/7/docs/api/java/lang/Object.html#wait%28long%29) in client actors' threads.  A couple unexpected bugs that are yet to be solved with actors:

1. Since Actors do not currently allow for synchronized operations - or blocking calls - the sleep methods can only be used in the context of true JVM threads.
2. Better monitoring and testing need to be put in place to make sure actors stay alive.  There's a race condition that keeps killing the waiter, a mystery that remains to be solved.

# Example output...when it works
```
hilips-MacBook-Air:GemJava Philip$ ./gradlew run
:compileJava UP-TO-DATE
:processResources UP-TO-DATE
:classes UP-TO-DATE
:run
objc[6439]: Class JavaLaunchHelper is implemented in both /Library/Java/JavaVirtualMachines/jdk1.8.0_74.jdk/Contents/Home/bin/java and /Library/Java/JavaVirtualMachines/jdk1.8.0_74.jdk/Contents/Home/jre/lib/libinstrument.dylib. One of the two will be used. Which one is undefined.
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
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=NIETCHE, table=Table(forkArrayList=[co.gem.philosopher.Fork@25cefb7e, co.gem.philosopher.Fork@617d5d23], seats=2), waiter=Waiter(eaters=[]), position=0, lFork=co.gem.philosopher.Fork@617d5d23, rFork=co.gem.philosopher.Fork@25cefb7e, rn=java.util.Random@2fdb7f6, count=null), state=1)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter serving NIETCHE
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE got hold of his left fork.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE got hold of his right fork.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE is eating.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE is done eating.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE is thinking.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=NIETCHE, table=Table(forkArrayList=[co.gem.philosopher.Fork@25cefb7e, co.gem.philosopher.Fork@617d5d23], seats=2), waiter=Waiter(eaters=[NIETCHE]), position=0, lFork=co.gem.philosopher.Fork@617d5d23, rFork=co.gem.philosopher.Fork@25cefb7e, rn=java.util.Random@2fdb7f6, count=null), state=2)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter cleaning up NIETCHE
[main] INFO co.gem.philosopher.Philosopher - ARISTOTLE requests to eat.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=ARISTOTLE, table=Table(forkArrayList=[co.gem.philosopher.Fork@25cefb7e, co.gem.philosopher.Fork@617d5d23], seats=2), waiter=Waiter(eaters=[]), position=1, lFork=co.gem.philosopher.Fork@25cefb7e, rFork=co.gem.philosopher.Fork@617d5d23, rn=java.util.Random@1d356700, count=null), state=1)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter serving ARISTOTLE
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE got hold of his left fork.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE got hold of his right fork.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE is eating.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE is done eating.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE is thinking.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=ARISTOTLE, table=Table(forkArrayList=[co.gem.philosopher.Fork@25cefb7e, co.gem.philosopher.Fork@617d5d23], seats=2), waiter=Waiter(eaters=[ARISTOTLE]), position=1, lFork=co.gem.philosopher.Fork@25cefb7e, rFork=co.gem.philosopher.Fork@617d5d23, rn=java.util.Random@1d356700, count=null), state=2)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter cleaning up ARISTOTLE
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE requests to eat.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=NIETCHE, table=Table(forkArrayList=[co.gem.philosopher.Fork@25cefb7e, co.gem.philosopher.Fork@617d5d23], seats=2), waiter=Waiter(eaters=[]), position=0, lFork=co.gem.philosopher.Fork@617d5d23, rFork=co.gem.philosopher.Fork@25cefb7e, rn=java.util.Random@2fdb7f6, count=null), state=1)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter serving NIETCHE
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE got hold of his left fork.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE got hold of his right fork.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE is eating.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE requests to eat.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=ARISTOTLE, table=Table(forkArrayList=[co.gem.philosopher.Fork@25cefb7e, co.gem.philosopher.Fork@617d5d23], seats=2), waiter=Waiter(eaters=[NIETCHE]), position=1, lFork=co.gem.philosopher.Fork@25cefb7e, rFork=co.gem.philosopher.Fork@617d5d23, rn=java.util.Random@1d356700, count=null), state=1)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter serving ARISTOTLE
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE is done eating.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE got hold of his left fork.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE is thinking.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE got hold of his right fork.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE is eating.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=NIETCHE, table=Table(forkArrayList=[co.gem.philosopher.Fork@25cefb7e, co.gem.philosopher.Fork@617d5d23], seats=2), waiter=Waiter(eaters=[NIETCHE, ARISTOTLE]), position=0, lFork=co.gem.philosopher.Fork@617d5d23, rFork=co.gem.philosopher.Fork@25cefb7e, rn=java.util.Random@2fdb7f6, count=null), state=2)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter cleaning up NIETCHE
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE requests to eat.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=NIETCHE, table=Table(forkArrayList=[co.gem.philosopher.Fork@25cefb7e, co.gem.philosopher.Fork@617d5d23], seats=2), waiter=Waiter(eaters=[ARISTOTLE]), position=0, lFork=co.gem.philosopher.Fork@617d5d23, rFork=co.gem.philosopher.Fork@25cefb7e, rn=java.util.Random@2fdb7f6, count=null), state=1)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter serving NIETCHE
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE is done eating.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE got hold of his left fork.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE is thinking.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE got hold of his right fork.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=ARISTOTLE, table=Table(forkArrayList=[co.gem.philosopher.Fork@25cefb7e, co.gem.philosopher.Fork@617d5d23], seats=2), waiter=Waiter(eaters=[NIETCHE, ARISTOTLE]), position=1, lFork=co.gem.philosopher.Fork@25cefb7e, rFork=co.gem.philosopher.Fork@617d5d23, rn=java.util.Random@1d356700, count=null), state=2)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter cleaning up ARISTOTLE
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE is eating.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE requests to eat.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=ARISTOTLE, table=Table(forkArrayList=[co.gem.philosopher.Fork@25cefb7e, co.gem.philosopher.Fork@617d5d23], seats=2), waiter=Waiter(eaters=[NIETCHE]), position=1, lFork=co.gem.philosopher.Fork@25cefb7e, rFork=co.gem.philosopher.Fork@617d5d23, rn=java.util.Random@1d356700, count=null), state=1)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter serving ARISTOTLE
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE is done eating.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE got hold of his left fork.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE got hold of his right fork.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE is thinking.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE is eating.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=NIETCHE, table=Table(forkArrayList=[co.gem.philosopher.Fork@25cefb7e, co.gem.philosopher.Fork@617d5d23], seats=2), waiter=Waiter(eaters=[NIETCHE, ARISTOTLE]), position=0, lFork=co.gem.philosopher.Fork@617d5d23, rFork=co.gem.philosopher.Fork@25cefb7e, rn=java.util.Random@2fdb7f6, count=null), state=2)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter cleaning up NIETCHE
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE requests to eat.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=NIETCHE, table=Table(forkArrayList=[co.gem.philosopher.Fork@25cefb7e, co.gem.philosopher.Fork@617d5d23], seats=2), waiter=Waiter(eaters=[ARISTOTLE]), position=0, lFork=co.gem.philosopher.Fork@617d5d23, rFork=co.gem.philosopher.Fork@25cefb7e, rn=java.util.Random@2fdb7f6, count=null), state=1)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter serving NIETCHE
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE is done eating.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE got hold of his left fork.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE is thinking.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE got hold of his right fork.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE is eating.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=ARISTOTLE, table=Table(forkArrayList=[co.gem.philosopher.Fork@25cefb7e, co.gem.philosopher.Fork@617d5d23], seats=2), waiter=Waiter(eaters=[NIETCHE, ARISTOTLE]), position=1, lFork=co.gem.philosopher.Fork@25cefb7e, rFork=co.gem.philosopher.Fork@617d5d23, rn=java.util.Random@1d356700, count=null), state=2)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter cleaning up ARISTOTLE
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE is done eating.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE is thinking.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=NIETCHE, table=Table(forkArrayList=[co.gem.philosopher.Fork@25cefb7e, co.gem.philosopher.Fork@617d5d23], seats=2), waiter=Waiter(eaters=[NIETCHE]), position=0, lFork=co.gem.philosopher.Fork@617d5d23, rFork=co.gem.philosopher.Fork@25cefb7e, rn=java.util.Random@2fdb7f6, count=null), state=2)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter cleaning up NIETCHE
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE requests to eat.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=ARISTOTLE, table=Table(forkArrayList=[co.gem.philosopher.Fork@25cefb7e, co.gem.philosopher.Fork@617d5d23], seats=2), waiter=Waiter(eaters=[]), position=1, lFork=co.gem.philosopher.Fork@25cefb7e, rFork=co.gem.philosopher.Fork@617d5d23, rn=java.util.Random@1d356700, count=null), state=1)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter serving ARISTOTLE
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE got hold of his left fork.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE got hold of his right fork.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE is eating.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE requests to eat.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=NIETCHE, table=Table(forkArrayList=[co.gem.philosopher.Fork@25cefb7e, co.gem.philosopher.Fork@617d5d23], seats=2), waiter=Waiter(eaters=[ARISTOTLE]), position=0, lFork=co.gem.philosopher.Fork@617d5d23, rFork=co.gem.philosopher.Fork@25cefb7e, rn=java.util.Random@2fdb7f6, count=null), state=1)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter serving NIETCHE
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE is done eating.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE got hold of his left fork.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE is thinking.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE got hold of his right fork.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE is eating.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=ARISTOTLE, table=Table(forkArrayList=[co.gem.philosopher.Fork@25cefb7e, co.gem.philosopher.Fork@617d5d23], seats=2), waiter=Waiter(eaters=[NIETCHE, ARISTOTLE]), position=1, lFork=co.gem.philosopher.Fork@25cefb7e, rFork=co.gem.philosopher.Fork@617d5d23, rn=java.util.Random@1d356700, count=null), state=2)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter cleaning up ARISTOTLE
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE is done eating.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE is thinking.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=NIETCHE, table=Table(forkArrayList=[co.gem.philosopher.Fork@25cefb7e, co.gem.philosopher.Fork@617d5d23], seats=2), waiter=Waiter(eaters=[NIETCHE]), position=0, lFork=co.gem.philosopher.Fork@617d5d23, rFork=co.gem.philosopher.Fork@25cefb7e, rn=java.util.Random@2fdb7f6, count=null), state=2)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter cleaning up NIETCHE
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE requests to eat.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=NIETCHE, table=Table(forkArrayList=[co.gem.philosopher.Fork@25cefb7e, co.gem.philosopher.Fork@617d5d23], seats=2), waiter=Waiter(eaters=[]), position=0, lFork=co.gem.philosopher.Fork@617d5d23, rFork=co.gem.philosopher.Fork@25cefb7e, rn=java.util.Random@2fdb7f6, count=null), state=1)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter serving NIETCHE
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE got hold of his left fork.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE got hold of his right fork.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE is eating.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE requests to eat.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=ARISTOTLE, table=Table(forkArrayList=[co.gem.philosopher.Fork@25cefb7e, co.gem.philosopher.Fork@617d5d23], seats=2), waiter=Waiter(eaters=[NIETCHE]), position=1, lFork=co.gem.philosopher.Fork@25cefb7e, rFork=co.gem.philosopher.Fork@617d5d23, rn=java.util.Random@1d356700, count=null), state=1)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter serving ARISTOTLE
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE is done eating.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE got hold of his left fork.
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE is thinking.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE got hold of his right fork.
[ARISTOTLE] INFO co.gem.philosopher.Philosopher - ARISTOTLE is eating.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=NIETCHE, table=Table(forkArrayList=[co.gem.philosopher.Fork@25cefb7e, co.gem.philosopher.Fork@617d5d23], seats=2), waiter=Waiter(eaters=[NIETCHE, ARISTOTLE]), position=0, lFork=co.gem.philosopher.Fork@617d5d23, rFork=co.gem.philosopher.Fork@25cefb7e, rn=java.util.Random@2fdb7f6, count=null), state=2)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter cleaning up NIETCHE
[NIETCHE] INFO co.gem.philosopher.Philosopher - NIETCHE requests to eat.
[Thread-1] INFO co.gem.philosopher.Waiter - Waiter received request: RequestMessage(originator=Philosopher(label=NIETCHE, table=Table(forkArrayList=[co.gem.philosopher.Fork@25cefb7e, co.gem.philosopher.Fork@617d5d23], seats=2), waiter=Waiter(eaters=[ARISTOTLE]), position=0, lFork=co.gem.philosopher.Fork@617d5d23, rFork=co.gem.philosopher.Fork@25cefb7e, rn=java.util.Random@2fdb7f6, count=null), state=1)
[Thread-1] INFO co.gem.philosopher.Waiter - waiter serving NIETCHE
> Building 75% > :run^CPhilips-MacBook-Air:GemJava Philip$
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