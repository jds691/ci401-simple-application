package com.neo.game.audio;

import com.neo.twig.Engine;
import com.neo.twig.annotations.ForceSerialize;
import com.neo.twig.audio.AudioPlayer;
import com.neo.twig.scene.NodeComponent;

import java.net.URISyntaxException;

/**
 * Handles music playback, looping and file retrieval based on SoundConfig.
 */
public class MusicComponent extends NodeComponent {
    @ForceSerialize
    private String musicConfigKey;
    @ForceSerialize
    private boolean autoplay;
    @ForceSerialize
    private boolean loop;
    private AudioPlayer player;

    @Override
    public void start() {
        try {
            player = Engine.getAudioService().createStreamPlayer(SoundConfig.getInstance().getMusicLocation(musicConfigKey).toURI());
            player.setAudioBus("Master/Music");

            player.setLooping(loop);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        if (autoplay) {
            player.setOnReadyCallback(() -> player.play());
        }
    }

    @Override
    public void destroy() {
        player.pause();
        player.release();
    }

    public void stop() {
        player.stop();
    }

    public void play() {
        player.play();
    }

    public void pause() {
        player.pause();
    }
}
