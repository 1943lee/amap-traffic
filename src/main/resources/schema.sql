PRAGMA foreign_keys = off;
BEGIN TRANSACTION;

-- 表：t_parts
DROP TABLE IF EXISTS t_parts;
CREATE TABLE t_parts
(
    id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE NOT NULL,
    row INTEGER,
    col INTEGER,
    xmin DECIMAL(9,6),
    xmax DECIMAL(9,6),
    ymin DECIMAL(9,6),
    ymax DECIMAL(9,6),
    xmin_gcj DECIMAL(9,6),
    xmax_gcj DECIMAL(9,6),
    ymin_gcj DECIMAL(9,6),
    ymax_gcj DECIMAL(9,6),
    xmin_mer DECIMAL(15,6),
    xmax_mer DECIMAL(15,6),
    ymin_mer DECIMAL(15,6),
    ymax_mer DECIMAL(15,6),
    in_region INTEGER (1),
    useful INTEGER(1),
    shape_geo TEXT,
    result_status INTEGER(1),
    result_info_code VARCHAR(10)
);

COMMIT TRANSACTION;
PRAGMA foreign_keys = on;
