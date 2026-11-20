# DOC L0001: Esegue il passaggio esplicito «"""Generate brief countdown beeps with an immediate attack. Standard library only, identical on both platforms."""» nel flusso: Generazione locale dei segnali WAV.
"""Generate brief countdown beeps with an immediate attack. Standard library only, identical on both platforms."""
# DOC L0002: Rende disponibili le dipendenze esplicite di questa riga: import io. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import io
# DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import math. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import math
# DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import random. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import random
# DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: from pathlib import Path. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
from pathlib import Path
# DOC L0006: Rende disponibili le dipendenze esplicite di questa riga: import struct. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import struct
# DOC L0007: Rende disponibili le dipendenze esplicite di questa riga: import wave. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import wave

# DOC L0009: Esegue il passaggio esplicito «RATE = 22050» nel flusso: Generazione locale dei segnali WAV.
RATE = 22050
# DOC L0010: Esegue il passaggio esplicito «DURATION = 0.12» nel flusso: Generazione locale dei segnali WAV.
DURATION = 0.12
# DOC L0011: Esegue il passaggio esplicito «FREQUENCIES = {'work': {3: 330, 2: 370, 1: 440}, 'rest': {3: 660, 2: 740, 1: 880}}» nel flusso: Generazione locale dei segnali WAV.
FREQUENCIES = {'work': {3: 330, 2: 370, 1: 440}, 'rest': {3: 660, 2: 740, 1: 880}}

# DOC L0013: single_note: Passaggio «single_note» dello strumento: Generazione locale dei segnali WAV. Gli argomenti e le operazioni sono descritti nelle righe del corpo.
def single_note(frequency):
    # DOC L0014: Esegue il passaggio esplicito «samples = []» nel flusso: Generazione locale dei segnali WAV. Nel contesto di single_note.
    samples = []
    # DOC L0015: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di single_note.
    for i in range(round(RATE * DURATION)):
        # DOC L0016: Esegue il passaggio esplicito «t = i / RATE» nel flusso: Generazione locale dei segnali WAV. Nel contesto di single_note.
        t = i / RATE
        # DOC L0017: Esegue il passaggio esplicito «envelope = min(1.0, t / 0.003, (DURATION - t) / 0.02)» nel flusso: Generazione locale dei segnali WAV. Nel contesto di single_note.
        envelope = min(1.0, t / 0.003, (DURATION - t) / 0.02)
        # DOC L0018: Esegue il passaggio esplicito «phase = 2 * math.pi * frequency * t» nel flusso: Generazione locale dei segnali WAV. Nel contesto di single_note.
        phase = 2 * math.pi * frequency * t
        # A gentle sine tone with a small harmonic, no repeated attacks or brass phrase.
        # DOC L0020: Esegue il passaggio esplicito «value = 11000 * envelope * (math.sin(phase) + 0.08 * math.sin(2 * phase))» nel flusso: Generazione locale dei segnali WAV. Nel contesto di single_note.
        value = 11000 * envelope * (math.sin(phase) + 0.08 * math.sin(2 * phase))
        # DOC L0021: Esegue il passaggio esplicito «samples.append(round(value))» nel flusso: Generazione locale dei segnali WAV. Nel contesto di single_note.
        samples.append(round(value))
    # DOC L0022: Esegue il passaggio esplicito «out = io.BytesIO()» nel flusso: Generazione locale dei segnali WAV. Nel contesto di single_note.
    out = io.BytesIO()
    # DOC L0023: Esegue il passaggio esplicito «with wave.open(out, 'wb') as wav:» nel flusso: Generazione locale dei segnali WAV. Nel contesto di single_note.
    with wave.open(out, 'wb') as wav:
        # DOC L0024: Esegue il passaggio esplicito «wav.setnchannels(1)» nel flusso: Generazione locale dei segnali WAV. Nel contesto di single_note.
        wav.setnchannels(1)
        # DOC L0025: Esegue il passaggio esplicito «wav.setsampwidth(2)» nel flusso: Generazione locale dei segnali WAV. Nel contesto di single_note.
        wav.setsampwidth(2)
        # DOC L0026: Esegue il passaggio esplicito «wav.setframerate(RATE)» nel flusso: Generazione locale dei segnali WAV. Nel contesto di single_note.
        wav.setframerate(RATE)
        # DOC L0027: Esegue il passaggio esplicito «wav.writeframes(struct.pack('<' + 'h' * len(samples), *samples))» nel flusso: Generazione locale dei segnali WAV. Nel contesto di single_note.
        wav.writeframes(struct.pack('<' + 'h' * len(samples), *samples))
    # DOC L0028: Restituisce il valore calcolato o conclude il percorso della funzione. Nel contesto di single_note.
    return out.getvalue()

# DOC L0030: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
for kind, notes in FREQUENCIES.items():
    # DOC L0031: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
    for second, frequency in notes.items():
        # DOC L0032: Esegue il passaggio esplicito «data = single_note(frequency)» nel flusso: Generazione locale dei segnali WAV.
        data = single_note(frequency)
        # DOC L0033: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
        for path in [f'web/audio/countdown-{kind}-{second}.wav', f'android/app/src/main/res/raw/countdown_{kind}_{second}.wav']:
            # DOC L0034: Esegue il passaggio esplicito «target = Path(path)» nel flusso: Generazione locale dei segnali WAV.
            target = Path(path)
            # DOC L0035: Esegue il passaggio esplicito «target.parent.mkdir(parents=True, exist_ok=True)» nel flusso: Generazione locale dei segnali WAV.
            target.parent.mkdir(parents=True, exist_ok=True)
            # DOC L0036: Esegue il passaggio esplicito «target.write_bytes(data)» nel flusso: Generazione locale dei segnali WAV.
            target.write_bytes(data)
        # DOC L0037: Esegue il passaggio esplicito «print(f'{kind} {second}: {frequency} Hz, 120 ms, {len(data)} bytes')» nel flusso: Generazione locale dei segnali WAV.
        print(f'{kind} {second}: {frequency} Hz, 120 ms, {len(data)} bytes')

# Previews keep one-second slots: a short beep followed by silence.
# DOC L0040: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
for kind in FREQUENCIES:
    # DOC L0041: Esegue il passaggio esplicito «frames = []» nel flusso: Generazione locale dei segnali WAV.
    frames = []
    # DOC L0042: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
    for second in [3, 2, 1]:
        # DOC L0043: Esegue il passaggio esplicito «with wave.open(f'web/audio/countdown-{kind}-{second}.wav', 'rb') as wav:» nel flusso: Generazione locale dei segnali WAV.
        with wave.open(f'web/audio/countdown-{kind}-{second}.wav', 'rb') as wav:
            # DOC L0044: Esegue il passaggio esplicito «frames.append(wav.readframes(wav.getnframes()) + bytes((RATE - wav.getnframes()) * 2))» nel flusso: Generazione locale dei segnali WAV.
            frames.append(wav.readframes(wav.getnframes()) + bytes((RATE - wav.getnframes()) * 2))
    # DOC L0045: Esegue il passaggio esplicito «target = Path(f'output/audio/countdown-{kind}-short-3s.wav')» nel flusso: Generazione locale dei segnali WAV.
    target = Path(f'output/audio/countdown-{kind}-short-3s.wav')
    # DOC L0046: Esegue il passaggio esplicito «target.parent.mkdir(parents=True, exist_ok=True)» nel flusso: Generazione locale dei segnali WAV.
    target.parent.mkdir(parents=True, exist_ok=True)
    # DOC L0047: Esegue il passaggio esplicito «with wave.open(str(target), 'wb') as wav:» nel flusso: Generazione locale dei segnali WAV.
    with wave.open(str(target), 'wb') as wav:
        # DOC L0048: Esegue il passaggio esplicito «wav.setnchannels(1)» nel flusso: Generazione locale dei segnali WAV.
        wav.setnchannels(1)
        # DOC L0049: Esegue il passaggio esplicito «wav.setsampwidth(2)» nel flusso: Generazione locale dei segnali WAV.
        wav.setsampwidth(2)
        # DOC L0050: Esegue il passaggio esplicito «wav.setframerate(RATE)» nel flusso: Generazione locale dei segnali WAV.
        wav.setframerate(RATE)
        # DOC L0051: Esegue il passaggio esplicito «wav.writeframes(b''.join(frames))» nel flusso: Generazione locale dei segnali WAV.
        wav.writeframes(b''.join(frames))


# Transition audio at 24 kHz: synthesized whistle/bell and the user-selected sigh excerpt.
# DOC L0055: Esegue il passaggio esplicito «MARKER_RATE = 24000» nel flusso: Generazione locale dei segnali WAV.
MARKER_RATE = 24000

# DOC L0057: save_marker: Passaggio «save_marker» dello strumento: Generazione locale dei segnali WAV. Gli argomenti e le operazioni sono descritti nelle righe del corpo.
def save_marker(name, samples, boost=1.0):
    # DOC L0058: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di save_marker.
    peak = max(1.0, max(abs(value) for value in samples))
    # DOC L0059: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di save_marker.
    pcm = [round(value / peak * 11000 * boost) for value in samples]
    # DOC L0060: Esegue il passaggio esplicito «out = io.BytesIO()» nel flusso: Generazione locale dei segnali WAV. Nel contesto di save_marker.
    out = io.BytesIO()
    # DOC L0061: Esegue il passaggio esplicito «with wave.open(out, 'wb') as wav:» nel flusso: Generazione locale dei segnali WAV. Nel contesto di save_marker.
    with wave.open(out, 'wb') as wav:
        # DOC L0062: Esegue il passaggio esplicito «wav.setnchannels(1)» nel flusso: Generazione locale dei segnali WAV. Nel contesto di save_marker.
        wav.setnchannels(1)
        # DOC L0063: Esegue il passaggio esplicito «wav.setsampwidth(2)» nel flusso: Generazione locale dei segnali WAV. Nel contesto di save_marker.
        wav.setsampwidth(2)
        # DOC L0064: Esegue il passaggio esplicito «wav.setframerate(MARKER_RATE)» nel flusso: Generazione locale dei segnali WAV. Nel contesto di save_marker.
        wav.setframerate(MARKER_RATE)
        # DOC L0065: Esegue il passaggio esplicito «wav.writeframes(struct.pack('<' + 'h' * len(pcm), *pcm))» nel flusso: Generazione locale dei segnali WAV. Nel contesto di save_marker.
        wav.writeframes(struct.pack('<' + 'h' * len(pcm), *pcm))
    # DOC L0066: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti. Nel contesto di save_marker.
    for path in [f'web/audio/{name.replace("_", "-")}.wav', f'android/app/src/main/res/raw/{name}.wav']:
        # DOC L0067: Esegue il passaggio esplicito «Path(path).write_bytes(out.getvalue())» nel flusso: Generazione locale dei segnali WAV. Nel contesto di save_marker.
        Path(path).write_bytes(out.getvalue())
    # DOC L0068: Esegue il passaggio esplicito «print(f'{name}: {len(pcm) / MARKER_RATE * 1000:.0f} ms, {len(out.getvalue())} bytes')» nel flusso: Generazione locale dei segnali WAV. Nel contesto di save_marker.
    print(f'{name}: {len(pcm) / MARKER_RATE * 1000:.0f} ms, {len(out.getvalue())} bytes')

# A lower, rounder referee whistle: two close resonances, gentle pea-like flutter,
# a little filtered air and smooth edges. One continuous 500 ms cue, not repeated blasts.
# DOC L0072: Esegue il passaggio esplicito «whistle = []» nel flusso: Generazione locale dei segnali WAV.
whistle = []
# DOC L0073: Esegue il passaggio esplicito «noise = random.Random(1701)» nel flusso: Generazione locale dei segnali WAV.
noise = random.Random(1701)
# DOC L0074: Esegue il passaggio esplicito «low_air = high_air = 0.0» nel flusso: Generazione locale dei segnali WAV.
low_air = high_air = 0.0
# DOC L0075: Esegue il passaggio esplicito «low_alpha = 1 - math.exp(-2 * math.pi * 700 / MARKER_RATE)» nel flusso: Generazione locale dei segnali WAV.
low_alpha = 1 - math.exp(-2 * math.pi * 700 / MARKER_RATE)
# DOC L0076: Esegue il passaggio esplicito «high_alpha = 1 - math.exp(-2 * math.pi * 2200 / MARKER_RATE)» nel flusso: Generazione locale dei segnali WAV.
high_alpha = 1 - math.exp(-2 * math.pi * 2200 / MARKER_RATE)
# DOC L0077: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
for i in range(12000):
    # DOC L0078: Esegue il passaggio esplicito «t = i / MARKER_RATE» nel flusso: Generazione locale dei segnali WAV.
    t = i / MARKER_RATE
    # DOC L0079: Esegue il passaggio esplicito «envelope = min(1.0, t / .012, (.5 - t) / .055)» nel flusso: Generazione locale dei segnali WAV.
    envelope = min(1.0, t / .012, (.5 - t) / .055)
    # DOC L0080: Esegue il passaggio esplicito «flutter = .9 + .1 * math.sin(2 * math.pi * 22 * t)» nel flusso: Generazione locale dei segnali WAV.
    flutter = .9 + .1 * math.sin(2 * math.pi * 22 * t)
    # DOC L0081: Esegue il passaggio esplicito «tone = math.sin(2 * math.pi * 1760 * t + .08 * math.sin(2 * math.pi * 22 * t))» nel flusso: Generazione locale dei segnali WAV.
    tone = math.sin(2 * math.pi * 1760 * t + .08 * math.sin(2 * math.pi * 22 * t))
    # DOC L0082: Esegue il passaggio esplicito «resonance = .18 * math.sin(2 * math.pi * 1880 * t)» nel flusso: Generazione locale dei segnali WAV.
    resonance = .18 * math.sin(2 * math.pi * 1880 * t)
    # DOC L0083: Esegue il passaggio esplicito «air = noise.uniform(-1, 1)» nel flusso: Generazione locale dei segnali WAV.
    air = noise.uniform(-1, 1)
    # DOC L0084: Esegue il passaggio esplicito «low_air += low_alpha * (air - low_air)» nel flusso: Generazione locale dei segnali WAV.
    low_air += low_alpha * (air - low_air)
    # DOC L0085: Esegue il passaggio esplicito «high_air += high_alpha * (air - high_air)» nel flusso: Generazione locale dei segnali WAV.
    high_air += high_alpha * (air - high_air)
    # DOC L0086: Esegue il passaggio esplicito «whistle.append(envelope * (flutter * (tone + resonance) + .035 * (high_air - low_air)))» nel flusso: Generazione locale dei segnali WAV.
    whistle.append(envelope * (flutter * (tone + resonance) + .035 * (high_air - low_air)))
# DOC L0087: Esegue il passaggio esplicito «save_marker('work_start', whistle, boost=1.2)» nel flusso: Generazione locale dei segnali WAV.
save_marker('work_start', whistle, boost=1.2)
# DOC L0088: Esegue il passaggio esplicito «Path('output/audio').mkdir(parents=True, exist_ok=True)» nel flusso: Generazione locale dei segnali WAV.
Path('output/audio').mkdir(parents=True, exist_ok=True)
# DOC L0089: Esegue il passaggio esplicito «Path('output/audio/Fischio.wav').write_bytes(Path('web/audio/work-start.wav').read_bytes())» nel flusso: Generazione locale dei segnali WAV.
Path('output/audio/Fischio.wav').write_bytes(Path('web/audio/work-start.wav').read_bytes())

# User-selected reference, cropped locally to 500 ms; provenance is stored beside it.
# DOC L0092: Esegue il passaggio esplicito «with wave.open('assets/audio/sigh-reference.wav', 'rb') as wav:» nel flusso: Generazione locale dei segnali WAV.
with wave.open('assets/audio/sigh-reference.wav', 'rb') as wav:
    # DOC L0093: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
    assert wav.getframerate() == MARKER_RATE and wav.getnchannels() == 1 and wav.getsampwidth() == 2
    # DOC L0094: Verifica la condizione esplicita; un fallimento segnala un contratto non rispettato o rifiuta l’operazione prima di proseguire.
    assert wav.getnframes() == 12000
    # DOC L0095: Esegue il passaggio esplicito «frames = wav.readframes(wav.getnframes())» nel flusso: Generazione locale dei segnali WAV.
    frames = wav.readframes(wav.getnframes())
# DOC L0096: Esegue il passaggio esplicito «sigh = struct.unpack('<' + 'h' * (len(frames) // 2), frames)» nel flusso: Generazione locale dei segnali WAV.
sigh = struct.unpack('<' + 'h' * (len(frames) // 2), frames)
# DOC L0097: Esegue il passaggio esplicito «save_marker('rest_start', sigh)» nel flusso: Generazione locale dei segnali WAV.
save_marker('rest_start', sigh)

# DOC L0099: Esegue il passaggio esplicito «bell = [0.0] * 21600» nel flusso: Generazione locale dei segnali WAV.
bell = [0.0] * 21600
# DOC L0100: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
for start in [0, .3, .6]:
    # DOC L0101: Elabora gli elementi o le iterazioni indicati; gli indici distinguono l’ultimo elemento per le pause o i limiti previsti.
    for i in range(5760):
        # DOC L0102: Esegue il passaggio esplicito «t = i / MARKER_RATE» nel flusso: Generazione locale dei segnali WAV.
        t = i / MARKER_RATE
        # DOC L0103: Esegue il passaggio esplicito «envelope = min(1.0, t / .002, (.24 - t) / .025) * math.exp(-12 * t)» nel flusso: Generazione locale dei segnali WAV.
        envelope = min(1.0, t / .002, (.24 - t) / .025) * math.exp(-12 * t)
        # DOC L0104: Esegue il passaggio esplicito «tone = math.sin(2 * math.pi * 1046.5 * t) + .35 * math.sin(2 * math.pi * 2890 * t) + .12 * math.sin(2 * math.pi * 5651 * t)» nel flusso: Generazione locale dei segnali WAV.
        tone = math.sin(2 * math.pi * 1046.5 * t) + .35 * math.sin(2 * math.pi * 2890 * t) + .12 * math.sin(2 * math.pi * 5651 * t)
        # DOC L0105: Esegue il passaggio esplicito «bell[round(start * MARKER_RATE) + i] += envelope * tone» nel flusso: Generazione locale dei segnali WAV.
        bell[round(start * MARKER_RATE) + i] += envelope * tone
# DOC L0106: Esegue il passaggio esplicito «save_marker('workout_finish', bell)» nel flusso: Generazione locale dei segnali WAV.
save_marker('workout_finish', bell)
