// DOC L0001: Rende disponibili le dipendenze esplicite di questa riga: import { locale } from './i18n.js';. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import { locale } from './i18n.js';
// DOC L0002: escapeHtml: Converte i caratteri speciali in entità HTML prima di mostrare testo inserito dall’utente.
export const escapeHtml = value => String(value).replace(/[&<>"']/g, char => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[char]));
// DOC L0003: dateLabel: Formatta una data con la lingua corrente per cronologia e tabelle.
export const dateLabel = value => new Date(value).toLocaleString(locale(), { dateStyle: 'short', timeStyle: 'short' });
// DOC L0004: decimal: Formatta un numero decimale nella lingua corrente.
export const decimal = value => Number(value).toLocaleString(locale(), { minimumFractionDigits: 1, maximumFractionDigits: 1 });
