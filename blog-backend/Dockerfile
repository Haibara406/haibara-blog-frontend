FROM openjdk:17-jdk-slim

# 设置标签信息
LABEL maintainer="Haibara <Haibara406@gmail.com>"
LABEL version="1.0.0"
LABEL description="Haibara Blog Backend Application"


# 设置环境变量
ENV TZ=Asia/Shanghai \
    JAVA_OPTS="" \
    SPRING_PROFILES_ACTIVE=prod \
    JVM_XMX=512m \
    JVM_XMS=256m \
    SERVER_PORT=8062

# 设置时区
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 创建应用用户和目录
RUN groupadd -r spring && useradd -r -g spring spring \
    && mkdir -p /app/logs /app/config \
    && chown -R spring:spring /app

# 设置工作目录
WORKDIR /app

# 复制应用文件
COPY target/blog-backend-0.0.1-SNAPSHOT.jar app.jar
RUN chown spring:spring app.jar

# 切换到非root用户
USER spring

# 暴露端口
EXPOSE 8062

# 启动脚本
ENTRYPOINT exec java \
  $JAVA_OPTS \
  -Djava.security.egd=file:/dev/./urandom \
  -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE \
  -Duser.timezone=$TZ \
  -Dserver.port=$SERVER_PORT \
  -Xmx$JVM_XMX \
  -Xms$JVM_XMS \
  -XX:+UseG1GC \
  -XX:+UseContainerSupport \
  -XX:MaxRAMPercentage=75.0 \
  -jar app.jar