import { locale } from './i18n.js';
export const escapeHtml = value => String(value).replace(/[&<>"']/g, char => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[char]));
export const dateLabel = value => new Date(value).toLocaleString(locale(), { dateStyle: 'short', timeStyle: 'short' });
export const decimal = value => Number(value).toLocaleString(locale(), { minimumFractionDigits: 1, maximumFractionDigits: 1 });
