## Random Utilities

A bunch of random utilities I either have needed / think are useful to have. Yes most of these have been done before in other libraries but I needed these without wanting to have 100 other classes on my classpath.

These are designed into modules that you can use individually. Everything depends on the core module but it will work on it's own. Everything module is kept up to date with each other, meaning that if one module updates they all do. Check the patch notes to see if anything special about your specific module changed.

# IMPORTANT NOTE FOR DOWNLOADING.

Version 1.0.0 was compiled with the jar name containing the (intended) scala version. Use 1.0.1 onward to ignore this.

### All current modules:

| Name                      | Description                                                                                       | Status                                           | Language |
|:--------------------------|:--------------------------------------------------------------------------------------------------|:-------------------------------------------------|:---------|
| core                      | Random utilities that other modules will depend on.                                               | Always more to add when another module needs it. | Java     |
| result                    | A more expansive Optional.                                                                        | Needs testing. Otherwise finished.               | Java     |
| event-dispatcher          | A generic EventDispatcher                                                                         | Planned                                          | Java     |
| primitive-specializations | A collection of primitive specializations, so you never have to use `Map<String, Integer>` again. | Planned                                          | Java     |
| cache                     | A way to cache various objects                                                                    | Started                                          | Java     |


### core

Core is made up of generally useful classes that many modules might rely on. While it can be downloaded by itself, that's not usually what you'll do. All modules depend on it, and as such
you'll have it by using any module.

