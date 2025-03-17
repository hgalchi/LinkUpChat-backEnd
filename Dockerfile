# base image
FROM amazoncorretto:17
# 빌드 파일의 경로
ARG JAR_FILE=build/libs/*.jar
# 빌드 파일을 app.jar 컨테이너로 복사
COPY ${JAR_FILE} app.jar
# jar 파일 실행
ENTRYPOINT ["java","-jar","/app.jar"]