# CollectorHub-智能潮玩社区平台

## 项目简介
主导开发的一站式潮玩二级市场平台，针对秒杀抢购与高频社交场景，攻克了分布式环境下并发控制与缓存高可用难题，构建了具备高吞吐量与低延迟响应能力的后端架构。

## 开发时间
2025.01-2025.09

## 技术栈
- Spring Boot
- Redis
- Redisson
- MySQL
- MyBatis-Plus
- Lua
- RabbitMQ
- 大模型API

## 核心成果

### 分布式并发与一致性
针对秒杀场景下的超卖风险，对比了数据库乐观锁与Redis分布式锁方案，因前者在极端并发下易造成数据库连接池打满，最终采用Redis+Lua原子预扣减方案，将核心接口QPS经JMeter压测从200提升至800+，实现零超卖。

### 多级缓存深度优化
针对百万级模拟数据，通过Explain分析执行计划并重构复合索引，将慢SQL耗时从800ms压降至15ms；设计"逻辑过期+互斥锁"方案处理热点Key失效，构建了防击穿/穿透的健壮缓存体系。

### 社交场景存储优化
利用Redis ZSet实现滚动分页Feed流，规避了传统Offset分页在数据高频更新时的"跳页"痛点；利用BitMap存储签到数据，将千万级用户规模下的内存占用降低了90%以上。

### AI交互落地
对接大模型实现SSE流式响应提升用户交互体验；通过Redis缓存语义向量有效降低了RAG链路的25% API调用成本。

## 项目结构
```
CollectorHub/
├── src/
│   ├── main/
│   │   ├── java/com/collectorhub/
│   │   │   ├── config/       # 配置类
│   │   │   ├── controller/    # 控制器
│   │   │   ├── dto/           # 数据传输对象
│   │   │   ├── entity/        # 实体类
│   │   │   ├── mapper/        # MyBatis映射器
│   │   │   ├── service/       # 服务层
│   │   │   ├── utils/         # 工具类
│   │   │   └── CollectorHubApplication.java  # 应用启动类
│   │   └── resources/         # 资源文件
│   └── test/                  # 测试文件
├── pom.xml                    # Maven配置文件
└── README.md                  # 项目说明文件
```

## 核心功能
- 潮玩商品管理与展示
- 秒杀抢购系统
- 社交互动与Feed流
- 用户签到与积分系统
- AI智能交互

## 快速启动
1. 克隆项目到本地
2. 配置数据库连接信息
3. 启动Redis服务
4. 运行`CollectorHubApplication.java`启动应用
5. 访问API接口

## 性能测试
- 核心接口QPS：800+
- 慢SQL优化：从800ms压降至15ms
- 内存优化：BitMap存储签到数据，内存占用降低90%以上
- API调用成本：通过Redis缓存语义向量，降低25% API调用成本

## 项目亮点
- 高并发秒杀解决方案
- 多级缓存体系设计
- 社交场景存储优化
- AI交互体验提升
- 分布式一致性保障
