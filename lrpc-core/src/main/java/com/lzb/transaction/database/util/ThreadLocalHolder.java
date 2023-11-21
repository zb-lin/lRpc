package com.lzb.transaction.database.util;

import java.sql.Connection;

public class ThreadLocalHolder {

    private final ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

    public Connection getConnection() {
        return threadLocal.get();
    }

    public void setConnection(Connection connection) {
        threadLocal.set(connection);
    }
}
