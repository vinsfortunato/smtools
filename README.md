![GUI](assets/logo.png?raw=true)

# SMTools [![Build Status](https://travis-ci.com/vinsfortunato/smtools.svg?branch=master)](https://travis-ci.com/vinsfortunato/smtools)
> A collection of tools for stepmania

![GUI](assets/screenshot.png?raw=true)

## Table of Contents
* [Features](#features)
* [Installation](#installation)
* [How to build](#how-to-build)
* [License](#license)
* [Acknowledgements](#acknowledgements)

## Features
- Offset incrementer: Increment/Decrement the offset of a set of SIM files by a given amount

**Upcoming**
- Set Banner and Background: Set a given banner/background to a set of SIM files

## Installation
The application is provided as an executable JAR file in the release package and
requires Java 8 (or greater) to be installed into the operating system.

**Installation procedure**
1. Install required Java 8 (or greater)
2. Download the latest release from [here](https://github.com/vinsfortunato/smtools/releases)
3. Unpack the release archive
4. Execute SMTools.jar by clicking on it. Alternatively you can run it from shell with the following commands
```shell
cd <path-to-release-dir>
java -jar SMTools-<version>.jar
```
where ```<path-to-release-dir>``` is the path to the extracted release directory and ```<version>``` is the version of the application.

## How to build
To build the project you need Java JDK 11 (or greater) to be installed on your system.  
Here is a list of shell commands to clone, build and operating with the project:

**Clone repository**
```shell
clone https://github.com/vinsfortunato/smtools/
```

**Build project**
```shell
gradlew build
```

**Generate JAR**
```shell
gradlew assemble
```
Will generate the application executable JAR inside directory /build/libs relative to project root

**Run application**
```shell
gradlew run
```

## License

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
- **[Apache 2.0 License](https://opensource.org/licenses/Apache-2.0)**

## Acknowledgements
- Marco Ruggiero - for the logo
