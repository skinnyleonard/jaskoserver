package online;

import javax.websocket.Session;

public class User {
    private String id;
    private String username;
    private Session session;

    public User(String id, String username, Session session) {
        this.id = id;
        this.username = username;
        this.session = session;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Session getSession() {
        return session;
    }
}
