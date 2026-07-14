package logic.systemsAdmin;
// crud opperations on systems users and prisoners model class

import java.io.Serializable;

public class SystemAdmin implements Serializable { 
    private static final long serialVersionUID = 1L;
    private int userId;
    private String name;
    private String password;

    public SystemAdmin(int userId, String name, String password) {
        this.userId = userId;
        this.name = name;
        this.password = password;
    }

    // Getters
    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getPassword() { return password; }
}
