package thread.data;

import java.util.ArrayList;
import java.util.List;

public class SharedMessage<T> {
    private T message;
    private final List<MessageChangeListener<T>> listeners = new ArrayList<>();

    public SharedMessage() {
    }
    public SharedMessage(T aDefault) {
        this.message = aDefault;
    }

    public synchronized T getMessage() {
        return message;
    }

    public synchronized void setMessage(T message) {
        this.message = message;
        notifyMessageChange(message);
        notifyAll();
    }

    public void addFrequencyChangeListener(MessageChangeListener<T> listener) {
        listeners.add(listener);
    }

    public void removeFrequencyChangeListener(MessageChangeListener<T> listener) {
        listeners.remove(listener);
    }

    private void notifyMessageChange(T newMessage) {
        for (MessageChangeListener<T> listener : listeners) {
            listener.onMessageChange(newMessage);
        }
    }
}