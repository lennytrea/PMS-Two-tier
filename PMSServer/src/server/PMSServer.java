package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class PMSServer {
    public static void main(String[] args) {
        try {
            // 1. Creating instance of the provider
            PrisonServiceProvider provider = new PrisonServiceProvider();

            // 2. Starting on port 1099
            Registry registry = LocateRegistry.createRegistry(1099);

            // 3. Registering with a lookup key
            registry.rebind("PrisonService", provider);
            
            System.out.println("  Prison Management RMI Server Running....... ");
            
        } catch (Exception e) {
            System.err.println("Server startup failed:");
            e.printStackTrace();
        }
    }
}