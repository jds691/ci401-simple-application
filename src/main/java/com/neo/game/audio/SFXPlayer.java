package com.neo.game.audio;

import com.neo.twig.Engine;
import com.neo.twig.audio.AudioPlayer;

import java.net.URISyntaxException;

/**
 * Handles SFX oneshot sounds and file retrieval based on SoundConfig.
 */
public class SFXPlayer {
    private final AudioPlayer player;

    public SFXPlayer(String sfxKey) {
        try {
            player = Engine.getAudioService().createOneshotPlayer(SoundConfig.getInstance().getSFXLocation(sfxKey).toURI());
            player.setAudioBus("Master/SFX");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void play() {
        player.play();
    }

    public void stop() {
        player.stop();
    }

    public void release() {
        player.release();
    }
}
