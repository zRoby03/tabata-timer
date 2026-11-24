# Tabata Timer 1.9.1

Revisione interna Android 1.9.1, codice 18. Il nome pubblico è **Tabata Timer**. L’APK usa lo stesso identificatore e certificato della 1.9.0.

## Modifiche

- **Import ed export.** Web e Android limitano gli archivi a 5 MiB e a 64 livelli di annidamento. I file con codifica UTF-8 non valida vengono rifiutati. L’importazione Android legge e valida fuori dal thread dell’interfaccia. Sul web, se il browser rifiuta la scrittura, l’archivio attivo resta quello precedente. I testi vengono trattati come dati e la finestra di conferma dà il focus ad Annulla.
- **CSV.** L’apostrofo iniziale protegge le celle di testo che iniziano con caratteri da formula, comprese le varianti a larghezza piena, gli spazi prima del prefisso e tabulazioni e a capo iniziali. Virgole, virgolette, testi multilinea e numeri conservano la struttura CSV. Usa il JSON per scambiare dati tra piattaforme. Alcuni fogli di calcolo possono cambiare l’interpretazione dopo un salvataggio o una nuova importazione del CSV. [OWASP CSV Injection](https://owasp.org/www-community/attacks/CSV_Injection)
- **Web.** La barra verticale ha spazio riservato su entrambi i lati quando il browser supporta la proprietà CSS. Il confronto prima e dopo su Edge a 1280 px passa da 7,5 px di spostamento a 0. Il file _headers applica CSP HTTP, blocco della visualizzazione in frame, limitazioni per fotocamera, microfono e geolocalizzazione, HSTS sull’host e le regole esistenti per cache e referrer. La cache statica tabata-static-v1.9.1 resta separata dai dati personali. [Regole Cloudflare Pages](https://developers.cloudflare.com/pages/configuration/headers/)
- **Studio.** Italiano «Timer Pomodoro. Imposta i tempi e premi Avvia.», inglese «Pomodoro timer. Set your times and press Start.», spagnolo «Temporizador Pomodoro. Configura los tiempos y pulsa Iniciar.».
- **Privacy.** Le Impostazioni cancellano programmi e attività dei due modi dopo conferma. Preferenze e file esportati restano disponibili. Il browser può eliminare anche preferenze e cache quando si puliscono i dati del sito. L’informativa descrive le richieste all’hosting. Le prove sul pacchetto non hanno osservato cookie dell’app o richieste a terzi durante timer e impostazioni. Il sito pubblico invia anche gli header di diagnostica propri di Cloudflare.
- **Pulizia.** Rimossi lo script scripts/package-project.py, che generava un vecchio archivio generico, e il metodo Android della demo già rimossa dall’interfaccia. Lo ZIP usa la lista delle risorse del service worker, più sw.js, _headers e ads.txt. Test, file per Node, configurazioni private e output di compilazione restano fuori.

## Audit e punti aperti

| Controllo | Esito |
| --- | --- |
| RLS, ruoli, accesso, recupero password, rate limit delle registrazioni | Architettura locale senza database remoto, account o API applicative. Le due modalità sono preferenze locali. |
| Import, errori di archiviazione, escaping e CSV | Correzioni e regressioni locali descritte sopra. Room usa le operazioni parametrizzate e la sostituzione in transazione. |
| Segreti e file privati | Scansione Git locale e verifica dei file effettivi di APK e ZIP. Il file ads.txt è pubblico per funzione. La configurazione AdSense fuori da web non entra nello ZIP. |
| Pubblicità, profilazione e consensi | AdSense è inattivo nel codice distribuito. Il pacchetto non aggiunge banner, CMP o strumenti analitici. Qualsiasi attivazione futura richiede una verifica sull’integrazione effettiva e sui requisiti locali e di Google. [Garante](https://www.garanteprivacy.it/home/docweb/-/docweb-display/docweb/9677876) · [Requisiti Google CMP](https://support.google.com/adsense/answer/13554116?hl=it) |
| Accessibilità | Verificati nomi dei controlli, link al contenuto, focus della finestra di importazione, contrasto testo/pulsanti nelle 18 combinazioni e layout fino a 320 px. Il valore visuale del timer ha annunci disattivati, la fase usa un aggiornamento annunciabile quando cambia. Servono ancora prove manuali complete con screen reader e dispositivi fisici. [WCAG 2.2](https://www.w3.org/WAI/WCAG22/quickref/) |
| Reel | I due video non sono stati recuperabili da Instagram durante la verifica. I controlli applicati derivano dai requisiti scritti. |
| Risorse | L’icona deriva da assets/branding, la tipografia usa font di sistema. Le note, il fischio e il finale storico sono sintetizzati dal generatore del progetto. I diritti del sospiro YouTube e dei tre DING forniti restano da documentare. La directory locale Proposte audio contiene quattro alternative originali con durata e picco comparabili, fuori dai pacchetti. Il volume percepito richiede ascolto. [Inventario audio](../assets/audio/README.md) |

### Toolchain Android

AGP 9.3.1, Gradle 9.5.0 con checksum della distribuzione, Kotlin e plugin Compose 2.4.21, KSP 2.3.14. JDK 17 o superiore compatibile con Gradle, con target Java 17 come prima. Il plugin Kotlin esplicito allinea la versione effettiva usata da AGP. Le dipendenze dichiarate per l’app restano le stesse. [Compatibilità Kotlin](https://kotlinlang.org/docs/gradle-configure-project.html) · [AGP 9.3](https://developer.android.com/build/releases/agp-9-3-0-release-notes)

OSV ha interrogato 66 coordinate runtime senza corrispondenze e 111 coordinate build. Il nuovo Kotlin non riceve più la segnalazione sulla cache di compilazione del precedente audit. Rimangono **10 avvisi su 6 componenti di build**.

| Componente build | Versione risolta | Avvisi e contesto |
| --- | --- | --- |
| Guava | 33.4.0-jre | [GHSA-xxph-c9ww-hj94](https://github.com/advisories/GHSA-xxph-c9ww-hj94), serializzazione |
| Commons Lang | 3.16.0 | [GHSA-j288-q9x7-2f5v](https://github.com/advisories/GHSA-j288-q9x7-2f5v), ricorsione su input lungo |
| jose4j | 0.9.5 | [GHSA-3677-xxcr-wjqv](https://github.com/advisories/GHSA-3677-xxcr-wjqv), input JWE |
| Bouncy Castle PKIX | 1.79 | [GHSA-wg6q-6289-32hp](https://github.com/advisories/GHSA-wg6q-6289-32hp), crittografia |
| Bouncy Castle provider | 1.79 | [GHSA-574f-3g2m-x479](https://github.com/advisories/GHSA-574f-3g2m-x479), [GHSA-9pwp-9qqc-pr26](https://github.com/advisories/GHSA-9pwp-9qqc-pr26), [GHSA-c3fc-8qff-9hwx](https://github.com/advisories/GHSA-c3fc-8qff-9hwx), [GHSA-p93r-85wp-75v3](https://github.com/advisories/GHSA-p93r-85wp-75v3), [GHSA-qp49-qgx5-5m26](https://github.com/advisories/GHSA-qp49-qgx5-5m26), input e operazioni crittografiche |
| JDOM | 2.0.6 | [GHSA-2363-cqg2-863c](https://github.com/advisories/GHSA-2363-cqg2-863c), parsing XML |

Gli avvisi indicano rischi per la compilazione se la toolchain riceve input non attendibile. Le versioni segnalate sono fuori dal classpath runtime. La cache Gradle era disabilitata per questa compilazione e il progetto è privo di cache remota. Le sei versioni arrivano dai plugin per cui è stata verificata la compatibilità. La verifica si è svolta in locale con dati sintetici, oltre a richieste GET ordinarie al sito pubblico. [API OSV](https://google.github.io/osv.dev/api/)

## Verifiche

- JavaScript **101 passati**, JVM **52 passati**, Android **20 passati** su emulatore API 37. Lint release **0 errori, 18 avvisi**. I test Android coprono background, schermo spento, controlli della notifica, volume attenuato con audio esterno di prova e tutte le traduzioni Studio.
- Browser: 10 suite funzionali e mirate, incluse **48 combinazioni per il layout**, 320–1280 px. Verificati audio locale, 13 WAV offline, esportazione, import annullato, sostituzione, cancellazione con conferma, focus, schermo intero e controllo per lo schermo acceso. La disponibilità effettiva del blocco schermo dipende da browser e dispositivo. Quattro scambi di dati Kotlin/JS producono gli stessi modelli e durate.
- Il manifest dell’APK conferma release, servizio timer non esportato, nessun permesso Internet, backup automatici esclusi. La firma è valida e allineata. Il certificato SHA-256 è e48adbb6e73ab44cc1645addbba4e5f8f6d3d82f28d28dd24e5d54709ec3dece.

## Pacchetti

| File | Byte | SHA-256 |
| --- | ---: | --- |
| Tabata Timer.apk | 8.399.516 | c0a9ec21a09714491dcf3f03a609e7012f8cf77e133c003d3ad0e56688e2c49a |
| Tabata Timer.zip | 411.077 | 4b8ee045ffc9f0067c0c0d427a92a79c35ad07763953c5b3899c37c87306b66f |

L’APK cresce di 53.248 byte rispetto alla 1.9.0, lo ZIP di 1.019 byte rispetto al precedente pacchetto Cloudflare. L’aggiornamento release da 17 a 18 è stato completato su emulatore senza disinstallare. Verificati per confronto dei dati un allenamento Workout, un programma Studio, una sessione in cronologia e le preferenze di lingua, aspetto e modalità. L’APK 1.9.0 consegnato conserva il suo hash precedente.

I WAV originali distribuiti sono gli stessi della revisione precedente. I diritti sui quattro suoni di provenienza esterna restano da chiarire prima della distribuzione pubblica. Il sorgente applicativo è destinato al ramo codex/workout-recuperi-audio. Il caricamento su Cloudflare e la distribuzione dell’APK restano operazioni separate.

## Ricostruzione e installazione

Dalla radice del branch applicativo su Windows, con Python, Node.js, Android SDK e Android Studio:

~~~powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
python scripts/generate-locales.py
node --test web/tests/*.test.js
.\android\gradlew.bat -p android --no-build-cache testDebugUnitTest lintRelease assembleRelease
node scripts/interop-check.mjs
python scripts/package-cloudflare.py
~~~

L’APK non firmato si trova in android/app/build/outputs/apk/release/app-release-unsigned.apk. Usa zipalign -P 16 -f 4 e apksigner, con la chiave locale già utilizzata per gli APK precedenti e disponibile soltanto nella macchina di build. La firma di sviluppo è stata mantenuta per consentire l’aggiornamento diretto. Per passare a una chiave di produzione occorre pianificare la rotazione o la migrazione. Android 9 e successivi supportano la rotazione v3 con condizioni diverse tra distribuzioni. Sui dispositivi supportati 26–27 va considerato il firmatario precedente e verificata l’intera sequenza prima della distribuzione. [Firma APK](https://developer.android.com/tools/apksigner) · [Rotazione](https://source.android.com/docs/security/features/apksigning/v3-1)

1. **Android.** Nella versione già installata esporta una copia JSON. Apri il nuovo APK ricevuto privatamente, abilita l’installazione per il gestore file se Android lo richiede e scegli *Aggiorna*. Lascia installata la versione precedente durante l’aggiornamento.
2. **Cloudflare.** Nel progetto Pages esistente apri *Create deployment*, seleziona *Production* e carica lo ZIP. Apri il sito dopo l’esito *Success* e controlla cache, audio e intestazioni. Il dominio resta lo stesso. Questa consegna prepara il pacchetto per il caricamento manuale.
