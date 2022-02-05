# SmartQueue
[![](https://jitpack.io/v/ch.swisscypher/smartqueue-api.svg)](https://jitpack.io/#ch.swisscypher/smartqueue-api)

## Installation

Just put the jar you can find at the [Release page](https://github.com/Swisscypher/smartqueue/releases). 

## Configuration

The default configuration file is as shown below :
```yml
lang: 'en.yml'
queues:
  - name: 'queue1'
    destination: 'srv'
    waiting: 1000
    need-priority: true
  - name: 'queue2'
    destination: 'srv2'
    waiting: 2000
    need-priority: false
```

* **lang** (string) Language file used for displaying text (located inside the _lang_ directory)
* **queues** (list) List of queues available in the server
    * **name** (string) Queue name
    * **destination** (string) Server name destination of the queue
    * **waiting** (int) Waiting time in milliseconds between sending multiple players to the server
    * **need-priority** (bool) The queue needs a permission in order to be joined

## Permissions
SmartQueue uses BungeeCord permissions in order to let the players execute various commands, access queues and define their priority inside queues.

* `smartqueue.join.*`
  * `smartqueue.join.<queuename>` Allow to execute the `/join <queue>` command that add the player calls it at the last place of the queue. The player can be added to any queue by the API regardless of this permission.
* `smartqueue.bypass.*`
    * `smartqueue.bypass.<queuename>` Allow to execute the `/bypass <queue>` command that add the player who calls it at the first place of the queue
* `smartqueue.<queuename>.priority.<integer>` Set the player priority for a given queue and allow him to join it
* `smartqueue.toggle.*`
    * `smartqueue.toggle.<queuename>` Allow to change the state of a given queue (enabled/disabled)
* `smartqueue.unstuck.*`
  * `smartqueue.unstuck.<queuename>` Allow to execute the `/unstuck <queue>` command that give a little push to the queue if it's locked
  
Warining, the permissions need to be set on the BungeeCord server !
## Use SmartQueue API as a dependency

Be aware to replace `Tag` with the latest available version (see the JitPack badge).

### Gradle

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
```

```groovy
dependencies {
    compileOnly group: 'ch.swisscypher', name: 'smartqueue-api', version: 'Tag'
}
```

### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>ch.swisscypher</groupId>
        <artifactId>smartqueue-api</artifactId>
        <version>Tag</version>
    </dependency>
</dependencies>
```


## Support

Support is available in french and english on [Discord](https://discord.gg/BYWrPX7erx).

## Licence

All the code (except the API) is licensed under GPL v3.
```
SmartQueue: Minecraft plugin implementing a queue system.
Copyright (C) 2021-2022 Zayceur (contact@zayceur.ch)

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, version 3 of the License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
```

The API code is licensed under Apache 2.0.

```
Copyright 2021-2022 Zayceur (contact@zayceur.ch)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```