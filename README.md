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

## 2. mérföldkő pontozás segitség:
- **Rövid leírás: Login / Register után bedob a Home activityre (logout a képernyő tetején lévő ikonnal lehet, welcome text felett), itt egy táblát látsz állapotokkal ezekhez hozzá tudsz adni új állapotokat (ÚJ ÁLLAPOT MINDIG NAGYOBB MINT A LEGUTOLSÓ ÁLLAPOT, nem is enged rossz inputot a mező). A táblák 4 elemet jelenitenek meg egy oldalon, a tábla alján lévő nyilakkal lépegethetsz. Egy állapotra kattintva egy edit bottom sheet ugrik fel, itt vagy módosíthatod vagy törölheted az állapotot. A módosításnál nem lehet nagyobb az értéke az utána következőtől és kisebb sem mint az előtte lévő (a mező ezt kezeli is és nem enged rossz adatot megadni).**

- Fordítási és Futtatási hiba sincs benne (nálam :D) - A bottomsheet fragmenteknél az ide jelezhet hibát, de fordítási és futtatási hibát se okoz, ide bug gradle resync megoldja (de enélkül is nyugodtan futtatható az app)

- Firebase Autentikáció megvalósítva a ``RegisterActivity``-be és ``LoginActivity``-be látható. A bejelentkezett felhasználó a ``HomeActivity``-re kerül, ahol egyéb dolgokat kezelhet, kijelentkezni pedig az oldal tetején lévő ikonnal tud

- Adatmodell(ek) is meg vannak valósítva a ``main/java/com/urbanxd/mobilalk_kotprog/data/model/User.java``, ``main/java/com.urbanxd.mobilalk_kotprog/data/model/WaterMeter.java`` és ``main/java/com.urbanxd.mobilalk_kotprog/data/model/WaterMeterState.java``

- Legalább 4 különböző activity - pont 4 van ``main/java/com/urbanxd/mobilalk_kotprog/ui/activity/HomeActivity.java``, ``main/java/com/urbanxd/mobilalk_kotprog/ui/activity/MainActivity.java``, ``main/java/com/urbanxd/mobilalk_kotprog/ui/activity/RegisterActivity.java`` és ``main/java/com/urbanxd/mobilalk_kotprog/ui/activity/LoginActivity.java``

- Beviteli mezők megfelelően beállítva: ``app/src/main/res/layout/activity_login.xml``, ``app/src/main/res/layout/activity_register.xml``, ``app/src/main/res/layout/add_state_bottom_sheet.xml`` és ``app/src/main/res/layout/add_state_bottom_sheet.xml``

- ConstraintLayout, RelativeLayout, LinearLayout is van használva pl mind három látható itt: ``app/src/main/res/layout/activity_login.xml``

- Reszponzív: Van sima layout, landscape és tablet is ez alapján láthatók módosítások, illetve scroll view miatt ha valami kilóg akkor scrollolható is a screen, szóval elméletileg minden elérhető minden esetbe

- 2 különböző animáció: ``app/src/main/res/anim/slide_in_top.xml`` ez pedig alkalmazva van többek között a ``main/java/com/urbanxd/mobilalk_kotprog/ui/activity/MainActivity.java`` 26-27 soraiban, vagy a ``main/java/com/urbanxd/mobilalk_kotprog/ui/activity/HomeActivity.java`` 152-153 soraiban, illetve a ``app/src/main/res/anim/fade_in.xml`` fel van használva a ``main/java/com/urbanxd/mobilalk_kotprog/ui/activity/HomeActivity.java`` 80-81 soraiban

- Intentek: Minden Activity között intentel van megoldva a navigáció és minden activity el is érhető.

- Legalább 1 Lifecycle Hook: onStart van használva a ``main/java/com/urbanxd/mobilalk_kotprog/ui/components/AddStateBottomSheetDialogFragment.java`` 169. sorától. Funkcionalitása, hogy megnyitás után vár 250ms-t majd autofocus az input mezőre, illetve beállítja, hogy meg van-e már nyitva olyan bottom sheet. (Edit bottom sheetbe is benne van, csak ott nincs autofocus)

- Legalább 2 Android erőforrás használata (permissionnel): Egyik az ACCESS_NETWORK_STATE + INTERNET, másik meg a POST_NOTIFICATION. Az internet checkolva van login és register előtt Utilsba megtalálható a kód (emulatoron én úgy teszteltem, hogy repülő üzemmódba raktam). Notification küldés előtt pedig checkolva van, hogy van-e permission ha nincs akkor kér, ha épp tud (``main/java/com/urbanxd/mobilalk_kotprog/ui/activity/HomeActivity.java`` egyéb hozzátartozó dolgok Utilsba). 

- 2 különböző rendszer-, háttérszolgáltatás: Notification (``main/java/com/urbanxd/mobilalk_kotprog/ui/components/NotificationHandler.java``, küldés egyszerűsítésére Utilsba szervezve maga a küldés) és a Job Scheduler (``main/java/com/urbanxd/mobilalk_kotprog/ui/components/NotificationJobService.java``) megvalósítva. Notification használva van egy welcome üzenet küldésére regisztrálás után: ``main/java/com/urbanxd/mobilalk_kotprog/ui/activity/HomeActivity.java`` 84-90 soraiban,. Illetve a Job Schedulerbe beállított job is használja egy értesítés kiküldésére (15percenként notify ha nem történt új state felvitele az elmúlt 15 percben): ``main/java/com/urbanxd/mobilalk_kotprog/ui/activity/HomeActivity.java`` 206-216 soraiban,

- CRUD műveletek: Minden megvalósítva Repository osztályokban (A WaterMeterState-re van minden C R U D is, szóval azt érdemes megnézni és bejelölni) ``main/java/com/urbanxd/mobilalk_kotprog/data/repository/UserRepository.java``, ``main/java/com/urbanxd/mobilalk_kotprog/data/repository/WaterMeterRepository.java`` és ``main/java/com/urbanxd/mobilalk_kotprog/data/repository/WaterMeterStateRepository.java``

- 3 komplex Firestore lekérdezés: ``main/java/com/urbanxd/mobilalk_kotprog/data/repository/WaterMeterStateRepository.java``: 1.) 29-33. sor, 2.) 67-72 sor és 3.) 74-79 sor  

- Szubjektív pontozás: 8pont (remélem) köszi puszi  előre :)