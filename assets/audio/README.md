# Riferimento del sospiro

Il file `sigh-reference.wav` contiene un estratto di 500 ms del riferimento indicato dall’utente:

- Video: [Sigh Sound Effect #soundeffects](https://www.youtube.com/watch?v=1E-WbRI_R64).
- Autore indicato: Popcorn81 Extras.
- Estratto: da 1,421375 s a 1,921375 s, scelto nella porzione con maggiore energia del suono.
- Elaborazione: conversione mono PCM a 24000 Hz, taglio di 500 ms, fade-in di 5 ms, fade-out di 10 ms, normalizzazione del picco a 11000/32768.
- I metadati del video non specificavano una licenza. Il riferimento e la provenienza sono registrati in `sigh-reference.json`; questa nota non attribuisce una licenza al contenuto.

Il video completo resta soltanto in `tmp/`, escluso da Git e dal sito. Il generatore del progetto usa l’estratto per creare file identici nelle risorse web e Android. Il sito pubblicato usa risorse della revisione precedente; questa revisione audio rimane locale.

## DING forniti dall’utente, 24 novembre 2026

Copie byte per byte dei file consegnati, senza normalizzazione o nuova sintesi. PCM mono 16 bit, 24000 Hz. Il generatore copia questi asset su entrambe le piattaforme senza dipendere da Downloads o dalla demo; gain 0,65 applicato solo in riproduzione. File originali dell’utente non modificati.

| Asset | Originale | Durata | SHA-256 |
| --- | --- | ---: | --- |
| workout-ding-1.wav | 01_Inizio_lavoro_1_DING_ring.wav | 1.050 s | `fded6b3d88a00ef80d7a25fdddae6bd7d2a4639491e715c9bc9f605b3ad43bbe` |
| workout-ding-2.wav | 02_Ripetizione_blocco_2_DING_ring.wav | 1.485 s | `ca2f41ddcd94ba7e7b0be2ba7d38d71244f4e3b06cd2289e56b1a6f3b37b8bc1` |
| workout-ding-3.wav | 03_Fine_allenamento_3_DING_ring.wav | 1.920 s | `d827239e1b8ab2c54dc8cfd1a1c1165178dc2d6f3be89350b6d2ee8d40f72967` |

`rest-double.wav` è composto dal sospiro esistente, 200 ms di silenzio e lo stesso sospiro: 1,2 secondi totali, gain 0,85 solo nel player. I nove asset audio precedenti restano invariati.
