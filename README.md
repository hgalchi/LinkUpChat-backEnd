# LinkUpTalk
</p>
<div align="center">
    <img width="374" alt="Image" src="https://github.com/user-attachments/assets/8fbfceff-fab4-4c28-9cbf-fb55ed3a8a9e" />
</div>
<br>


## 프로젝트 목표 
**Redis & STOMP** 
- 다중 인스턴스  환경에서 WebSocket 세션 공유 문제 해결
- 개인 채팅, 단체 채팅 

**Spring Security & JWT** 
- JWT를 이용한 AccessToken과 RefreshToken 발급 및 로그인 처리

**CI/CD 자동 배포**
- GitHub Acations, Docker기반 자동 CD파이프라인을 구축 
- TestContainer로 활용하여 테스트 환경을 통일하고 환경 차이를 최소화

## 실행 방법
Docker 

```sh
docker-compose -f docker-compose-dev.yml up -d
```

## 개발 환경
**BackEnd**

> Spring Boot, Spring Web MVC,
Spring Data JPA, 
Spring Security, JJWT,
WebSocket,STOMP, 
TestContainers, Jacoco
> 

**Database**

> PostgreSQL,  Mongo,  Redis
> 

**Infra**

> Docker Compose, EC2, RDB
>


## 시스템 디자인

### 인프라 아키텍처
<img width="548" alt="Image" src="https://github.com/user-attachments/assets/55d895ac-4556-4658-a8c5-01847993a7c7" width="30%" height="30%"/>

### ERD
<img width="577" alt="Image" src="https://github.com/user-attachments/assets/303254ad-3eb5-440b-9321-f3b9bcb1ff56" width="30%" height="30%"/>
