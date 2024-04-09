package thread.data;

public class SharedMessage<T> {
    private T message;

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
        notifyAll();
    }
}