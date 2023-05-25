package com.skyblock.skyblock.sql;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.UUID;

public class SQLConfiguration {

    private static final String TABLE_NAME = "players";
    public static final File f = new File(Skyblock.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "database.db");

    public static boolean delete(UUID uuid) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + f.getAbsolutePath());

            String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = '" + uuid.toString() + "'";

            Statement statement = connection.createStatement();

            statement.execute(sql);

            return true;
        } catch (SQLException | ClassNotFoundException e) {
            return false;
        }
    }

    private final File file;
    private FileConfiguration config;
    private final SkyblockPlayer player;
    private final Connection connection;

    public SQLConfiguration(File file, SkyblockPlayer player) {
        this.file = file;
        this.player = player;

        this.config = YamlConfiguration.loadConfiguration(file);

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + f.getAbsolutePath());

            String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    "id TEXT PRIMARY KEY" +
                    ")";

            connection.createStatement().execute(sql);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void initializeUUID() {
        try {
            String sql = "INSERT INTO " + TABLE_NAME + " (id) VALUES (?)";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, player.getBukkitPlayer().getUniqueId().toString());

            statement.execute();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public Object get(String original) {
        String path = parsePaths(original);

        if (!columnExists(path)) {
            updateYamlFile();
            return config.get(original);
        }

        try {
            String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = " + getId();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    Object obj = resultSet.getObject(path);

                    if (obj instanceof String) {
                        String s = (String) obj;

                        if (s.equals("true") || s.equals("false")) return Boolean.parseBoolean(s);
                    } else {
                        return obj;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public int getInt(String path) {
        Object value = get(path);
        if (value instanceof Integer) {
            return (int) value;
        } else {
            return 0;
        }
    }

    public String getString(String path) {
        Object value = get(path);
        if (value instanceof String) {
            return (String) value;
        } else {
            return "";
        }
    }

    public long getLong(String path) {
        Object value = get(path);
        if (value instanceof Long) {
            return (long) value;
        } else {
            return 0L;
        }
    }

    public double getDouble(String path) {
        Object value = get(path);
        if (value instanceof Double) {
            return (double) value;
        } else {
            return 0.0;
        }
    }

    public float getFloat(String path) {
        Object value = get(path);
        if (value instanceof Float) {
            return (float) value;
        } else {
            return 0.0f;
        }
    }

    public boolean getBoolean(String path) {
        Object value = get(path);
        if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        } else {
            return false;
        }
    }

    public ConfigurationSection getConfigurationSection(String path) {
        return (ConfigurationSection) get(path);
    }

    public void set(String original, Object o) {
        String path = parsePaths(original);

        if (parseColumnType(o).equals("NULL")) {
            this.config.set(original, o);
            updateYamlFile();
            return;
        }

        try {
            if (!columnExists(path)) {
                String columnType = parseColumnType(o);
                String sql = "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + path + " " + columnType;

                Statement statement = connection.createStatement();

                statement.execute(sql);
            }

            if (o instanceof Boolean) {
                o = o.toString();
            }

            String sql = "UPDATE " + TABLE_NAME + " SET " + path + " = '" + o + "' WHERE id = " + getId();

            Statement statement = connection.createStatement();

            statement.executeUpdate(sql);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void updateYamlFile() {
        try {
            this.config.save(file);
            this.config = YamlConfiguration.loadConfiguration(file);
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Failed to save Yaml Data for player: " + player.getBukkitPlayer().getName());
        }
    }

    private String parseColumnType(Object o) {
        if (o instanceof String) {
            return "TEXT";
        } else if (o instanceof Integer) {
            return "INT";
        } else if (o instanceof Long) {
            return "BIGINT";
        } else if (o instanceof Double) {
            return "DOUBLE";
        } else if (o instanceof Float) {
            return "FLOAT";
        } else if (o instanceof Boolean) {
            return "TEXT";
        } else if (o instanceof java.util.Date) {
            return "DATE";
        }

        return "NULL";
    }

    private boolean columnExists(String columnName) {
        try {
            DatabaseMetaData metadata = connection.getMetaData();
            try (ResultSet resultSet = metadata.getColumns(null, null, TABLE_NAME, columnName)) {
                return resultSet.next();
            }
        } catch (SQLException e) { e.printStackTrace(); }

        return false;
    }

    private String parsePaths(String path) {
        return path.replaceAll("\\.", "_").replaceAll(" ", "__");
    }

    private String getId() {
        return "'" + player.getBukkitPlayer().getUniqueId().toString() + "'";
    }

}
