# Segnali sonori di Tabata Timer

## Preparazione e conto alla rovescia

Ogni avvio aggiunge 20 secondi di preparazione, esclusi da durata pianificata e statistiche. Negli ultimi tre secondi delle fasi attive suona un bip breve sul 3, sul 2 e sull’1: circa 120 ms ciascuno, senza note prolungate o avvisi arretrati in raffica. Lavoro e concentrazione usano 330/370/440 Hz; preparazione e recuperi 660/740/880 Hz. Il defaticamento Workout non emette bip.

## Workout

| Evento | Campione | Durata | Guadagno |
| --- | --- | ---: | ---: |
| Primo lavoro e lavori ordinari | workout-ding-1.wav, file 01 dell’utente | 1050 ms | 0,65 |
| Primo lavoro del nuovo ciclo o blocco | workout-ding-2.wav, file 02, sostituisce il ding singolo | 1485 ms | 0,65 |
| Recupero personale, anche prima di ripetere il blocco | rest-start.wav, sospiro attuale | 500 ms | 0,85 |
| Recupero tra blocchi differenti | rest-double.wav, sospiro + 200 ms silenzio + sospiro | 1200 ms | 0,85 |
| Fine naturale dell’ultimo lavoro | workout-ding-3.wav, file 03 | 1920 ms | 0,65 |
| Fine naturale del defaticamento, se attivo | lo stesso file 03, una seconda volta | 1920 ms | 0,65 |

L’ingresso nel defaticamento non ha un segnale proprio: i tre ding appartengono alla fine del lavoro. Senza defaticamento il finale suona una sola volta. I finali hanno identità distinte e possono suonare al massimo una volta ciascuno per sessione, anche tornando indietro. Termina manuale e salto dell’ultima fase non producono un falso finale.

I dings non diventano fasi e non aumentano durata o statistiche. La coda finale termina anche dopo lo zero. I lavori e i recuperi attivi durano almeno 5 secondi, il defaticamento almeno 10: i campioni terminano prima del successivo 3-2-1.

## Studio, invariato

Fischio morbido da arbitro di 500 ms a inizio concentrazione; sospiro di 500 ms a inizio pausa; campanella di 900 ms alla fine. Fischio: WAV già preamplificato ×1,2, gain 1 (non applicare 1,2 di nuovo); sospiro 0,85; campanella e bip 0,65. Nessuna pausa finale. I nove campioni precedenti sono conservati byte per byte.

## Risorse e riproduzione

I tre WAV originali dell’utente sono in `assets/audio/`; il generatore non dipende da Downloads o dalla demo. I campioni sono copiati identici in `web/audio/` e `android/app/src/main/res/raw/`. Il set complessivo contiene 13 WAV, tutti offline. [Provenienza](../assets/audio/README.md).

```powershell
python scripts/generate-horn.py
```

Il generatore usa solo Python standard, conserva i vecchi timbri e compone il doppio sospiro senza sovrapposizione. I nuovi dings sono PCM mono, 16 bit, 24000 Hz; nessun campione raggiunge la saturazione. Il guadagno viene applicato una volta dal player. Velocità sempre 1. Web Audio e SoundPool hanno una sola voce attiva. Pausa, salto, precedente, termine, nuova sessione e Suoni disattivati cancellano la riproduzione precedente. Ripresa e ridisegni non ripetono gli avvisi già emessi.

Android richiede un focus MAY_DUCK breve, comune alle modalità: dal primo bip alla fine del segnale successivo, oppure per la durata di un avviso isolato. Non modifica il volume globale. Rilascia su pausa/mute/termine/distruzione e alla fine del campione. Se negato o perso, omette l’avviso e continua il timer e l’eventuale vibrazione. [Dettagli e limiti](android-background.md).

Sul web non viene richiesta una priorità sulla musica delle altre app: browser sospesi e schermo bloccato possono impedire gli avvisi. Service worker e cache includono tutti i WAV e i nuovi moduli. Nessuna API esterna o nuova dipendenza.

## Verifica

Test Node/JVM confrontano sequenza, metadati, salti, visite, finali idempotenti, ritardi e statistiche. I controlli PCM verificano frequenze, durata, picchi, guadagni, copie identiche e il silenzio del doppio sospiro. Il browser offline decodifica 13 WAV e misura eventi e coda finale completa. SoundPool è verificato su emulatore con tutti i campioni e rilascio del focus. [Risultati e comandi](testing.md).
