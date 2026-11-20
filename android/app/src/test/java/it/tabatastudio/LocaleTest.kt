// DOC L0001: Assegna il file al namespace it.tabatastudio, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio

// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import it.tabatastudio.i18n.AppText. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import it.tabatastudio.i18n.AppText
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import org.junit.Assert.*. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import org.junit.Assert.*
// DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import org.junit.Test. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import org.junit.Test

// DOC L0007: Dichiara il tipo o contenitore di questo modulo: Prove di regressione in LocaleTest.kt. I campi e i metodi sono descritti nelle righe successive.
class LocaleTest {
    // DOC L0008: deviceLanguageAndPreference: Prepara o verifica il caso di regressione «device Language And Preference»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun deviceLanguageAndPreference() {
        // DOC L0009: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di deviceLanguageAndPreference.
        assertEquals("en", AppText.initialLanguage(null, "en-US"))
        // DOC L0010: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di deviceLanguageAndPreference.
        assertEquals("es", AppText.initialLanguage(null, "es-MX"))
        // DOC L0011: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di deviceLanguageAndPreference.
        assertEquals("it", AppText.initialLanguage(null, "it-IT"))
        // DOC L0012: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di deviceLanguageAndPreference.
        assertEquals("es", AppText.initialLanguage("es", "en-US"))
    // DOC L0013: Chiude il costrutto aperto alla riga 8.
    }
    // DOC L0014: unsupportedLanguageFallsBack: Prepara o verifica il caso di regressione «unsupported Language Falls Back»; le asserzioni nel corpo specificano l’esito atteso.
    @Test fun unsupportedLanguageFallsBack() {
        // DOC L0015: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di unsupportedLanguageFallsBack.
        assertEquals("it", AppText.initialLanguage(null, "de-DE"))
        // DOC L0016: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire. Nel contesto di unsupportedLanguageFallsBack.
        assertEquals("en", AppText.initialLanguage("unknown", "en-US"))
    // DOC L0017: Chiude il costrutto aperto alla riga 14.
    }
// DOC L0018: Chiude il costrutto aperto alla riga 7.
}
