## SQL 응용
DDL <br>
-  create, alter, drop 

```sql
create view 뷰 이름(속성명, 이름, 전화번호) as select ~
create index 인덱스 이름 on 
alter table 테이블명 add 이름 varchar(30) 
alter table 테이블명 alter 이름 varchar(30)
alter table 테이블명 drop column 이름
```

DCL <br>
- commit, rollback, grant, revoke
```sql
grant 권한 on 개체 to 사용자 
revoke 권한 on 개체 from 사용자
```

DML <br>
- select, insert, delete, update

집합 연산자 <br>
- Union - 합집합, 중복된 행은 한번만 출력
- Union ALL - 합집합, 중복된 행도 그대로 출력
- Intersect - 교집합
- Except - 차집합

트리거 <br>
트랜잭션 등 이벤트가 발생할 때 관련 작업이 자동으로 수행되게 하는 절차형 SQL 이다. 









































































































