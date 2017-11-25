package tpchat.client;

/**
 *
 * @author paul
 */
public enum MessageType {
    MSG("msg"),
    HIST("hist");
    
    private String id;
    
    private MessageType(String id) {
        this.id = id;
    }
    
    @Override
    public String toString() {
        return id;
    }
}
