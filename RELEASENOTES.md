## Release 0.2.0
  * upgraded guava to 11.0; gson to 2.0; jquery to 1.7.1; commons-io to 2.1
  * better startup message written to stdout
  * better analysis of green jenkins jobs, so we always get the start time of the latest build
  * toying with sound-effects for doh moments
  * removed css media type for controlling desktop-mode
  * introduced fly-out menu in the top-right corner to control desktop and silent modes
  * major reworking of ant build.xml script to remove duplication and use parameters and macros properly
  * rehoused resources in a /resources sub-directory so that they do not clutter the release jar
  * adjusted the project structure to be more maven-esque, purely because this is a nice standard
  * abstracted the dependency on log4J, as it is pretty crusty, and having static refs all over is smelly
  * moved 3rd party javascript into the vendor directory
  * general refactoring and whitespace nuking

[Full changelog](https://github.com/netmelody/ci-eye/compare/v0.1.3...v0.2.0)

## Release 0.1.3
This release upgrades gson to 1.7.2 with a fix for a threading bug (gson Issue 354), upgrades guava to 10.0 (and improves caching as a result), and tidies up some naming to ensure consistency.

[Full changelog](https://github.com/netmelody/ci-eye/compare/v0.1.2...v0.1.3)

## Release 0.1.2
Jenkins concurrent builds are now represented correctly, using multiple progress bars. TeamCity external links now login as guest automatically.

[Full changelog](https://github.com/netmelody/ci-eye/compare/v0.1.1...v0.1.2)

## Release 0.1.1
Displays the current landscape name in the title bar. Correctly responds with 404 to requests for static resources that do not exist. Redirects malformed landscape URLs. Fixes "doh" mode.

[Full changelog](https://github.com/netmelody/ci-eye/compare/v0.1.0...v0.1.1)

## Release 0.1.0
Committer analysis was improved. The program now checks for new versions automatically. The welcome page was improved.
