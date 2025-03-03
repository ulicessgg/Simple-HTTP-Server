package server.handlers.handlersConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HandleUtils {
    private static final String SERVER_ROOT = "server/data/";
    public static String getBodyMethod(File file, String fileType) throws IOException {
        String body = null;
        if (file.exists() && file.canRead()) {
            if (fileType.startsWith("text/")) {
                body = Files.readString(file.toPath());
            }
        }

        return body;
    }

    // get the expiration time 24 hours after request
    public static String getExpirationDate() {
        return new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
                .format(new Date(System.currentTimeMillis() + 86400000));
    }

    public static String getModifiedDate(File file) {
        return new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
                .format(new Date(file.lastModified()));
    }

    public static String getCurrentDate() {
        return new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
                .format(new Date());
    }

    public static File resolveFilePath(File requestedFile) throws IOException{ 
        File safeFile = new File(SERVER_ROOT, requestedFile.getName()).getCanonicalFile();
        if (!safeFile.getAbsolutePath().startsWith(new File(SERVER_ROOT).getAbsolutePath())) {
            throw new IOException("Invalid file path");
        }

        return safeFile;
    }
}
