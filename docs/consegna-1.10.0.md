# Consegna Tabata Timer 1.10.0

Il nome pubblico è **Tabata Timer**. La versione Android è **1.10.0**, codice **19**. Il sito mantiene lo stesso indirizzo sul progetto Cloudflare Pages esistente.

## Stili

**Impostazioni → Aspetto → Stile** propone Classico, Editoriale sportivo, Neo-brutalismo soft, Retro digitale e Bauhaus contemporaneo, nello stesso ordine su web e Android. Il selettore è tradotto in italiano, inglese e spagnolo. Classico è lo stile predefinito quando la preferenza è assente o sconosciuta.

Stile, tema e colore restano indipendenti. La preferenza di stile è salvata localmente e usata in entrambe le modalità. Il motore del timer, il formato degli archivi, le notifiche e gli audio esistenti restano quelli della versione precedente. Il semicerchio Bauhaus è decorativo. La barra esistente indica l’avanzamento effettivo.

Il display Retro usa il carattere DSEG7 Classic Bold 0.46 di keshikan. Il font è incluso nelle due piattaforme e disponibile offline. La licenza SIL Open Font License 1.1 accompagna entrambe le copie. [Sorgente e licenza DSEG](https://github.com/keshikan/DSEG/tree/v0.46)

## Verifiche

- Web: 102 test JavaScript, 11 suite browser, 90 combinazioni reali di stile, palette e tema. Verificati contrasto, focus, cambio di aspetto durante una pausa e allineamento su 240 schermate nei cinque stili, da 320 a 1280 px.
- Android: 54 test JVM e 22 test strumentali passati. Su emulatore API 37, tutte le 90 combinazioni hanno visualizzato il timer Studio da 1:55:00 su una sola riga, anche ripetendo la prova a 320 dp. Il cambio di stile in pausa ha mantenuto la stessa sessione e lo stesso tempo rimanente. Lint ha riportato 0 errori e 17 avvisi.
- Le prove web e Android includono anteprime di tutti e cinque gli stili in Viola, Verde chiaro e Giallo. Il display Retro si carica offline. I backup di prova restano interoperabili tra le piattaforme.
- L’aggiornamento con gli APK release da 1.9.1 a 1.10.0 è riuscito sull’emulatore. Ho ritrovato i dati di prova Workout e Studio, la cronologia e la lingua precedente. Classico era selezionato per l’archivio privo della nuova preferenza. Rimane da provare l’APK su un telefono fisico.

## Pacchetti

| File | Byte | SHA-256 |
| --- | ---: | --- |
| Tabata Timer.apk | 8.428.313 | beff6b67d4b14a0df10675013cec44ebff4f763eaea39685ab1e7e5c43ff8456 |
| Tabata Timer.zip | 420.172 | c8b41a119d74bbbc39bdbad57da90d7eac4faa08b5120a72610c1dec9706a6de |

I pacchetti si trovano nella cartella di consegna locale del 25 novembre 2026. Lo ZIP contiene 50 file, tra cui ads.txt, _headers e la nuova cache statica. L’APK è una build release con debug disattivato e usa il certificato di sviluppo delle versioni già consegnate per permettere l’aggiornamento diretto. L’APK precedente è stato conservato.

## Installazione

1. **Cloudflare.** Apri **Workers & Pages → tabata-timer → Create deployment → Production**. Carica **Tabata Timer.zip** e seleziona **Save and deploy**. Dopo **Success**, apri l’indirizzo abituale e controlla la nuova voce **Stile**. Se una scheda ancora aperta usa la cache precedente, chiudi le schede di Tabata Timer e riapri il sito. [Caricamento diretto su Cloudflare Pages](https://developers.cloudflare.com/pages/get-started/direct-upload/#create-a-new-deployment)
2. **Android.** Dalle Impostazioni dell’app precedente esporta una copia JSON. Apri **Tabata Timer.apk** sul telefono e scegli **Aggiorna**. Se Android lo chiede, consenti al gestore file l’installazione dell’APK. Apri l’app e prova **Impostazioni → Aspetto → Stile** con un tema e un colore già selezionati.

Il push del codice e il caricamento dei file sono operazioni separate. I pacchetti sono predisposti per aggiornare manualmente il sito e installare l’APK. Lo ZIP non attiva AdSense.

## Ricompilazione su Windows

Dal checkout applicativo, con Python, Node.js, Android SDK e JDK già installati:

~~~powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
python scripts/generate-locales.py
node --test web/tests/*.test.js
.\android\gradlew.bat -p android --no-build-cache testDebugUnitTest lintRelease assembleRelease
node scripts/interop-check.mjs
python scripts/package-cloudflare.py
~~~

Il comando web prepara lo ZIP in output/cloudflare. Il comando Android produce android/app/build/outputs/apk/release/app-release-unsigned.apk. Per una build di aggiornamento compatibile, allinealo con zipalign e firmalo con apksigner usando la stessa chiave locale delle consegne precedenti. [Firma APK](https://developer.android.com/tools/apksigner)
