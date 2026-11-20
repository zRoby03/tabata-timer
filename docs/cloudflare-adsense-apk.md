# Cloudflare, pubblicità e app Android

Stato del 20 novembre 2026: **sito pubblicato su https://tabata-timer-ek1.pages.dev**. Nessun annuncio o download pubblico dell’APK è attivo. Il precedente indirizzo Sites resta disponibile per esportare i salvataggi. Nome e contatto pubblico autorizzati dal gestore: RobertStruzzi, robert.struzzi03@gmail.com.

## Cloudflare Pages gratuito

Cloudflare Pages ospita i file statici e assegna un sottodominio `nome.pages.dev`, con HTTPS. È un sottodominio gratuito; non comprende la registrazione di un proprio dominio `.it` o `.com`. Il nome esatto dipende dalla disponibilità nel momento della creazione.

Il progetto non richiede Workers, Functions, database, R2, un piano a pagamento o nuove dipendenze. Non si devono attivare questi servizi per caricare il timer.

Da PowerShell nella cartella del progetto:

```powershell
python scripts/package-cloudflare.py
```

Il comando genera `output/cloudflare/Tabata Timer.zip`: gli stessi file web verificati, più `_headers` per le risposte statiche e la rivalidazione del service worker. Controlla integrità ZIP, identità dei file e limiti del caricamento. Non inserisce APK, dati personali o credenziali.

Con un account Cloudflare già creato e autenticato:

1. Aprire il pannello Cloudflare e Workers & Pages.
2. Scegliere Create application, Pages e il caricamento diretto dei file (Drag and drop / Upload assets, secondo l’interfaccia).
3. Scegliere il nome del progetto, preferibilmente `tabata-timer` se disponibile.
4. Caricare `output/cloudflare/Tabata Timer.zip`, con `index.html` nella radice.
5. Pubblicare sul piano gratuito e conservare l’indirizzo HTTPS effettivamente restituito. Non acquistare un dominio o un piano.
6. Verificare le tre lingue, Workout/Studio, preparazione e timer, salvataggio, import/export e offline. Gli aggiornamenti successivi si caricano nello stesso progetto.

Direct Upload non si trasforma in un progetto con integrazione Git: per quella modalità Cloudflare richiede un altro progetto. Per questo sito senza compilazione, l’aggiornamento tramite ZIP è sufficiente.

Prima di passare all’indirizzo Cloudflare, esportare i dati JSON dalle Impostazioni del sito corrente e importarli sul nuovo indirizzo. Ogni origine ha il proprio archivio browser; non c’è trasferimento automatico.

## AdSense

Operazioni completate nell’account del publisher:

- Aggiunto `tabata-timer-ek1.pages.dev`, pubblicato `ads.txt` e verificato lo stato **Autorizzato**.
- Inviata la richiesta di revisione; stato **In fase di preparazione / Revisione richiesta**.
- Creata una sola unità display adattabile `Tabata Timer`, ID `9553674851`, client `ca-pub-9043574024513063`. Snippet originale in `hosting/cloudflare/adsense-snippet.html`, fuori dal sito pubblico.
- Pubblicato il messaggio europeo Google `Tabata Timer`, italiano predefinito più inglese/spagnolo, logo t. e URL privacy pubblico. Acconsento, Gestisci le opzioni e Nega il consenso; rifiuto abilitato in tutte le regioni. Ottimizzazione del consenso OFF; interesse legittimo non abilitato per impostazione predefinita. Mantenuta la copertura di riserva della CMP.
- Verificati annunci automatici OFF. Nessun codice AdSense o CMP è ancora caricato nelle pagine del sito; la pubblicazione del messaggio nel pannello non equivale all’integrazione del tag nel sito.

Il gestore ha completato direttamente in Google **Pagamenti → Informazioni su di te**. Verificati nell’account i messaggi **Hai completato tutti i passaggi** e **Abbiamo ricevuto le tue informazioni**. Nessun dato finanziario o documento è stato copiato nel progetto. Il sito rimane **In fase di preparazione / Revisione richiesta**. L’approvazione dipende da Google e non è garantita.

L’accesso all’account esistente e l’approvazione di Google sono necessari per attivare annunci reali. Un indirizzo su un suffisso pubblico può essere aggiunto ad AdSense; `pages.dev` è nella Public Suffix List. Questo permette la richiesta, senza garantire l’approvazione.

Configurazione prevista: una sola unità display manuale sulle pagine informative con contenuto originale adeguato, lontano dai controlli. Nessun annuncio nella schermata del conto alla rovescia, nessun interstitial, ancoraggio, vignetta o annuncio automatico. Un timer da solo non garantisce l’idoneità: Google valuta anche contenuti e valore della pagina.

Passaggi di integrazione e attivazione:

1. Attendere l’esito Google. Informazioni del publisher completate; sito, `ads.txt`, informativa, unità display e messaggio CMP già predisposti. Non creare duplicati.
2. Dopo l’approvazione integrare il codice dell’unità esistente solo in Informazioni. Usare un formato orizzontale di altezza contenuta secondo gli esempi ufficiali Google; niente espansione a tutta larghezza del dispositivo, overlay o annunci nel timer.
3. Integrare la CMP Google pubblicata e un comando per riaprire le preferenze, tramite `googlefc.callbackQueue` e `showRevocationMessage()`. Un banner fatto a mano non sostituisce la CMP certificata. Non interpretare l’API pronta come consenso già dato.
4. Adeguare la CSP della sola pagina con annunci: Google supporta la CSP rigorosa e sconsiglia liste di domini mutevoli. Per le pagine statiche valutare un bootstrap con hash e `strict-dynamic`, oppure una soluzione con nonce generato per risposta. Non usare un nonce fisso e non eliminare la CSP del timer. La policy attuale blocca gli script esterni; incollare lo snippet da solo non basta.
5. Aggiornare l’informativa con i trattamenti effettivi prima dell’attivazione, verificare consenso/rifiuto/revoca, richieste terze parti, assenza di annunci durante il timer, offline, CSP e layout mobile. Nessun clic di prova sugli annunci reali.

AdSense riguarda il sito. Un’eventuale pubblicità dentro l’app Android richiederebbe un’integrazione distinta, normalmente AdMob, che non è stata richiesta né aggiunta.

## Condividere l’APK

Per farlo provare ad amici è possibile inviare `output/Tabata Timer.apk` direttamente oppure, dopo la scelta di distribuirlo, fornire un download HTTPS dal sito. Il file attuale è una build debug destinata al collaudo, non una release per una distribuzione ampia.

Per il sito pubblico conviene prima preparare un APK release firmato con una chiave conservata e copiata al sicuro fuori da Git e dal sito. La stessa chiave deve firmare gli aggiornamenti. Non distribuire il keystore o le sue password. Per passare da una build debug già installata a una release con firma diversa, esportare prima i dati JSON: Android non accetta l’aggiornamento con un certificato differente.

Il download diretto non richiede l’iscrizione a Google Play. Su Android l’utente autorizza l’installazione per il browser o gestore file usato; gli aggiornamenti successivi vengono scaricati e installati manualmente. Non bisogna disattivare Play Protect. L’APK attuale è circa 11,2 MiB, sotto il limite Cloudflare Pages di 25 MiB per file; non serve uno storage a pagamento.

Google Play offre ricerca, recensioni pubbliche e gestione degli aggiornamenti. L’account sviluppatore costa 25 USD una volta; per i nuovi account personali si applicano, prima della richiesta di accesso alla produzione, almeno 12 tester per 14 giorni consecutivi. La pubblicazione richiede anche scheda, dichiarazioni e verifica, e non è stata avviata.

Per piccoli gruppi esiste anche la distribuzione limitata Android senza quota di registrazione. L’annuncio ufficiale del 19 novembre 2026 porta il limite a 50 dispositivi autorizzati; alcune guide precedenti riportano ancora 20. La verifica sviluppatore viene estesa globalmente nel 2027: per una distribuzione ampia dell’APK fuori da Play bisogna considerare anche questi requisiti, senza promettere gratuità illimitata nel tempo.

La web app può essere aggiunta alla schermata Home senza store. Per l’uso in background con musica resta preferibile l’APK nativo: l’installazione web non elimina i limiti del browser sui segnali a schermo spento.

## Fonti ufficiali

- Cloudflare Direct Upload: https://developers.cloudflare.com/pages/get-started/direct-upload/
- Limiti Pages: https://developers.cloudflare.com/pages/platform/limits/
- Prezzi richieste statiche: https://developers.cloudflare.com/pages/functions/pricing/
- Header Pages: https://developers.cloudflare.com/pages/configuration/headers/
- Siti aggiungibili ad AdSense: https://support.google.com/adsense/answer/12170421?hl=it
- Public Suffix List: https://publicsuffix.org/list/public_suffix_list.dat
- Idoneità AdSense: https://support.google.com/adsense/answer/9724?hl=it
- Contenuti e annunci: https://support.google.com/publisherpolicies/answer/11112688?hl=it
- Consenso Google: https://support.google.com/adsense/answer/13554116?hl=it
- Messaggio europeo: https://support.google.com/adsense/answer/10960768?hl=it
- Firma Android: https://developer.android.com/studio/publish/app-signing
- Account Google Play: https://support.google.com/googleplay/android-developer/answer/6112435?hl=it
- Test account personali: https://support.google.com/googleplay/android-developer/answer/14151465?hl=it
- Verifica Android 2027: https://developer.android.com/blog/posts/android-developer-verification-2027
- CSP AdSense: https://support.google.com/adsense/answer/16283098?hl=en
- CSP per pagine statiche: https://web.dev/articles/strict-csp
- API consenso Google: https://developers.google.com/funding-choices/fc-api-docs
- Banner adattabile: https://support.google.com/adsense/answer/9183363?hl=en
