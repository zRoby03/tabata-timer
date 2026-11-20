# Marchio Tabata Timer

Il nome pubblico è **Tabata Timer** in tutte e tre le lingue e in entrambe le modalità. L’icona è la **t.** bianca su fondo viola; i colori dell’interfaccia restano personalizzabili. L’icona Android usa il medesimo tracciato del sito, con spazio sicuro per le maschere del launcher e variante monocromatica per il tema di sistema. La notifica usa la t. bianca su fondo trasparente.

Le revisioni in Gradle e nella cache sono tecniche: servono a installare aggiornamenti e aggiornare i file offline. Non sono aggiunte al nome pubblico. Gli archivi dei programmi e i nomi scelti dall’utente non vengono modificati.

Il tracciato comune è salvato in `assets/branding/t-dot-path.txt`; non occorre distribuire o installare font. Per rigenerare SVG e risorse Android, usa Python standard:

```powershell
python scripts/generate-brand-icons.py
```

I PNG web 192/512 sono già inclusi. Solo per rigenerarli serve il Playwright già usato nelle verifiche, indicando `PLAYWRIGHT_MODULE` e lanciando `node scripts/render-brand-icons.mjs`. Nessuna dipendenza runtime o servizio remoto.

L’indirizzo pubblico verrà definito alla pubblicazione autorizzata. La pagina locale è disponibile alla radice del server, senza numero di versione nell’URL. Il nome pubblico e i file utente restano Tabata Timer. La pubblicazione continua ad attendere “andiamo”.
