# Italiano, English, Español — versione 1.8.0

## Uso

La prima lingua viene scelta dal dispositivo tra italiano, inglese e spagnolo. La scelta manuale in Impostazioni → Lingua ha precedenza e rimane sul dispositivo. Nessun supporto trovato: italiano. La preferenza è comune a Workout e Studio, separata da archivi e backup.

Sono tradotti navigazione, controlli, campi, accessibilità, Guida, anteprima, timer, attività, statistiche, messaggi di validazione e recupero, finestre di salvataggio/importazione e notifica Android. Il timer continua durante il cambio lingua. Preparazione, suoni, durate, fasi, identificatori e formule statistiche rimangono invariati.

I nomi e le descrizioni già salvati vengono mostrati nella loro forma originale. I nuovi elementi usano la lingua corrente. Il JSON e le colonne del CSV conservano il contratto tecnico v1 per lo scambio dati. Tema, palette e lingua non entrano nel JSON.

## Catalogo comune e rigenerazione

`locales/messages.json` è l’unica fonte delle traduzioni. Ogni voce usa il testo italiano come identificatore e contiene inglese e spagnolo. Un terzo valore italiano facoltativo distingue i pochi contesti ambigui: Pausa del timer diventa Pause/Pausar, mentre il campo Pausa Studio diventa Break/Pausa.

I segnaposto `{0}`, `{1}` ecc. conservano nomi e valori dinamici. Non viene eseguita alcuna traduzione automatica sul DOM o sui dati personali. Un adattatore di sola presentazione gestisce le etichette delle fasi e gli errori prodotti dai contratti precedenti.

Dalla radice esegui:

```powershell
python scripts/generate-locales.py
```

Il generatore verifica completezza e segnaposto e produce `web/js/messages.js`, le risorse Android predefinite/inglesi/spagnole e la mappa degli identificatori. I nomi delle risorse derivano da hash stabili. Le risorse generate non vanno modificate a mano. Tutti i file sono inclusi nei sorgenti. Non servono librerie di traduzione, chiavi API, download a runtime o servizi esterni.

Web: `web/js/i18n.js` legge `tabata:language`, aggiorna `html.lang` e formatta date/numeri. Solo gli elementi statici marcati sono tradotti; i valori dei campi e i nomi salvati restano intatti. Il service worker conserva i cataloghi offline.

Android: `AppText` usa risorse native configurate nella lingua scelta e SharedPreferences. La stessa preferenza è letta da Compose e dalla notifica; non viene ricreato il motore del timer. Le tre lingue sono incluse nel pacchetto, senza download da Play.

## Verifiche

Test JS per preferenze/dispositivo/fallback, catalogo, segnaposto, nomi personali, durate, errori e termini contestuali. Il test browser percorre salvataggio, editor, timer, statistiche, Guida, palette, quattro larghezze e offline per tutte le lingue. Android verifica le risorse reali, il cambio lingua con timer attivo, la notifica e la ricreazione dell’Activity. Le prove strumentali usano un emulatore dedicato; resta la verifica sui telefoni reali.
