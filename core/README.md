### core

Core is made up of generally useful classes that many modules might rely on. While it can be downloaded by itself, that's not usually what you'll do. All modules depend on it, and as such
you'll have it by using any module.

### Downloading

Replace $VERSION$ with the wanted version.

Maven:

```xml
<dependencies>
  <dependency>
    <groupId>it.xaan</groupId>
    <artifactId>random-core</artifactId>
    <version>$VERSION$</version>
  </dependency>
</dependencies>
```

Gradle:

```groovy
implementation 'it.xaan:random-core:$VERSION$'
```

Sbt:
```
libraryDependencies += "it.xaan" % "random-core" % "$VERSION$"
```