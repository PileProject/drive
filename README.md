Drive
=====
[![Build Status](https://travis-ci.org/PileProject/drive.svg?branch=master)](https://travis-ci.org/PileProject/drive)

`Drive` provides children a gate to learn programming
with visual programming and a real robot.

### Available robots
We currently support 1 robot and plan to support more robots.
We are going to implement them as [product flavors][ProductFlavor]: `nxt`, `ev3` and `pile`.

Released:
- nxt: [LEGO MINDSTORMS NXT](https://shop.lego.com/en-US/LEGO-MINDSTORMS-NXT-2-0-8547)

Developing:
- ev3: [LEGO MINDSTORMS EV3](https://shop.lego.com/en-US/LEGO-MINDSTORMS-EV3-31313)
- pile: [PILE Original Robot](http://pileproject.com/en.html)


## Screen Shoots
The title page of NxtDrive:

<img src="https://raw.githubusercontent.com/PileProject/drive/16ab440f7fb81c7cce69498c244dd880aaf2bf8d/docs/images/title.png" alt="title" width="250" >
<!--<img src="url" alt="alt text" width="whatever" height="whatever">-->

In the programming page, users can choose commands from the block list and put them to make a program:

<img src="https://raw.githubusercontent.com/PileProject/drive/16ab440f7fb81c7cce69498c244dd880aaf2bf8d/docs/images/blocks.png" alt="block-list" width="250">
<img src="https://raw.githubusercontent.com/PileProject/drive/16ab440f7fb81c7cce69498c244dd880aaf2bf8d/docs/images/programming.png" alt="programming" width="250">

In the setting page, users can make some configurations:

<img src="https://raw.githubusercontent.com/PileProject/drive/16ab440f7fb81c7cce69498c244dd880aaf2bf8d/docs/images/setting.png" alt="setting" width="250">


## Development setup
Please fork this repository, make a new branch, modify it and send Pull Request.


### Build
Linux & OS X:

```sh
./gradlew build
```

Windows:

```sh
./gradlew.bat build
```


### Run Test and Generate Javadoc
Linux & OS X:

```sh
./gradlew check
./gradlew connectedCheck # do this after launching an emulator
./gradlew generateNxtReleaseJavadoc
```

Windows:

```sh
./gradlew.bat check
./gradlew.bat connectedCheck # do this after launching an emulator
./gradlew.bat generateNxtReleaseJavadoc
```

Test reports will be generated in build/reports/tests (See index.html).
Javadocs will be generated in build/docs/javadoc (See index.html).


### checkLicenses
This repository uses [a plugin](https://github.com/cookpad/license-tools-plugin) to maintain the copyright notice
and it does `checkLicenses` task automatically in CI.
If you add a new plugin and get an error in the process,
please follow [the instruction of the plugin](https://github.com/cookpad/license-tools-plugin#how-to-use),
and add a new `license.html` in your Pull Request.


## Release History
* 1.0.0
	* First: Release NxtDrive.

## Meta
[PILE Project](http://pileproject.com/en.html)
– [@pileproject](https://twitter.com/pileproject) - dev@pileproject.com

Let's discuss anything on our [Mailing List](https://groups.google.com/forum/#!forum/pile-dev)!

Distributed under the Apache License, Version 2.0. See ``LICENSE`` for more information.

[drive]: https://github.com/PileProject/drive
[ICommunicator]: https://github.com/PileProject/drivecommand/blob/develop/src/main/java/com/pileproject/drivecommand/model/com/ICommunicator.java
[BluetoothCommunicator]: https://github.com/PileProject/drive/blob/develop/app/src/main/java/com/pileproject/drive/comm/BluetoothCommunicator.java
[MachineBase]: https://github.com/PileProject/drivecommand/blob/develop/src/main/java/com/pileproject/drivecommand/machine/MachineBase.java
[NewMachine]: https://github.com/PileProject/drive/blob/develop/app/src/nxt/java/com/pileproject/drive/execution/NxtExecutionActivity.java#L38
[NxtMachine]: https://github.com/PileProject/drivecommand/blob/develop/src/main/java/com/pileproject/drivecommand/model/nxt/NxtMachine.java
[model]: https://github.com/PileProject/drivecommand/tree/develop/src/main/java/com/pileproject/drivecommand/model
[ev3]: https://github.com/PileProject/drivecommand/tree/develop/src/main/java/com/pileproject/drivecommand/model/ev3
[nxt]: https://github.com/PileProject/drivecommand/tree/develop/src/main/java/com/pileproject/drivecommand/model/nxt
[pile]: https://github.com/PileProject/drivecommand/tree/develop/src/main/java/com/pileproject/drivecommand/model/pile
[ProductFlavor]: https://developer.android.com/studio/build/build-variants.html#product-flavors]
