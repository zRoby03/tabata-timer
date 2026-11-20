"""Generate matching SVG/Android vector icons from the shared t. outline. Standard library only."""
from pathlib import Path
ROOT=Path(__file__).resolve().parent.parent
path=(ROOT/'assets/branding/t-dot-path.txt').read_text(encoding='utf-8').strip()
background='#8650A7'
(ROOT/'web/icon.svg').write_text(f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 192 192"><rect width="192" height="192" rx="52" fill="{background}"/><path fill="white" d="{path}"/></svg>\n',encoding='utf-8')
res=ROOT/'android/app/src/main/res'
(res/'values/icon_colors.xml').write_text(f'<?xml version="1.0" encoding="utf-8"?>\n<resources><color name="ic_launcher_background">{background}</color></resources>\n',encoding='utf-8')
header='<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="108dp" android:height="108dp" android:viewportWidth="192" android:viewportHeight="192">'
foreground=header+f'<group android:pivotX="96" android:pivotY="96" android:scaleX="0.75" android:scaleY="0.75"><path android:fillColor="#FFFFFF" android:pathData="{path}"/></group></vector>\n'
(res/'drawable/ic_launcher_foreground.xml').write_text(foreground,encoding='utf-8')
# minSdk 26 supports adaptive icons directly. Older platforms ignore the monochrome layer.
folder=res/'mipmap-anydpi';folder.mkdir(exist_ok=True)
(folder/'ic_launcher.xml').write_text('<?xml version="1.0" encoding="utf-8"?>\n<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android"><background android:drawable="@color/ic_launcher_background"/><foreground android:drawable="@drawable/ic_launcher_foreground"/><monochrome android:drawable="@drawable/ic_launcher_foreground"/></adaptive-icon>\n',encoding='utf-8')
for old in [res/'drawable/ic_studio.xml', res/'mipmap-anydpi-v26/ic_launcher.xml', res/'mipmap-anydpi-v33/ic_launcher.xml']:
    assert old.resolve().is_relative_to(ROOT)
    if old.exists(): old.unlink()
for obsolete_folder in [res/'mipmap-anydpi-v26', res/'mipmap-anydpi-v33']:
    assert obsolete_folder.resolve().is_relative_to(ROOT)
    if obsolete_folder.exists() and not any(obsolete_folder.iterdir()): obsolete_folder.rmdir()
(res/'drawable/ic_timer_notification.xml').write_text('<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="24dp" android:height="24dp" android:viewportWidth="192" android:viewportHeight="192"><group android:pivotX="96" android:pivotY="96" android:scaleX="1.2" android:scaleY="1.2"><path android:fillColor="#FFFFFF" android:pathData="'+path+'"/></group></vector>\n',encoding='utf-8')
print('Shared t. icon generated: SVG, adaptive, round, monochrome and notification.')
