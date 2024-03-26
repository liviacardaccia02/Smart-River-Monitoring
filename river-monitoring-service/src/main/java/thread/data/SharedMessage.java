package thread.data;

public class SharedMessage {
    private String messaggio;

    public synchronized String getMessaggio() {
        return messaggio;
    }

    public synchronized void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
        notifyAll(); // Notifica tutti i thread in attesa sul messaggio
    }
}