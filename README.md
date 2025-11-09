# 목차

1. [프로젝트 목적](#프로젝트-목적)
2. [프로젝트 소개](#프로젝트-소개)
3. [기술 스택](#기술-스택)
4. [주요 기능](#주요-기능)
5. [API 명세](#api-명세)
6. [SnapShot](#snapshot)
<br></br>

# 프로젝트 목적
MSA 구조 기반의 시스템으로 팝업 스토어 개설 과정에서 발생하는 비용 부담을 없애고, 누구나 손쉽게 자신만의 팝업 스토어를 개설할 수 있도록 돕는 것입니다. 이를 통해 다양한 판매자들이 부담 없이 자신의 제품을 온라인에서 홍보하고 판매할 수 있는 기회를 제공하며, 팝업 스토어를 통해 비즈니스 확장과 실험을 시도할 수 있는 환경을 마련하는 데 목적을 두고 있습니다. 이 플랫폼은 판매자가 기술적인 지식이 없어도 간편하게 팝업 스토어를 운영할 수 있게 합니다.
<br></br>
# 프로젝트 소개

온라인 팝업 스토어 사이트입니다.

이 사이트는 사용자가 온라인으로 팝업 스토어를 개최하는 기능, 온라인으로 팝업 물품을 구매하는 기능, 후기를 나눌 수 있는 후기 게시판을 제공합니다.
<br></br>
## 개발 기간

25.02.10 ~ 25.02.19 (1주)
<br></br>
## 팀원

| Backend | Frontend | Frontend | Backend | Backend | 
|:-------:|:-------:|:-------:|:-------:|:-------:|
| <img src="https://github.com/user-attachments/assets/44c5ca02-64c7-4a53-8e27-dc125462651d" alt="증사 2" width="170" height="200"> | <img src="https://github.com/user-attachments/assets/44c5ca02-64c7-4a53-8e27-dc125462651d" alt="프로필" width="170" height="200"> |  <img src="https://github.com/user-attachments/assets/44c5ca02-64c7-4a53-8e27-dc125462651d" alt="프로필" width="170" height="200"> | <img src="https://github.com/user-attachments/assets/44c5ca02-64c7-4a53-8e27-dc125462651d" alt="프로필" width="170" height="200"> | <img src="https://github.com/user-attachments/assets/03b048bc-9299-4c6b-a084-57fbc3da9499" alt="프로필" width="170" height="200"> |
| [김상훈](https://github.com/sanghunkim20)  | [윤병욱](https://github.com/ByeongukYun)  |[이주원](https://github.com/kanguju)  |[정병준](https://github.com/ttjbjtt)  |[채승표](https://github.com/py0o0)  |

<br></br>

## 담당 엄무 (백엔드)

  - 유저 관련 기능
    - JWT를 활용한 인증 및 인가 기능
    - Redis 활용하여 Refresh 토큰 관리
    - 회원 및 관리자 CRUD
    - 팔로우 및 신고 관련 기능
    - 웹소켓을 활용한 1:1 채팅 기능
    - 카프카와 웹소켓 활용하여 댓글, 물품 판매 시 실시간 알림
    
  - 게시글 관련 기능
    - 게시글 및 댓글 CRUD
    - 게시글 및 댓글 좋아요 기능
    - aws s3를 활용한 파일 업로드 기능
      
  - 팝업 스토어 관련 기능
    - 팝업 스토어 CRUD
    - aws s3를 활용한 파일 업로드 기능
    
  - MSA 구조 설계 및 라우팅
  - CI/CD
   
<br>
<br>

 ## ERD
[ERD](https://www.notion.so/ERD-2a63e509776d80d79c95cb8046898e9f)

<br>

# 기술 스택

## **백엔드**
- ![Spring](https://img.shields.io/badge/Spring-6DB33F?style=flat-square&logo=Spring&logoColor=white)
- ![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=flat-square&logo=SpringSecurity&logoColor=white)
- ![JPA](https://img.shields.io/badge/JPA-6DB33F?style=flat-square&logo=Hibernate&logoColor=white)
- ![AWS EC2](https://img.shields.io/badge/AWS%20EC2-FF4C00?style=flat-square&logo=AmazonEC2&logoColor=white)
- ![AWS RDS](https://img.shields.io/badge/AWS%20RDS-FF9900?style=flat-square&logo=AmazonAWS&logoColor=white)
- ![AWS S3](https://img.shields.io/badge/AWS%20S3-569A31?style=flat-square&logo=AmazonS3&logoColor=white)
- ![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=MySQL&logoColor=white)
- ![Java](https://img.shields.io/badge/Java-007396?style=flat-square&logo=Java&logoColor=white)
- ![Redis](https://img.shields.io/badge/Redis-D92C2F?style=flat-square&logo=Redis&logoColor=white)
- ![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=Docker&logoColor=white)
- ![WebSocket](https://img.shields.io/badge/WebSocket-1C6DD0?style=flat-square&logo=websocket&logoColor=white)

## **프론트엔드**
- ![React](https://img.shields.io/badge/React-61DAFB?style=flat-square&logo=React&logoColor=white)
- ![TypeScript](https://img.shields.io/badge/TypeScript-3178C6?style=flat-square&logo=TypeScript&logoColor=white)
- ![Next.js](https://img.shields.io/badge/Next.js-000000?style=flat-square&logo=Next.js&logoColor=white)
- ![Tailwind CSS](https://img.shields.io/badge/Tailwind%20CSS-38B2AC?style=flat-square&logo=Tailwind%20CSS&logoColor=white)
- ![WebSocket](https://img.shields.io/badge/WebSocket-1C6DD0?style=flat-square&logo=websocket&logoColor=white)

## **협업 툴**
- ![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=GitHub&logoColor=white)

## **개발 툴**
- ![VS Code](https://img.shields.io/badge/VS%20Code-007ACC?style=flat-square&logo=VisualStudioCode&logoColor=white)
- ![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ%20IDEA-000000?style=flat-square&logo=IntelliJIDEA&logoColor=white)
  
 <br></br>
# 주요 기능

## 1. 유저

- JWT를 활용한 인가 및 인증 기능
- Redis로 Refresh 토큰 관리
- 회원 및 관리자 CRUD
- 팔로우 및 신고 기능
- 팔로우/팔로워 리스트 조회
- 신고 조회 및 상세 조회
- 웹소켓을 활용한 1:1 채팅 기능
- 장바구니 및 결제 기능
- 카프카와 웹소켓 활용하여 댓글, 물품 판매 시 실시간 알림

## 2. 게시판

- 게시글 CRUD
- 댓글 CRUD
- 좋아요 및 조회수 기능
- 이미지 파일 업로드 기능
- 작성한 글 및 좋아요한 글 조회

## 3. 팝업 스토어  
-  팝업 스토어 CRUD
-  이미지 파일 업로드 기능
   
## 4. 상품

 - 상품 관련 CRUD
 - 이미지 파일 업로드 기능
 - 장바구니 담기, 빼기
 - 주문하기, 결제 정보

# API 명세

[API 명세 바로가기](https://patch-brochure-60e.notion.site/API-1993e509776d807fbdc2cf4ebff0a263?pvs=74)

# SnapShot

[SnapShot 바로가기](https://patch-brochure-60e.notion.site/SnapShot-1993e509776d801bbb55e7293690bd47?pvs=74)

