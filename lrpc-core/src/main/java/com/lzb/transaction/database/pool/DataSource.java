package com.lzb.transaction.database.pool;

import java.sql.Connection;

public interface DataSource {
    Connection getConnection();
    void closeConnection();
}
