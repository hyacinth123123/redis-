# CollectorHub - 智能潮玩社区平台

项目概述
一站式潮玩二级市场平台，针对秒杀抢购与高频社交场景优化。展示了分布式并发控制、缓存高可用与高频社交优化的工程化实践，适合作为个人技术作品集。

技术栈
- Spring Boot
- Redis / Redisson
- MySQL / MyBatis-Plus
- Lua 脚本
- RabbitMQ
- 大模型 API (SSE / 向量缓存)

核心成果
- 设计基于 Redis+Lua 的原子预扣减方案并结合 Redisson WatchDog，实现分布式一致性保障；并结合 RabbitMQ 做削峰填谷，核心接口 QPS 增加 4 倍（JMeter 测试）。
- 采用多级缓存（逻辑过期 + 互斥锁）和空值缓存策略，防止缓存击穿/穿透，并通过 Explain 与复合索引把慢 SQL 降至 15ms。
- 使用 Redis ZSet 实现滚动分页 Feed 流，使用 BitMap 存储签到数据，在千万级用户模拟下内存占用降低 90% 以上。
- 对接大模型实现 SSE 流式响应，采用 Redis 缓存语义向量降低 RAG 链路调用成本，探索自动分类与内容审核场景。

文件清单与说明见 docs/，核心代码在 src/。