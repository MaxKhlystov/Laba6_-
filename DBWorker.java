package repository;

import model.Battlefield;
import model.HeavyTank;
import model.LightTank;
import model.Tank;

import java.sql.*;

public class DBWorker {
    public static final String PATH_TO_DB_FILE = "tanks.db";

    static {
        try {
            // Регистрация драйвера
            Class.forName("org.sqlite.JDBC");

            // Подключение к БД
            try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + PATH_TO_DB_FILE)) {
                // Создание таблицы, если её нет
                createTables(connection);

                System.out.println("Подключение к SQLite успешно установлено");
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Ошибка при работе с БД: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createTables(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Таблица для танков (общие свойства)
            stmt.execute("CREATE TABLE IF NOT EXISTS tanks (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "hp INTEGER NOT NULL CHECK (hp > 0), " +
                    "type TEXT NOT NULL CHECK (type IN ('HEAVY', 'LIGHT')), " +
                    "special_value INTEGER NOT NULL)");
        }
    }

    public static void saveBattlefield(Battlefield battlefield) {
        if (battlefield == null || battlefield.getTanks().isEmpty()) {
            System.out.println("Нет данных для сохранения");
            return;
        }

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + PATH_TO_DB_FILE)) {
            // Отключаем авто-коммит для транзакции
            connection.setAutoCommit(false);

            try {
                // 1. Очистка таблицы
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("DELETE FROM tanks");
                }

                // 2. Вставка новых данных
                try (PreparedStatement pstmt = connection.prepareStatement(
                        "INSERT INTO tanks (name, hp, type, special_value) VALUES (?, ?, ?, ?)")) {

                    for (Tank tank : battlefield.getTanks()) {
                        pstmt.setString(1, tank.getName());
                        pstmt.setInt(2, tank.getHPTank());

                        if (tank instanceof HeavyTank) {
                            pstmt.setString(3, "HEAVY");
                            pstmt.setInt(4, ((HeavyTank) tank).getArmorThickness());
                        } else if (tank instanceof LightTank) {
                            pstmt.setString(3, "LIGHT");
                            pstmt.setInt(4, ((LightTank) tank).getViewRange());
                        }

                        pstmt.addBatch();
                    }

                    pstmt.executeBatch();
                }

                // Фиксируем транзакцию
                connection.commit();
                System.out.println("Данные успешно сохранены в БД");

            } catch (SQLException e) {
                // Откатываем при ошибке
                connection.rollback();
                System.err.println("Ошибка при сохранении в БД: " + e.getMessage());
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.err.println("Ошибка подключения к БД: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Battlefield loadBattlefield() {
        Battlefield battlefield = new Battlefield();
        battlefield.getTanks().clear();

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + PATH_TO_DB_FILE);
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM tanks")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int hp = rs.getInt("hp");
                String type = rs.getString("type");
                int specialValue = rs.getInt("special_value");

                if ("HEAVY".equals(type)) {
                    battlefield.addTank(new HeavyTank(id, name, hp, specialValue));
                } else if ("LIGHT".equals(type)) {
                    battlefield.addTank(new LightTank(id, name, hp, specialValue));
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при загрузке из БД: " + e.getMessage());
            e.printStackTrace();
        }

        return battlefield;
    }
}