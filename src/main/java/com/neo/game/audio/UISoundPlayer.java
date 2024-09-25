package com.neo.game.audio;

import com.neo.twig.Engine;
import com.neo.twig.audio.AudioPlayer;

import java.net.URISyntaxException;

public class UISoundPlayer {
    private final AudioPlayer player;

    public UISoundPlayer(String uiKey) {
        try {
            player = Engine.getAudioService().createOneshotPlayer(SoundConfig.getInstance().getSFXLocation(uiKey).toURI());
            player.setAudioBus("Master/UI");
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