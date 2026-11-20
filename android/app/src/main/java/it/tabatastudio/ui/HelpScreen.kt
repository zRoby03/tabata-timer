package it.tabatastudio.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable internal fun HelpScreen(vm: StudioViewModel) = Page {
    Text(tr("Guida all’uso"), style = MaterialTheme.typography.headlineMedium)
    Panel {
        Text(tr("Workout e Studio"), style = MaterialTheme.typography.titleLarge)
        Text(tr("Usa lo switch accanto a Tabata Timer: Workout ha tempi in secondi, Studio in minuti. L’ultima modalità utilizzata viene ricordata."))
        Text(tr("Programmi, attività e statistiche sono separati; tema e palette sono comuni."))
        Text(tr("In Altre opzioni puoi impostare pause e serie personalizzate a tuo piacimento ed ad ogni tua necessità."))
        Text(tr("Termina il timer per cambiare modalità."))
    }
    Panel {
        Text(tr("Per conservare un allenamento"), style = MaterialTheme.typography.titleLarge)
        Text(tr("Nel Timer premi Salva allenamento (Workout) o Salva programma di studio (Studio), scegli un nome e conferma Salva sul dispositivo. Lo ritrovi in Allenamenti o Programmi di studio, nella stessa modalità."))
        Text(tr("I dati restano sul telefono. Per conservarne una copia o trasferirli sul web, esporta il JSON da Impostazioni e importalo sull’altro dispositivo."))
    }
    Panel {
        Text(tr("Tema e Colori"), style = MaterialTheme.typography.titleLarge)
        Text(tr("In Impostazioni → Aspetto scegli Chiaro o Scuro e una palette: Viola, Blu, Verde chiaro, Verde scuro, Azzurro, Arancione, Giallo, Rosso, Rosa."))
        Text(tr("Le preferenze cambiano subito e restano su questo dispositivo."))
    }
    AccentButton(onClick = { vm.navigate("Home") }) { Text(tr("Vai al timer")) }
}
