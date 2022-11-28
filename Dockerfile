FROM openjdk:11
# RUN addgroup spring && adduser springboot && adduser springboot spring
# USER spring:spring
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","com.hedon.springbootyara.SpringBootYaraApplication"]
