# Mobil alkalmazásfejlesztés projektmunka - V4PUDX
Vizóra alkalmazás

## 1. mérföldkő pontozás segitség:
- Fordítási és Futtatási hiba sincs benne (nálam :D)

- Firebase Autentikáció megvalósítva a ``RegisterActivity``-be és ``LoginActivity``-be látható. A bejelentkezett felhasználó a ``HomeActivity``-re kerül, ahol ha minden igaz akkor a neve is látszódik és ki tud jelentkezni.

- Beviteli mezők megfelelően beállítva: ``app/src/main/res/layout/activity_login.xml`` és ``app/src/main/res/layout/activity_register.xml``

- ConstraintLayout, RelativeLayout, LinearLayout is van használva pl mind három látható itt: ``app/src/main/res/layout/activity_login.xml``

- Reszponzív: Van sima layout, landscape és tablet is ez alapján láthatók módosítások, illetve scroll view miatt ha valami kilóg akkor scrollolható is a screen

- Animáció: ``app/src/main/res/anim/slide_in_top.xml`` ez pedig alkalmazva van többek között a ``MainActivity`` 25-29 soraiban, vagy a ``HomeActivity`` 49-53 soraiban

- Intentek: Minden Activity között intentel van megoldva a navigáció és minden activity el is érhető.

- Szubjektív pontozás: 3pont köszi :)