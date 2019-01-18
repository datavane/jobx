
Dev分支,技术栈和功能全面升级
    Netty+Dubbo+SpringBoot+Zookeeper+Vue,目标是打造一个高可用,稳定的,美观的,前后端分离的全新JobX
    
    
## Docker

### Build images

#### 1. jobx-agent

```bash
cd /path/to/JobX
docker build -f jobx-docker/jobx-agent/Dockerfile -t jobx-agent .
```

#### 2. jobx-server

```bash
cd /path/to/JobX
docker build -f jobx-docker/jobx-server/Dockerfile -t jobx-server .
```

#### 3. jobx-service    

```bash
cd /path/to/JobX
docker build -f jobx-docker/jobx-service/Dockerfile -t jobx-service .
```

### Run server

```bash
docker-compose -f docker-compose.yml up -d
```

### Run agent

```bash
docker-compose -f docker-compose-agent.yml up -d
```
