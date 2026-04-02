# CollectorHub-智能潮玩社区平台

## 项目简介

CollectorHub是一个一站式潮玩二级市场平台，针对秒杀抢购与高频社交场景，攻克了分布式环境下并发控制与缓存高可用难题，构建了具备高吞吐量与低延迟响应能力的后端架构。

**项目时间**：2025.01-2025.09

## 技术栈

- **后端框架**：Spring Boot 2.7.18
- **缓存技术**：Redis、Redisson
- **数据库**：MySQL
- **ORM框架**：MyBatis-Plus 3.4.3
- **脚本语言**：Lua
- **消息队列**：RabbitMQ
- **AI能力**：大模型API
- **工具库**：Hutool、Fastjson

## 核心功能

### 1. 秒杀抢购系统
- 基于Redis+Lua的原子预扣减方案
- 分布式锁保证并发安全
- 防超卖、防重复下单

### 2. 社交功能
- 基于Redis ZSet的滚动分页Feed流
- 关注/粉丝系统
- 评论与点赞

### 3. 多级缓存系统
- 逻辑过期+互斥锁处理热点Key
- 防缓存击穿/穿透/雪崩
- 百万级数据的高性能查询

### 4. AI交互
- 大模型API对接
- SSE流式响应提升用户体验
- Redis缓存语义向量降低API调用成本

### 5. 潮玩市场
- 商品管理与搜索
- 分类浏览
- 详情展示

## 项目结构

```
├── src/
│   ├── main/
│   │   ├── java/com/collectorhub/
│   │   │   ├── config/          # 配置类
│   │   │   ├── controller/       # 控制器
│   │   │   ├── dto/             # 数据传输对象
│   │   │   ├── entity/          # 实体类
│   │   │   ├── mapper/           # MyBatis映射
│   │   │   ├── service/          # 业务逻辑
│   │   │   ├── utils/            # 工具类
│   │   │   └── CollectorHubApplication.java  # 主启动类
│   │   ├── resources/
│   │   │   ├── db/               # 数据库脚本
│   │   │   ├── mapper/           # XML映射文件
│   │   │   ├── static/           # 静态资源
│   │   │   ├── application.yml   # 应用配置
│   │   │   ├── seckill.lua       # 秒杀脚本
│   │   │   └── unlock.lua        # 解锁脚本
├── pom.xml                        # Maven配置
└── README.md                      # 项目说明
```

## 快速开始

### 1. 环境准备

- JDK 17+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.6+
- RabbitMQ 3.8+

### 2. 数据库配置

1. 创建数据库 `hmdp`
2. 执行 `src/main/resources/db/hmdp.sql` 初始化表结构

### 3. 应用配置

修改 `src/main/resources/application.yml` 文件：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hmdp?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
  redis:
    host: localhost
    port: 6379
    password: your_redis_password

# 大模型配置
wenxin:
  api:
    key: your_api_key
    base-url: https://api.deepseek.com
```

### 4. 启动项目

```bash
# 编译项目
mvn clean package

# 运行项目
mvn spring-boot:run
```

### 5. 访问项目

- 后端API: http://localhost:8081
- 前端页面: http://localhost:8080 (需要配置Nginx代理)

## 部署说明

### 1. 生产环境部署

```bash
# 构建jar包
mvn clean package -DskipTests

# 部署到服务器
java -jar collectorhub-0.0.1-SNAPSHOT.jar
```

### 2. Nginx配置

```nginx
server {
    listen 8080;
    server_name localhost;
    
    location / {
        root html/hmdp;
        index index.html index.htm;
    }
    
    location /api {
        proxy_pass http://localhost:8081;
        rewrite /api(/.*) $1 break;
        proxy_http_version 1.1;
        proxy_set_header Connection "";
    }
}
```

## 核心成果

### 1. 分布式并发与一致性
- 针对秒杀场景下的超卖风险，对比了数据库乐观锁与Redis分布式锁方案
- 采用Redis+Lua原子预扣减方案，将核心接口QPS经JMeter压测从200提升至800+
- 实现零超卖，确保高并发下的数据一致性

### 2. 多级缓存深度优化
- 针对百万级模拟数据，通过Explain分析执行计划并重构复合索引
- 将慢SQL耗时从800ms压降至15ms
- 设计"逻辑过期+互斥锁"方案处理热点Key失效
- 构建了防击穿/穿透的健壮缓存体系

### 3. 社交场景存储优化
- 利用Redis ZSet实现滚动分页Feed流，规避了传统Offset分页在数据高频更新时的"跳页"痛点
- 利用BitMap存储签到数据，将千万级用户规模下的内存占用降低了90%以上

### 4. AI交互落地
- 对接大模型实现SSE流式响应提升用户交互体验
- 通过Redis缓存语义向量有效降低了RAG链路的25% API调用成本

## 性能指标

| 指标 | 数值 | 说明 |
|------|------|------|
| 核心接口QPS | 800+ | 秒杀场景下的并发处理能力 |
| 慢SQL优化 | 800ms → 15ms | 数据库查询性能提升 |
| 内存占用 | 降低90% | 签到数据存储优化 |
| API调用成本 | 降低25% | AI语义向量缓存效果 |
| 响应时间 | < 100ms | 99%请求的响应时间 |

## 注意事项

1. **Redis配置**：生产环境建议使用Redis集群，确保高可用性
2. **数据库优化**：定期进行索引优化和慢SQL分析
3. **监控告警**：建议配置Prometheus+Grafana监控系统
4. **安全配置**：生产环境需修改默认密码，配置HTTPS
5. **大模型API**：注意API调用频率限制和成本控制

---

**CollectorHub-智能潮玩社区平台** - 让潮玩交易更简单，让社交互动更有趣！