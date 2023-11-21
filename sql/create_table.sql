create database if not exists database1;

-- 切换库
use database1;
create table if not exists undo_log
(
    id            bigint auto_increment comment 'id' primary key,
    xid           varchar(100)                       NOT NULL,
    context       varchar(128)                       NOT NULL,
    rollback_info varchar(1024)                      NOT NULL,
    log_status    int(11)                            NOT NULL,
    create_time   datetime default CURRENT_TIMESTAMP not null,
    update_time   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    index idx_xid (xid)
) comment 'undo_log' collate = utf8mb4_unicode_ci;

