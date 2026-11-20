# DOC L0001: Esegue il passaggio esplicito «"""Generate matching SVG/Android vector icons from the shared t. outline. Standard library only."""» nel flusso: Generatore delle icone vettoriali condivise.
"""Generate matching SVG/Android vector icons from the shared t. outline. Standard library only."""
# DOC L0002: Rende disponibili le dipendenze esplicite di questa riga: from pathlib import Path. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
from pathlib import Path
# DOC L0003: Esegue il passaggio esplicito «ROOT=Path(__file__).resolve().parent.parent» nel flusso: Generatore delle icone vettoriali condivise.
ROOT=Path(__file__).resolve().parent.parent
# DOC L0004: Esegue il passaggio esplicito «path=(ROOT/'assets/branding/t-dot-path.txt').read_text(encoding='utf-8').strip()» nel flusso: Generatore delle icone vettoriali condivise.
path=(ROOT/'assets/branding/t-dot-path.txt').read_text(encoding='utf-8').strip()
# DOC L0005: Esegue il passaggio esplicito «background='#8650A7'» nel flusso: Generatore delle icone vettoriali condivise.
background='#8650A7'
# DOC L0006: Esegue il passaggio esplicito «(ROOT/'web/icon.svg').write_text(f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 192 192"><rect width="192" height="192" rx="52" fill="{background}"/><path fill="white" d="{» nel flusso: Generatore delle icone vettoriali condivise.
(ROOT/'web/icon.svg').write_text(f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 192 192"><rect width="192" height="192" rx="52" fill="{background}"/><path fill="white" d="{path}"/></svg>\n',encoding='utf-8')
# DOC L0007: Esegue il passaggio esplicito «res=ROOT/'android/app/src/main/res'» nel flusso: Generatore delle icone vettoriali condivise.
res=ROOT/'android/app/src/main/res'
# DOC L0008: Esegue il passaggio esplicito «(res/'values/icon_colors.xml').write_text(f'<?xml version="1.0" encoding="utf-8"?>\n<resources><color name="ic_launcher_background">{background}</color></resources>\n',encoding='ut» nel flusso: Generatore delle icone vettoriali condivise.
(res/'values/icon_colors.xml').write_text(f'<?xml version="1.0" encoding="utf-8"?>\n<resources><color name="ic_launcher_background">{background}</color></resources>\n',encoding='utf-8')
# DOC L0009: Esegue il passaggio esplicito «header='<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="108dp" android:height="108dp" android:viewportWidth="192" android:viewportHeight="192">'» nel flusso: Generatore delle icone vettoriali condivise.
header='<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="108dp" android:height="108dp" android:viewportWidth="192" android:viewportHeight="192">'
# DOC L0010: Esegue il passaggio esplicito «foreground=header+f'<group android:pivotX="96" android:pivotY="96" android:scaleX="0.75" android:scaleY="0.75"><path android:fillColor="#FFFFFF" android:pathData="{path}"/></group>» nel flusso: Generatore delle icone vettoriali condivise.
foreground=header+f'<group android:pivotX="96" android:pivotY="96" android:scaleX="0.75" android:scaleY="0.75"><path android:fillColor="#FFFFFF" android:pathData="{path}"/></group></vector>\n'
# DOC L0011: Esegue il passaggio esplicito «(res/'drawable/ic_launcher_foreground.xml').write_text(foreground,encoding='utf-8')» nel flusso: Generatore delle icone vettoriali condivise.
(res/'drawable/ic_launcher_foreground.xml').write_text(foreground,encoding='utf-8')
# minSdk 26 supports adaptive icons directly. Older platforms ignore the monochrome layer.
# DOC L0013: Esegue il passaggio esplicito «folder=res/'mipmap-anydpi';folder.mkdir(exist_ok=True)» nel flusso: Generatore delle icone vettoriali condivise.
folder=res/'mipmap-anydpi';folder.mkdir(exist_ok=True)
# DOC L0014: Esegue il passaggio esplicito «(folder/'ic_launcher.xml').write_text('<?xml version="1.0" encoding="utf-8"?>\n<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android"><background android:drawabl» nel flusso: Generatore delle icone vettoriali condivise.
(folder/'ic_launcher.xml').write_text('<?xml version="1.0" encoding="utf-8"?>\n<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android"><background android:drawable="@color/ic_launcher_background"/><foreground android:drawable="@drawable/ic_launcher_foreground"/><monochrome android:drawable="@drawable/ic_launcher_foreground"/></adaptive-icon>\n',encoding='utf-8')
# DOC L0015: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
for old in [res/'drawable/ic_studio.xml', res/'mipmap-anydpi-v26/ic_launcher.xml', res/'mipmap-anydpi-v33/ic_launcher.xml']:
    # DOC L0016: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
    assert old.resolve().is_relative_to(ROOT)
    # DOC L0017: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
    if old.exists(): old.unlink()
# DOC L0018: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
for obsolete_folder in [res/'mipmap-anydpi-v26', res/'mipmap-anydpi-v33']:
    # DOC L0019: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
    assert obsolete_folder.resolve().is_relative_to(ROOT)
    # DOC L0020: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
    if obsolete_folder.exists() and not any(obsolete_folder.iterdir()): obsolete_folder.rmdir()
# DOC L0021: Esegue il passaggio esplicito «(res/'drawable/ic_timer_notification.xml').write_text('<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="24dp" android:height="24dp" android:viewpor» nel flusso: Generatore delle icone vettoriali condivise.
(res/'drawable/ic_timer_notification.xml').write_text('<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="24dp" android:height="24dp" android:viewportWidth="192" android:viewportHeight="192"><group android:pivotX="96" android:pivotY="96" android:scaleX="1.2" android:scaleY="1.2"><path android:fillColor="#FFFFFF" android:pathData="'+path+'"/></group></vector>\n',encoding='utf-8')
# DOC L0022: Esegue il passaggio esplicito «print('Shared t. icon generated: SVG, adaptive, round, monochrome and notification.')» nel flusso: Generatore delle icone vettoriali condivise.
print('Shared t. icon generated: SVG, adaptive, round, monochrome and notification.')
