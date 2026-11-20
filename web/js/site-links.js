// DOC L0001: Rende disponibili le dipendenze esplicite di questa riga: import { getLanguage } from './i18n.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { getLanguage } from './i18n.js';

// DOC L0003: siteLinksView: Produce i due collegamenti Informazioni e Privacy nella lingua corrente.
export function siteLinksView() {
  // DOC L0004: labels: valore locale ottenuto dall’espressione di inizializzazione riportata nella riga. Nel contesto di siteLinksView.
  const labels = { it: ['Informazioni', 'Privacy e cookie'], en: ['About', 'Privacy and cookies'], es: ['Información', 'Privacidad y cookies'] }[getLanguage()];
  // DOC L0005: Restituisce il valore calcolato o conclude il percorso della funzione. Genera o aggiorna HTML della UI; i dati utente devono passare attraverso l’escaping previsto dal modulo. Nel contesto di siteLinksView.
  return `<p class="row site-links"><a href="./about.html">${labels[0]}</a><a href="./privacy.html">${labels[1]}</a></p>`;
// DOC L0006: Chiude il costrutto aperto alla riga 3.
}
