package thread.data;

public interface MessageChangeListener<T> {
    void onMessageChange(T newMessage);
}