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
- **Integrowanie Api:** Rest Api + Okhttp + JSON
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

<img width="1472" height="3111" alt="Screenshot_20261031_163621" src="https://github.com/user-attachments/assets/c8ab1f21-d2ae-47c6-9a44-b556b8549c48" />
<img width="1472" height="3111" alt="Screenshot_20261031_163612" src="https://github.com/user-attachments/assets/399c71d8-1743-47db-adc7-bda78612d1d4" />
<img width="1472" height="3111" alt="Screenshot_20261031_163551" src="https://github.com/user-attachments/assets/cf218cb5-9e5b-4038-88e9-a742d4f81d80" />
<img width="1472" height="3111" alt="Screenshot_20261031_163544" src="https://github.com/user-attachments/assets/31e8f1c1-50d9-482b-8d23-0949f51afa12" />
<img width="1472" height="3111" alt="Screenshot_20261031_163535" src="https://github.com/user-attachments/assets/2fcdadde-4dd5-4af5-9287-d96cf383f20f" />
<img width="1472" height="3111" alt="Screenshot_20261031_163430" src="https://github.com/user-attachments/assets/1098a070-a82b-470e-abb8-c5b5ae18a2b9" />
<img width="1472" height="3111" alt="Screenshot_20261031_163416" src="https://github.com/user-attachments/assets/3aa1e828-7385-48ec-affc-c0b2d15e3838" />
<img width="1472" height="3111" alt="Screenshot_20261031_163758" src="https://github.com/user-attachments/assets/83cccc94-3235-4065-935a-8c4c18be67ee" />
<img width="1472" height="3111" alt="Screenshot_20261031_163752" src="https://github.com/user-attachments/assets/4632b62d-8423-4e37-9919-875fcee8013d" />
<img width="1472" height="3111" alt="Screenshot_20261031_163714" src="https://github.com/user-attachments/assets/51d1ab3a-8062-4c20-af8c-fbec90239ee2" />
<img width="1472" height="3111" alt="Screenshot_20261031_163707" src="https://github.com/user-attachments/assets/88d1e807-7c8f-4eec-a458-34e79dc3a43b" />
<img width="1472" height="3111" alt="Screenshot_20261031_163656" src="https://github.com/user-attachments/assets/b849d892-ef39-4491-b52b-283cfd8148e4" />
<img width="1472" height="3111" alt="Screenshot_20261031_163651" src="https://github.com/user-attachments/assets/3f3cc579-214b-4d90-a6ba-d22b97ffb390" />
<img width="1472" height="3111" alt="Screenshot_20261031_163644" src="https://github.com/user-attachments/assets/89a034ec-1ed8-43a1-96f2-2a7c4e968530" />
<img width="1472" height="3111" alt="Screenshot_20261031_163636" src="https://github.com/user-attachments/assets/96815244-49dc-4de7-9479-9c1ce20ac35e" />
<img width="1472" height="3111" alt="Screenshot_20261031_163629" src="https://github.com/user-attachments/assets/a69d3ba4-0555-45ba-9c66-6665dc6ce2d3" />

