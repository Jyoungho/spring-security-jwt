## 기능구현
1. /api/v1/signup 회원가입
2. /api/v1/login 로그인
3. /api/v1/new/token refreshToken 으로 accessToken 갱신
4. /api/v1/me 내 정보 보기

## 기능구현 상세

### 1. /api/v1/signup 회원가입
- 기능상세
    - 회원가입 요청 시 아래와 같이 데이터를 요청한다.
    ```text
    userId - "test" 
    password - "1234"
    name - "홍길동"
    regNo - "920910-1234567"
    ```
    - 회원가입 시 비밀번호는 __단방향__ 암호화를 하고, 주민등록번호의 경우 __양방향__ 암호화를 한다.

- 예외처리
    - 기존에 회원가입한 경우 안내메세지를 보낸다.
      - (message) 해당계정은 아이디는 이미 존재합니다.

### 2. /api/v1/login 로그인
- 기능상세
    - 로그인 시 아래와 같이 데이터를 요청한다.
    ```text
    userId - "test" 
    password - "1234"
    ```
    - 계정정보가 일치하는지 확인 후 accessToken 과 refreshToken 을 발급해준다.
    - accessToken 유효기간은 5분으로 하고, refreshToken 의 경우 15일의 유효기기간을 갖는다. 

- 예외처리
    - 아이디, 비밀번호가 일치하지 않을 경우 안내메세지를 보낸다.
        - (message) 아이디 혹은 비밀번호를 확인해주세요.
    
### 3. /api/v1/new/token
- 기능상세
    - accessToken 이 만료되었을 경우 refreshToken 으로 계속 사용할 수 있도록 한다.
    - 요청 데이터는 아래와 같다.
    ```text
    id - 1
    refreshToken - "eyJ0eXBlIjoidG9rZW4iLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiZXhwIjoxNjUwNjk3NzAxLCJ1c2VySWQiOiJ0ZXN0In0._C7Hf1dVpBFG3DfVxAdrUjDnN2S145amYOtLgumCJKA"
    ```
    
- 예외사항
    - 요청 refreshToken 과 DB refreshToken 값이 일치하지 않을 경우
        - (message) refresh token 이 비정상적입니다. 재발급받아 주시기 바랍니다.
    - DB 에서 해당하는 refreshToken id 값을 찾을 수 없을 경우
        - (message) refresh token 을 찾을 수 없습니다.
    - refreshToken 이 유효하지 않을 경우
        - (message) refresh token 이 유효하지 않습니다
    
### 4. /api/v1/me
- 기능상세
    - Jwt token 을 기반으로 유저의 정보를 조회합니다.
    - Jwt token 값은 accessToken 을 활용하고, Header 값에 넣어준다.
    - Bearer + accessToken 형식으로 보내야해 한다.
    ```text
    Authorization: Bearer eyJ0eXBlIjoidG9rZW4iLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiZXhwIjoxNjQ5NDA0ODE5LCJ1c2VySWQiOiJ0ZXN0In0.Px8P_yEe3vFKvODrj3kQrFPF6sOJPa3fznUGDWdVW38
    ```
  
- 예외사항
    - token 을 입력하지 않을 경우
        - (message) 인증완료 후 이용가능합니다.
    - accessToken 이 만료되었을 경우
        - (message) 토큰인증이 만료 되었습니다.
        
## 검증방법
- 테스트 코드를 통하여 API 검증을 진행한다.
- swagger 통해 test 할 수 있습니다.

### swagger test
- swagger test 시 진행 순서는 아래와 같습니다.
1. 회원가입
2. 로그인
3. 로그인 후 Authorize 에 accessToken 추가 -> ex) Bearer eyJ0eXBlIjoidG9rZW4iLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiZXhwIjoxNjQ5NDAyNjAxLCJ1c2VySWQiOiJ0ZXN0In0.2TDFUfzGgigmsp4sRMUXdBKCJGiZRCGkJqJOhW5CHBo
4. 내정보보기
