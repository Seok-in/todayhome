# Today_house_test_Server_Bingsu/Popo

빙수/포포 - 오늘의집

## 포포 개발일지
### 2021-10-30
- 기획서 제출 100%
  - 추후 수정 보완 필요

### 2021-10-31
- ERD 설계 80% 완료
  - ERD 설계간 클라이언트와 소통하여 기능 협의<br>
❓ 게시글 관련 ERD 데이터 타입 설계에 대한 고민<br><Br>
- 서버구축 50% 완료
  - 로컬서버구축 완료
  - 로컬에서 dev 서버 구축 완료

### 2021-11-01
- 서버 구축 완료 100%<br>
  - Spring에서 dev, local, prod 각각 포트 구분하여 구축완료<br>
❓ 서버 개발시 유의사항에 클라이언트의 개발을 Dev에서 진행하도록 되어있는데, 이게 명확한가에 대해서 고민하였으나 팀원끼리 협의하여 Bingsu의 로컬서버를 Dev서버로 하여 포트포워딩 통해 열어줌.
  - 서버구축간 오류<br>
🚨 - ```spring.profiles.active``` 로 local, dev, prod 서버를 나누려고 하였으나 계속 active 최하단코드가 작동을 함<br> 
🗝 - 찾아보니 ```spring.profiles.active``` 는 기본값으로 실행되는 코드고, ```spring.profiles``` 코드는 SpringBoot 2.5버전에서는 더이상 지원하지 않아 deprecated 된 코드고, 따라서 ```config.activate.on-profile``` 로 대체해줌<br><br>
🚨 - ```java -jar -DSpring.profiles.active=? test.jar```을 통해 프로필에 따라 jar파일을 구현하려 하였으나 .profiles.active=? jar파일을 찾을수 없다. 라고 나옴.<br>
🗝 - 구글링을 통해 찾아보니 ```java -jar "-DSpring.profiles.active=?" test.jar``` 형식으로 ""를 붙여줘야 정상적으로 실행이 됨.
- ERD 1차 설계 완료
  - 클라이언트와 협의하여 Discord에 질문한 게시글에 대한 데이터베이스 형태 협의
    - 게시글 하나당 이미지3, 텍스트3개씩만 입력되도록 구현
- 설계된 ERD 토대 API 명세서 작성 5%완료
  - 로그인API, 회원가입 API 구성\

- 🕐 1차 스크럼 (20:30 ~ 21:30)
  - 현재진행상황 공유
  - 1차 피드백 전까지 진행예정사항 공유
  - 문제점 공유
  
  
## 빙수 개발일지

### 2021-10-31
서버구축 10% 완료
  - ec2 인스턴스 구축 완료
  
### 2021-11-01
- 로컬 서버 구축 완료
- 포트 포워딩 완료
- API 리스트 작성 10% 완료
