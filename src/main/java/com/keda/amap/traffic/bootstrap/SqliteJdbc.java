package com.keda.amap.traffic.bootstrap;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * Created by liChenYu on 2018/9/4
 */
@Slf4j
public final class SqliteJdbc {
    public static String DB_NAME = "amap.db";
    public static String DB_PATH;
    public static String DB_SRC;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (Exception e) {
            log.error("load sqlite driver error", e);
        }
    }

    /**
     * 测试连接并导入数据库
     */
    public static void importSql() {
        try {
            DB_PATH = System.getProperty("user.dir") + File.separator + DB_NAME;
            DB_SRC = "jdbc:sqlite://" + DB_PATH;

            log.info("load sqlite database path [{}]", DB_PATH);
            log.info("load sqlite database src [{}]", DB_SRC);

            Connection con       = DriverManager.getConnection(DB_SRC);
            Statement statement = con.createStatement();
            ResultSet rs        = statement.executeQuery("SELECT count(*) FROM sqlite_master WHERE type='table' AND name='t_parts'");
            int        count     = rs.getInt(1);
            if (count == 0) {
                String            cp  = Consts.CLASSPATH;
                InputStreamReader isr = new InputStreamReader(new FileInputStream(cp + "schema.sql"), "UTF-8");

                String sql = new BufferedReader(isr).lines().collect(Collectors.joining("\n"));
                int    r   = statement.executeUpdate(sql);
                log.info("initialize import database - {}", r);
            } else {
                log.info("database has been initialized");
            }
            rs.close();
            statement.close();
            con.close();
            log.info("database path is: {}", DB_PATH);
        } catch (Exception e) {
            log.error("initialize database fail", e);
        }
    }
}
