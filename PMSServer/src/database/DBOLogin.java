
package database;

public interface DBOLogin {
    Object authenticateUser(int userId, String password);
}