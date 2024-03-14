package com.neo.game.audio;

import com.neo.twig.Engine;
import com.neo.twig.annotations.ForceSerialize;
import com.neo.twig.audio.AudioPlayer;
import com.neo.twig.resources.URLResource;
import com.neo.twig.scene.NodeComponent;

import java.net.URISyntaxException;

public final class AudioStreamComponent extends NodeComponent {
    @ForceSerialize
    private URLResource audioResource;
    @ForceSerialize
    private boolean autoplay;
    @ForceSerialize
    private boolean loop;
    private AudioPlayer player;

    @Override
    public void start() {
        try {
            player = Engine.getAudioService().createStreamPlayer(audioResource.get().toURI());

            if (loop) {
                player.setLooping(true);
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        if (autoplay) {
            player.setOnReadyCallback(() -> player.play());
        }
    }

    @Override
    public void destroy() {
        player.release();
    }
}
