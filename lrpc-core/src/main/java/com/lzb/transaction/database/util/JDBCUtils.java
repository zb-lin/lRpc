package com.lzb.transaction.database.util;

import com.lzb.exception.RpcException;
import com.lzb.transaction.database.config.RequestStatusEnum;
import com.lzb.transaction.database.pool.DefaultDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class JDBCUtils {
    private static final DefaultDataSource defaultDataSource = DefaultDataSource.getDefaultDataSource();

    public static void insert(String rpcRequestId) {
        String sql = "insert into undo_log (xid, context, rollback_info, log_status) values (?,?,?,?)";
        Connection connection = defaultDataSource.getConnection();
        PreparedStatement preparedStatement = null;
        try {
            assert connection != null;
            preparedStatement = connection.prepareStatement(sql);
            // 给sql设参
            preparedStatement.setString(1, rpcRequestId);
            preparedStatement.setString(2, "init");
            preparedStatement.setString(3, "init");
            preparedStatement.setInt(4, RequestStatusEnum.PROCESSING.getStatus());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new RpcException("数据库操作执行异常", e);
        } finally {
            defaultDataSource.closeConnection();
        }
    }

    public static void update(String context, String rollbackInfo, int logStatus, String rpcRequestId) {
        String sql = "update undo_log set context=?,rollback_info=?,log_status=? where xid=?";
        Connection connection = defaultDataSource.getConnection();
        PreparedStatement preparedStatement = null;
        try {
            assert connection != null;
            preparedStatement = connection.prepareStatement(sql);
            // 给sql设参
            preparedStatement.setString(1, context);
            preparedStatement.setString(2, rollbackInfo);
            preparedStatement.setInt(3, logStatus);
            preparedStatement.setString(4, rpcRequestId);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new RpcException("数据库操作执行异常", e);
        } finally {
            defaultDataSource.closeConnection();
        }
    }

    public static void update(int logStatus, String rpcRequestId) {
        String sql = "update undo_log set log_status=? where xid=?";
        Connection connection = defaultDataSource.getConnection();
        PreparedStatement preparedStatement = null;
        try {
            assert connection != null;
            preparedStatement = connection.prepareStatement(sql);
            // 给sql设参
            preparedStatement.setInt(1, logStatus);
            preparedStatement.setString(2, rpcRequestId);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new RpcException("数据库操作执行异常", e);
        } finally {
            defaultDataSource.closeConnection();
        }
    }

    public static boolean query(String rpcRequestId, String sql) {
        Connection connection = defaultDataSource.getConnection();
        PreparedStatement preparedStatement = null;
        try {
            assert connection != null;
            preparedStatement = connection.prepareStatement(sql);
            // 给sql设参
            preparedStatement.setString(1, rpcRequestId);
            ResultSet resultSet = preparedStatement.executeQuery();
            Integer value = null;
            while (resultSet.next()) {
                value = resultSet.getInt(1);
            }
            return value != null && value > 0;
        } catch (Exception e) {
            throw new RpcException("database handler error", e);
        } finally {
            defaultDataSource.closeConnection();
        }
    }

    public static boolean queryCount(String rpcRequestId) {
        String sql = "select count(id) from undo_log where xid = ? and log_status != 2";
        return query(rpcRequestId, sql);
    }

    public static int queryResult(String rpcRequestId) {
        String sql = "select log_status from undo_log where xid = ?";
        Connection connection = defaultDataSource.getConnection();
        PreparedStatement preparedStatement = null;
        try {
            assert connection != null;
            preparedStatement = connection.prepareStatement(sql);
            // 给sql设参
            preparedStatement.setString(1, rpcRequestId);
            ResultSet resultSet = preparedStatement.executeQuery();
            Integer value = null;
            while (resultSet.next()) {
                value = resultSet.getInt(1);
            }
            return value == null ? RequestStatusEnum.ERROR.getStatus() : value;
        } catch (Exception e) {
            throw new RpcException("database handler error", e);
        } finally {
            defaultDataSource.closeConnection();
        }
    }
}
