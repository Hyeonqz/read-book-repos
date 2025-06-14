## 7장 - EventLoop 와 스레딩 모델
- 스레딩 모델의 개요
- 이벤트 루프 개념과 구현
- 작업 스케줄링
- 구현 세부 사항

스레딩 모델이란 간단히 말해: 운영체제, 프로그래밍 언어, 프레임워크 또는 어플리케이션의 맥락에서 스레드 관리에 대한 주요 측면을 정의하는 것을 의미한다 <br>
스레드가 생성되는 방법과 시기는 애플리케이션의 코드으 실행에 중요한 영향을 미치므로 개발자는 여러 모델의 장단점을 잘 알고 있어야 한다 <br>

네티의 스레딩 모델은 사용하기 쉽고 네티 자체와 마찬가지로 애플리케이션 코드르 간소화하고 성능과 유지관리성을 극대화하는 목표를 가지고 있다 <br>
또한 현재의 모델을 선택한 계기가 된 경험에 대헤서도 설명한다 <br>

자바의 동시성 API(java.util.concurrent) 에 대해 어느 정도 알고 있다면 이번 장의 내용을 이해하는 데 문제가 없을 것이다 <br>

### 스레딩 모델의 개요
스레딩 모델은 코드가 실행되는 방법을 지정한다 <br>
동시 실행의 부작용에 대해 항상 주의해야 하므로 적용되는 모델의 영향을 이해하는 것이 중요하다 <br>

현재는 다중 코어나 CPU 를 장착한 시스템이 일반적이므로 최신 애플리케이션은 시스템 리소스를 효율적으로 활용하기 위해 정교한 멀티스레딩 기법을 이용하는 경우가 많다 <br>

현재는 다중 코어나 CPU 를 장착한 시스템이 일반적이므로 최신 애플리케이션은 시스템 리소스를 효율적으로 활용하기 위해 정교한 멀티스레딩 기법을 이용하는 경우가 많다 <br>

기본 스레드 풀링 패턴
- 요청 작업(Runnable 구현) 을 실행하기 위해 풀의 가용 리스트에서 Thread 하나를 선택해 할당한다
- 작업이 완료되면 Thread 가 리스트로 반환되고 재사용할 수 있게 된다.

스레드를 풀링하고 재사용하는 방식은 작업별로 스레드를 생성하고 삭제하는 방식보다 분명히 개선된 것이지만 <br>
컨텍스트 전환 비용이 아예 사라진 것은 아니다 <br>
이 비용은 스레드의 수가 증가하면 명백하게 드러나고 부하가 심한 상황에서는 심각한 문제가 된다 <br>
또한 애플리케이션의 동시성 요건이나 전반적인 복잡성 때문에 프로젝트의 수명주기 동안 다른 스레드 관련 문제가 발생할 수 있다 <br>

### EventLoop 인터페이스
연결의 수명 기간 동안 발생하는 이벤트를 처리하는 작업을 실행하는 것은 네트워킹 프레임워크의 기본 기능이다 <br>
이를 나타내느 프로그래밍 구조를 이벤트 루프 라고 한다. <br>

다음 예제는 이벤트 루프의 기본 개념을 보여준다.
```java
while(!terminated) {
	List<Runnable> readyEvents = blockUntilEventsReady();
	for(Runnable ev: readyEvents) {
		ev.run();
    } // 모든 이벤트를 대상으로 반복하고 실행
}
```

네티의 EventLoop 는 동시성과 네트워킹 2가지 기본 API 를 공동으로 활용해 설계되었다 <br>
구성과 사용 가능한 코어에 따라서는 리소스 활용을 최적화하기 위해 여러 EventLoop 가 생성되고, 여러 Channel 에 서비스를 제공하기 위해 단일 EventLoop 가 할당되는 경우도 있다 <br>

![img_10.png](image/img_10.png) <br>
이벤트 작업은 FIFO 순서로 실행이 된다. 큐 자료구조와 비슷하다. <br>

#### 네티 4의 입출력과 이벤트 처리
입출력 작업에 의해 발생한 이벤트는 하나 이상의 ChannelHandler 가 설치된 ChannelPipeline 을 통과하며 처리된다 <br>
ChannelHandler 는 이러한 이벤트를 전파하는 메소드 호출을 가로채고 필요에 따라 이벤트를 처리할 수 있다 <br>

네티 4의 입출력 작업과 이벤트는 EventLoop에 할당된 Thread 에 의해 처리된다 <br>

### 작업 스케줄링
작업을 나중에 지연 실행하거나 주기적으로 실행해야 하는 경우가 있다. <br>

#### JDK 스케줄링 API
JDK 는 ScheduledExecutorService 인터페이스를 정의하는 java.util.concurrent 패키지를 제공한다 <br>
선택 사항이 많지는 않지만 제공되는 기능으로도 대부분의 사용 사례를 충분히 처리할 수 있다 <br>

```java
    import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

ScheduledExecutorService executorService =
	Executors.newScheduledThreadPool(10);

ScheduledFuture<?> scheduledFuture = executorService.schedule(new Runnable() {
	@Override
	public void run () {
		System.out.println("60 seconds later");
	}
}, 60, TimeUnit.SECONDS);

executorService.shutdown();
```

위 API 는 이용하기는 쉽지만 부하가 심한 상황에서는 성능이 저하될 수 있다 <br>

#### EventLoop 를 이용한 작업 스케줄링
ScheduledExecutorService 구현은 풀 관리 자겅의 일부로 스레드가 추가로 생성되는 등의 한계점을 가지고 있어, 많은 작업이 있을 경우 병목 현상이 발생할 수 있다 <br>

```java
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

Channel ch = ...;
ScheduledFuture<?> future = ch.eventLoop().schedule(
	new Runnable() {
		@Override
		public void run () {
			System.out.println("60 seconds later");
		}
	}, 60, TimeUnit.SECONDS);
```

60초가 지난 후 해당 채널에 할당된 EventLoop 에 의해 Runnable 인스턴스가 실행된다.<br>
네티의 스케줄링 기능은 나중에 살펴볼 기반 스레딩 모델에 영향을 받는다

### 구현 세부 사항
#### 스레드 관리
네티 스레딩 모델이 탁월한 성능을 내는 데는 현재 실행 중인 Thread 의 ID 를 확인하는 기능 <br>
즉 Thread 가 현재 Channel 과 해당 EventLoop 에 할당된 것인지 확인하는 기능이 중요한 역할을 한다 <br>
[EventLoop 는 생명주기 동안 Channel 하나의 모든 이벤트를 처리한다]<br>

호출 Thread 가 EventLoop 에 속하는 경우 해당 코드 블록이 실행되며, 그렇지 않으면 EventLoop 가 나중에 실행하기 위해 작업을 예약하고 내부 큐에 넣는다 <br>
EventLoop 는 다음 해당 이벤트를 처리할 때 큐에 있는 항목을 실행한다 <br>
Thread 가 ChannelHandler 를 동기화하지 않고도 Channel 과 직접 상호작용할 수 있는 것은 이런 작동 방식 덕분이다 <br>

각 EventLoop 에는 다른 EventLoop 로부터 분리된 자체 작업 큐가 들어있다<br>

블로킹 호출을 해야하거나 장기 실행 작업은 실행 큐에 넣지 않아야 하며, 그렇지 않으면 동일한 스레드에서 다른 작업을 실행할 수 없게 된다 <br>
그렇지 않으면 동일한 스레드에서 다른 작업을 실행할 수 없게 된다<br>

즉 블로킹 호출을 해야 하거나  장기 실행 작업을 실행해야 하는 경우 전용 EventExecutor 를 이용하는 것이 좋다 <br>

위 사례를 제외하면 스레딩 모델은 전송의 이벤트 처리 구현과 마찬가지로 큐에 대기 중인 작업이 전체 시스템 성능에 미치는 영향을 크게 좌우한다 <br>

#### EventLoop 와 스레드 할당
Channel 에 이벤트와 입출력을 지원하는 EventLoop 는 EventLoopGroup 에 포함된다 <br>
EventLoop 가 생성 및 할당되는 방법은 전송의 구현에 따라 다르다 <br>

#### 비동기 전송
비동기 구현은 적은 수의 EventLoop(이와 연결된 Thread) 를 이용하여, 현재 모델에서는 이를 여러 Channel 에서 공유할 수 있다 <br>
덕분에 Channel 마다 Thread 를 할당하지 않고 최소한의 Thread 로 다수의 Channel 을 지원할 수 있다 <br>

EventLoopGroup 은 새로 생성된 각 Channel 에 EventLoop 를 할당한다<br>

Channel 은 EventLoop 가 할당되면 할당된 EventLoop 를 수명주기 동안 이용한다 <br>
덕분에 ChannelHandler 구현에서 동기화와 스레드 안정성에 대해 걱정할 필요가 없다 <br>

#### 블로킹 전송
OIO(기존 블로킹 입출력)와 같은 다른 전송의 설계는 약간 다르다 <br>

### 용어 정리