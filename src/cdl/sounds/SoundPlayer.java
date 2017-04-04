package cdl.sounds;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class SoundPlayer {

	public static final SoundPlayer SP = new SoundPlayer();

	public static final Path MENU = Paths.get("sounds", "menu.wav");
	public static final Path GAME = Paths.get("sounds", "game.wav");
	public static final Path JUMP = Paths.get("sounds", "jump.wav");

	private Clip clip;

	private SoundPlayer() {
	}

	public void playSound(Path path) {
		try {
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(path.toFile());
			Clip sound = AudioSystem.getClip();
			if (sound != null) {
				sound.open(inputStream);

				FloatControl ctrl = (FloatControl) sound.getControl(FloatControl.Type.MASTER_GAIN);
				ctrl.setValue(6);
				sound.loop(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void playMusic(Path p) {
		if (clip == null) {
			try {
				AudioInputStream inputStream = AudioSystem.getAudioInputStream(p.toFile());
				clip = AudioSystem.getClip();
				if (clip != null) {
					clip.open(inputStream);

					FloatControl ctrl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
					ctrl.setValue(-15);

					clip.loop(Clip.LOOP_CONTINUOUSLY);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			this.changeMusicTo(p);
		}
	}

	public void changeMusicTo(Path p) {
		if (clip != null) {
			try {
				AudioInputStream inputStream = AudioSystem.getAudioInputStream(p.toFile());
				clip.close();
				clip = AudioSystem.getClip();
				if (clip != null) {
					clip.open(inputStream);

					FloatControl ctrl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
					ctrl.setValue(-10);

					clip.loop(Clip.LOOP_CONTINUOUSLY);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			this.playMusic(p);
		}
	}
}
