### Version 0.5 (February 28, 2014)

* Capture errors from external program output and render it in console.
* Each Vagrant operation now has a custom task type.
* Split up code into two plugins:
    * `vagrant-base`: Provides custom tasks and preconfigures them.
    * `vagrant`: Creates tasks of all custom types with default values.

### Version 0.4 (February 27, 2014)

* Correctly capture command line output for external programs on Windows.

### Version 0.3 (February 26, 2014)

* Fix how the Vagrant executable is run on Windows - [Issue 1](https://github.com/bmuschko/gradle-vagrant-plugin/issues/1).
* Default to `projectDir` for `boxDir` property - [Issue 2](https://github.com/bmuschko/gradle-vagrant-plugin/issues/2).
* Upgrade to Gradle Wrapper 1.11.

### Version 0.2 (September 28, 2013)

* Upgrade to Gradle Wrapper 1.8.
* Wrote some unit tests.
* Validating if Vagrant and VirtualBox are installed correctly.
* Support for configuring backend provider.
* Internal refactorings.

### Version 0.1 (September 15, 2013)

* Initial release.