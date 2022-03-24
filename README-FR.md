# SmartQueue
[![](https://jitpack.io/v/ch.swisscypher/smartqueue-api.svg)](https://jitpack.io/#ch.swisscypher/smartqueue-api)

## Installation

Copiez simplement le jar que vous pouvez trouver sur la page [Releases](https://github.com/Swisscypher/smartqueue/releases). 

## Configuration

Le fichier de base que vous trouverez se compose comme suit :
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

* **lang** (string) Définit le fichier de langue à utiliser (qui se trouve dans le dossier _lang_)
* **queues** (list) Définit une liste des files d'attente disponibles sur le serveur
  * **name** (string) Définit le nom de la file d'attente
  * **destination** (string) Définit le nom du serveur de destination de la file d'attente
  * **waiting** (int) Définit le temps d'attente en millisecondes entre l'envoi de plusieurs personnes sur le serveur
  * **need-priority** (bool) Définit si la file d'attente nécessite forcément une permission pour être rejointe
## Permissions

SmartQueue se base sur les permissions pour permettre aux joueurs d'exécuter différentes commandes, d'accéder à différentes queues, de définir leur priorités.

* `smartqueue.join.*`
  * `smartqueue.join.<queuename>` Permet d'utiliser la commande `/join <queue>` qui met automatiquement le joueur qui l'exécute à la fin de la file. Le joueur peut être rajouté à la file d'attente via l'API sans qu'il ait cette permission.
* `smartqueue.bypass.*`
  * `smartqueue.bypass.<queuename>` Permet d'utiliser la commande `/bypass <queue>` qui met automatiquement le joueur qui l'exécute au début de la file
* `smartqueue.<queuename>.priority.<integer>` Définit la priorité d'un joueur sur une queue et l'autorise à la rejoindre
* `smartqueue.toggle.*`
  * `smartqueue.toggle.<queuename>` Permet de changer l'état de la file (activée ou désactivée)
* `smartqueue.unstuck.*`
  * `smartqueue.unstuck.<queuename>` Permet d'utiliser la commande `/unstuck <queue>` qui donne un petit coup de pouce à une queue qui serait bloquée

Attention, les permissions doivent être mises au niveau du serveur Bungeecord !
## Use SmartQueue API as a dependency

Faites bien attention à replacer `Tag` par la dernière version disponible (voir le badge JitPack).

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

## DeluxeHub action

Depuis la version 1.1.14, nous supportons [DeluxeHub 3](https://www.spigotmc.org/resources/deluxehub-3-professional-hub-management.49425/).
Ci-dessous se trouve un exemple pour rejoindre une file d'attente depuis une action dans DeluxeHub, ici le joueur rejoint la file d'attente nommée `faction .`

```yaml
  actions:
    - '[SMARTQUEUE] faction'
```

## Support

Le support est disponible en français et en anglais sur [Discord](https://discord.gg/UTr4frTxMS).

## Licence

Tout le code (sauf celui de l'API) est sous licence GPL v3.
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

Le code de l'API est sous licence Apache 2.0.

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