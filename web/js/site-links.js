import { getLanguage } from './i18n.js';

export function siteLinksView() {
  const labels = { it: ['Informazioni', 'Privacy e cookie'], en: ['About', 'Privacy and cookies'], es: ['Información', 'Privacidad y cookies'] }[getLanguage()];
  return `<p class="row site-links"><a href="./about.html">${labels[0]}</a><a href="./privacy.html">${labels[1]}</a></p>`;
}
