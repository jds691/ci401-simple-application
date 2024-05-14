package com.neo.game.message;

import com.neo.twig.scene.NodeComponent;

import java.util.ArrayDeque;

public class MessageServiceComponent extends NodeComponent {
    private static MessageServiceComponent instance;

    private MessageServiceUIComponent uiComponent;
    private ArrayDeque<Message> messageQueue = new ArrayDeque<>();

    private boolean isShowingMessage = false;

    public static MessageServiceComponent getInstance() {
        if (instance == null) {
            return null;
        }

        return instance;
    }

    @Override
    public void start() {
        super.start();

        uiComponent = getNode().getComponent(MessageServiceUIComponent.class);
        uiComponent.getOnMessageCompleteEvent().addHandler(this::handleMessageComplete);

        instance = this;
    }

    public void addToQueue(Message message) {
        if (!isShowingMessage) {
            processMessage(message);
        } else {
            messageQueue.add(message);
        }
    }

    public void removeFromQueue(Message message) {
        messageQueue.remove(message);
    }

    public void stopCurrentMessage() {
        handleMessageComplete(null);
    }

    private void processMessage(Message message) {
        isShowingMessage = true;

        uiComponent.setTitle(message.title());
        uiComponent.setText(message.message());
        uiComponent.setButtons(message.options());

        uiComponent.setVisible(true);
    }

    private void handleMessageComplete(Object ignored) {
        uiComponent.setVisible(false);
        isShowingMessage = false;

        if (!messageQueue.isEmpty())
            processMessage(messageQueue.pop());
    }
}
