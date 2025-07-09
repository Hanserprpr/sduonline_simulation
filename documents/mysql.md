# 数据库设计文档

## 创建数据库

```sql
CREATE DATABASE sduonline_simulation
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_0900_ai_ci;
```

- 注：utf8mb4_0900_ai_ci不区分大小写，在密码等其它高级别的安全性要求下，可能需要使用utf8mb4_bin。

---

## 用户表(users)

```sql
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    casdoor_sub VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255),
    avatar VARCHAR(255),
    email VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## 志愿时长（学线币）

```sql
CREATE TABLE coins (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sub VARCHAR(50) NOT NULL,
    coins INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sub) REFERENCES users(casdoor_sub) ON DELETE CASCADE
);
```

---

## 暂存表(temp)

```sql
CREATE TABLE temp (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    completed_levels JSON COMMENT '已完成的关卡列表',
    total_duration INT DEFAULT 0 COMMENT '总时长（秒）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```
