package server.auth;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public class AuthUtils {
    public static void loadPasswordFile(Map<String, String> cred) {
        try {
            File passFile = new File("a-secret/files.password");
            if (passFile.exists()) {
                List<String> lines = Files.readAllLines(passFile.toPath());
                for (String line : lines) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        cred.put(parts[0].trim(), parts[1].trim());
                    }
                }
            }
        }
        catch (IOException e) {
            System.err.println("Can't load password file: " + e.getMessage());
        }
    }

    public static boolean requiresAuth(String path) {
        return (path != null) && (path.endsWith(".password"));
    }

    public String getPassword(File passwordFile) throws Exception {
        if (!passwordFile.exists() || !passwordFile.getName().endsWith(".password")) {
            throw new Exception("Password file not found");
        }

        return Files.readString(passwordFile.toPath());
    }
}
