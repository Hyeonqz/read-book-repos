# 11장 - 네티에서 제공하는 ChannelHandler 와 코덱
- SSL/TLS 를 이용한 네티 어플리케이션 보안
- 네티 HTTP/HTTPS 애플리케이션 개발
- 유휴 연결과 시간 만료 처리
- 구분 기호 및 길이 기반 프로토콜의 디코딩
- 대용량 데이터 기록

네티는 다양한 일반적인 프로토콜을 위한 코덱과 핸들러를 제공함으로 써 지루한 작업에 필요한 시간과 노력을 크게 절약할 수 있게 해준다 <br>

## SSL/TLS 를 이용한 네티 어플리케이션 보안
ssl/tls 는 보통 웹 사이트에 기본적으로 이용되지만, SMTPS 메일 서버나 관계형 데이터베이스 시스템과 같이 HTTP 기반이 아닌 애플리케이션에도 이용된다 <br>
```java
public class SslChannelInitializer extends ChannelInitializer<Channel> {
	private final SslContext sslContext;
	private final boolean stratTls;

	public SslChannelInitializer (SslContext sslContext, boolean stratTls) {
		this.sslContext = sslContext;
		this.stratTls = stratTls;
	}

	@Override
	protected void initChannel (Channel ch) throws Exception {
		SSLEngine engine = sslContext.newEngine(ch.alloc());
		ch.pipeline()
			.addFirst("ssl", new SslHandler(engine, stratTls));
	}

}

```

### 네티 HTTP/HTTPS 어플리케이션 개발
HTTP 는 요청/응답 패턴에 기반을 두고 있다 <br>

### 대용량 데이터 기록
대용량 데이터 기록하는 작업은 네트워크 포화의 가능성 때문에 비동기 프레임워크의 특수한 문제로 여겨지고 있다 <br>
기록 작업은 논블로킹 이므로 완료 시 반환되며 모든 데이터를 기록하지 않았더라도 ChannelFuture 에 알림을 전달한다 <br>
이 경우 기록을 중단하지 않으면 메모리가 부족해질 우려가 있다 <br>
따라서 대용량 데이터를 기록할 때는 원격 피어와의 느린 연결 속도 때문에 메모리 해제가 지연될 가능성에 대비 해야 한다 <br>

ChunkedInput<B> 인터페이스는 대용량 파일 전송에 가장 중요한 역할을 하며, 여기서 매개변수 B 는 readChunk() 메소드에서 반환하는 형식이다 <br>

### 데이터 직렬화
직렬화 하려는 객체는 Serializable 인터페이스를 구현해야 한다. <br>
위 인터페이스를 구현하지 않으면 직렬화를 할 수 없다 <br>

JDK 는 네트워크를 통해 전송하는 기본형 데이터 형식과 POJO 형식의 그래프를 직렬화/역직렬화 하기 위한 <br>
`ObjectOutputStream` , `ObjectInputStream` 을 제공한다 <br>
API 는 복잡하지 않으며 Serializable 을 구현하는 모든 객체에 적용할 수 있다, 하지만 효율적이지도 않은 단점이 있다. <br>

#### JDK 직렬화

#### JBoss 마셜링을 이용한 직렬화
외부 의존성을 사용하면 JDK 직렬화보다 3배 이상 빠르고 크기도 작다.<br>
- CompatibleMarshallingDecoder -> JDK 직렬화를 이용하는 피어와 호환된다.
- CompatibleMarshallingEncoder -> JDK 직렬화를 이용하는 피어와 호환된다.

```java
public class MarsahllingInitializer extends ChannelInitializer<Channel> {
	private final MarshallerProvider marshallerProvider;
	private final UnmarshallerProvider unmarshallerProvider;

	public MarsahllingInitializer (MarshallerProvider marshallerProvider, UnmarshallerProvider unmarshallerProvider) {
		this.marshallerProvider = marshallerProvider;
		this.unmarshallerProvider = unmarshallerProvider;
	}

	@Override
	protected void initChannel (Channel ch) throws Exception {
		ChannelPipeline channelPipeline = ch.pipeline();
		channelPipeline.addFirst(new MarshallingDecoder(unmarshallerProvider));
		channelPipeline.addLast(new MarshallingEncoder(marshallerProvider));
		channelPipeline.addLast(new ObjectHandler());
	}
	
	public static final class ObjectHandler extends SimpleChannelInboundHandler<Serializable> {

		@Override
		protected void messageReceived (ChannelHandlerContext ctx, Serializable msg) throws Exception {
			
		}

	}

}

```

#### 프로토콜 버퍼를 통한 직렬화
프로토콜 버퍼는 구조화된 데이터를 작고 효율적으로 인코딩/디코딩 하며 다중 언어 프로젝트에 적합하다 <br>

네티의 코덱과 핸들러는 여러 대규모 시스템에서 입증된 강력하고 안정적인 컴포넌트로서, 이를 조합하고 확장하는 방법으로 광범위한 처리 시나리오를 구현할 수 있다


### 용어 정리
- 직렬화: 객체 -> 데이터로 변환
- 역직렬화: 데이터 -> 객체로 변환
- 유휴: 사용가능한 상태이나 실제적인 작업이 없는시간(대기 시간에 있는 것)
- 코덱 : 인코더, 디코더 두 기능을 모두 가진 컴포넌트
- 메시지: 특정 어플리케이션에서 의미가 있는 바이트의 시퀀스 구조
- 스트림: 데이터가 순서대로 흐르는 것을 의미한다.
    - 바이트 스트림: 바이트 데이터가 순서대로 흘러가는 것을 의미한다.
- **부트스트랩**: 애플리케이션을 실행하도록 구성하는 과정
    - 사전적인 용어로는 : 현재 상황에서 어떻게든 한다
- **EventLoop**: 멀티플렉싱 방식을 사용하여 단일 쓰레드로 여러 채널의 I/O 이벤트를 반복적으로 확인하고 처리하는 요소
- **ChannelHandler** : 네트워크를 통해 주고받아지는 데이터를 처리하고, 변환하며, 목적지에 도달하도록 하는 역할 -> 네트워크 데이터를 처리를 담당
- **ChannelPipeline**: ChannelHandler 를 각 단계 별로 처리하는 역할을 함.
- **원격피어**: 클라이언트 - 서버 가 연결된 상태를 의미.



































