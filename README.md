# QrCode Scanner

Natywna aplikacja Android do skanowania i generowania kodów QR / kodów kreskowych, 
napisana w Kotlinie z wykorzystaniem architektury MVVM.

## Funkcjonalności

- Skanowanie kodów QR i kodów kreskowych przy użyciu aparatu (CameraX + ML Kit)
- Generowanie własnych kodów QR z różnymi typami danych (tekst, link, itd.)
- Historia zeskanowanych i utworzonych kodów (zapisywana lokalnie)
- Profil użytkownika z wyborem motywu (jasny / ciemny) i języka aplikacji
- Wsparcie wielu języków: angielski, polski, rosyjski, ukraiński, niemiecki, 
  francuski, hiszpański, portugalski (BR), arabski, hindi, japoński, koreański

## Stack technologiczny

- **Kotlin**
- **Architektura:** MVVM (ViewModel + Repository), ViewModel Factory dla każdego ekranu
- **UI:** Fragmenty + Navigation Component, View Binding
- **Baza danych:** Room (historia skanów, profil użytkownika)
- **Skanowanie:** CameraX + Google ML Kit Barcode Scanning
- **Generowanie kodów QR:** ZXing (journeyapps/zxing-android-embedded)
- **Inne:** Coroutines / Flow (Room KTX), sealed interfaces / classes do zarządzania 
  stanem UI (np. stan skanowania, motyw, język)

## Struktura projektu

```
com.example.qrcodescanner
├── Activity/         — MainActivity (host nawigacji)
├── Fragments/         — ekrany: Scanner, QrCreator, QrInsertData, QrView, 
│                         QrCodeHistory, Profile
├── ViewModel/         — logika poszczególnych ekranów
├── VmFactory/         — fabryki ViewModeli (wstrzykiwanie zależności bez DI-frameworka)
├── Repository/        — warstwa dostępu do danych (Room)
├── DataBase/           — Room: DAO oraz encje (tabele)
├── DataClass/         — modele danych
├── SealedInterface/   — stany UI (skanowanie, motyw, język, akcje)
├── Handler/            — logika przetwarzania różnych typów danych QR
├── QrLogicalClass/     — generowanie kodów QR, analiza obrazu z kamery
├── RVAdapter/          — adaptery RecyclerView (historia, lista tworzonych kodów)
├── UIExtensions/       — funkcje rozszerzające do UI
└── View/               — customowe widoki (overlay skanera, bottom sheety)
```

## Wymagania

- Android Studio (najnowsza stabilna wersja)
- JDK 17+
- Min SDK: 30, Target SDK: 37
- Fizyczne urządzenie z aparatem (zalecane) lub emulator z obsługą kamery

## Jak zbudować i uruchomić lokalnie

1. Sklonuj repozytorium:
   ```
   git clone <adres-repozytorium>
   ```
2. Otwórz projekt w Android Studio (`File → Open`, wybierz folder projektu).
3. Poczekaj na zakończenie synchronizacji Gradle (pobranie zależności).
4. Podłącz urządzenie fizyczne z aparatem lub uruchom emulator z obsługą kamery
   (w Android Studio: AVD Manager → wybierz obraz z kamerą w ustawieniach).
5. Uruchom aplikację (`Run → app`).
6. Przy pierwszym uruchomieniu aplikacja poprosi o uprawnienie do aparatu — 
   należy je zaakceptować, aby korzystać ze skanowania.

## Uprawnienia

Aplikacja wymaga dostępu do **kamery** (`android.permission.CAMERA`) — wyłącznie 
do skanowania kodów QR / kodów kreskowych. Żadne inne dane nie są zbierane 
ani wysyłane na zewnątrz — cała logika działa lokalnie na urządzeniu.
