# hosting/cloudflare/_headers

Header interpretati da Cloudflare Pages.

Imposta MIME corretto, protezione dal content sniffing e rivalidazione del service worker; non è un backend.
I numeri si riferiscono al sorgente originale; il collegamento apre esattamente quella riga. I commenti DOC nel branch sono aggiunte didattiche.

## Ogni riga

| Riga originale | Codice (estratto) | Spiegazione |
| ---: | --- | --- |
| [1](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/hosting/cloudflare/_headers#L1) | <code>/*</code> | Commento originale: conserva la motivazione o l’istruzione già presente nel sorgente. |
| [2](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/hosting/cloudflare/_headers#L2) | <code>X-Content-Type-Options: nosniff</code> | Configura la regola letterale «X-Content-Type-Options: nosniff» per header interpretati da cloudflare pages. |
| [3](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/hosting/cloudflare/_headers#L3) | <code>Referrer-Policy: strict-origin-when-cross-origin</code> | Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. |
| [4](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/hosting/cloudflare/_headers#L4) | <code>∅</code> | Riga vuota: separa visivamente i passaggi, senza introdurre istruzioni o dati. |
| [5](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/hosting/cloudflare/_headers#L5) | <code>/sw.js</code> | Configura la regola letterale «/sw.js» per header interpretati da cloudflare pages. |
| [6](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/hosting/cloudflare/_headers#L6) | <code>Cache-Control: no-cache, max-age=0, must-revalidate</code> | Configura la regola letterale «Cache-Control: no-cache, max-age=0, must-revalidate» per header interpretati da cloudflare pages. |
| [7](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/hosting/cloudflare/_headers#L7) | <code>∅</code> | Riga vuota: separa visivamente i passaggi, senza introdurre istruzioni o dati. |
| [8](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/hosting/cloudflare/_headers#L8) | <code>/manifest.webmanifest</code> | Configura la regola letterale «/manifest.webmanifest» per header interpretati da cloudflare pages. |
| [9](https://github.com/zRoby03/tabata-timer/blob/5d18d6d633e8fd8c8241aed078246f1953b084ef/hosting/cloudflare/_headers#L9) | <code>Content-Type: application/manifest+json; charset=utf-8</code> | Configura la regola letterale «Content-Type: application/manifest+json; charset=utf-8» per header interpretati da cloudflare pages. |
