## cache

A collection of caches for... caching.

### Usage

If you're familiar with Optional, the methods should feel familiar
to you. The method `onSuccess` is similar to `ifPresent`. The method `onEmpty` is similar to `ifEmpty`. `isSuccess` is the same as `isPresent`.

What might be unfamiliar is `onError` and `isError`. These are for handling errors. See the example below.


```java
Result<String> result = ...; // Use any of the static Result methods.
result
    .onSuccess(elem -> System.out.println("Element is: " + elem) // Print out the original element if it's Success
    .filter(elem -> !elem.isEmpty()) // If it's success, turns the result empty if the condition is false.
    .map(elem -> "Hello world") // If it's success, map to a new element.
    .flatMap(elem -> Result.of("Hello world")) // Same thing as above, except it uses the element of the passed Result.
    .onSuccess(elem -> System.out.println("Hello world? Hello back at you!")) // This will be called if none of the others result in a different state.
    .onEmpty(() -> System.out.println("Result is empty.")) // Prints if the result is empty.
    .onError(Throwable.class, error -> System.out.println("Throwable with error message: " + error.getMessage())); // Prints if there was an error that is a subtype of Throwable
```

As you can see, I only use onEmpty and onError once, as the other methods only run on Success.

### Downloading

Maven:

Gradle:

Sbt:


### Submodules
| Name                      | Description                                                                                       | Status                                           | Language |
|:--------------------------|:--------------------------------------------------------------------------------------------------|:-------------------------------------------------|:---------|
| reactive-cache                      | Caches utilising reactor.                                               | Planned | Java     |
| scala-cache                    | Caches utilising Scala.                                                                        | planned         | Scala     |