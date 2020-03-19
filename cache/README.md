## cache

A collection of caches for... caching. Allows you to cache in whatever way possible as long as you can convert them back to the Objects.

### Usage

```java
Cache<String, String> cache = new HashingMemoryCache<>();
cache.store("Hello", "World");
System.out.println(cache.get("Hello")); // prints World

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