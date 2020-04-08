## cache

A collection of caches for... caching. Allows you to cache in whatever way possible as long as you can convert them back to the Objects.

### Usage

```java
// Single value cache
Cache<String, String> cache = new MapMemoryCache<>(HashMap::new);
cache.store("Hello", "World");
System.out.println(cache.get("Hello")); // prints World

// Multi value cache
Cache<String, List<String>> cache = new MultiValueCacheDelegate(
  new MapMemoryCache(HashMap::new),
  ArrayList::new    
);

// Or if you want things like store(K key, V... values)
MutliValueCache<String, String, List<String>> cache = new MultiValueCacheDelegate(
                                                        new MapMemoryCache(HashMap::new),
                                                        ArrayList::new    
                                                      );
```

### Downloading

Replace $VERSION$ with the wanted version.

Maven:

```xml
<dependencies>
  <dependency>
    <groupId>it.xaan</groupId>
    <artifactId>random-cache</artifactId>
    <version>$VERSION$</version>
  </dependency>
</dependencies>
```

Gradle:

```groovy
implementation 'it.xaan:random-cache:$VERSION$'
```

Sbt:
```sbt
libraryDependencies += "it.xaan" % "random-cache" % "$VERSION$"
```


### Submodules
| Name                      | Description                                                                                       | Status                                           | Language |
|:--------------------------|:--------------------------------------------------------------------------------------------------|:-------------------------------------------------|:---------|
| reactive-cache                      | Caches utilising reactor.                                               | Planned | Java     |
| scala-cache                    | Caches utilising Scala.                                                                        | planned         | Scala     |