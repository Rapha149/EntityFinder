# EntityFinder

1. [Deutsch](#deutsch)
   - [Anwendung](#anwendung)
   - [NBT-Eingabe](#nbt-eingabe)
   - [Output-Format](#output-format)
   - [Wichtig](#wichtig)
   - [Systemvoraussetzungen](#systemvorraussetzungen)
   - [Referenzen](#referenzen)
2. [English](#english)
   - [Usage](#usage)
   - [NBT input](#nbt-input)
   - [Output format](#output-format-1)
   - [Important](#important)
   - [System requirements](#system-requirements)
   - [References](#references)
3. [Konsole öffnen / Open console](#konsole-öffnen--open-console)
4. [Download](#download)

## Deutsch
EntityFinder ist ein Programm, mit dem man Entitäten und deren NBT-Daten von einer Minecraft-Welt finden kann. Dies ist nützlich, wenn bspw. zu viele Entitäten gespawnt wurden und die Welt nicht mehr lädt, denn das Programm kann außerdem auch Entitäten entfernen.
### Anwendung
Einfach die Jar-Datei in den Welt-Ordner downloaden. Dann im Welt-Ordner ein Terminal öffnen; unter Windows geht das ganz einfach, indem man auf den Pfad klickt und `cmd` reinschreibt. In die jetzt geöffnete Konsole schreibt man den Befehl `java -jar EntityFinder.jar`. Man kann die Sprache des Programms mit einem Argument auswählen, zum Beispiel: `java -jar EntityFinder.jar de`. Verfügbare Sprachen sind Deutsch (de, ger, german) und Englisch (en, english). Ab der Eingabe des Befehls leitet das Programm einen durch den weiteren Ablauf.
### NBT-Eingabe
Das NBT-Format ist wie in Minecraft das JSON-Format. Aber beim Eintragen der NBT-Daten bitte nicht wie in Minecraft Buchstaben hinten an Zahlen schreiben (wie bspw. `1b`), sondern in diesem Fall nur `1` oder `true`. Es kann auch nur die ID der Entität angegeben werden.
#### Beispiele
- `pig` - Für alle Schweine
- `{id:"minecraft:sheep",Tags:["test"]}` - Für alle Schafe, die den Tag *test* haben.
- `{UUID:[-967100684,836652890,-1255466304,1448748218]}` - Für die Entität mit der angegebenen UUID (UUID Format der 1.16+)
- `{UUIDMost:-3730637873437454622,UUIDLeast:-8609514179256729820}` - Für die Entität mit der angegebenen UUID (UUID Format bis zur 1.15).
### Output-Format
Beim Finden von Entitäten werden diese in einer Datei gespeichert.  
Das Format ist: `[Typ] Welt / X Y Z: NBT-Daten`. Verschiedene Entitäten werden durch eine Zeile Freiraum getrennt.
### Wichtig
Dieses Programm nicht benutzen, während die Welt in Minecraft geöffnet ist. Beim Speichern durch Minecraft werden Änderungen von diesem Programm nicht übernommen.
### Systemvorraussetzungen
Java 8 oder höher. [Java-Download](https://java.com/de/)
### Referenzen
- Als API habe ich die [NBT-API](https://github.com/Querz/NBT) von [Querz](https://github.com/Querz/) benutzt.

## English
EntityFinder is a program to find entities and their NBT data from a Minecraft world. This is useful if, for example, too many entities have been spawned and the world no longer loads. The program can also remove entities.
### Usage
Just download the jar file into the world folder. Then open a terminal in the world folder; in Windows this can be done very easy by clicking on the path and typing `cmd` into it. After that write the command `java -jar EntityFinder.jar` into the console. You can select the language of the program with an argument, for example: `java -jar EntityFinder.jar en`. Available languages are German (de, ger, german) and English (en, english). After entering the command, the program guides you through the rest of the process.
### NBT Input
The NBT format is like in Minecraft the JSON format. But when entering the NBT data please do not append letters behind numbers (like `1b`) as you would do in Minecraft. In this case just write `1` or `true`. You can also just enter the entity id.
#### Beispiele
- `pig` - For all pigs.
- `{id:"minecraft:sheep",Tags:["test"]}` - For all sheep that have the tag *test*.
- `{UUID:[-967100684,836652890,-1255466304,1448748218]}` - For the entity with the given UUID (UUID format 1.16+).
- `{UUIDMost:-3730637873437454622,UUIDLeast:-8609514179256729820}` - For the entity with the given UUID (UUID format up to 1.15).
### Output format
When Commmand Blocks are found, they are stored in a file.  
The format is: `[Type] World / X Y Z: NBT`. Different entities are separated by a line of free space.
### Important
Do not use this program while the world is open in Minecraft. When saving the world in Minecraft, changes applied by this program will not be saved.
### System requirements
Java 8 or higher. [Java download](https://java.com/en/)
### References
- As an API I used the [NBT-API](https://github.com/Querz/NBT) by [Querz](https://github.com/Querz/).

## Konsole öffnen / Open console
![cmd tutorial](https://github.com/Rapha149/EntityFinder/blob/main/cmd.gif)

## Download
[EntityFinder.jar](https://www.dropbox.com/s/z5229j375xkv6z3/EntityFinder.jar?dl=1)
