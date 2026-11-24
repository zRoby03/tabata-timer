# Tabata Timer — verifica di sicurezza del 24 novembre 2026

Prodotto verificato: commit `a7bf376`, APK 1.9.0. La verifica riguarda il checkout applicativo `codex/workout-recuperi-audio`, l’APK consegnato e richieste di sola lettura al sito pubblico. Il sito pubblico contiene ancora la revisione precedente; non va confuso con il nuovo codice locale/GitHub.

## Esito

Le suite automatiche passano. L’audit individua tuttavia punti da correggere o rafforzare: segnalazioni sulle dipendenze degli strumenti di compilazione, protezione CSV incompleta, firma di sviluppo e intestazioni web. Non è corretto dichiarare il progetto completamente sicuro.

| Verifica rieseguita | Esito |
| --- | --- |
| Node, tutte le suite | 99/99, nessun fallimento o test saltato; 5 nuovi gruppi di regressione sicurezza |
| Kotlin/JVM, esecuzione forzata | 52/52, nessun fallimento/errore/salto; 4 nuovi test sicurezza |
| Android su emulatore API 37 | 19/19, nessun fallimento/errore/salto, incluso titolo SQL/HTML ostile trattato come dato |
| Browser locale | 3 gruppi sicurezza + 9 casi limite: escaping, CSP, import, storage negato/quota, dati intatti |
| Interoperabilità | Quattro export Kotlin/JS coerenti |
| Gitleaks 8.30.1 | 4 commit e tutti i riferimenti Git, zero credenziali; sorgenti web/Android zero |
| APK effettivamente consegnato | Firma valida, debug disattivato, backup automatici esclusi, nessun permesso Internet |
| Servizio timer Android | Avvio da processo esterno negato: servizio non esportato |
| TLS pubblico | Certificato verificato, TLS 1.3; GET browser 200 per /, /privacy e /sw.js |
| OSV | 179 coordinate effettive: 66 runtime senza corrispondenze note; 113 build, di cui 7 con 11 avvisi |

Lint è stato rieseguito forzando le fasi di analisi e report: 0 errori e 18 avvisi già documentati (versioni SDK/librerie, suggerimenti KTX e tipografia del testo 3-2-1). Esito in `output/security/lint-release-rerun.log` e `android/app/build/reports/lint-results-release.txt`. Nessun controllo Lint disattivato.

## 1. Dipendenze di compilazione: avvisi confermati per versione

Le coordinate sono state risolte da Gradle, non dedotte soltanto dai file di configurazione. Sono stati interrogati i pacchetti e le versioni nell’[API OSV](https://google.github.io/osv.dev/api/). Tutti gli abbinamenti seguenti sono nel classpath degli strumenti di build; nessuno dei 66 artefatti runtime interrogati ha ricevuto un abbinamento. Questo non dimostra né assenza assoluta di vulnerabilità né sfruttabilità degli avvisi trovati.

| Componente build | Versione | Avvisi e gravità della banca dati |
| --- | --- | --- |
| com.google.guava:guava | 33.3.1-jre | [GHSA-xxph-c9ww-hj94](https://github.com/advisories/GHSA-xxph-c9ww-hj94) (HIGH) |
| org.apache.commons:commons-lang3 | 3.16.0 | [GHSA-j288-q9x7-2f5v](https://github.com/advisories/GHSA-j288-q9x7-2f5v) (MODERATE) |
| org.bitbucket.b_c:jose4j | 0.9.5 | [GHSA-3677-xxcr-wjqv](https://github.com/advisories/GHSA-3677-xxcr-wjqv) (HIGH) |
| org.bouncycastle:bcpkix-jdk18on | 1.79 | [GHSA-wg6q-6289-32hp](https://github.com/advisories/GHSA-wg6q-6289-32hp) (MODERATE) |
| org.bouncycastle:bcprov-jdk18on | 1.79 | [GHSA-574f-3g2m-x479](https://github.com/advisories/GHSA-574f-3g2m-x479) (CRITICAL); [GHSA-9pwp-9qqc-pr26](https://github.com/advisories/GHSA-9pwp-9qqc-pr26) (CRITICAL); [GHSA-c3fc-8qff-9hwx](https://github.com/advisories/GHSA-c3fc-8qff-9hwx) (MODERATE); [GHSA-p93r-85wp-75v3](https://github.com/advisories/GHSA-p93r-85wp-75v3) (HIGH); [GHSA-qp49-qgx5-5m26](https://github.com/advisories/GHSA-qp49-qgx5-5m26) (HIGH) |
| org.jdom:jdom2 | 2.0.6 | [GHSA-2363-cqg2-863c](https://github.com/advisories/GHSA-2363-cqg2-863c) (HIGH) |
| org.jetbrains.kotlin:kotlin-gradle-plugin | 2.2.10 | [GHSA-r937-wjx7-w2jp](https://github.com/advisories/GHSA-r937-wjx7-w2jp) (MODERATE) |

Fra gli avvisi ce ne sono due classificati CRITICAL nella libreria crittografica Bouncy Castle della toolchain. La gravità della banca dati non equivale alla dimostrazione che Tabata Timer utilizzi il percorso vulnerabile. Non sono stati tentati exploit o importate cache manipolate. I dettagli completi e gli intervalli di versioni sono in `output/security/osv-all.json`.

Kotlin 2.2.10 è effettivamente risolto dalla build: l’avviso CVE-2026-53914 riguarda deserializzazione dei metadati della cache di compilazione. Il controllo Gradle mostra `buildCacheEnabled=false`; nel progetto non è configurata una cache remota. Questo limita l’esposizione osservata, senza eliminare la segnalazione. Gli altri casi comprendono deserializzazione, XML, JWE e percorsi crittografici. [Avviso Kotlin](https://github.com/advisories/GHSA-r937-wjx7-w2jp) · [Cache Gradle](https://docs.gradle.org/current/userguide/build_cache.html).

Intervento consigliato: aggiornare la toolchain e le sue dipendenze con versioni compatibili, verificando nuovamente build, firma, database e test. Evitare di forzare casualmente una sola dipendenza transitiva o una versione beta senza verificare AGP/Kotlin/Compose/KSP insieme. Non è stata modificata la toolchain durante questo audit.

## 2. Esportazione CSV: copertura incompleta

Il test sui sei prefissi già gestiti passa: `=`, `+`, `-`, `@`, tabulazione e ritorno carrello vengono prefissati e le virgolette sono raddoppiate. La sonda aggiuntiva sull’output reale web trova però cinque prefissi senza protezione: line feed e le varianti a larghezza piena `＝`, `＋`, `－`, `＠`. Il sorgente Kotlin applica la stessa lista di sei caratteri e presenta la stessa scopertura logica.

È una lacuna rispetto ai casi descritti da [OWASP CSV Injection](https://community.owasp.org/attacks/CSV_Injection), da correggere e coprire con regressioni su entrambe le piattaforme. Non è stato aperto né eseguito alcun CSV in Excel o LibreOffice: l’esecuzione di una formula non è dimostrata da questa sola ispezione. Le diverse applicazioni e il salvataggio/riapertura possono comportarsi diversamente; nessuna sanificazione CSV è universale. Il JSON originale non deve essere alterato per proteggere l’export CSV.

## 3. Firma APK e confini Android

L’APK ha `debuggable=false`, firma v2/v3 valida e lo stesso certificato Android Debug del precedente. Mantenerlo permette l’aggiornamento senza disinstallazione. Non è una chiave di produzione dedicata; prima di una distribuzione ampia o Google Play occorre definire una firma e un percorso di aggiornamento appropriati, secondo la [documentazione Android sulla firma](https://developer.android.com/studio/publish/app-signing). Nessuna chiave privata o credenziale è stata trovata nella cronologia pubblicata. Non cambiare unilateralmente certificato: interromperebbe l’aggiornamento dei pacchetti già installati.

Il servizio timer è non esportato e il tentativo da adb shell viene negato. L’Activity launcher è esportata come previsto. Il receiver AndroidX ProfileInstaller richiede `android.permission.DUMP`; gli avvisi di notifica usano PendingIntent immutabili legati alla sessione. Backup e trasferimento automatici sono esclusi dalle regole incluse. L’app non richiede Internet, microfono, contatti, posizione o lettura dei file generali. [Indicazione Android sul debug](https://developer.android.com/privacy-and-security/risks/android-debuggable).

Room usa query parametrizzate e il nuovo caso con titolo SQL/HTML ostile conserva tutte le tabelle. I dati locali beneficiano dell’isolamento dell’app; backup JSON esportati e localStorage web non sono cifrati dall’app. Un file JSON condiviso contiene titoli e cronologia in chiaro.

## 4. Sito pubblico e intestazioni

Le risposte effettive del sito hanno `X-Content-Type-Options: nosniff`, Referrer-Policy e cache del service worker senza riuso persistente. Non hanno X-Frame-Options, Content-Security-Policy come intestazione HTTP, HSTS o Permissions-Policy. La CSP definita nel meta HTML rimane una protezione distinta: nel browser locale blocca script inline e connessioni esterne. Non va dichiarata assente soltanto perché manca l’header.

Rafforzamento consigliato: politica `frame-ancestors` via header e X-Frame-Options per evitare incorporamenti non desiderati; valutare HSTS e Permissions-Policy sui requisiti reali. [`frame-ancestors`](https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/frame-ancestors) non viene applicato tramite meta. Le regole vanno inserite in `_headers` e distribuite su Cloudflare con verifica successiva. [Configurazione ufficiale Cloudflare](https://developers.cloudflare.com/pages/configuration/headers/).

La sonda di incorporamento da un’altra origine è andata in timeout: NON viene dichiarato riuscito un attacco di clickjacking. È dimostrata l’assenza delle intestazioni nelle risposte 200, non l’effettiva sfruttabilità. Il client Python ha ricevuto Cloudflare 403/1010: quelle intestazioni sono della pagina di blocco e non sono state attribuite al sito. Il browser normale ha ricevuto 200; nessuna protezione è stata aggirata o disabilitata.

## Segreti e integrità

Gitleaks è stato scaricato dalla release ufficiale, con SHA-256 verificato, in una cartella temporanea locale; nessuna installazione globale o servizio pagato. La scansione dei quattro commit su tutti i riferimenti e delle directory sorgente non trova segreti. Una scansione più ampia della cartella di lavoro ha segnalato quattro righe nei report di firma esclusi da Git: analizzate, sono impronte pubbliche SHA-256/SHA-1 della chiave, non credenziali. Il valore di una chiave privata non è stato esposto nei report.

SHA-256 dell’APK consegnato, verificato invariato: `46b11332f4834036d54b702c8192d989c35f6f6b3401b3aa8b27142017b1e752`.

## Ripetizione delle prove

Dalla radice del checkout applicativo, con server locale avviato e un emulatore dedicato:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
node --test web/tests/*.test.js
.\android\gradlew.bat -p android testDebugUnitTest --rerun
.\android\gradlew.bat -p android lintAnalyzeRelease --rerun lintReportRelease --rerun lintRelease
.\android\gradlew.bat -p android connectedDebugAndroidTest --rerun
node scripts/interop-check.mjs
# PLAYWRIGHT_MODULE deve indicare il Playwright disponibile nella macchina.
node scripts/browser-security.mjs
node scripts/browser-edge-cases.mjs
```

I nuovi test sono in `web/tests/security.test.js`, `android/app/src/test/java/it/tabatastudio/SecurityTest.kt` e nel caso aggiunto a `RepositoryTest.kt`; il nuovo script browser è `scripts/browser-security.mjs`. Le prove Android aggiungono dati solo all’emulatore dedicato. I report grezzi sono in `output/security/` del worktree.

## Ambito e limiti

Questa verifica non è una certificazione né un pentest completo OWASP/MASVS. Non include un’analisi manuale di raggiungibilità degli 11 avvisi build, test fisici su telefono/lettori musicali, esecuzione di formule in fogli di calcolo, controllo dell’account Cloudflare/GitHub o prove di carico. Il sito pubblico è stato solo letto; payload e importazioni ostili sono stati provati esclusivamente nel browser locale isolato.

Non sono stati modificati il codice di produzione, l’APK ufficiale, il sito o le dipendenze. Sono stati aggiunti soltanto test e report locali. Nessun nuovo commit, push, deploy o attivazione a pagamento è stato eseguito durante questo audit. Le correzioni elencate restano da applicare.
