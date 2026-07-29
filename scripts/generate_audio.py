import wave
import struct
import math
import os

def generate_sine_wave(frequency, duration, volume=0.5, sample_rate=44100):
    num_samples = int(duration * sample_rate)
    samples = []
    for i in range(num_samples):
        value = int(volume * 32767.0 * math.sin(2.0 * math.pi * frequency * i / sample_rate))
        samples.append(value)
    return samples

def save_wav(filename, samples, sample_rate=44100):
    with wave.open(filename, 'w') as f:
        f.setnchannels(1)
        f.setsampwidth(2)
        f.setframerate(sample_rate)
        for s in samples:
            f.writeframesraw(struct.pack('<h', s))

output_dir = 'app/src/main/res/raw'
if not os.path.exists(output_dir):
    os.makedirs(output_dir)

# win.wav: Triumphant upward notes
win_samples = []
win_samples.extend(generate_sine_wave(440, 0.1)) # A4
win_samples.extend(generate_sine_wave(554, 0.1)) # C#5
win_samples.extend(generate_sine_wave(659, 0.1)) # E5
win_samples.extend(generate_sine_wave(880, 0.3)) # A5
save_wav(os.path.join(output_dir, 'win.wav'), win_samples)

# lose.wav: Discordant downward notes
lose_samples = []
lose_samples.extend(generate_sine_wave(220, 0.2)) # A3
lose_samples.extend(generate_sine_wave(207, 0.2)) # G#3
lose_samples.extend(generate_sine_wave(196, 0.4)) # G3
save_wav(os.path.join(output_dir, 'lose.wav'), lose_samples)

# milestone.wav: High ding
milestone_samples = generate_sine_wave(1760, 0.1) # A6
save_wav(os.path.join(output_dir, 'milestone.wav'), milestone_samples)

# start.wav: Upward arpeggio
start_samples = []
start_samples.extend(generate_sine_wave(261.63, 0.05)) # C4
start_samples.extend(generate_sine_wave(329.63, 0.05)) # E4
start_samples.extend(generate_sine_wave(392.00, 0.05)) # G4
start_samples.extend(generate_sine_wave(523.25, 0.1))  # C5
save_wav(os.path.join(output_dir, 'start.wav'), start_samples)

# touch.wav: Short high blip
touch_samples = generate_sine_wave(880, 0.05, volume=0.3) # A5
save_wav(os.path.join(output_dir, 'touch.wav'), touch_samples)

# collect.wav: Bright double blip
collect_samples = []
collect_samples.extend(generate_sine_wave(1046.50, 0.05)) # C6
collect_samples.extend(generate_sine_wave(1318.51, 0.1))  # E6
save_wav(os.path.join(output_dir, 'collect.wav'), collect_samples)

print("Audio files generated in " + output_dir)
