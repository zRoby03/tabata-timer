// DOC L0001: Assegna il file al namespace it.tabatastudio.ui, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio.ui

// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.material3.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.material3.*
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import androidx.compose.runtime.Composable. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import androidx.compose.runtime.Composable

// DOC L0006: HelpScreen: Mostra i tre soli riquadri della Guida, traducendone i testi con il catalogo condiviso.
@Composable internal fun HelpScreen(vm: StudioViewModel) = Page {
    // DOC L0007: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di HelpScreen.
    Text(tr("Guida all’uso"), style = MaterialTheme.typography.headlineMedium)
    // DOC L0008: Esegue il passaggio esplicito «Panel {» nel flusso: Guida Compose equivalente alla guida web. Nel contesto di HelpScreen.
    Panel {
        // DOC L0009: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di HelpScreen.
        Text(tr("Workout e Studio"), style = MaterialTheme.typography.titleLarge)
        // DOC L0010: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di HelpScreen.
        Text(tr("Usa lo switch accanto a Tabata Timer: Workout ha tempi in secondi, Studio in minuti. L’ultima modalità utilizzata viene ricordata."))
        // DOC L0011: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di HelpScreen.
        Text(tr("Programmi, attività e statistiche sono separati; tema e palette sono comuni."))
        // DOC L0012: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di HelpScreen.
        Text(tr("In Altre opzioni puoi impostare pause e serie personalizzate a tuo piacimento ed ad ogni tua necessità."))
        // DOC L0013: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di HelpScreen.
        Text(tr("Termina il timer per cambiare modalità."))
    // DOC L0014: Chiude il costrutto aperto alla riga 8.
    }
    // DOC L0015: Esegue il passaggio esplicito «Panel {» nel flusso: Guida Compose equivalente alla guida web. Nel contesto di HelpScreen.
    Panel {
        // DOC L0016: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di HelpScreen.
        Text(tr("Per conservare un allenamento"), style = MaterialTheme.typography.titleLarge)
        // DOC L0017: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di HelpScreen.
        Text(tr("Nel Timer premi Salva allenamento (Workout) o Salva programma di studio (Studio), scegli un nome e conferma Salva sul dispositivo. Lo ritrovi in Allenamenti o Programmi di studio, nella stessa modalità."))
        // DOC L0018: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di HelpScreen.
        Text(tr("I dati restano sul telefono. Per conservarne una copia o trasferirli sul web, esporta il JSON da Impostazioni e importalo sull’altro dispositivo."))
    // DOC L0019: Chiude il costrutto aperto alla riga 15.
    }
    // DOC L0020: Esegue il passaggio esplicito «Panel {» nel flusso: Guida Compose equivalente alla guida web. Nel contesto di HelpScreen.
    Panel {
        // DOC L0021: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di HelpScreen.
        Text(tr("Tema e Colori"), style = MaterialTheme.typography.titleLarge)
        // DOC L0022: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di HelpScreen.
        Text(tr("In Impostazioni → Aspetto scegli Chiaro o Scuro e una palette: Viola, Blu, Verde chiaro, Verde scuro, Azzurro, Arancione, Giallo, Rosso, Rosa."))
        // DOC L0023: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di HelpScreen.
        Text(tr("Le preferenze cambiano subito e restano su questo dispositivo."))
    // DOC L0024: Chiude il costrutto aperto alla riga 20.
    }
    // DOC L0025: Descrive un componente Compose e collega i suoi valori/callback allo stato osservabile. Nel contesto di HelpScreen.
    AccentButton(onClick = { vm.navigate("Home") }) { Text(tr("Vai al timer")) }
// DOC L0026: Chiude il costrutto aperto alla riga 6.
}
