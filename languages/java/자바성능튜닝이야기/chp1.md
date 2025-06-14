# 디자인 패턴, 꼭 써야 한다.
시스템을 만들기 위해서 전체 중 일부 의미 있는 클래스 들을 묶은 각각의 집합을 디자인 패턴이라고 생각하면 된다 <br>

### DTO 에 Serializable 을 왜 구현할까?
+ 이 인터페이스를 구현하면, 객체를 직렬화할 수 있다 
+ 서버 사이의 데이터 전송이 가능해진다.


# 내가 만든 프로그램의 속도를 알고 싶다
시스템의 성능이 느릴 때 가장 먼저 해야 하는 작업은 병목 지점을 파악하는 것이다 <br>
- APM 툴
- 프로파일링 툴
  - 응답시간 프로파일링
  - 메모리 프로파일링


### System.currentTimeMills, System.nanoTime
- currentTimeMills: 현재의 시간을 ms로 리턴한다(1/1000초)

JMH 툴을 사용하여 메소드 실행시간을 측정하는 것 또한 좋은 방법 중 하나이다 <br>

# 왜 자꾸 String 을 쓰지 말라는거야
String 클래스는 잘 사용하면 상관이 없지만, 잘못 사용하면 메모리에 많은 영향을 준다. <br>

String 객체가 GC에 영향을 주는 것은 확실하다 <br>
대부분의 웹 기반 시스템은 DB 에서 데이터를 갖고 와서 그 데이터를 화면에 출렷하는 시스템이기 때문에 쿼리문장을 만들기 위한 String 클래스와 결과를 처리하기 위한 Collection 을 많이 사용한다.<br>

### StringBuffer, StringBuilder
StringBuffer 는 스레드에 안전하게 설계되어 있어, 여러 스레드에서 하나의 StringBuffer 객체를 처리해도 전혀 문제가 되지 않는다 <br>
StringBuilder 는 단일 스레드에서의 안전성만을 보장한다 <br>
그렇기에 여러 스레드에서 하나의 StringBuilder 객체를 처리하면 문제가 발생한다 <br>

### String, StringBuffer, StringBuilder
속도로만 따지면 StringBuilder 가 StringBuffer 보다 빠른 장점이 있다 <br>
하지만 멀티스레드 환경에서는 스레드 세이프한 StringBuilder 가 더 권장이 된다 <br>
그대신 동시성 문제에 대비하기 위해서 동기화에 대한 내용을 잘 숙지해서 대비해야 한다 <br>

StringBuffer, StringBuilder 는 같은 주소에서 문자열을 계속 더한다 <br>
하지만 String 은 문자열을 더해야할 때 마다 새로운 객체를 생성하므로 성능이 안좋을 수 밖에 없다 <br>

### 그럼 언제 사용해야 하나?
- String : 짧은 문자열을 더할 경우 사용한다.
- StringBuffer
  - 스레드에 안전한 프로그램이 필요할 때, 스레드에 안전한지 모를 때 사용한다.
  - static 으로 선언한 문자열을 변경하거나, singleton 으로 선언된 클래스에 선언된 문자열일 경우 사용
- StringBuilder
  - 스레드 세이프 한 여부 관계 없는 프로그램 개발시 사용한다.
  - 메소드 내에 변수를 선언했다면 해당 변수는 메소드 내에서만 살아 있으므로, StringBuilder 를 사용하면 된다.


# 어디에 담아야 할까...?
배열은 처음부터 크기를 지정해야 하지만, Collection 들은 그럴 필요가 없다 <br>

### Collection, Map 인터페이스의 이해
배열을 제외하면 List, Map 에 담고는 한다 <br>

- ArrayList: Vector 와 비슷하지만, 동기화 처리 되어 있지 않다.
- LinkedList: ArrayList 와 비슷하지만, Queue 인터페이스를 구현하였기 때문에 FIFO 큐 작업을 수행한다

List 의 큰 단점은 데이터가 많은 경우 처리 시간이 늘어난다 <br>

### Queue
Queue 인터페이스를 구현한 클래스는 2가지 이다 <br>
LinkedList, PriorityQueue 이다 <br>

HastSet 과 LinkedHastSet 의 성능이 비슷하고, TreeSet 을 성능이 제일 좋지 않다 <br>
컬렉션 또한 데이터의 크기를 알고 있을 경우 생성시 미리 크기를 지정하는 것이 성능에 유리하다 <br>

TreeSet 은 속도가 느리다, 그 이유는 데이터를 저장하면서 자동으로 정렬하기 때문이다 <br>

### Collection 관련 클래스의 동기화
컬렉션을 동기화를 지원하기 위해선 synchronized 를 사용할 수 있다 <br>

# for 문을 더 빠르게 사용해 보자.
Java 에서 사용하는 반복구문은 3가지다
- for
- do~while
- while

일반적으로 for 문이 가장 많이 사용된다 <br>
while 은 잘못하면 무한 루프에 빠질 수 있기 때문이다 <br>

```java
int listSize = list.size();
for(int i=0; i < listSize; i++)
```

위 처럼 코딩하는 방식을 지향해야 한다 <br>

# static 제대로 써보자
Static 은 정적이라는 뜻이다 <br>