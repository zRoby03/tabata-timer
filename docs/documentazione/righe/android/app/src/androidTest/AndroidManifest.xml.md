# android/app/src/androidTest/AndroidManifest.xml

Prove di regressione in AndroidManifest.xml.

Le asserzioni verificano il comportamento; fixture, clock e servizi di prova non sono funzionalità aggiunte al prodotto.
I numeri si riferiscono al sorgente originale; il collegamento apre esattamente quella riga. I commenti DOC nel branch sono aggiunte didattiche.

## Ogni riga

| Riga originale | Codice (estratto) | Spiegazione |
| ---: | --- | --- |
| [1](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/androidTest/AndroidManifest.xml#L1) | <code>&lt;manifest xmlns:android=&quot;http://schemas.android.com/apk/res/android&quot;&gt;</code> | Definisce la struttura dichiarativa manifest nel documento: Prove di regressione in AndroidManifest.xml. |
| [2](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/androidTest/AndroidManifest.xml#L2) | <code>&lt;uses-permission android:name=&quot;android.permission.FOREGROUND_SERVICE&quot; /&gt;</code> | Dichiara il permesso Android riportato qui; la richiesta runtime delle notifiche è gestita nell’Activity, mentre il manifest non concede Internet. |
| [3](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/androidTest/AndroidManifest.xml#L3) | <code>&lt;uses-permission android:name=&quot;android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK&quot; /&gt;</code> | Dichiara il permesso Android riportato qui; la richiesta runtime delle notifiche è gestita nell’Activity, mentre il manifest non concede Internet. |
| [4](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/androidTest/AndroidManifest.xml#L4) | <code>&lt;application&gt;</code> | Dichiara componenti e attributi Android: application. I nomi collegano il manifest alle classi del progetto. |
| [5](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/androidTest/AndroidManifest.xml#L5) | <code>&lt;!-- Separate test APK/UID: a local music player, never included in the delivered app. --&gt;</code> | Commento originale: conserva la motivazione o l’istruzione già presente nel sorgente. Separate test APK/UID: a local music player, never included in the delivered app. -- &gt; |
| [6](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/androidTest/AndroidManifest.xml#L6) | <code>&lt;service android:name=&quot;it.tabatastudio.MediaPlaybackProbeService&quot; android:exported=&quot;true&quot; android:foregroundServiceType=&quot;mediaPlayback&quot; /&gt;</code> | Dichiara componenti e attributi Android: service. I nomi collegano il manifest alle classi del progetto. |
| [7](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/androidTest/AndroidManifest.xml#L7) | <code>&lt;/application&gt;</code> | Dichiara componenti e attributi Android: application. I nomi collegano il manifest alle classi del progetto. |
| [8](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/android/app/src/androidTest/AndroidManifest.xml#L8) | <code>&lt;/manifest&gt;</code> | Definisce la struttura dichiarativa manifest nel documento: Prove di regressione in AndroidManifest.xml. |
