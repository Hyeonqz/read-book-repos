# DDD

### 도메인 영역의 주요 구성요소
- 엔티티
- 밸류 -> 고유 식별자를 갖지 않음 , 불변으로 구현할 것을 권장
- 애그리거트 -> 엔티티, 밸류 개체를 개념적으로 묶은 것 ex) Orderer,OrderLine, Order 하나로 묶으면 '주문' 으로 묶을 수 있다. 주문을 할 때 필요한 객체이기 때문.
- 레포지토리
  - 도메인 객체를 영속화 하는데 필요한 기능을 추상화환 것 -> InfraStructure 영역
- 도메인 서비스

에릭 에반스 [도메인 주도 설계] 책 참고하기! <br>

도메인 모델과, 엔티티모델이 같을 필요가 전혀 없다 <br>


도메인 모델 엔티티는 도메인 기능도 함께 제공한다 <br>

#### 1. 애그리거트 -> 관련 객체를 하나로 묶은 군집
도메인이 커질수록 개발할 도메인 도메인 모델도 커지면서 많은 엔티티와 Value 가 출현한다 <br>
엔티티와 밸류 개수가 많아질수록 모델은 점점 더 복잡해진다 <br>

#### 2. 인프라스트럭처
표현,응용, 도메인 영역을 지원한다. <br>
도메인 객체의 영속성 처리, 트랜잭션, SMTP, REST 클라이언트 등 외부 영역에서 필요로 하는 프레임워크, 구현 기술, 보조 기능을 지원한다 <br>
외부라 함은, 인프라 영역이 아닌, 표현,응용,도메인이 외부이다 <br>
이 외부에서 필요로 하는 무언가를 위 부분에서 구현한다 <br>

DIP 처럼, 도메인,응용 영역이 인프라 영역을 직접 의존하는 것보다, 도메인,응용 영역에 정의한 인터페이스를 인프라 영역에서 구현하는 것이 시스템을 더 유연하고 테스트하기 쉽게 만들어준다 <br>

구현의 편리함은 DIP 가 주는 장점(유연함, 테스트) 만큼 중요하기 때문에, DIP 의 장점을 해치지 않는 범위에서 응용 영역과 도메인 영역에서 구현 기술에 대한 의존을 가져가는 것이 나쁘지 않다고 생각한다 <br>

#### 모듈 구성
패키지 구성에는 정답이 없다. 아래는 영역별로 패키지를 구성한다<br>
```java
-- ui
-- application
-- domain
-- infrastructure
```

도메인이 크면, 도메인 별로 패키지를 나누고 하위 도메인마다 계층을 구성한다
```html
catalog
-- ui
-- application
-- domain
-- infrastructure

order
-- ui
-- application
-- domain
-- infrastructure

member
-- ui
-- application
-- domain
-- infrastructure
```

개인적으로는 한 패키지에 가능하면 10~15개 미만으로 타입 개수를 유지하면 좋을 것 같다 <br>
위 수가 넘어가면 패키지를 분리하는 시도를 해보자 <br>


> Tip: 상위 수준에서 모델을 정리하면 도메인 모델의 복잡한 관계를 이해하는데 도움이 된다

복잡한 도메인을 이해하고 관리하려면 '애그리거트' 가 필요하다 <br>
애그리거트는 모델을 이해하는데 도움을 줄 뿐만 아니라, 일관성을 관리하는 기준도 된다 <br>

- 한 애그리거트에 속한 객체는 다른 애그리거트에 속하지 않는다.
  - 애그리거트는 독립된 객체 군이다.

애그리거트는 여러 객체로 구성되기 때문에 한 객체만 상태가 정상이면 안 된다 <br>
도메인 규칙을 지키려면 애그리거트에 속한 모든 객체가 정상 상태를 가져야 한다 <br>


트랜잭션 범위는 작을수록 좋다 <br> 
한 트랜잭션이 한 개 테이블을 수정하는 것과 세 개의 테이블을 수정하는 것을 비교하면 성능에서 차이가 발생한다 <br>

만약 두 개 이상의 애그리거트를 변경해야 한다면, Service 에서 각 애그리거트의 상태를 변경해야 한다 <br>


### ⭐️ID를 이용한 애그리거트 참조⭐ 
ID 를 이용한 참조 방식을 사용하면 복잡도를 낮출 수 있다 <br>
ID 를 애그리거트로 참조하면 레포지토리 마다 다른 저장소를 사용하도록 구현할 때 확장에 용이하다 <br>

ID 를 이용한 애그리거트 참조는 지연 로딩과 같은 효과를 만드는데 지연 로딩과 관련된 대표적인 문제가 N+1 조회 문제가 발생할 수 있다 <br>

N+1 조회 문제는 더 많은 쿼리를 실행하기에 전체 조회 속도가 느려지는 원인이 된다 <br>
위 문제가 발생하지 않게 하려면 '조인' 을 사용해야 한다 <br>

조인을 사용하는 가장 쉬운 방법은 ID 참조 방식 -> 객체 참조 방식으로 바꾸고 '즉시 로딩' 을 사용하는 것이다 <br>
ID 참조 방식에서 N+1 문제를 발생시키지 않으려면 조회 전용 쿼리를 사용하면 된다 <br>
DAO 를 만들고, DAO 의 조회 메소드에서 조인을 이용해 한 번의 쿼리로 필요한 데이터를 로딩하면 된다 <br>

쿼리가 복잡하거나, SQL 에 특화된 기능을 사용해야 한다면, 조회를 위한 부분만 마이바티스 같은 기술을 이용해서 구현할 수 있다 <br>

@Embeddable -> Value 타입 객체 <br>

- Value 컬렉션 매핑 -> AttributeConverter 를 사용하자.

```java
@Converte(autoApply = true)
// 위 설정을 해주지 않으면 컨버팅을 원하는 Value 객체에 직접 설정을 해줘야 한다. ex) @Converter(converter = EmailSetConverter.class)
public class EmailSetConverter implements AttributeConverter<EmailSet, String> {
  @Override

  public String convertToDatabaseColumn (EmailSet attribute) {
    // 로직
  }

  @Override
  public String convertToEntityAttribute (String dbData) {
    // 로직
  }

}
```

기본적으로 2개의 메소드를 제공하며 위 메소드를 가공하여 원하는 데이터로 만들 수 있다 <br>

- JPA 에서 식별자 타입은 Serializable 타입이어야 하므로 위 인터페이스를 구현해야 한다 

Value 타입을 식별자로 구현할 때 장점은, 식별자에 기능을 추가할 수 있다는 점이다 <br>
- Value 타입은 엔티티를 비교할 목적이기 때문에 equals,hashcode 메소드를 알맞게 구현을 해줘야한다.

@SecondaryTable 을 이용하면 일반 조회시, 두 테이블을 Join 하여 데이터를 조회한다 <br>

Value 타입을 구현 기술의 한계나 팀 표준 때문에 @Entity 로 구현해야 할 수도 있다 <br>
JPA 는 @Embeddable 타입의 클래스 상속 매핑을 지원하지 않는다 <br>


### 애그리거트 로딩 전략
JPA 매핑을 설정할 때 항상 기억해야 할 점은 애그리거트에 속한 객체가 모두 모여야 완전한 하나가 된다는 것이다 <br>

위 책에서는 root Entity 를 root 애그리거트 라고 부른다 <br>

@Entity 에서 연관 매핑 조회 방식 (FetchType.EAGER, FetchType.LAZY) 2가지가 있다 <br>
root 엔티티 조회시 연관 테이블 전체 조회를 하려면 EAGER, 필요할 때 Join 을  통해서 조회하려면 LAZY 방식을 사용한다 <br>

하이버네이트는 중복된 데이터를 알맞게 제거해준다 <br>

애그리거트는 개념적으로 1개여야 한다 <br>
JPA 는 트랜잭션 범위 내에서 지연 로딩을 허용한다. 아래 예시 코드를 보자
```java
@Transactional
public void removeOptions(ProductId id, int optIndex) {
	// Product 로딩 -> 지연 로딩 상태라면 나머지 연관 객체는 조회되지 않음.
	Product product = productRepotisory.findById(id);
	
	// 트랜잭션 범위 안에 있으므로, 지연 로딩으로 설정한 연관 로딩 또한 가능
    product.removeOptions(optIndex);
}
```

만약 fetchType 이 EAGER 였다면 트랜잭션 범위가 아니였더라도, 연관객체 상태를 바꾸거나 할 수 있다 <br>

대부분의 어플리케이션은 CUD 보다는 조회 기능을 실행하는 빈도가 훨씬 많다 <br>

그러므로 상태 변경을 위해 지연 로딩을 사용할 때 발생하는 추가 쿼리로 인한 실행 속도 저하는 보통 문제가 되지 않는다 <br>

LAZY 로딩은 동작 방식이 항상 동일하기 때문에 즉시 로딩처럼 경우의 수를 따질 필요가 없다 <br>

하지만 지연 로딩은 즉시 로딩보다 쿼리 실행 횟수가 많아질 가능성이 더 높다 <br>
따라서 무조건 즉시 로딩이나 지연 로딩으로만 설정하기 보다는 애그리거트에 맞게 즉시 로딩, 지연 로딩을 선택해야 한다 <br>

#### 애그리거트의 영속성 전파
@Embeddable 매핑 타입은 함께 저장되고, 삭제되므로 cascade 속성은 필요하지 않다 <br>
기본적으로 연관관계는 cascade 옵션은 없고, 지정해줘야 한다 <br>

보통 저장시 자식도 같이 저장, 부모 삭제시 자식도 같이 삭제 하게 하는 옵션을 주로 사용한다 <br>
> @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)

위 옵션을 자주 보게 될 것이라고 생각한다 <br>

#### 식별자 생성 기능
- 사용자 직접 생성
- 도메인 로직으로 생성
- DB 일련번호 생성

DIP 를 완벽하게 지키면 좋겠지만, 개발 편의성과 실용성을 가져가면서 구조적인 유연함은 어느 정도 유지할 필요가 있다 <br>
복잡도를 높이지 않으면서 기술에 따른 구현 제약이 낮다면 합리적인 선택이라고 생각한다 <br>

### Spring Data JPA 를 이용한 조회 기능
CQRS 는 명령, 조회 모델을 분리하는 패턴이다 <br>
명령 모델은 상태를 변경하는 기능을 구현할 때 사용하고 조회 모델은 데이터를 조회하는 기능을 구현할 때 사용한다 <br>

즉 도메인 모델은 명령 모델로 주로 사용된다 <br>
보통 정렬,페이징,검색 조건 등 일반적인 조회 기능은 조회 모델을 구현할 때 주로 사용한다 <br>

조회 모델 구현시 JPA, MyBatis, Jdbc 등 여러가지 기능을 사용해서 구현할 수 있다 <br>
그러므로 너무 JPA 를 통한 구현에만 집착을 하면 안됀다 <br>

스펙은 인터페이스를 정의해보자
```java
public interface Specification<T> {
	public boolean isSatisfiedBy(T agg);
}
```

Spring Data JPA 를 이용한 스펙 구현에 대하여 알아보자 <br>

```java
import java.io.Serializable;
import java.util.function.Predicate;

public interface Specification<T> extends Serializable {

  @Nullable
  Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb);
}
```

#### Spring Data JPA 정렬
1. OrderBy
2. Sort

2가지 방법으로 정렬을 진행할 수 있다 <br>

```java
1) 
List<Payment> findByNameOrderByIdDesc();

2)
List<Payment> findAll(String name, Sort sort);

Sort sort = Sort.by("id").descending();
findAll("user1",sort);
```

위 처럼 사용을 할 수 있다 <br>

#### 페이징 처리
Spring Data JPA 는 페이징을 위해 Pageable 타입을 제공한다 <br>
> PageRequest page = PageRequest.of(1,10);

그리고 PageRequest 는 Pageable 인터페이스를 구현하는 구현체 중 하나이다 <br>

Pageable 사용할 때 리턴타입이 Page 일 경우 스프링 데이터 JPA 는 목록 조회 쿼리와 함께 count 쿼리도 실행하여 조건에 해당하는 데이터 개수를 구한다 <br>
Page 는 전체 개수, 페이지 개수 등 페이징 처리에 필요한 데이터를 제공한다 <br>'

즉 Page 를 리턴타입으로 사용할 경우 부가적인 쿼리가 더 실행된다는 것을 기억해야 한다 <br>

도메인 로직을 도메인 영역과 응용 서비스에 분산해서 구현하면 코드 품질에 문제가 발생한다 <br>
- 코드의 응집성이 떨어진다.
  - 도메인 데이터와 그 데이터를 조작하는 도메인 로직이 한 영역에 위치하지 않고 서로 다른 영역에 위치한다는 것은 도메인 로직을 파악하기 위해 여러 영역을 분석해야 한다는 것을 의미한다.
- 여러 응용 서비스에 동일한 로직을 구현할 가능성이 높아진다.
```java
@RequiredArgsConstructor
@Service
public class MemberService {
	
	private final MemberRepository memberRepository;
	
	public void deactivate(Long id, String memberId, String pwd) {
		Member member = memberRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Member not found"));
		
		// 도메인 로직이 있으면 member 를 통해서 메소드를 호출하여 검증을 한다.
	}
}
```

소프트웨어가 가져야 할 중요한 경쟁 요소 중 하나는 변경 용이성인데, 변경이 어렵다는 것은 그만큼 소프트웨어의 가치가 떨어진다는 것을 의미한다 <br>
소프트웨어의 가치를 높이려면 도메인 로직을 도메인 영역에 모아서 코드 중복을 줄이고 응집도를 높여야 한다 <br>

application 은 표현영역과 도메인 영역을 연결하는 매개체 역할을 하는데, 이는 디자인 패턴에서 '**FACADE**' 와 같은 역할을 한다 <br>

응용 서비스 자체는 복잡한 로직을 수행하지 않기 때문에, 응용 서비스 구현은 어렵지 않다 <br>
그 대신 응용 서비스의 크기를 중요하게 생각해야 한다 <br> 

각 기능에서 동일한 로직을 위한 코드를 작성하여 중복을 줄일 수있지만, 서비스에 클래스의 크기가 커진다는것이 단점이다 <br>
코드 크기가 커지면 연관성이 적은 코드가 한 클래스에 함께 위치할 가능성이 높아지게 되는데 결과적으로 관련 없는 코드가 뒤섞여 코드를 이해하는데 방해가 된다 <br>

각 응용 서비스에서 공통되는 로직을 별도 클래스로 구현한다.
```java
public final class MemberServiceHelper {
	// 로직
}
```

한 클래스가 여러 역할을 갖는 것보다, 각 클래스마다 구분되는 역할을 갖는 것을 선호한다 -> DIP <br>
한 도메인과 관련된 기능을 하나의 응용 서비스 클래스에서 모두 구현하는 방식보다 구분되는 기능을 별도의 서비스 클래스로 구현하는 방식이 좋다 <br>
ex) 회원 관련 기능(조회,가입) , 회원 정산 기능, 회원 거래 기능 <br>

#### 응용 서비스의 인터페이스와 클래스
어플리케이션 영역에서 인터페이스를 만들어야 할지에 대한 논쟁은 끊임없다 <br>
```java
public interface ChangePwService {
	void changePw(Long id, String curPw, String oldPw);
}

public class ChangePwServiceImpl implements ChangePwService {
	@Override
    public void changePw(Long id, String curPw, String oldPw) {
		// 구현
    } 
}
```

인터페이스를 꼭 사용할 필요는 없다. 하지만 사용하면 좋은 상황은 있다 <br>
1. 구현 클래스가 여러 개인 경우

구현 클래스가 다수 존재하거나 런타임에 구현 객체를 교체해야 할 때 인터페이스를 유용하게 사용할 수 있다 <br>
application 서비스는 런타임에 교체하는 경우가 거의 없고, 한 응용 서비스의 구현 클래스가 두 개인 경우도 드물다 <br>

이런 이유로 인터페이스,클래스를 따로 구현하면 소스 파일만 많아지고 구현 클래스에 대한 간접 참조가 증가해서 전체 구조가 복잡해진다 <br>
따라서 인터페이스가 명확하게 필요하기 전까지는 응용 서비스에 대한 인터페이스를 작성하는 것이 좋은 선택이라고 볼 수는 없다 <br>

표현영역이 아닌 도메인 영역이나 응용 영역의 개발을 먼저 시작하면 응용 서비스 클래스가 먼저 만들어진다 <br>
이렇게 되면 표현 영역의 단위 테스트를 위해 응용 서비스 클래스의 가짜 객체가 필요한데 이를 위해 인터페이스를 추가할 수도 있다 <br>

application Service 가 제공하는 메소드는 도메인을 이용해서 사용자가 요구한 기능을 실행하는데 필요한 값을 파라미터로 전달받아야 한다 <br>

표현 영역에 응용 계층이 의존하면 안된다 <br>

스프링에 @Transactional 은 메소드가 RuntimeException 을 발생시키면 트랜잭션을 롤백하고, 그렇지 않으면 commit 한다 <br>

### Presentation Layer
- User 가 시스템을 사용할 수 있는 화면을 제공하고 제어한다.
- User 요청을 알맞은 Application 에 전달하고 결과를 전달한다
- User 의 세션을 관리한다.

#### 값 검증 (Validation)
값 검증은, 표현 영역 및 응용 서비스 두 곳에서 모두 수행할 수 있다 <br>
원칙적으로 모든 값에 대한 검증은 응용 서비스에서 처리한다 <br>

#### 권한 검사
스프링 시큐리티를 사용하여 유연하고 확장성 있게 사용할 수 있다 <br>
하지만 유연한 만큼 복잡하다는 것을 의미한다 <br>

보안 프레임워크의 복잡도를 떠나 보통 다음 세 곳에서 권한 검사를 진행할 수 있다 <br>
- presentation
- application
- domain

표현 영역에서는 인증된 사용자인지, 아닌지 검사하는 것이다 <br>
위 접근 제어를 하기 좋은 위치는 Servlet Filter 이다 <br>
인증 필터, 접근 검사 필터를 사용하여 권한을 검사한다 <br>

## 도메인 서비스
도메인 영역의 코드를 작성하다 보면, 한 애그리거트로 기능을 구현할 수 없을 때가 있다. <br>

도메인 서비스는 도메인 영역에 위치한 도메인 로직을 표현할 때 사용한다. <br>
한 애그리거트에 넣기 애매한 도메인 개념을 구현하려면 억지러 넣기보다는 도메인 서비스를 만들어 개념을 명시적으로 표현하는 것이 좋다 <br>

도메인 서비스는 도메인 로직을 수행하지, 응용 로직을 수행하진 않는다 <br>
트랜잭션 처리와 같은 로직은 응용 로직이므로 도메인 서비스가 아닌 응용 서비스에서 처리해야 한다 <br>

특정 기능이, 응용 서비스인지 도메인 서비스인지 감을 잡기 어려울 때는 해당 로직이 애그리거트의 상태를 변경하거나, 애그리거트의 상태 값을 변경하는지 검사해보자 <br>

외부 시스템이나, 타 도메인과 연동 기능도 도메인 서비스가 될 수 있다 <br>

도메인 서비스는 도메인 로직을 표현하므로 위치는 다른 도메인 구성요소와 동일한 패키지에 위치한다 <br>
ex) domain (order, orderRepository, DiscountCalculateService) <br> 

## 애그리거트 트랜잭션 관리
### 애그리거의 트랜잭션
한 애그리거트를 두 사용자가 동시에 변경할 때 트랜잭션이 필요하다 <br>
ex) 운영자 스레드, 고객 스레드 <br>

개념적으로 동일한 애그리거트지만, 물리적으로 서로 다른 애그리거트 객체를 사용한다 <br>
다른 스레드가 동시에 작업을 하게되면 DB 일관성이 깨지게 된다 <br>

위 문제를 해결하기 위해서는 트랜잭션 잠금 기법을 사용할 수 있다 <br>
- 비관적 락(Pessimisitic Lock)
- 낙관적 락(Optimisitic Lock)

대표적으로 2가지 기법이 있다 <br>

추가적으로 오프라인 선점 잠금 또한 존재한다 <br>

단일 트랜잭션에서 동시 변경을 막는 선점 잠금 방식돠 달리 오프라인 선점 잠금은 여러 트랜잭션에 걸쳐 동시 변경을 막는다 <br>
첫 번째 트랜잭션을 시작할 때 오프라인 잠금을 선점하고, 마지막 트랜잭션에 잠금을 해체한다 <br>

## 도메인 모델과 경계
처음 도메인을 만들 때 빠지기 쉬운 함정이 도메인을 완벽하게 표현하는 단일 모델을 만드는 시도를 하는 것이다 <br>
한 도메인은 여러 하위 도메인으로 구분되고 연관되기 때문에 한 개의 모델로 여러 하위 도메인을 표현하는 것은 좋지 않은 설계다 <br>

각 모델은 명시적으로 구분되는 경계를 가져서 섞이지 않도록 해야 한다 <br>
여러 하위 도메인의 모델이 섞이기 시작하면 모델의 의미가 약해질 뿐만 아니라 여러 도메인의 모델이 서로 얽히기 때문에 각 하위 도메인별로 다르게 발전하는 요구사항을 모델에 반영하기 어렵다 <br>

모델은 특정한 컨텍스트하에서 완전한 의미를 갖는다 <br>
같은 제품이라도 카탈로그 컨텍스트와 재고 컨텍스트에서 의미가 서로 다르다 <br>
위 처럼 경계를 갖는 컨텍스트를 'DDD' 에서 바운디드 컨텍스트라고 부른다 <br>

바운디드 컨텍스트는 도메인 모델만 포함하는 것은 아니고, 도메인 기능을 사용하는 사용자에게 제공하는 데 필요한 표현,응용,인프라스트럭쳐 영역을 모두 포함한다 <br>

TODO: 위 챕터는 잘 이해가 안 되었으므로 나중에 다시 보자.20250217

## 이벤트
강한 결합을 없앨 수 있는 방법 중 하나가 바로 이벤트 이다 <br>
비동기 이벤트 방식에 익숙해지면 모든 연동을 이벤트와 비동기로 처리하고 싶을 정도로 강력하고 매력적인 것이 이벤트다 <br>

이벤트라 함은 '과거에 벌어진 어떤 것' 을 의미한다 <br>
ex) 주문을 취소했으면 '주문을 취소했음 이벤트' 가 발생했다고 할 수 있다 <br>

이벤트가 발생했다는 것은 상태가 변경되었다는 것을 의미한다 <br>

이벤트는 발생하는 것에서 끝나지 않는다 <br>
이벤트가 발생하면 그 이벤트에 반응하여 원하는 동작을 수행하는 기능을 구현한다 <br>

#### 이벤트 관련 구성요소
도메인 모델에 이벤트를 도입하려면 4개의 구성요소인 이벤트, 이벤트 생성 주체, 이벤트 디스패처(퍼블리셔), 이벤트 핸들러(구독자) 를 구현해야 한다 <br>

도메인 모델에서 이벤트 생성 주체는 엔티티, 밸류, 도메인 서비스와 같은 도메인 객체이다 <br>
이들 도메인 객체는 도메인 로직을 실행해서 상태가 바뀌면 관련 이벤트를 발생시킨다 <br>

이벤트 핸들러는 이벤트 생성 주체가 발생한 이벤트에 반응한다 <br>
생성 주체가 발생한 이벤트를 전달받아 이벤트에 담긴 데이터를 이용해서 원하는 기능을 실행한다 <br>
ex) 주문 취소됨 이벤트를 받는 이벤트 핸들러는 해당 주문의 주문자에게 SMS 로 주문 취소 사실을 통지할 수 있다 <br>

이벤트 생성 주체와 이벤트 핸들러를 연결해주는 것이 이벤트 디스패처 이다 ex) Kafka Zookeeper<br>
이벤트 생성 주체는 이벤트를 생성해서 디스패처에 이벤트를 전달한다 <br>

이벤트를 전달받은 디스패처는 해당 이벤트를 처리할 수 있는 핸들러에 이벤트를 전파한다 <br>
이벤트 디스패처의 구현 방식에 따라 이벤트 생성과 처리를 동기나 비동기로 실행하게 된다 <br>

이벤트는 발생한 정보를 포함한다 <br>

```java
import java.time.LocalDateTime;

@Setter
@Getter
public class ShippingInfoChangedEvent {
  private String orderNumber;
  private Instant timestamp;
  private ShippingInfo shippingInfo;

  public ShippingInfoChangedEvent (String orderNumber, ShippingInfo shippingInfo) {
    this.orderNumber = orderNumber;
    this.timestamp = Instant.now();
    this.shippingInfo = shippingInfo;
  }

}

```

이벤트는 현재 기준으로 과거에 벌어진 것을 표현하기 때문에 이벤트 이름에는 과거 시제를 사용한다 <br>

위 이벤트를 발생하는 주체는 'Order' 엔티티이다 <br>

위 이벤트는 배송지 변경을 하였을 경우 발생한다 <br>
```java
Events.raise(new ShippingInfoChangedEvent("",""));
```

ShippingInfoChangedEvent 를 처리하는 핸들러는 디스패처로부터 이벤트를 전달받아 필요한 작업을 수행한다 <br>
예를 들어 변경된 배송지 정보를 물류 서비스에 전송하는 핸들러는 다음과 같이 구현할 수 있다 
```java
public class ShippingInfoChangedHandler {
	
	@EventListener(ShippingInfoChangedEvent.class)
	public void handle(ShippingInfoChangedEvent event) {
		shippingInfoSynchronizer.sync(
			event.getOrderNumber(),
			event.getNewShippingInfo()
		);
	}
}

```

이벤트는 이벤트 핸들러가 작업을 수행하는 데 필요한 데이터를 담아야 한다 <br>
이 데이터가 부족하면 핸들러는 필요한 데이터를 읽기 위해 관련 API 를 호출하거나 DB에서 데이터를 조회해야한다 <br>

이벤트는 데이터를 담아야 하지만, 그렇다고 이벤트 자체와 관련 없는 데이터를 포함할 필요는 없다 <br>

### Event 용도
이벤트는 크게 2가지 용도로 쓰인다.
- 트리거 -> 다른 기능을 실행하는 트리거로 쓰임
  - 도메인의 상태가 바뀔 때 다른 후처리가 필요하면 후처리를 실행하기 위한 트리거로 이벤트를 사용할 수 있다.
  - 주문에서는 주문 취소 이벤트를 트리거로 사용할 수 있다.
  - 주문을 취소하면 환불을 처리해야 하는데 이 때 환불 처리를 위한 트리거로 주문 취소 이벤트를 사용할 수 있다.

#### 이벤트 장점
- 서로 다른 도메인 로직이 섞이는 것을 방지할 수 있다.
- 이벤트를 사용하면 기능 확장도 용이하다.


### 이벤트, 핸들러, 디스패처 구현
- Event class: Event 표현
- Dispatcher: Spring 이 제공하는 ApplicationEventPublisher 이용
- Events: 이벤트를 발행한다. ApplicationEventPublisher 사용
- 이벤트 핸들러: 이벤트를 수신해서 처리한다. 스프링이 제공하는 기능을 사용한다.


이벤트 자체를 위한 상위 타입은 존재하지 않는다 <br>
원하는 클래스를 이벤트로 사용하면 된다 <br>

이벤트를 발행할 때는 이벤트를 처리할 수 있게 최소한의 정보는 꼭 넣어야 한다 <br>
만약 모든 이벤트가 공통으로 갖는 프로퍼티가 존재한다면 관련 상위 클래스를 만들 수 도 있다.
```java
@Getter
public abstract class Event {
	private long timestamp;

	public Event (long timestamp) {
		this.timestamp = System.currentTimeMillis();
	}
	
}

@Getter
public class OrderCanceledEvent extends Event{

  public OrderCanceledEvent (String orderNumber) {
    super();
    this.orderNumber = orderNumber;
  }

  private String orderNumber;

}
```

#### Events 클래스와 ApplicationEventPublisher
```java
public class Events {
	private static ApplicationEventPublisher eventPublisher;

	static void setPublisher (ApplicationEventPublisher publisher) {
		Events.eventPublisher = publisher;
	}

	public static void raise (Object event) {
		if (eventPublisher != null) {
			eventPublisher.publishEvent(event);
		}
	}

}

```

Events 클래스의 raise() 는 ApplicationEventPublisher 가 제공하는 publishEvent() 를 이용하여 이벤트를 발생시킨다 <br>
Events 클래스가 사용할 ApplicationEventPublisher 객체는 setPublisher() 메소드롤 통해 전달받는다 <br>

Events.eventPublisher = publisher; 위 로직에 이벤트 퍼블리셔를 전달하기 위해 스프링 Config 가 필요하다.
```java
@Configuration
@RequiredArgsConstructor
public class EventsConfiguration {
	private final ApplicationContext applicationContext;
	
	@Bean
	public InitializingBean eventsInitializer() {
		return () -> Events.setPublisher(applicationContext);
	}
	
}

```

이벤트를 발생시킬 코드는 Events.raise() 메소드를 사용한다 <br>
```java
@Getter
public class Order {
	private String number;
	private String state;
	
	
	public void cancel() {
		this.verityShipped();
		this.state = "CANCELED";
		Events.raise(new OrderCanceledEvent(number));
	}

	private void verityShipped () {
	}

}

```

이벤트를 처리할 핸들러는 스프링이 제공하는 @EventListener 어노테이션을 사용해서 구현한다 <br>
```java
@RequiredArgsConstructor
@Service
public class OrderCanceledEventHandler {
	private final RefundService refundService;
	
	@EventListener(OrderCanceledEvent.class)
	public void handle(OrderCanceledEvent event) {
		refundService.refund(event.getOrderNumber());
	}
}
```

raise() 에 의해 이벤트가 실행되면 OrderCanceledEvent 타입을 전달하면 OrderCanceledEvent.class 값을 갖는 @EventListener 어노테이션을 붙인 메소드를 찾아 실행한다 <br>
도메인 상태 변경과 이벤트 핸들러는 같은 트랜잭션 범위에서 실행된다 <br>

이벤트를 사용하여 강결합 문제는 해소하였다 <br>

### 이벤트 비동기 구현
이벤트를 비동기로 구현할 수 있는 방법은 다양하다 <br>
- 로컬 핸들러를 비동기로 실행하기
- 메시지 큐 이용하기
- 이벤트 저장소와 이벤트 포워드 사용하기
- 이벤트 저장소와 이벤트 제공 API 사용하기

#### 1) 로컬 핸들러 비동기 실행
이벤트 핸들러를 별도 스레드로 실행 시킨다 <br>
```java
@EventListener(OrderCanceledEvent.class)
@Async
public void handler(OrderCanceledEvent event) {
	refundService.refund(event.getOrderNumber());
}
```

스프링은 OrderCanceledEvent 가 호출이 되면 handle() 메소드를 별로 스레드를 이용하여 비동기로 실행한다 <br>

#### 2) 메시징 시스템을 이용한 비동기 구현
kafka, RabbitMQ 와 같은 메시징 시스템을 사용할 수 있다 <br>

이벤트가 발생하면 이벤트 디스패처는 이벤트를 메시지 큐에 보낸다 <br>
메시지 큐는 이벤트를 메시지 리스너에 전달하고, 메시지 리스너는 알맞은 이벤트 핸들러를 이용해서 이벤트를 처리한다 <br>

이때 이벤트를 메시지 큐에 저장하는 과정과 메시지 큐에서 이벤트를 읽어와 처리하는 과정은 별도 스레드나 프로세스로 처리 된다 <br>

글로벌 트랜잭션을 사용하면 안전하게 이벤트를 메시지 큐에 전달할 수 있는 장점이 있지만 반대로 전체 성능이 떨어질 수 있다 <br>

메시지 큐를 사용하면 보통 이벤트를 발생시키는 주체와 이벤트 핸들러가 별도 프로세스에서 동작한다 <br>
이것은 이벤트 발생 JVM 과 이벤트 처리 JVM 이 다르다는 것을 의미한다 <br>

동일 JVM 에서 비동기 처리를 위해 메시지 큐를 사용하는 것은 시스템을 복잡하게 만들 뿐이다 <br>

rabbitmq 는 글로벌 트랜잭션을 지원하지만, kafka 는 글로벌 트랜잭션을 지원하지 않는다 <br>
하지만 kafka 는 다른 메시징 시스템에 비해 높은 성능을 보여준다 <br>

#### 3) 이벤트 저장소를 이용한 비동기 처리
이벤트를 일단 DB 에 저장한 뒤에 별도 프로그램을 이용해서 이벤트 핸들러에 전달하는 것이다 <br>

이벤트 발생시 핸들러는 스토리지에 이벤트를 저장한다 <br>
포워더는 주기적으로 이벤트 저장소에서 이벤트를 가져와 이벤트 핸들러를 실행한다 <br>

포워더는 별도 스레드를 이용하기 때문에 이벤트 발행과 처리가 비동기로 처리된다 <br>

이 방식은 도메인의 상태와 이벤트 저장소로 동일한 DB 를 사용한다 <br>
즉 도메인의 상태 변화와 이벤트 저장이 로컬 트랜잭션으로 처리된다 <br>

이벤트를 물리적 저장소에 저장하기에 이벤트 처리 실패시 포워더는 다시 이벤트 저장소에서 이벤트를 읽어와 핸들러를 실행하면 된다 <br>

이벤트 저장소를 이용한 두 번째 방법은 외부 API 를 이용하는 방식이다 <br>
API 방식과 포워더 방식의 차이점은 이벤트를 전달하는 방식에 있다 <br>

포워더 방식이 포워더를 이용해서 이벤트를 외부에 전달한다면, API 방식은 외부 핸들러가 API 서버를 통해 이벤트 목록을 가져간다 <br>
포워더 방식은 이벤트 처리 목록을 알 수 있지만, API 방식은 알 수 없다 <br>

#### 이벤트 저장소 구현
포워더 방식, API 방식 모두 이벤트 저장소를 사용하므로 이벤트를 저장할 저장소가 필요하다 <br>
- EventEntry: 이벤트 저장소에 보관할 데이터 -> 이벤트 dto
- EventStore: 이벤트를 저장하고 조회하는 인터페이스를 구현한다.
- JdbcEventStore: JDBC 를 이용한 EventStore 구현 클래스
- EventApi: restApi 를 이용해서 이벤트 목록 제공

```java
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EventEntry {
	private Long id;
	private String type;
	private String contentType;
	private String payload;
	private long timestamp;
	
}
```

EventStore 는 이벤트 객체를 직렬화해서 payload 에 저장한다 <br>
이때 JSON 으로 직렬화했다면 contentType 값으로 'application/json' 을 갖는다 <br>

```java
public interface EventStore {
	void save(Object event);
	List<EventEntry> get(long offset, long limit);
}
```

이벤트는 과거에 벌어진 사건이므로 데이터가 변경되지 않는다 <br>
위 이유로 이벤트 저장소는 이벤트 추가 및 조회 기능만 제공해야 한다 <br>

```java
@RequiredArgsConstructor
@Component
public class JdbcEventStore implements EventStore {
	private final ObjectMapper objectMapper;
	private final JdbcTemplate jdbcTemplate;
	
	@Override
	public void save (Object event) {
		EventEntry entry = new EventEntry(event.getClass().getName(),
			"application/json", toJson(event));
		
		jdbcTemplate.update(
			"insert into evententry" +
				"(type, content_type, payload, timestamp)" +
				"values(?,?,?,?)",
			ps -> {
				ps.setString(1, entry.getType());
				ps.setString(2, entry.getContentType());
				ps.setString(3, entry.getPayload());
				ps.setTimestamp(4, new Timestamp(entry.getTimestamp()));
			}
		);
	}


	@Override
	public List<EventEntry> get (long offset, long limit) {
		// 조회 쿼리
		return List.of();
	}

	private String toJson (Object event) {
		try {
			return objectMapper.writeValueAsString(event);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
	
}
```

위 로직이 있고 기초적으로 evententry 라는 테이블이 존재해야 한다 <br>

#### 이벤트 저장을 위한 이벤트 핸들러 API 구현
```java
@RequiredArgsConstructor
@Component
public class EventStoreHandler {
	private final EventStore eventStore;
	
	@EventListener(Event.class)
	public void handle(Event event) {
		eventStore.save(event);
	}
}

```

Event 객체 또는 하위 객체를 상속받은 이벤트 타입만 이벤트 저장소에 보관한다 <br>
즉 Event 타입이 publish 가 될 때 == 호출하는 요청이 있을 때만 이벤트가 발생한다는 뜻이다. <br>

Spring 은 ApplicationContext 가 초기화 될 때 @EventListener 어노테이션이 붙은 메소드를 스캔하여 자동으로 등록한다 <br>
이는 Spring 이벤트 매커니즘의 일부로 AOP 프록시를 통해 구현되어 있다 <br>

```java
@GetMapping("/api/events")
public List<EventEntry>list(Long offset, Long limit) {
	return eventStroe.get(offset,limit);
}
```

위 api 호출을 통하여 이벤트 목록을 구할 수 있다 <br>

API 를 사용하는 클라이언트는 일정 간격으로 다음과정을 실행한다 <br>
- 가장 마지막에 처리한 데이터의 offset 인 lastOffset 을 구한다. 저장한 lastOffset 이 없으면 0을 사용한다.
- 마지막에 처리한 lastOffset 을 offset 으로 사용해서 API 를 실행한다 <br>
- API 결과로 받은 데이터를 처리한다.
- offset + 데이터 개수를 lastOffset 으로 저장한다.

마지막에 처리한 lastOffset 을 저장하는 이유는 같은 이벤트를 중복해서 처리하지 않기 위해서 이다 <br>

#### 이벤트 포워더 구현
포워더는 일정 주기로 EventStore 에서 이벤트를 읽어와 이벤트 핸들러에 전달하면 된다 <br>


결론적으로 대규모 시스템을 처리해야한다면 메시지 큐 + 이벤트 저장소 방식이 좋은것 같다.
```java
@Component
@RequiredArgsConstructor
public class EventHandler {
    private final EventStore eventStore;
    private final KafkaTemplate<String, Event> kafkaTemplate;
    
    @TransactionalEventListener
    public void handle(DomainEvent event) {
        // 이벤트 저장
        eventStore.save(event);
        // 동시에 메시지 큐에 발행
        kafkaTemplate.send("domain-events", event);
    }
}
```

특별한 요구사항이 없다면 메시지 큐 방식, 너무 간단하다면 로컬 핸들러 비동기 실행 <br>


#### 이벤트 적용 시 추가 고려 사항.
1. EventEntry 추가 여부
2. 포워더 전송 실패를 얼마나 허용할 것인가?
3. 이벤트 손실에 대한 것
4. 이벤트 순서
5. 이벤트 재처리


연산을 여러번 적용해도 결과가 달라지지 않는 성질을 멱등성 이라고 한다 <br>
수학에서 절댓값 함수인 abs() 가 멱등성을 갖는 대표적인 예이다 <br>

이벤트 핸들러가 멱등성을 가지면 시스템 장애로 인해 같은 이벤트가 중복해서 발생해도 결과적으로 동일한 상태가 된다 <br>

이벤트 처리를 동기로 하든 비동기로 하든 이벤트 처리 실패와 트랜잭션 실패를 함께 고려해야 한다 <br>
트랜잭션 실패와 이벤트 처리 실패를 모두 고려하면 복잡해지므로 경우의 수를 줄이면 도움이 된다 <br>

경우의 수를 줄이는 방법은 트랜잭션이 성공할 때만 이벤트 핸들러를 실행하는 것이다 <br>
스프링은 @TransactionalEventListener 어노테이션을 지원한다 <br>

이 어노테이션은 스프링 트랜잭션 상태에 따라 이벤트 핸들러를 실행할 수 있게 한다 <br>
```java
@TransacationalEventListener(
	classes = OrderCanceledEvent.class,
        phase = TransactionalPhase.AFTER_COMMIT
)
public void handle(OrderCanceledEvent event) {
	refundService.refund(event.getNumber());
}
```

위 AFTER_COMMIT 은 트랜잭션 커밋에 성공한 뒤 핸들러 메소드를 실행한다 <br>
중간에 에러가 발생해서 트랜잭션이 롤백 되면 핸들러 메소드를 실행하지 않는다 <br>

이 기능을 사용하면 이벤트 핸들러를 실행했는데 트랜잭션이 롤백 되는 상황은 발생하지 않는다 <br>

이벤트 저장소로 DB 를 사용해도 동일한 효과를 볼 수 있다 <br>
이벤트 발생 코드와 이벤트 저장 처리를 한 트랜잭션으로 처리하면 된다 <br>

위 처럼 진행시 트랜잭션이 성공할 때만 이벤트가 DB에 저장된다 <br>

### CQRS 패턴
조회화면 특성상 조회 속도가 빠를수록 좋은데 여러 애그리거트의 데이터가 필요하면 구현 방식법을 고민해야 한다 <br>

시스템 상태를 변경할 때와 조회할 때 단일 도메인 모델을 사용하기 때문에 잦는 에러가 발생한다 <br>
JPA 를 사용한 ORM 기법은 도메인 상태 변경 기능을 구현하는데는 적합하지만 여러 엔티티를 엮어와서 출력할 때는 고려할게 많아 구현을 복잡하게 한다 <br>

위 구현 복잡도를 낮추는 간단한 방법이 바로 상태 변경을 위한 모델과 조회 모델을 분리하는 것이다 <br>

시스템이 제공하는 기능은 크게 2가지로 나뉜다
- 상태 변경 -> CUD
- 상태 조회 -> R

단일 모델을 사용할 때 발생하는 복잡도를 해결하기 위해 사용하는 방법이 바로 CQRS 이다.<br>
즉 상태 변경 명령 모델과 상태 제공 조회 모델을 분리하는 패턴이다 <br>

예를들어 명령 모델은 객체 지향에 기반해서 도메인 모델을 구현하기에 좋은 JPA 를 사용하고 <br>
조회 모델은 DB 테이블에서 SQL 로 데이터를 조회할 때 좋은 마이바티스를 사용해서 구현할 수 있다 <br>

즉 명령, 모델 은 서로 다른 기술을 이용해서 구현할 수 있다 <br>

명령 모델은 트랜잭션을 지원하는 RDBMS 를 사용하고, 조회 모델은 조회 성능이 좋은 메모리 기반 NoSQL 을 사용할 수 있다 <br>
두 데이터 저장소 간 데이터 동기화는 이벤트를 활용해서 처리한다. 명령 모델에서 상태를 변경하면 이에 해당하는 이벤트가 발생하고, 그 이벤트를 조회 모델에 전달해서 변경 내역을 반영하면 된다 <br>

#### 웹과 CQRS
일반적인 웹 서비스는 상태를 변경하는 요청보다 상태를 조회하는 요청이 많다 <br>

조회 속도를 높이기 위해 별도 처리를 하고 있다면 명시적으로 명령 모델과 조회 모델을 구분하자 <br>
이를 통해 조회 기능 때문에 명령 모델이 복잡해지는 것을 막을 수 있고, 명령 모델에 관계없이 조회 기능에 특화된 구현 기법을 보다 쉽게 적용할 수 있다 <br>

메모리에 캐싱하는 데이터는 DB에 보관된 데이터를 그대로 저장하기보다는 화면에 맞는 모양으로 변환한 데이터를 캐싱 할 때 성능이 더 유리하다 <br>
즉, 조회 전용 모델을 캐시하는 것이다 <br>

CQRS 패턴을 적용할 때 얻을 수 있는 장점은 명령 모델을 구현할 때 도메인 자체에 집중 할 수 있다는 점이다 <br>
복잡한 도메인은 주로 상태 변경 로직이 복잡한데 명령 모델과 조회 모델을 구분하면 조회 성능을 위한 코드가 명령 모델에 없으므로 도메인 로직을 구현하는 데 집중 할 수 있다 <br>

또한 명령 모델에서 조회 관련 로직이 사라져 복잡도가 낮아진다. <br>

또 다른 장점은 조회 성능을 향상시키는 데 유리하다는 점이다 <br>
조회 단위로 캐시 기술을 적용할 수 있고, 조회에 특화된 쿼리를 마음대로 사용할 수 도 있다 <br>

캐시뿐만 아니라 조회 전용 저장소를 사용하면 조회 처리량을 대폭 늘릴 수도 있다 <br>

단점은 구현해야 할 코드가 더 많다는 점이다 <br>
도메인이 복잡하거나 대규모 트래픽이 발생하는 서비스라면 조회 전용 모델을 만다는 것이 향후 유지 보수에 유리하다 <br>
반면에 도메인이 단순하거나 트래픽이 많지 않은 서비스라면 조회 전용 모델을 따로 만들 때 얻을 수 있는 이점이 있는지 따져봐야 한다 <br>

또 다른 단점은 더 많은 구현 기술이 필요하다 <br>
명령,조회 모델을 다른 구현 기술을 이용하거나 다른 저장소를 사용한다면, 데이터 동기화를 위해 메시징 시스템을 도입해야 할 수도 있다 <br>
