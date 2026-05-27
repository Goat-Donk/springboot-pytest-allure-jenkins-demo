# Spring Boot + Pytest + Allure + Jenkins Demo

这是一个用于练习接口自动化测试与 Jenkins 持续集成的示例项目，包含：

- 一个基于 Spring Boot 的用户管理接口服务
- 一套基于 `requests + pytest + allure` 的接口自动化测试
- 使用 JSON 测试数据的参数化数据驱动示例
- 适合 Jenkins Windows 节点执行的 `Jenkinsfile`

## 1. 项目结构

```text
.
├── Jenkinsfile
├── pom.xml
├── requirements.txt
├── scripts
│   ├── start_app.ps1
│   ├── stop_app.ps1
│   └── wait_for_app.ps1
├── src
│   └── main
│       ├── java/com/example/demo
│       └── resources/application.yml
└── tests
    ├── conftest.py
    ├── data/create_user_cases.json
    ├── test_user_api.py
    └── utils/data_loader.py
```

## 2. Java 接口说明

启动后地址默认是 `http://127.0.0.1:18080`。

接口列表：

- `GET /api/health`：健康检查
- `GET /api/users`：查询全部用户
- `GET /api/users/{id}`：查询单个用户
- `POST /api/users`：新增用户
- `PUT /api/users/{id}`：修改用户
- `DELETE /api/users/{id}`：删除用户

统一响应格式：

```json
{
  "code": 0,
  "message": "create success",
  "data": {}
}
```

## 3. 本地运行

### 3.1 打包 Java 项目

```powershell
mvn clean package -DskipTests
```

### 3.2 启动服务

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\start_app.ps1
powershell -ExecutionPolicy Bypass -File .\scripts\wait_for_app.ps1
```

### 3.3 运行自动化测试

```powershell
python -m pip install -r requirements.txt
pytest tests --alluredir=allure-results
```

### 3.4 生成 Allure 报告

```powershell
allure serve .\allure-results
```

### 3.5 停止服务

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\stop_app.ps1
```

### 3.6 一键模拟 Jenkins 流程

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\run_local_ci.ps1
```

## 4. 数据驱动体现

数据驱动文件是 [tests/data/create_user_cases.json](tests/data/create_user_cases.json)。

测试中使用了 `pytest.mark.parametrize(...)` 读取 JSON 数据，实现：

- 多组新增用户用例共用一套测试逻辑
- 正常数据与异常数据放在同一个数据源中管理
- 使用 `{suffix}` 占位符避免重复执行时邮箱冲突

## 5. Jenkins 配置方法

### 5.1 Jenkins 需要的插件

建议安装以下插件：

- Git plugin
- Pipeline
- Allure Jenkins Plugin

### 5.2 Jenkins 全局工具建议

在 Jenkins 的 `Manage Jenkins` -> `Tools` 中配置：

- JDK 17
- Maven 3.9.x
- Allure Commandline

### 5.3 创建流水线任务

1. 新建任务，选择 `Pipeline`
2. 在源码管理里选择 `Git`
3. 填写你的 Git 仓库地址
4. `Script Path` 填 `Jenkinsfile`
5. 保存

### 5.4 提交代码后自动触发

这个项目的 `Jenkinsfile` 已经内置了：

```groovy
triggers {
    pollSCM('H/2 * * * *')
}
```

意思是 Jenkins 每 2 分钟轮询一次仓库，只要发现新提交就会自动构建。

如果你想更实时，推荐再配 `Webhook`：

- 如果仓库在 GitHub：配置仓库 Webhook 指向 `http://你的Jenkins地址/github-webhook/`
- 如果仓库在 Gitee：安装对应插件后配置 Gitee Webhook

Webhook 通常比轮询更及时。

## 6. Jenkins 执行流程

流水线会按下面顺序执行：

1. 拉取最新代码
2. `mvn clean package -DskipTests`
3. `python -m pip install -r requirements.txt`
4. 在同一个脚本中启动 Spring Boot 服务
5. 轮询健康检查接口直到服务就绪
6. 执行 `pytest` 接口自动化测试
7. 自动停止服务
8. 收集 `allure-results` 与归档日志、jar 包

## 7. 适合你继续扩展的方向

- 把内存存储改成 MySQL
- 增加登录接口与鉴权 Token
- 测试数据改成 Excel 或 YAML
- 接入环境配置：测试环境、预发环境
- 增加 Jenkins 构建失败通知
