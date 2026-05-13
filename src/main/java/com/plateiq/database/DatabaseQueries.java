package com.plateiq.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DatabaseQueries {

    private static final Logger LOGGER = Logger.getLogger(DatabaseQueries.class.getName());

    private DatabaseQueries() {
        // Utility class.
    }

    @FunctionalInterface
    public interface ResultSetMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }

    public static int executeUpdate(String sql, Object... params) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, params)) {
            return stmt.executeUpdate();
        }
    }

    public static <T> T executeQuerySingle(String sql, ResultSetMapper<T> mapper, Object... params) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, params);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return mapper.map(rs);
            }
            return null;
        }
    }

    public static <T> List<T> executeQueryList(String sql, ResultSetMapper<T> mapper, Object... params) throws SQLException {
        List<T> results = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, params);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                results.add(mapper.map(rs));
            }
        }
        return results;
    }

    private static PreparedStatement prepareStatement(Connection conn, String sql, Object... params) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            int index = i + 1;
            if (param == null) {
                stmt.setNull(index, Types.NULL);
            } else if (param instanceof LocalDate) {
                stmt.setDate(index, Date.valueOf((LocalDate) param));
            } else {
                stmt.setObject(index, param);
            }
        }
        return stmt;
    }

    public static boolean executeExists(String sql, Object... params) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, params);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next();
        }
    }
}
