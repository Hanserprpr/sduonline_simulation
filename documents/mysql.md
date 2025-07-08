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
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

- 注：密码字段使用VARCHAR(255)以存储加密后的密码。

---
