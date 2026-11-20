# Verifiche del branch documentazione

Controlli eseguiti il **20 novembre 2026**, sul codice annotato del branch `documentazione`. Base applicativa immutabile: `5d18d6d633e8fd8c8241aed078246f1953b084ef`. Il successivo commit `82d3580` su main corregge soltanto README e installazione, allineandoli allo stato della pubblicazione già eseguita.

| Verifica eseguita | Esito |
| --- | --- |
| Copertura dei sorgenti e configurazioni | 137 file, 11.538 righe originali e altrettante spiegazioni |
| Annotazioni e simboli | 5.273 commenti nel codice; 865 funzioni/callback indicizzate; nessuna funzione applicativa senza descrizione curata |
| Identità del testo originale | Rimozione dei soli commenti DOC → sorgenti identici, normalizzando le terminazioni a LF |
| Analisi sintattica | AST invariato per 44 file JS/MJS; AST Python e token significativi dei 44 file analizzati dal lexer Kotlin invariati |
| Configurazioni e risorse | JSON e XML/SVG validi, struttura invariata; SHA-256 dei 23 binari uguale alla base |
| `node --test web/tests/*.test.js` | **82 passati**, zero falliti o saltati |
| `testDebugUnitTest` | **39 passati**, zero falliti, errori o saltati |
| `assembleDebug` | **BUILD SUCCESSFUL**, APK di sviluppo generato soltanto in locale |
| `lintDebug` | **0 errori, 15 warning**, stesso conteggio del report precedente; segnalazione di deprecazione `srcDir` nella configurazione Gradle |
| `node scripts/interop-check.mjs` | Web → Kotlin → Web: campione 220 s, Studio legacy, nuovo Studio piatto e recupero comune Workout coerenti |
| `node scripts/browser-languages.mjs` | Tre lingue, salvataggio/riapertura e nomi utente conservati, entrambe le modalità, editor/timer/statistiche, tre riquadri Guida, nove palette, larghezze 320/390/768/1440 e offline verificati |

Il report di copertura è [copertura.json](copertura.json); il risultato macchina del confronto è [verifica-commenti.json](verifica-commenti.json). Il manifest contiene gli hash, i conteggi per file e la mappa delle righe. Le istruzioni per ripetere i controlli sono in [installazione-verifiche.md](installazione-verifiche.md).

Sono stati controllati anche i collegamenti locali delle guide e i file selezionati per Git. File temporanei, cache, credenziali, chiavi, APK e archivi personali sono esclusi. I link con espressioni tra parentesi mostrati **dentro estratti di codice** del generatore sono codice di esempio, non collegamenti documentali da aprire.

## Prove precedenti e limiti della verifica

I precedenti report Android/web e di pubblicazione sono in [docs/verifiche](../verifiche). Le 16 prove Android strumentali su emulatore della revisione applicativa precedente non sono state rieseguite per questa modifica di soli commenti e documentazione; non vengono contate come nuovi test. Non sono state eseguite prove aggiuntive su telefoni fisici. Il servizio Android, l'audio e gli asset rimangono quelli del codice verificato, come dimostra il confronto di sorgenti e token.

Il lint ha warning, quindi «zero errori» non significa «zero segnalazioni». Una build locale può produrre un APK con hash diverso per metadati e compilazione: non è stato sostituito il file APK già consegnato e non è stato pubblicato un nuovo binario.

Il caricamento GitHub riguarda i sorgenti e la documentazione. Non è stato eseguito un deploy Cloudflare o Sites, un'attivazione AdSense o una distribuzione pubblica dell'APK. I remoti `main` e `documentazione` vengono verificati dopo il push; il branch è consultabile dal repository pubblico senza un account GitHub.
