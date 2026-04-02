# CollectorHub

CollectorHub 是一个基于 Spring Boot 的综合性社区服务平台，集成了用户管理、店铺管理、博客系统、优惠券系统和 AI 分析等功能。

## 技术栈

- **后端框架**: Spring Boot 3.0+
- **持久层**: MyBatis-Plus
- **数据库**: MySQL
- **缓存**: Redis
- **分布式锁**: Redisson
- **AI 服务**: 文心一言 API
- **前端**: 原生 JavaScript + HTML + CSS

## 功能模块

### 1. 用户管理
- 用户注册、登录
- 个人信息管理
- 令牌刷新机制

### 2. 店铺管理
- 店铺信息查询
- 店铺类型管理
- 店铺缓存优化

### 3. 博客系统
- 博客发布、查询
- 博客评论管理
- 关注功能

### 4. 优惠券系统
- 优惠券管理
- 秒杀优惠券
- 订单管理

### 5. AI 分析
- 评论情感分析
- 智能推荐

## 项目结构

```
CollectorHub/
├── src/
│   └── main/
│       ├── java/com/collectorhub/
│       │   ├── config/          # 配置类
│       │   ├── controller/      # 控制器
│       │   ├── dto/             # 数据传输对象
│       │   ├── entity/          # 实体类
│       │   ├── mapper/          # MyBatis 映射
│       │   ├── service/         # 业务逻辑
│       │   ├── utils/           # 工具类
│       │   └── CollectorHubApplication.java  # 应用入口
│       └── resources/
│           ├── db/              # 数据库脚本
│           ├── mapper/          # MyBatis XML
│           ├── static/          # 静态资源
│           ├── application.yml  # 应用配置
│           └── *.lua            # Redis Lua 脚本
├── pom.xml                      # Maven 配置
└── README.md                    # 项目说明
```

## 核心功能

### 1. 分布式ID生成
使用 Redis 实现的分布式 ID 生成器，确保全局唯一 ID。

### 2. 缓存优化
- 店铺信息缓存
- 热点数据缓存
- 缓存更新策略

### 3. 分布式锁
使用 Redisson 实现的分布式锁，解决秒杀等高并发场景的线程安全问题。

### 4. 秒杀系统
基于 Redis + Lua 脚本实现的秒杀系统，确保高并发下的数据一致性。

### 5. AI 评论分析
集成文心一言 API，对用户评论进行情感分析。

## 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 7.0+

## 安装与运行

### 1. 克隆项目

```bash
git clone https://github.com/hyacinth123123/redis-.git
cd CollectorHub
```

### 2. 配置数据库

1. 创建数据库：`CREATE DATABASE collectorhub DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
2. 导入数据库脚本：`src/main/resources/db/hmdp.sql`
3. 修改数据库配置：`src/main/resources/application.yml`

### 3. 配置 Redis

修改 Redis 配置：`src/main/resources/application.yml`

### 4. 配置 AI 服务

修改文心一言 API 配置：`src/main/resources/application.yml`

### 5. 构建与运行

```bash
# 构建项目
mvn clean package

# 运行项目
java -jar target/CollectorHub-1.0.0.jar
```

## 接口文档

项目启动后，可通过 Swagger UI 查看接口文档：

```
http://localhost:8080/swagger-ui.html
```

## 许可证

本项目采用 MIT 许可证。

## 贡献

欢迎提交 Issue 和 Pull Request！

---

**注意**：本项目仅供学习和参考使用，生产环境部署需进行安全加固。