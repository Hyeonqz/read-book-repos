## 새로운 날짜와 시간 API
- 자바 8에서 새로운 날짜와 시간 라이브러리를 제공하는 이유
- 사람이나 기계가 이해할 수 있는 날짜와 시간 표현 방법
- 시간의 양 정의하기
- 날짜 조작, 포매팅, 파싱
- 시간대와 캘린더 다루기

기존 자바1.0 에서는 java.util.Date 클래스 하나로 날짜와 시간 관련 기능을 제공했습니다. <br>
```java
Date date = new Date(117,8,21);
```
출력결과
> Thu Sep 21 00:00:00 CET 2017

결과가 직관적이지 않다. 또한 Date 클래스의 toString 으로 반환되는 문자열을 추가로 활용하기가 어렵다. <br>
그리고 Date 는 JVM 기본 시간대인 CET, 중앙 유럽 시간대를 사용한다. 그리고 Date 클래스는 자체적으로 시간대 정보를 알고있는 것도 아니다 <br>
그리고 Date 를 개선하여 Calendar 클래스가 제공되었지만, 이 또한 에러를 일으키는 설계 문제를 갖고 있다. <br>
그래서 개발자들은 Date, Calendar 어떤 클래스를 사용해야 할지 혼동을 가져왔다 <br>
게다가 DateFormat 같은 일부 기능은 Date 클래스에만 작동했다 <br>

DateFormat 에도 문제가 있다. 바로 스레드에 안전하지 않다 <br>
즉 멀티 스레드가 동시에 하나의 포매터 날짜를 파싱할때 예기지 못한 날짜가 나타나게 될 것이다. <br>

마지막으로 Date 와 Calendar 는 모두 가변 클래스 이다. <br>
즉 유지보수가 힘들 수 있다. 

위 단점들 때문에 Java 8 에서는 java.time 패키지가 추가 되었다. 

### LocalDate, LocalTime, Instant, Duration, Period 클래스
#### LocalDate & LocalTime
새로운 날짜와 시간 API 를 사용할 때 처음 접하게 되는 것이 LocalDate 이다. <br>
LocalDate 인스턴스는 시간을 제외한 날짜를 표현하는 불변 객체입니다. LocalDate 객체는 어떤 시간대 정보도 포함하지 않는다. 
```java
LocalDate date = LocalDate.of(2024,5,16); // 2024-05-16
int year = date.getYear(); // 2024
Month month = date.getMonth(); // MAY
int day = date.getDayOfMonth(); // 16
LocalDate today = LocalDate.now(); // 2024-05-16
```
```java
LocalTime time = LocalTime.of(13,45,20); // 13:45:20
int hour = time.getHour(); //13
int minute = time.getMinute(); //45
int second = time.getSecond(); //20
```

#### 날짜와 시간 조합
LocalDateTime 은 LocalDate + LocalTime 을 역할을 할 수 있는 복합 클래스 이다. 즉 날짜, 시간 모두 표현할 수 있다는 뜻이다.
```java
LocalDateTime dt1 = LocalDateTime.of(2024, Month.MAY, 16, 13,45,20);
LocalDate date1 = dt1.toLocalDate();
LocalTime time1 = dt1.toLocalTime();
```

#### Instant 클래스 : 기계의 날짜와 시간
사람은 보통 주,날짜,시간, 분으로 날짜와 시간을 계산한다. 하지만 기계에서는 이와 같은 단위로 시간을 표현하기 어렵다 <br>
기계의 관점에서는 연속된 시간에서 특정 지점을 하나의 큰 수로 표현하는 것이 가장 자연스러운 시간 표현 방법이다 <br>
Instant 클래스는 유닉스 에포크 시간 UTC 를 기준으로 시간을 초로 표현한다. <br>

#### 날짜 조정, 파싱, 포매팅
withAttribute 메소드로 기존의 LocalDate 내용을 바꿀 수 있다.
```java
LocalDate date1 = LocalDate.of(2024,5,16);
LocalDate date2 = date1.withYear(2025);
LocalDate date3 = date1.withDayOfMonth(6);
LocalDate date4 = date3.with(ChronoField.MONTH_OF_YEAR,2);
```

#### 다양한 시간대와 캘린더 활용 방법
아직 시간대와 관련한 정보는 없었다. <br>
기존의 java.util.TimeZone 을 대체할 수 있는 java.time.ZoneId 클래스가 등장했다 <br>
새로운 클래스를 이용하면서 서머타임(DST) 같은 복잡한 사항이 자동으로 처리가 되었다. <br>
날짜와 시간 API 에서 제공하는 다른 클래스와 마찬가지로 ZoneId 는 불변 클래스이다 <br>

#### 시간대 사용하기
ZoneRules 클래스에는 약 40개 정도의 시간대가 있다 <br>
```java
ZoneId romeZone = ZoneId.of("Europe/Rome");
```
```java
ZoneId zoneId = TimeZone.getDefault().toZoneId();
```































