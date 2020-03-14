## Random Utilities

A bunch of random utilities I either have needed / think are useful to have. Yes most of these have been done before in other libraries but I needed these without wanting to have 100 other classes on my classpath.

These are designed into modules that you can use individually. Everything depends on the core module but it will work on it's own. Everything module is kept up to date with each other, meaning that if one module updates they all do. Check the patch notes to see if anything special about your specific module changed.

### Downloading

With maven:

With gradle:

With sbt:

### All current modules:

| Name   | Description                                         |
|:-------|:----------------------------------------------------|
| core   | Random utilities that other modules will depend on. |
| result | A more expansive Optional.                          |
