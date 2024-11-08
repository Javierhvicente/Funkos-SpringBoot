package dev.javierhvicente.funkosb.storage.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

public abstract class StorageException extends RuntimeException {
    public StorageException(String message) {
        super(message);
    }
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public static class StorageInternal extends StorageException {
        public StorageInternal(String message) {
            super(message);
        }
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class StorageBadRequest extends StorageException {
        public StorageBadRequest(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class StorageFileNotFound extends StorageException {
        public StorageFileNotFound(String message) {
            super(message);
        }
    }
}
