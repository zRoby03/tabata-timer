"""Generate brief countdown beeps with an immediate attack. Standard library only, identical on both platforms."""
import io
import math
import random
from pathlib import Path
import struct
import wave

RATE = 22050
DURATION = 0.12
FREQUENCIES = {'work': {3: 330, 2: 370, 1: 440}, 'rest': {3: 660, 2: 740, 1: 880}}

def single_note(frequency):
    samples = []
    for i in range(round(RATE * DURATION)):
        t = i / RATE
        envelope = min(1.0, t / 0.003, (DURATION - t) / 0.02)
        phase = 2 * math.pi * frequency * t
        # A gentle sine tone with a small harmonic, no repeated attacks or brass phrase.
        value = 11000 * envelope * (math.sin(phase) + 0.08 * math.sin(2 * phase))
        samples.append(round(value))
    out = io.BytesIO()
    with wave.open(out, 'wb') as wav:
        wav.setnchannels(1)
        wav.setsampwidth(2)
        wav.setframerate(RATE)
        wav.writeframes(struct.pack('<' + 'h' * len(samples), *samples))
    return out.getvalue()

for kind, notes in FREQUENCIES.items():
    for second, frequency in notes.items():
        data = single_note(frequency)
        for path in [f'web/audio/countdown-{kind}-{second}.wav', f'android/app/src/main/res/raw/countdown_{kind}_{second}.wav']:
            target = Path(path)
            target.parent.mkdir(parents=True, exist_ok=True)
            target.write_bytes(data)
        print(f'{kind} {second}: {frequency} Hz, 120 ms, {len(data)} bytes')

# Previews keep one-second slots: a short beep followed by silence.
for kind in FREQUENCIES:
    frames = []
    for second in [3, 2, 1]:
        with wave.open(f'web/audio/countdown-{kind}-{second}.wav', 'rb') as wav:
            frames.append(wav.readframes(wav.getnframes()) + bytes((RATE - wav.getnframes()) * 2))
    target = Path(f'output/audio/countdown-{kind}-short-3s.wav')
    target.parent.mkdir(parents=True, exist_ok=True)
    with wave.open(str(target), 'wb') as wav:
        wav.setnchannels(1)
        wav.setsampwidth(2)
        wav.setframerate(RATE)
        wav.writeframes(b''.join(frames))


# Transition audio at 24 kHz: synthesized whistle/bell and the user-selected sigh excerpt.
MARKER_RATE = 24000

def save_marker(name, samples, boost=1.0):
    peak = max(1.0, max(abs(value) for value in samples))
    pcm = [round(value / peak * 11000 * boost) for value in samples]
    out = io.BytesIO()
    with wave.open(out, 'wb') as wav:
        wav.setnchannels(1)
        wav.setsampwidth(2)
        wav.setframerate(MARKER_RATE)
        wav.writeframes(struct.pack('<' + 'h' * len(pcm), *pcm))
    for path in [f'web/audio/{name.replace("_", "-")}.wav', f'android/app/src/main/res/raw/{name}.wav']:
        Path(path).write_bytes(out.getvalue())
    print(f'{name}: {len(pcm) / MARKER_RATE * 1000:.0f} ms, {len(out.getvalue())} bytes')

# A lower, rounder referee whistle: two close resonances, gentle pea-like flutter,
# a little filtered air and smooth edges. One continuous 500 ms cue, not repeated blasts.
whistle = []
noise = random.Random(1701)
low_air = high_air = 0.0
low_alpha = 1 - math.exp(-2 * math.pi * 700 / MARKER_RATE)
high_alpha = 1 - math.exp(-2 * math.pi * 2200 / MARKER_RATE)
for i in range(12000):
    t = i / MARKER_RATE
    envelope = min(1.0, t / .012, (.5 - t) / .055)
    flutter = .9 + .1 * math.sin(2 * math.pi * 22 * t)
    tone = math.sin(2 * math.pi * 1760 * t + .08 * math.sin(2 * math.pi * 22 * t))
    resonance = .18 * math.sin(2 * math.pi * 1880 * t)
    air = noise.uniform(-1, 1)
    low_air += low_alpha * (air - low_air)
    high_air += high_alpha * (air - high_air)
    whistle.append(envelope * (flutter * (tone + resonance) + .035 * (high_air - low_air)))
save_marker('work_start', whistle, boost=1.2)
Path('output/audio').mkdir(parents=True, exist_ok=True)
Path('output/audio/Fischio.wav').write_bytes(Path('web/audio/work-start.wav').read_bytes())

# User-selected reference, cropped locally to 500 ms; provenance is stored beside it.
with wave.open('assets/audio/sigh-reference.wav', 'rb') as wav:
    assert wav.getframerate() == MARKER_RATE and wav.getnchannels() == 1 and wav.getsampwidth() == 2
    assert wav.getnframes() == 12000
    frames = wav.readframes(wav.getnframes())
sigh = struct.unpack('<' + 'h' * (len(frames) // 2), frames)
save_marker('rest_start', sigh)

bell = [0.0] * 21600
for start in [0, .3, .6]:
    for i in range(5760):
        t = i / MARKER_RATE
        envelope = min(1.0, t / .002, (.24 - t) / .025) * math.exp(-12 * t)
        tone = math.sin(2 * math.pi * 1046.5 * t) + .35 * math.sin(2 * math.pi * 2890 * t) + .12 * math.sin(2 * math.pi * 5651 * t)
        bell[round(start * MARKER_RATE) + i] += envelope * tone
save_marker('workout_finish', bell)
