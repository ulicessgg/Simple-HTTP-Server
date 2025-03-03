package server.auth;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public class AuthUtils {
    private String documentRoot;

    public AuthUtils(String documentRoot)
    {
        this.documentRoot = documentRoot;
    }

    public void loadPasswordFile(Map<String, String> cred, String passwordsPath) {
        try {
            File passFile = new File(passwordsPath);
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

    public boolean requiresAuth(String path) {
        if (path == null || documentRoot == null) {
            return false;
        }

        File requestedFile = new File(documentRoot, path);
        File parentDir = requestedFile.getParentFile();

        if (parentDir == null) {
            return false;
        }

        String passwordFileName = requestedFile.getName() + ".passwords";
        File passwordFile = new File(parentDir, passwordFileName);

        return passwordFile.exists();
    }
}
