# CollectorHub

CollectorHub是一个基于Spring Boot的后端项目，主要功能包括用户管理、店铺管理、优惠券管理、秒杀功能、评论分析等。

## 项目功能

### 核心功能
- **用户管理**：用户注册、登录、个人信息管理
- **店铺管理**：店铺信息查询、分类管理
- **优惠券管理**：优惠券创建、发放、使用
- **秒杀功能**：限时秒杀、库存管理
- **评论分析**：基于AI的评论情感分析
- **关注功能**：用户之间的关注关系管理
- **博客系统**：用户发布博客、评论功能

### 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.x | 应用框架 |
| MyBatis-Plus | 3.x | ORM框架 |
| Redis | 7.x | 缓存、分布式锁、限流 |
| Redisson | 3.x | 分布式锁实现 |
| MySQL | 8.x | 数据库 |
| Spring Security | 6.x | 安全框架 |
| Wenxin AI | - | 评论情感分析 |

## 项目结构

```
CollectorHub/
├── src/
│   ├── main/
│   │   ├── java/com/collectorhub/
│   │   │   ├── config/          # 配置类
│   │   │   ├── controller/      # 控制器
│   │   │   ├── dto/             # 数据传输对象
│   │   │   ├── entity/          # 实体类
│   │   │   ├── mapper/          # MyBatis映射器
│   │   │   ├── service/         # 业务逻辑
│   │   │   ├── utils/           # 工具类
│   │   │   └── CollectorHubApplication.java  # 应用启动类
│   │   └── resources/
│   │       ├── db/              # 数据库脚本
│   │       ├── mapper/          # MyBatis XML映射文件
│   │       ├── static/          # 静态资源
│   │       ├── application.yml  # 应用配置
│   │       └── *.lua            # Redis Lua脚本
│   └── test/                    # 测试代码
├── .gitignore                   # Git忽略文件
├── pom.xml                      # Maven依赖管理
└── README.md                    # 项目说明
```

## 快速开始

### 环境要求
- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 7.0+

### 安装步骤

1. **克隆项目**
   ```bash
   git clone https://github.com/hyacinth123123/redis-.git
   cd CollectorHub
   ```

2. **配置数据库**
   - 创建数据库 `collectorhub`
   - 执行 `src/main/resources/db/hmdp.sql` 初始化数据库结构

3. **配置Redis**
   - 确保Redis服务已启动
   - 修改 `application.yml` 中的Redis连接配置

4. **配置AI服务**
   - 修改 `application.yml` 中的Wenxin AI配置

5. **构建项目**
   ```bash
   mvn clean package -DskipTests
   ```

6. **运行项目**
   ```bash
   java -jar target/CollectorHub-1.0.0.jar
   ```

## API文档

### 主要API端点

| 模块 | 路径 | 说明 | 方法 |
|------|------|------|------|
| 用户管理 | /user | 用户相关操作 | POST/GET |
| 店铺管理 | /shop | 店铺相关操作 | POST/GET |
| 优惠券管理 | /voucher | 优惠券相关操作 | POST/GET |
| 秒杀功能 | /voucher-order/seckill | 秒杀接口 | POST |
| 评论分析 | /ai/analyze | 评论情感分析 | POST |
| 关注功能 | /follow | 关注相关操作 | POST/GET |
| 博客系统 | /blog | 博客相关操作 | POST/GET |

## 核心功能实现

### 秒杀功能
- 使用Redis Lua脚本保证原子性
- 采用分布式锁防止超卖
- 异步处理订单创建

### 评论分析
- 集成Wenxin AI API
- 实时情感分析
- 结果缓存

### 缓存策略
- 店铺信息缓存
- 优惠券缓存
- 热点数据预热

### 安全措施
- JWT token认证
- 密码加密存储
- 接口限流

## 贡献

欢迎提交Issue和Pull Request！

## 许可证

本项目采用MIT许可证。
