package com.neo.game.message;

import com.neo.twig.scene.NodeComponent;

import java.util.ArrayDeque;

/**
 * The service responsible for creating and showing UI messages
 */
public class MessageServiceComponent extends NodeComponent {
    private static MessageServiceComponent instance;

    private MessageServiceUIComponent uiComponent;
    private ArrayDeque<Message> messageQueue = new ArrayDeque<>();

    private boolean isShowingMessage = false;

    /**
     * Gets the singleton instance of this object
     *
     * @return Singleton instance
     */
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

    /**
     * Adds the message to the message queue.
     *
     * <p>
     *     If a message is currently being shown it is added to a queue.
     *     Once the current message is finished the next message in the queue will be shown.
     *     Otherwise, the message is shown immediately
     * </p>
     *
     * @param message Message to add
     */
    public void addToQueue(Message message) {
        if (!isShowingMessage) {
            processMessage(message);
        } else {
            messageQueue.add(message);
        }
    }

    /**
     * Removes the specified message from the processing queue.
     *
     * <p>
     *     It is not possible to remove a message if it is currently being shown
     * </p>
     *
     * @param message Message to remove
     */
    public void removeFromQueue(Message message) {
        messageQueue.remove(message);
    }

    /**
     * Forces the current message to be stopped.
     *
     * <p>
     *     If there are more messages in the queue they will still be processed
     * </p>
     */
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
