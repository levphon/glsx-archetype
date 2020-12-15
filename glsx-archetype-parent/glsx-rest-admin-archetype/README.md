# glsx-rest-admin(微服务快速开发的脚手架)

## 背景&目标
当前新建项目时，配置文件往往都是从其它项目拷贝过来，拷贝时很容易踩坑，并且不容易定位问题。尤其对新人来说由于在对公司项目结构、基础中间件、配置还不了解的情况下，会浪费很多不必要的时间。故，希望通过脚手架解决此痛点，达到如下几个基本目标：

1. 统一规范工程项目&代码层次结构——约定大于配置；
2. 提升工作效率——脚手架&代码生成器&自定义插件；
3. 降低项目维护成本——易于组内成员相互协防；
另，发现当前各自创建的项目，存在如下几个现象，希望借机推进组内代码规范落地：

1. 项目&包层次结构不统一；
2. 配置文件命名、存放位置不统一；
3. 类命名不统一（有的叫entity，有的叫BO，有的叫Domain；有的叫job，有的叫crane，有的叫schedule；接口命名有的I开头，有的无I开头）；
希望在项目模板（脚手架）中在每个层次环节代码中直接给予相应的Demo，以此达到规范引导作用。

## 项目介绍

基于 Spring Cloud 完整的微服务架构的一个脚手架项目

## 技术栈

> 权限认证
>
> > Jwt

> 配置中心
>
> > Alibaba nacos

> 服务注册与发现
>
> > Alibaba nacos

> 分布式链路追踪
>
> > sleuth
> >
> > zipkin

> 网关
>
> > SpringCloud Gateway

> 流量控制、熔断降级
>
> > Alibaba sentinel

> Http声名式转发
>
> > Feign
> >
> > RestTemplate
> >
> > WebClient

> 持久层
>
> > mybatis
> >
> > pagehelper
> >
> > tk-mapper
> >
> > Jpa

> 数据存储
>
> > MySQL
> >
> > Redis
> >
> > MongoDB

> 文件服务
>
> > Fastdfs
> > 
> > Minio

> 消息中间件
>
> > Kafka

## 项目结构


## 模块说明
![架构图](https://images.gitee.com/uploads/images/2019/0528/205306_9a8b8d83_1899222.png "1.png")


## 项目涉及服务组件

#### 1. nacos

Nacos是一个更易于构建云原生应用的动态服务发现、配置管理和服务管理平台。Nacos 支持几乎所有主流类型的服务的发现、配置和管理:

* Kubernetes Service
* gRPC & Dubbo RPC Service
* Spring Cloud RESTful Service

官网：https://nacos.io/

github:https://github.com/alibaba/nacos/releases

```sh
sh startup.sh -m standalone
```

开发环境部署地址：/data/nacos

开发环境访问地址：http://192.168.0.63:8848/nacos/

账号密码：nacos/nacos


#### 2. zipkin


官网：https://zipkin.io/

github:https://github.com/openzipkin/zipkin

https://dl.bintray.com/openzipkin/maven/io/zipkin/java/zipkin-server/2.12.9/

```sh
nohup java -jar zipkin-server-2.12.9-exec.jar &
```

关于zipkin的基本使用请查看其他文章或者自行百度



开发部署：http://192.168.0.63:9411/zipkin/

开发环境部署地址：/data/zipkin

开发环境访问地址：http://192.168.0.63:9411/zipkin/

#### 3. sentinel-dashboard

github:https://github.com/alibaba/Sentinel

```sh
nohup java -Dserver.port=8718 -Dcsp.sentinel.dashboard.server=localhost:8718 -Dproject.name=sentinel-dashboard -Dcsp.sentinel.api.port=8719 -jar sentinel-dashboard-1.8.0.jar &
```

开发环境部署地址：/data/sentinel

开发环境访问地址：http://192.168.0.63:8718/#/login

账号密码：sentinel/sentinel

![](https://upload-images.jianshu.io/upload_images/5417792-ae78d6da78d48b31.png)

#### 4. spring-cloud-gateway-server

svn地址:svn://192.168.3.233/frms/code/glsx-archetype/glsx-archetype/glsx-archetype-parent/glsx-rest-admin-gateway-server

![](https://img-blog.csdnimg.cn/20191208161419451.png)

#### 4. spring-boot-admin-server

svn地址:svn://192.168.3.233/frms/code/glsx-archetype/glsx-archetype/glsx-archetype-parent/glsx-rest-admin-monitor-server

![](https://oscimg.oschina.net/oscnet/up-a38e9bb6b0d08cccf3ab9c6c99b4b398650.png)

开发环境部署地址：/data/webserver/glsx-rest-admin-monitor-server-1.0-SNAPSHOT

开发环境访问地址：http://192.168.0.63:8769/applications


## 脚手架项目运行调试
#### SVN地址

svn地址:svn://192.168.3.233/frms/code/glsx-archetype/glsx-archetype/glsx-archetype-parent/glsx-rest-admin

[脑图](https://www.kdocs.cn/view/l/cDUsOKQht? "项目引用框架结构")

### 添加配置中心

1. 打开nacos的管理中心(http://192.168.0.63:8848/nacos/#/login)


2. 新增data-id为${prefix}-${spring.profile.active}.${file-extension}

* prefix 默认为 spring.application.name 的值，也可以通过配置项 spring.cloud.nacos.config.prefix来配置。
* spring.profile.active 即为当前环境对应的 profile，详情可以参考 Spring Boot文档。注意：当 spring.profile.active 为空时，对应的连接符 - 也将不存在，dataId 的拼接格式变成 prefix.{file-extension}
* file-extension 为配置内容的数据格式，可以通过配置项 spring.cloud.nacos.config.file-extension 来配置。目前只支持 properties 和 yaml 类型。

关于配置格式，通俗的讲，即为file-extension。如配置格式选择properties格式，则DataId后缀必须为properties（注意yaml，可写为yml，与yaml同义，但与bootstrap配置中的file-extension必须一致），配置内容也必须按照properties格式编写。

<pre>
<code>
server.port=8828
server.servlet.context-path=/${spring.application.name}
</code>
</pre>

[glsx-rest-admin-DEV.properties](http://192.168.0.63:8848/nacos/v1/cs/configs?dataId=glsx-rest-admin-DEV.properties&group=DEV_GROUP "脚手架项目配置")

3. 执行Application.main()启动项目

### 修改网关服务网关配置
<pre>
<code>
#glsx-rest-admin
spring.cloud.gateway.routes[0].id=glsx-rest-admin
spring.cloud.gateway.routes[0].uri=lb://glsx-rest-admin
spring.cloud.gateway.routes[0].predicates[0]=Path=/glsx-rest-admin/**
spring.cloud.gateway.routes[0].filters[0]=StripPrefix=0
</code>
</pre>

[glsx-rest-admin-gateway-DEV.properties](http://192.168.0.63:8848/nacos/v1/cs/configs?dataId=glsx-rest-admin-gateway-DEV.properties&group=DEV_GROUP "网关服务配置")

##脚手架
a. 构建脚手架命令
<pre><code>cd glsx-rest-admin-archetype</code></pre>
<pre><code>mvn archetype:create-from-project</code></pre>

b. 安装脚手架命令
<pre><code>
cd target\generated-sources\archetype
mvn clean install
</code></pre>

c. 发布
<pre><code>mvn deploy</code></pre>
groupId:上传到私服的groupId<br>
artifactId:上传到私服的artifactId<br>
version:上传到私服的version<br>
file:jar包的本地路径<br>
url: 你的maven私服地址<br>
repositoryId:setting.xml配置的server id
jar复制到非仓库目录，不然可能会发布失败

OR

<pre><code>mvn deploy:deploy-file -DgroupId=com.glsx -DartifactId=glsx-rest-admin-archetype-archetype -Dversion=1.0-SNAPSHOT -Dpackaging=jar -Dfile=D:\glsx-rest-admin-archetype-archetype-1.0-SNAPSHOT.jar -Durl=http://192.168.3.233/nexus/content/repositories/snapshots -DrepositoryId=nexus-snapshots</code></pre>
