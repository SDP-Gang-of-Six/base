# 指定基础镜像
FROM openjdk:11-jre-slim

# 拷贝jdk和java项目的包
COPY ./target/base-1.0-SNAPSHOT.jar /base/base.jar

# 暴露端口
EXPOSE 8080
# 入口，java项目的启动命令
ENTRYPOINT java -jar -Xms4g -Xmx4g /base/base.jar --spring.profiles.active=pro
