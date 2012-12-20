nuxeo-marketplace-sample
========================

## Introduction

Sample Maven projects for building Nuxeo Marketplace packages.

Those sample MP (Marketplace Packages) will deploy the [nuxeo-sample-project](https://github.com/nuxeo/nuxeo-sample-project/) into a Nuxeo CAP.

See [documentation on Creating Packages for the Marketplace](http://doc.nuxeo.com/x/CwIz) for more information about the packages' content.

Marketplace packages is a powerful packaging system when extending Nuxeo.
It improves your development process (for instance by allowing you to better automate the integration tests) but it also greatly improves
the deployment process. For instance, with Marketplace packages you can easily send upgrades to your production team either as a ZIP, either
directly from the Nuxeo Admin Center (where the administrator will see a new package is available).
Moreover, the Marketplace packaging system automatically maintains a bundles registry: you don't have anymore to know what files (especially the third-party libraries)
must be added or removed to or from your server.

The assemblies are using [org.nuxeo.build:nuxeo-distribution-tools](https://github.com/nuxeo/nuxeo-distribution-tools).

The functional tests are using [org.nuxeo:nuxeo-ftest](https://github.com/nuxeo/tools-nuxeo-ftest).

## Content listing

    .
    |-- README.md
    |-- marketplace explicit                * Marketplace Package - explicit solution
    |-- marketplace noear                   * Marketplace Package - no EAR solution
    |-- marketplace                         * Marketplace Package - recommended solution
    |   |-- pom.xml
    |   |-- src
    |   |   `-- main
    |   |       |-- assemble
    |   |       |   `-- assembly.xml          ** MP assembly
    |   |       `-- resources
    |   |           |-- install
    |   |           |   `-- templates
    |   |           |       `-- sample        ** Sample configuration template
    |   |           |           `-- nuxeo.defaults
    |   |           |-- install.xml           ** MP install instructions
    |   |           `-- package.xml           ** MP description
    |   `-- target                            ** Output directory with MP ZIP
    |
    |-- ftest
    |   |-- funkload                        * Functional or performance tests with Funkload
    |   |   |-- itests.xml
    |   |   |-- pom.xml
    |   |   `-- README.txt
    |   |
    |   |-- selenium                        * Functional tests with Selenium
    |   |   |-- itests.xml
    |   |   |-- pom.xml
    |   |   `-- tests                         ** Testing suites
    |   |       `-- suite.html                ** Sample empty test suite
    |   |
    |   `-- webdriver                       * Functional tests with WebDriver
    |       |-- itests.xml
    |       |-- pom.xml
    |       `-- src
    |           `-- test                      ** Java and resources test files
    |               |-- java
    |               |   `-- com
    |               |       `-- nuxeo
    |               |           `-- functionaltests
    |               |               `-- ITLoginLogoutTest.java
    |               `-- resources
    |                   `-- log4j.xml
    `-- pom.xml

## About the three sample Marketplace Package solutions.

There are multiple ways to build a Marketplace Package. We only look here at those using Maven and
[org.nuxeo.build:nuxeo-distribution-tools](https://github.com/nuxeo/nuxeo-distribution-tools).

### Here are three different solutions, from the better one to the quicker one

 * Recommended method

   The recommended method is to build an EAR corresponding to the wanted result after the package install. Then we operate a comparison ("diff") between that product and a reference one (usually the Nuxeo CAP) and generate the Marketplace Package which will perform the wanted install. That method is the better one since it will always be up-to-date in regards to the dependencies and requirements (other bundles and third-party libraries). The drawback is it takes some build time and has a dependency on a whole Nuxeo distribution.

 * No-EAR method.

   That method is using the same principle for building the Marketplace Package as for building an EAR. It is as much reliable regarding at the dependencies as the above recommended method. The drawback is since the solution is empiric, it will likely embed useless files and generate a bigger archive.

 * Explicit method.

   That latest method is explicitly listing everything that must be packaged. Easy to write and very quick at build time, it requires more maintenance than the two above since you have to manually update the package assembly every time the dependencies change. You also risk not to see that an indirect dependency has changed and requires some changes on the third-party libraries. That method is not recommended except for specific cases or for a proof of concept.

### Applied to the sample project, here are the results from those three methods.

 * Recommended method

4 directories, 6 files, 128KB.

    recommended/
    |-- install
    |   |-- artifacts-sample.properties
    |   |-- bundles
    |   |   `-- The sample project bundle.
    |   |-- templates
    |   |   `-- sample
    |   `-- test-artifacts-sample.properties
    |-- install.xml
    `-- package.xml

The `lib` directory is empty because all required third-parties are already included in the basic Nuxeo distribution.
The `bundles` directory only contains the sample project bundle because all its dependencies are also already included in the basic distribution.

 * No-EAR method.

5 directories, 150 files, 33MB.

    noear/
    |-- install
    |   |-- artifacts-sample.properties
    |   |-- bundles
    |   |   `-- 46 bundles.
    |   |-- lib
    |   |   `-- 99 third-party libraries.
    |   |-- templates
    |   |   `-- sample
    |   `-- test-artifacts-sample.properties
    |-- install.xml
    `-- package.xml

Here, we are embedding a lot of bundles and libraries which are useless because already included in the basic Nuxeo distribution but that cannot be detected by the build process.

 * Explicit method.

5 directories, 8 files, 1,6MB.

    explicit/
    |-- install
    |   |-- bundles
    |   |   `-- The sample project bundle, explicitly listed.
    |   |-- lib
    |   |   `-- 4 third-party libraries, explicitly listed.
    |   `-- templates
    |       `-- sample
    |-- install.xml
    `-- package.xml

That solution builds a lighter package than the No-EAR method but we don't know if it will be missing some dependencies or not. The embedded bundles and libraries list must be manually maintained.

