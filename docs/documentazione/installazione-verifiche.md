# Installazione e verifiche ripetibili

Eseguire i comandi dalla radice del repository, salvo un `Set-Location` esplicito. Le procedure del prodotto sono anche in [setup-windows.md](../setup-windows.md); qui sono raccolti i passaggi correnti e quelli aggiunti per questo branch. I comandi di seguito non pubblicano il sito né inviano l'APK.

## Ottenere i sorgenti

```powershell
git clone https://github.com/zRoby03/tabata-timer.git
Set-Location tabata-timer
git switch documentazione
```

Per studiare il codice si può anche navigare su GitHub. Per lavorare sui sorgenti senza le annotazioni usare `git switch main` da un albero di lavoro pulito. Il branch didattico può essere eseguito e compilato, ma lo ZIP di produzione va preparato dal ramo applicativo verificato. Non esistono workflow di pubblicazione automatica.

## Web: nessuna installazione npm necessaria

Prerequisito: Python 3 disponibile come `python` o `py`.

```powershell
python --version
python -m http.server 5500 --bind 127.0.0.1 --directory web
```

Aprire [127.0.0.1:5500](http://127.0.0.1:5500). Fermare il server con Ctrl+C. Non aprire `index.html` con doppio clic: import dei moduli e service worker richiedono HTTP locale o HTTPS. Per l'uso normale non servono Node, Acorn, Playwright, database, chiavi o account.

Al primo caricamento la cache offline richiede che le risorse siano state scaricate. Il salvataggio persistente dei programmi è una scelta distinta. Usare sempre la stessa origine per ritrovare l'archivio; [dati.md](dati.md) spiega esportazione e trasferimento.

## Android: strumenti e versioni

Aprire la cartella `android` in Android Studio, scegliere il JDK integrato JBR 21 per Gradle, installare Android SDK Platform 36 e gli strumenti proposti dal progetto, accettare le licenze SDK e attendere la sincronizzazione. Per Run serve un telefono Android 8/API 26 o superiore oppure un emulatore. Non occorre un account Play.

| Componente bloccato nel progetto | Versione |
| --- | --- |
| Gradle wrapper | 9.4.1 |
| Android Gradle Plugin | 9.2.1 |
| Kotlin integrato / compilatore Compose | 2.2.10 |
| KSP | 2.3.9 |
| Compose BOM | 2025.08.00 |
| Activity Compose / Lifecycle ViewModel | 1.10.1 / 2.9.2 |
| Room | 2.8.4 |
| minSdk / compileSdk / targetSdk | 26 / 36 / 36 |

Sono versioni del codice consegnato, non un elenco delle ultime versioni disponibili. Nell'ambiente originale Python, Node e Android Studio erano già presenti; la build ha risolto dipendenze dai repository gratuiti configurati. `android/local.properties`, generato localmente dall'IDE, non viene caricato su GitHub.

In PowerShell, adattare i percorsi alla propria installazione:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
.\android\gradlew.bat -p android testDebugUnitTest assembleDebug lintDebug --console=plain
```

Il wrapper evita un'installazione globale di Gradle. APK di sviluppo: `android/app/build/outputs/apk/debug/app-debug.apk`. Rimane ignorato da Git. Un APK ricompilato dal branch commentato può avere un'impronta diversa anche se il codice ha lo stesso comportamento; la chiave debug locale non è un'identità di firma condivisa da tutti gli sviluppatori.

Per installarlo su un dispositivo di test USB:

```powershell
& "$env:ANDROID_HOME\platform-tools\adb.exe" devices
& "$env:ANDROID_HOME\platform-tools\adb.exe" install -r 'android/app/build/outputs/apk/debug/app-debug.apk'
```

## Test del prodotto

Node serve ai test JavaScript, non al sito:

```powershell
node --version
node --test web/tests/*.test.js
.\android\gradlew.bat -p android testDebugUnitTest
node scripts/interop-check.mjs
```

Il test Kotlin genera gli export campione letti da `interop-check.mjs`: rispettare quest'ordine. Il controllo incrociato verifica sia i programmi legacy sia Studio piatto e recupero comune Workout.

Con emulatore o telefono **dedicato ai test**, già avviato:

```powershell
.\android\gradlew.bat -p android connectedDebugAndroidTest
```

I test strumentali aggiungono dati di prova. Le precedenti 16 prove su emulatore e i controlli del background sono documentati nei report conservati; la riesecuzione di questo branch è indicata separatamente in [verifiche.md](verifiche.md). Non confondere un report precedente con un test appena eseguito su hardware fisico.

Gli script browser usano Playwright e un server locale avviato in un altro terminale. Per un'installazione indipendente e isolata dagli asset pubblici:

```powershell
npm install --prefix tmp/browser-tools --no-audit --no-fund playwright
& '.\tmp\browser-tools\node_modules\.bin\playwright.cmd' install chromium
$env:PLAYWRIGHT_MODULE = Join-Path (Get-Location) 'tmp/browser-tools/node_modules/playwright/index.mjs'
node scripts/browser-check.mjs
```

Nell'ambiente originale Playwright era già incluso nel runtime di sviluppo: non è stato aggiunto alle dipendenze del sito. Gli altri script `browser-*` verificano aspetti mirati; i loro requisiti e risultati sono in [testing.md](../testing.md). Alcuni assumono browser o artefatti disponibili nell'ambiente originale: leggere la loro configurazione prima di avviarli. Non avviare test browser che cancellano dati su un profilo personale.

## Rigenerare traduzioni e risorse

```powershell
python scripts/generate-locales.py
python scripts/generate-horn.py
python scripts/generate-brand-icons.py
node scripts/render-brand-icons.mjs
```

Questi comandi sono necessari **solo se si modifica la sorgente della risorsa**. Non vanno eseguiti per il semplice avvio. Il rendering PNG richiede Playwright; gli altri generatori usano Python standard. Dopo una rigenerazione controllare diff e test. Nel branch didattico rigenerare un file applicativo può eliminare i suoi commenti: prima modificare il ramo applicativo, poi aggiornare la base della documentazione.

## Installare il solo strumento di documentazione

Il branch aggiunge Acorn 8.15.0, bloccato nel lockfile del tool:

```powershell
npm ci --prefix tools/documentation --no-audit --no-fund
```

Non modifica `web/package.json` e non installa dipendenze nel sito. Il generatore usa Python standard, Acorn e il lexer del compilatore Kotlin già scaricato dalla build Gradle. Prima eseguire almeno una build Android per popolare la cache del compilatore e della stdlib. Impostare JAVA_HOME come sopra se necessario. Non serve installare globalmente Kotlin o un secondo Java.

Per rigenerare sulla base immutabile documentata:

```powershell
python -X utf8 tools/documentation/generate.py --base 5d18d6d633e8fd8c8241aed078246f1953b084ef
python -X utf8 tools/documentation/verify.py
```

Il primo comando riscrive **i file annotati nel checkout corrente** partendo da quella base Git e ricrea tabelle/manifest. Usarlo su un checkout pulito dedicato alla documentazione, senza modifiche funzionali non salvate. Non aggiorna `main` o remoti, non compila un nuovo prodotto e non pubblica. Le guide scritte a mano restano separate dai documenti generati. Dopo modifiche ai tool, rigenerare le loro tabelle prima di usare `verify.py`.

## Cosa controlla il verificatore

Per ogni sorgente verifica presenza nella guida, una voce per ogni riga, assenza di righe duplicate, hash e corrispondenza delle posizioni. Rimuove i soli commenti DOC e richiede l'identità col testo originale normalizzato a LF. Confronta l'AST JavaScript ignorando posizioni/commenti, l'AST Python e la sequenza significativa dei token Kotlin. Analizza JSON, XML/SVG e confronta gli hash dei binari. In XML ignora la sola indentazione senza contenuto intorno ai commenti.

Questo controllo non pretende che i byte dell'APK siano uguali dopo ricompilazione. Dimostra che il branch aggiunge documentazione senza cambiare istruzioni, stringhe e asset; build e test verificano poi che i commenti non abbiano introdotto problemi di sintassi o comportamento.
