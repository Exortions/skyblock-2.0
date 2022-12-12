package com.skyblock.skyblock.features.bazaar;

import com.skyblock.skyblock.features.bazaar.escrow.Escrow;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

public interface Bazaar {

    class BazaarItemNotFoundException extends Exception {

        public BazaarItemNotFoundException(String message) {
            super(message);
        }

        public BazaarItemNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }

    }

    class BazaarIOException extends Exception {

        public BazaarIOException(String message) {
            super(message);
        }

        public BazaarIOException(String message, Throwable cause) {
            super(message, cause);
        }

    }

    String FILE_NAME = "bazaar.yml";

    YamlConfiguration getBazaarConfig();

    Escrow getEscrow();

    List<BazaarCategory> getCategories();

    BazaarItemData getItemData(String name) throws BazaarItemNotFoundException;

    void set(String path, Object value) throws BazaarIOException;
    Object get(String path);

    <T> T get(String path, Class<T> clazz);

    default int getCategoryAmount() {
        return this.getCategories().size();
    }

}
