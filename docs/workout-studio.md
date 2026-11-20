# Workout e Studio — versione 1.7.0

Lo switch accanto al logo Tabata Timer cambia modalità. L’ultima scelta viene ricordata sul dispositivo. Termina il timer per cambiarla. Tema e palette sono comuni; programmi, attività, statistiche e CSV sono separati. I backup JSON comprendono entrambe le modalità.

## Studio: Pomodoro semplice

La schermata iniziale e l’editor usano la stessa lista piatta. Ogni scheda è numerata Attività 1, Attività 2… e contiene soltanto Nome attività, Concentrazione, Sessioni e Pausa. Puoi aggiungere o eliminare attività; ne deve rimanere almeno una. Il nome personalizzato si vede anche nel timer. Il nome del programma rimane distinto e si sceglie al salvataggio.

Concentrazione e pause si impostano in minuti interi, massimo 60 per fase. Sessioni indica quante volte ripetere la concentrazione di quell’attività. Pausa viene inserita soltanto tra le sue sessioni. Il campo globale Pausa tra attività, esterno alle schede, compare una sola volta e si applica soltanto tra due attività consecutive. Lo zero disattiva la relativa pausa. Nessuna pausa dopo l’ultima sessione di un’attività né dopo l’ultima attività del programma.

Esempio: Attività 1 con concentrazione 1, sessioni 2, pausa 2; Attività 2 con concentrazione 3 e una sessione; pausa tra attività 4. Anteprima: concentrazione 1 → pausa tra sessioni 2 → concentrazione 1 → pausa tra attività 4 → concentrazione 3. Totale 11:00, concentrazione effettiva 5:00. La pausa della seconda attività non viene usata perché ha una sola sessione.

Studio 1, 2 e 3 impostano rispettivamente 25/5, 50/10 e 45/15 sulla prima attività. Conservano nomi, numero di sessioni e altre attività. Non ci sono blocchi, cicli, serie, pause lunghe, frequenze o pause finali configurabili.

Ogni avvio aggiunge 20 secondi di preparazione, indicati separatamente: tre bip alti sul 3, 2, 1 e fischio al primo turno di concentrazione. La preparazione non conta nella durata del programma o nelle statistiche.

## Workout

Il timer rapido conserva Lavoro, Recupero e Giri; Altre opzioni contiene Serie, Recupero tra serie e Defaticamento. L’editor mantiene blocchi ed esercizi differenti, cicli e recuperi tra ripetizioni, cicli e blocchi.

Ogni blocco presenta un solo Recupero tra esercizi. Vale per ogni coppia di esercizi consecutivi nel blocco, per ciascun ciclo, mai dopo l’ultimo esercizio. Non viene mostrato nelle schede esercizio. Le altre impostazioni conservano il comportamento precedente.

Workout si imposta in secondi. Le abbreviazioni delle unità sono state tolte da campi, preset e anteprime; conversioni interne e formato numerico del timer restano invariati.

## Salvataggio e trasferimento

Nel Timer premi Salva allenamento o Salva programma di studio, scegli un nome e conferma Salva sul dispositivo. Ritrovi il risultato in Allenamenti o Programmi di studio della modalità corrente. Dall’editor usa lo stesso pulsante finale. Il menu della raccolta permette modifica, duplicazione ed eliminazione. Eliminare un programma non elimina la cronologia.

Sul web il salvataggio vale per lo stesso browser, profilo e indirizzo; la conferma attiva la persistenza locale. Android salva nel database locale. Esporta copia JSON da Impostazioni per creare un file indipendente, poi usa Importa copia JSON sull’altro dispositivo. L’importazione mostra un’anteprima e sostituisce l’archivio soltanto dopo conferma. Non esiste sincronizzazione automatica. Il CSV contiene solo le attività della modalità corrente.

## Archivi precedenti

Lettura, apertura dell’editor, anteprima, duplicazione ed esecuzione non sovrascrivono il programma originale. La cronologia conserva i valori registrati in passato. I nuovi tempi valgono per nuove esecuzioni; solo Salva sul dispositivo sostituisce il programma modificato. Esporta prima un JSON se vuoi tenere anche una copia indipendente della struttura precedente.

- Studio con blocchi: gli esercizi diventano attività nell’ordine originale, ripetendo le attività per ogni ciclo del blocco. Nomi, concentrazione, sessioni e pause tra sessioni sono conservati. La pausa comune tra attività usa il primo valore del vecchio programma che separava realmente due attività (tra esercizi, cicli o blocchi); in assenza di confini usa 5 minuti. Preparazioni personalizzate, pause lunghe, frequenza e pausa finale sono escluse dalla nuova esecuzione; la preparazione fissa resta 20 secondi. Un avviso è visibile nella raccolta, nell’editor e all’avvio.
- Workout senza recupero comune: ogni blocco usa il recupero del suo primo esercizio. Se i recuperi precedenti tra esercizi differivano, l’avviso esplicita la scelta. I vecchi campi per esercizio rimangono leggibili nel JSON, ma non governano più l’esecuzione.
- I backup precedenti restano importabili ed esportabili senza perdere la struttura originale. JSON e Room restano versione 1. Per leggere i nuovi programmi Studio piatti su entrambi i dispositivi occorre Tabata Timer 1.7.0 o successiva; non importare un nuovo backup in una vecchia app.

## Background e audio

Android continua in background e a schermo spento, con Pausa, Riprendi, Salta e Termina nella notifica. Nessuna modifica al servizio o alla gestione del focus audio. Il fischio dura 500 ms al livello equivalente al 120%, il sospiro 500 ms all’85%, bip e campanella al 65%. I suoni si mescolano a musica e video. Sul web gli avvisi richiedono un browser non sospeso. [Dettagli Android](android-background.md) e [audio](audio-countdown.md).
