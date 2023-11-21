package com.lzb.transaction.database.pool;


import com.lzb.config.RpcConfig;
import com.lzb.transaction.database.util.ThreadLocalHolder;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class DefaultDataSource implements DataSource {

    private static final class DefaultDataSourceHolder {
        static final DefaultDataSource DEFAULT_DATA_SOURCE = new DefaultDataSource();
    }

    public static DefaultDataSource getDefaultDataSource() {
        threadLocalHolder = new ThreadLocalHolder();
        return DefaultDataSourceHolder.DEFAULT_DATA_SOURCE;
    }

    private static final RpcConfig rpcConfig = RpcConfig.getRpcConfig();
    /**
     * 连接池对象
     */
    protected static final List<Connection> connectionPool = new CopyOnWriteArrayList<>();
    private static ThreadLocalHolder threadLocalHolder;

    /**
     * 最大连接数
     */
    protected static final int POOL_MAX_SIZE = 100;
    /**
     * 最小连接数
     */
    protected static final int POOL_MIN_SIZE = 10;

    /**
     * 构造器创建连接池
     */
    private DefaultDataSource() {
        initPool();
    }

    /**
     * 初始化连接池, 使池中连接数达到最小值
     */
    public void initPool() {
        while (connectionPool.size() < POOL_MIN_SIZE) {
            connectionPool.add(createConnection());
        }
    }

    /**
     * 创建新的Connection对象
     */
    protected Connection createConnection() {
        try {
            Class.forName(rpcConfig.getDriver());
            return DriverManager.getConnection(rpcConfig.getUrl(),
                    rpcConfig.getUsername(), rpcConfig.getPassword());
        } catch (ClassNotFoundException | SQLException e) {
            log.error("createConnection error", e);
            return null;
        }
    }

    @Override
    public Connection getConnection() {
        if (connectionPool.isEmpty()) {
            for (int i = 0; i < 5; i++) {
                connectionPool.add(createConnection());
            }
        }
        Connection connection = connectionPool.remove(connectionPool.size() - 1);
        threadLocalHolder.setConnection(connection);
        return connection;
    }

    @Override
    public void closeConnection() {
        connectionPool.add(threadLocalHolder.getConnection());
        if (connectionPool.size() < POOL_MAX_SIZE) {
            return;
        }
        while (connectionPool.size() >= POOL_MAX_SIZE) {
            try {
                if (connectionPool.get(connectionPool.size() - 1) != null) {
                    connectionPool.remove(connectionPool.size() - 1).close();
                }
            } catch (SQLException e) {
                log.error("closeConnection error", e);
            }
        }
    }
}
