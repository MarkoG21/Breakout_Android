# Mobile Games Programming Labor - Breakout

Programmierung des 80er Jahre Klassikers für Android.

## Beschreibung

Dieses Spiel ist im Rahmen einer Semesterlaborarbeit an der Hochschule Offenburg entstanden. Zur Steuerung werden die Beschleunigungssensoren des Smartphones verwendet.

## Screenshots
![Screenshot_2021-11-02-13-29-30-426_com example breakout_marko_guastella](https://user-images.githubusercontent.com/82582800/139859860-476c590e-4589-44a0-aaf9-b3fcb34c452a.jpg)


![Screenshot_2021-11-02-13-30-04-892_com example breakout_marko_guastella](https://user-images.githubusercontent.com/82582800/139859931-a7507b9a-f874-4460-b330-3ddff72833bb.jpg)

![Screenshot_2021-11-02-13-29-38-465_com example breakout_marko_guastella](https://user-images.githubusercontent.com/82582800/139860017-84f079b3-b63b-4ba7-a0cf-1421a4b94c6f.jpg)
## Installation
Lade die aktuellste Version aus dem "releases" Tab herunter.
Bei der Installation müssen ggf. Sicherheitseinstellungen deaktiviert werden.
## Wichtige Hinweise:

### Dependencies
* In build.gradle(Project...) unter allprojects -> repositorys einfügen.
```
allprojects {
    repositories {
        google()
        jcenter()
       maven { url 'https://jitpack.io' }
    }
}
```

* In build.gradle(Module...) unter dependencies einfügen.
```
implementation 'com.github.mohammadatif:Animatoo:master'
```

## Quellenverweise:

* [AtifSayings] - (https://github.com/AtifSayings/Animatoo)

