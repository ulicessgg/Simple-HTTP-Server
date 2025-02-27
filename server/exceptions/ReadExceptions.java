package server.exceptions;

import java.io.File;
import java.io.IOException;

import server.config.MimeTypes;

public class ReadExceptions {
    private String getFileExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        return (lastDot == -1)? "" : name.substring(lastDot + 1);
    }

    public void checkFile(File file) throws IOException {
        if (!file.exists() || !file.isFile()) {
            // TODO: return response code that a file is not found or is not a file

            throw new IOException("File does not exist or is not a file: " + file.getAbsolutePath());
        }
    }

    public String checkExtension(File file) throws IOException {
        String fileExtension = getFileExtension(file);
        String mimeType = MimeTypes.getDefault().getMimeTypeFromExtension(fileExtension);
        if (mimeType == null) {
            throw new IOException("Unsupported file type: " + fileExtension);
        }

        return mimeType;
    }

    public void checkAuthenticate(File targetFile) throws IOException {
        if (!targetFile.canWrite()) {
            throw new IOException("File can't be deleted");
        }
    }
}