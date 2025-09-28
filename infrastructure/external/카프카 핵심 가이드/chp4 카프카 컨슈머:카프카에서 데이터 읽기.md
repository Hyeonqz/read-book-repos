# 4장 카프카 컨슈머: 카프카에서 데이터 읽기
카프카에서 데이터를 읽는 어플리케이션은 토픽을 '구독' 하고 구독한 토픽들로부터 메시지를 받기 위해 KafkaConsumer 를 사용한다 <br>


## 카프카 컨슈머
### 4.1.1 컨슈머와 컨슈머 그룹
기본적으로 어플리케이션은 kafkaConsumer 를 생성하고, 해당 토픽을 구독하고, 메시지를 받아야한다 <br>
만약 프로듀서가 어플리케이션이 검사할 수 있는 속도보다 더 빠른 속도로 토픽에 메시지를 쓰게 된다면? <br>

만약 데이터 처리하는 컨슈머가 1개 뿐이라면 어플리케이션은 새로 발행되는 메시지를 처리하기 버거워진다 <br>
즉, 토픽으로부터 데이터를 읽어 오는 작업을 확장할 수 있어야 한다 <br>

여러개의 프로듀서가 동일한 토픽에 메시지를 쓰듯이, 여러 개의 컨슈머가 같은 토픽으로부터 데이터를 분할해서 읽어올 수 있어야 한다 <br>

카프카 컨슈머는 보통 '컨슈머 그룹'의 일부로서 동작한다 <br>

컨슈머 그룹안에 여러개의 컨슈머가 있으면, 구독하고 있는 토픽의 서로 분배해서 담당한다 <br>

하나의 토픽을 구독하는 컨슈머 그룹에 파티션 수보다 더 많은 컨슈머를 추가한다면, 컨슈머 중 몇몇은 **유휴** 상태가 되어 메시지를 전혀 받지 못한다 <br>

컨슈머 그룹에 컨슈머를 추가하는 것은 카프카 토픽에서 읽어오는 데이터 양을 확장하는 주된 방법이다 <br>
어떻게 보면 배포되는 서버에 따라서 파티션과 컨슈머를 적절하게 분배를 해야한다 <br>

즉 토픽에 설정된 파티션 수 이상으로 컨슈머를 투입하는 것은 아무 의미가 없다 <br>

카프카는 성능 저하 없이 많은 수의 컨슈머와 컨슈머 그룹으로 확장이 가능하다 <br>

요약을 하면 1개 이상의 토픽에 대해 모든 메시지를 받아야 하는 애플리케이션별로 새로운 컨슈머 그룹을 생성한다 <br>
토픽에서 메시지를 읽거나 처리하는 규모를 확장하기 위해서는 이미 존재하는 컨슈머 그룹에 새로운 컨슈머를 추가하여 처리하게 한다 <br>

### 4.1.2 컨슈머 그룹과 파티션 리밸런스
컨슈머 그룹에 속한 컨슈머들은 자신들이 구독하는 토픽의 파티션들에 대한 소유권을 공유한다 <br>
새로운 컨슈머를 컨슈머 그룹에 추가하면 이전에 다른 컨슈머가 읽고 있던 파티션으로부터 메시지를 읽기 시작한다 <br>

해당 컨슈머가 컨슈머 그룹에서 나가면 원래 이 컨슈머가 읽던 파티션들은 그룹에 잔류한 나머지 컨슈머 중 하나가 대신 받아서 읽기 시작하는 것이다 <br>

컨슈머에 파티션을 재할당하는 작업은 컨슈머 그룹이 읽고 있는 토픽이 변경되었을 때도 발생한다 <br>
컨슈머에 할당된 파티션을 다른 컨슈머에게 할당해주는 작업을 '**리밸런스**' 라고 한다 <br>

리밸런스는 컨슈머 그룹에 높은 가용성과 규모 가변성을 제공하는 기능이라 매우 중요하지만, 문제없이 작업이 수행되고 있는 와중에 주기적으로 발생하므로 달갑지는 않다 <br>

#### 조급한 리밸런스
> 조급한 리밸런스는 모든 파티션 할당을 해제한 뒤 읽기 작업을 정지시키고, 파티션을 다시 할당시킨다 <br>

위 방식은 근본적으로 전체 컨슈머 그룹에 대해 짧은 시간 동안 작업을 멈추게 한다 <br>
우선 모든 컨슈머가 자신에게 할당된 파티션을 포기하고, 파티션을 포기한 컨슈머 모두가 다시 그룹에 참여한 뒤에야 새로운 파티션을 할당받고 읽기 작업을 재개한다 <br>

#### 협력적 리밸런스
> 전체 파티션 중 재할당될 것들에 한정해서 읽기 작업을 정지시킨다.

- 컨슈머 그룹 리더가 다른 컨슈머들에게 각자에게 할당된 파티션 중 일부가 재할당될 것이라고 통보
- 컨슈머들은 해당 파티션에서 데이터를 읽어오는 작업을 멈추고 해당 파티션에 대한 소유권을 포기
- 컨슈머 그룹 리더가 포기된 파티션들을 새로 할당


위 방식은 시간이 조금 더 걸리지만, 전체 작업이 중단되지 않는다 그러므로 비교적 안전하다 <br>
위 작업은 컨슈머 수가 많은 경우에 특히 중요하다 <br>

컨슈머는 해당 컨슈머 그룹의 그룹 코디네이터 역할을 지정받은 카프카 브로에 하트비트를 전송함으로써 멤버쉽과 할당된 파티션에 대한 소유권을 유지한다 <br>
하트비트는 컨슈머의 백그라운드 스레드에 의해 전송되는데, 일정한 간격을 두고 전송되는한 연결이 유지되고 있는 것으로 간주된다. <br>

컨슈머가 일정 시간 이상 하트비트를 전송하지 않으면, 세션 타임아웃이 발생하면서 그룹 코디네이터는 해당 컨슈머가 죽었다고 간주하고 리밸런스를 실행한다 <br>
만약 컨슈머가 크래시 나서 메시지 처리를 중단했을 경우, 그룹 코디네이터는 몇 초 이상 하트비트가 들어오지 않는 것을 보고 컨슈머가 죽었다고 판단한뒤 리밸런스를 실행한다 <br>

위 몇 초 동안 죽은 컨슈머에 할당되어 있던 파티션에서는 아무 메시지도 처리되지 않는다 <br>

컨슈머를 깔끔하게 닫을 경우, 컨슈머는 카프카 브로커 그룹 코디네이터에게 그룹을 나간다고 통지하는데, 그러면 그룹 코디네이터는 즉시 리밸런스를 실행하여 처리가 정지되는 시간을 줄인다 <br>

> ⭐⭐⭐ Q. 파티션은 어떻게 컨슈머에게 할당되나?

컨슈머가 그룹에 참여하고 싶을 때는 그룹 코디네이터에게 Join Group 요청을 보낸다 <br>
가장 먼저 그룹에 참여한 컨슈머가 그룹 리더가 된다 <br>
그룹 리더는 각 컨슈머에게 파티션의 일부를 할당해준다 <br>
-> 어느 타피션이 어느 컨슈머에게 할당되어야 하는지 결정하기 위해서는 PartitionAssignor 인터페이스의 구현체 사용 <br>

파티션 할당이 결정되면 컨슈머 그룹 리더가 그룹 코디네이터에게 전달하고 그룹 코디네이터가 전체 컨슈머에게 전파한다 <br>
각 컨슈머 입장에서는 자신에게 할당된 내역만 보인다 <br>

즉 리더만, 유일하게 그룹 내 컨슈머 할당 내역을 볼수 있다 <br>


### 4.1.3 정적 그룹 멤버쉽
컨슈머가 컨슈머 그룹을 떠나는 순간 해당 컨슈머에 할당된 파티션들은 해제되고, 다시 참여하면 새로운 멤버ID 가 발급되고 리밸런스에 의해 새로운 파티션들이 할당된다 <br>

위 설명은 group.instance.id 값을 잡아 주지 않는한 유효하다 <br>
위 값이 세팅되어 있으면 리밸런싱이 일어나도 예전에 할당받았던 파티션들을 그대로 할당받는다 <br>

그룹 코디네이터는 메타데이터를 캐시해뒀다가, 파티션 재할당시 리밸런스를 시키지 않고 그대로 할당한다 <br>
만약 같은 group.instance.id 컨슈머가 들어오면 이미 존재하는 id 라는 예외를 발생시킨다 <br>

## 4.2 카프카 컨슈머 생성하기
기본적으로 들어가야 하는 옵션은 4가지가 있다.
- bootstrap.servers
- group.id
- key.deserializer
- value.deserializer

## 4.3 토픽 구독하기
컨슈머를 생성했으면 1개의 토픽을 구독해야 한다.

## 4.4 폴링 루프
컨슈머 API의 핵심은 서버에 추가 데이터가 들어왔는지 폴링하는 단순한 루프이다 <br>
- 위 폴링은 무한 루프이기 때문에 종료되지 않는다.
- 컨슈머는 카프카를 계속해서 폴링하지 않으면 죽은것으로 간주되어 이 컨슈머가 읽던 파티션들은 그룹 내의 다른 컨슈머에게 넘겨진다.
- poll() 은 consumerRecord 정보가 저장된 List 를 리턴한다.

위 폴링 루프는 단순히 데이터를 가져오는 것보다 훨씬 더 많은 일을 한다 <br>
- 새 컨슈머에서 처음으로 Poll() 호출 시 컨슈머는 그룹 코디네이터를 찾아서 컨슈머 그룹에 참가하고, 파티션을 할당받는다.


### 4.4.1 스레드 안정성
하나의 스레드에서 동일한 그룹 내에 여러 개의 컨슈머를 생성할 수는 없다 <br>
하나의 스레드당 하나의 컨슈머가 원칙이다 <br>

## 4.5 컨슈머 설정하기
```java
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String offsetReset;

    @Bean("bestConsumerFactory")
    public ConsumerFactory<String, Object> bestConsumerFactory(){
        var consumerProps = new HashMap<String, Object>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);

        // 직렬화 & 역직렬화
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        // consumer group_id 지정
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "a-group"); // "동적으로 할당하게 바꾸자!"

        // 컨슈머가 브로커로부터 레코드를 얻어올 때 발생하는 데이터의 최소량(byte)
        // 위 값을 증가시킬 경우 처리량이 적은 상황에서 지연 또한 증가 할 수 있다.
        consumerProps.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1); // 기본값 1byte

        // FETCH_MIN_BYTES_CONFIG 설정함에 따라 컨슈머에게 응답하기 전 충분한 데이터가 모일 때 까지 기다리게 하는 옵션
        // 조금을 지연이라도 없애고 싶을 경우 시간을 줄이면 된다!
        // -> fetch.min.bytes 파라미터 값 만큼 모이거나 fetch.max.wait.ms 시간이 지나거나 하면 값을 가져와서 리턴한다.
        consumerProps.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500); // 기본 500ms

        // 컨슈머가 브로크를 폴링할 때 카프카가 리턴하는 최대 바이트 수 지정.
        // 컨슈머가 브로커러보투 받은 데이터를 저장하기 위해 사용하는 메모리의 양을 제한하기 위해 사용한다. (최대 읽기 크기 제한)
        consumerProps.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, 100); // 기본값은 50mb

        // poll() 호출 시 리턴하는 최대 레코드 수 지정
        consumerProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);

        // 서버가 파티션별로 리턴하는 최대 바이트 결정
        // kafkaConsumer 가 poll() 을 통해 ConsumerRecords를 리턴할 떄 메모리 상에 저장된 레코드 객체의 크기는 컨슈머에 할당된 파티션별로 max.partition.fetch.bytes 까지 차지할 수 있다.
        consumerProps.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 1); // 기본값은 1mb

        // session.timeout.ms 설정과 heartbeat.interval.ms 설정은 같이 변경되어야 한다.
        // 보통 heartbeat.interval.ms 는 session.timeout.ms 값을 1/3 으로 설정한다.
        // 컨슈머가 브로커와 신호를 주고받지 않고도 살아 있는 것으로 판정되는 최대 시간의 기본값은 45초 임.
        // 컨슈머가 그룹 코디네이터에게 하트비트를 보내지 않고 session.timeout.ms 가 지나면 그룹 코디네이터는 컨슈머가 죽은걸로 판단하고 죽은 컨슈머에게 할당된 파티션을 리밸런싱 진행
        consumerProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 45000); // 10s
        consumerProps.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 15000);

        // 컨슈머가 폴링을 하지 않고도 죽은 것으로 판정되지 않을 수 있는 최대 시간
        // 메인 스레드 데드락에 대비를 위한 설정(하트비트는 백그라운드 프로세스에 의해 일어나기 때문에)
        consumerProps.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000); // 기본값은 5분

        // api 호출 시 명시적인 타임아웃이 없을 경우 모든 컨슈머 api 호출에 적용되는 타임아웃 값
        consumerProps.put(ConsumerConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, 60000); // 기본값은 1분

        // 컨슈머가 브로커로부터 응답을 기다릴 수 있는 최대 시간
        // 지정된 시간안에 응답이 없을 경우, 연결을 닫고 재연결을 시도한다.
        consumerProps.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000); // 기본값 30초 -> 변경하지 않을 것을 권장

        // 파티션을 다시 읽는 경우가 생길 때 옵션
        // latest -> 읽지 않은 메시지 중 마지막 메시지만 읽음 (메시지 유실 o)
        // earliest -> 읽지 않은 메시지부터 전체 읽음 (메시지 유실 x)
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetReset); // 기본값은 latest

        // 컨슈머가 자동으로 오프셋을 커밋할지 여부 결정
        // 중복 최소화 및 유실되는 데이터 방지를 위해서 라면 false 로 설정하고 로직에서 offset 을 커밋하자.
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true); // 기본값 true

        /* 파티션 할당 전략 */
        // Range -> 연속된 그룹으로 할당 (C1,C2) (C3,C4)
        // RoundRobin -> 순차적 할당 (C1,C3) (C2, C4)
        // Sticky -> 파티션 균등하게 할당
        // Cooperative Sticky -> 협력적 리밸런스 기능을 지원한다.
        consumerProps.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, );

        // 브로커가 요청을 보낸 클라이언트를 식별하는데 쓰인다. ex) 로깅 모니터링 지표
        consumerProps.put(ConsumerConfig.CLIENT_ID_CONFIG, "dev-client");

        // 가장 가까운 레플리카로부터 메시지를 읽어오게 하는 설정
        // 추가적으로 브로커 설정을 replicas.selector.class 설정 기본값을 RackAwareReplicasSelector 로 잡아줘야 한다.
        consumerProps.put(ConsumerConfig.CLIENT_RACK_CONFIG, 123);


        // 컨슈머에 정적 그룹 멤버쉽 기능을 적용하기 위해 사용 -> 리밸런싱 시 같은 파티션을 할당한다,.
        consumerProps.put(ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, "dev-group-instance");

        /* 데이터를 읽거나 쓸 때 소켓이 사용하는 TCP 수신 및 수신 버퍼의 크기 지정 */
        // -1 지정시 운영체제 기본갑 사용
        consumerProps.put(ConsumerConfig.RECEIVE_BUFFER_CONFIG, -1);
        consumerProps.put(ConsumerConfig.SEND_BUFFER_CONFIG, -1);

        return new DefaultKafkaConsumerFactory<>(consumerProps);
    }
}

```

## 4.6 오프셋과 커밋
우리가 poll() 을 호출할 때 마다 카프카에 쓰여진 메시지 중에서 컨슈머 그룹에 속한 컨슈머들이 아직 읽지 않은 레코드가 리턴된다 <br>
즉 컨슈머가 어떤 레코드를 읽었는지를 판단할 수 있다 <br>

카프카는 컨슈머가 카프카를 사용해서 각 파티션에서의 위치를 추적할 수 있게 한다 <br>

카프카에서는 파티션에서의 현재 위치를 업데이트하는 작업을 '**오프셋 커밋**' 이라고 부른다 <br>
카프카는 레코드를 개별적으로 커밋하지 않는다 <br>

대신 컨슈머는 파티션에서 성공적으로 처리해 낸 마지막 메시지를 커밋함으로써 그 앞의 모든 메시지들 역시 성공적으로 처리되었음을 나타낸다 <br>

⭐️️️️️️️⭐️️️️️️️⭐️️️️️️️ 그럼 컨슈머는 어떻게 오프셋을 커밋할까? <br>
카프카에 특수 토픽인 __consumer_offsets 토픽에 각 파티션별로 커밋된 오프셋을 업데이트 하도록 하는 메시지를 보냄으로써 이루어진다 <br>

모든 컨슈머들이 정상적으로 실행중일 때는 아무런 영향을 주지 않는다 <br>
하지만 컨슈머가 크래시 되거나, 새로운 컨슈머가 그룹에 추가될 경우 리밸런스가 발생한다 <br>
리밸런스 이후 각각의 컨슈머는 리밸런스 이전에 처리하고 있던 것과는 다른 파티션들 할당받게 된다(정적 그룹이 아닐 경우) <br>
어디서 부터 작업을 재개해야 하는지를 알아내기 위해서 컨슈머는 각 파티션의 마지막으로 커밋된 메시지를 읽어온 뒤 거기서부터 처리를 재개한다 <br>


## 4.7 리밸런스 리스너
오프셋 커밋에 대해 이야기 했듯이, 컨슈머는 종료하기 전이나 리밸런싱이 시작되기 전에 정리 작업을 해줘야 한다 <br>

만약 컨슈머에 할당된 파티션이 해제될 것이라는 걸 알게된다면 해당 파티션에서 마지막으로 처리한 이벤트의 오프셋을 커밋해야 한다 <br>
파일 핸들 또는 데이터베이스 연결 등 역시 닫아줘야 한다 <br>

컨슈머 API 는 컨슈머에 파티션이 할당되거나 해제될 때 사용자의 코드가 실행되도록 하는 메커니즘을 제공한다 <br>
subScribe() 호출시 ConsumerRebalanceListener 를 전달해주면 3개의 메소드를 구현할 수 있다. <br>

```java
public void onPartitionAssigned(Collection<TopicPartition> partitions)
// 파티션이 컨슈머에게 재할당된 후에, 컨슈머가 메시지를 읽기전 호출된다.


public void onPartitionsRevoked(Collection<TopicPartition> partitions)
// 컨슈머가 할당받았던 파티션이 해제될 때 호출된다 (리밸런스 or 컨슈머 닫힘)
// 위 시점에서 오프셋을 커밋해줘야 위 파티션 다음에 할당받는 컨슈머가 시작할 지점을 알아낼 수 있다.


public void onPartitionsLost(Collection<TopicPartition> partitions)
// 협력적 리밸런스 알고리즘이 사용될 경우, 할당된 파티션이 리밸런스 알고리즘에 의해 해제되기 전에 다른 컨슈머에 먼저 할당된 예외적인 상황에만 호출된다
// 여기서는 파티션과 함께 사용되었던 상태나 자원들을 정리해주어야 한다.

```


## 4.8 특정 오프셋의 레코드 읽어오기
파티션의 맨 앞에서부터 메시지를 읽기 위해서는 seekToBeginning() 메소드를 사용한다 <br>
앞의 메시지를 전부 건너뛰고 맨 뒤에서부터 읽고자 하면 seekToEnd() 메소드를 사용한다 <br>

카프카 API 를 사용하면 특정한 오프셋으로 탐색해 갈 수도 있다 <br>

-> 잘 이해가 안됌

## 4.9 폴링 루프를 벗어나는 방법
컨슈머를 종료하고자 할 때, 컨슈머가 poll() 을 오랫동안 기다린다면, 즉시 탈출을 위해서 consumer.wakeyup() 을 호출해줘야 한다 <br>
만약 메인 스레드에서 컨슈머 루프가 돌고 있다면 ShutdownHook 을 사용할 수 있다 <br>

consumer.wakeup() 은  다른 스레드에서 호출해줄 때만 안전하게 작동하는 유일한 컨슈머 메소드이다 <br>

컨슈머를 닫으면 오프셋을 커밋하고 그룹 코디네이터에게 컨슈머가 그룹을 떠난다는 메시지를 전송한다 <br>
이때 컨슈머 코디네이터가 즉시 리밸런싱을 실행시키기 때문에 닫고 있는 컨슈머에 할당되어 있던 파티션들이 그룹 안의 다른 컨슈머에게 할당될 때까지 세션 타임아웃을 기다릴 필요가 없다 <br>

```java
        Runtime.getRuntime().addShutdownHook(new Thread() {});
            public void run() {
            System.out.println("Exit...");
            consumer.wakeup();
            try {
                mainThread.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
```

## 4.10 디시리얼라이저
카프카 프로듀서는 카프카에 데이터를 쓰기 전 커스텀 객체를 바이트 배열로 변환하기 위해 시리얼라이저가 필요하다 <br>
반대로 카프카 컨슈머는 바이트 배열을 자바 객체로 변환하기 위해 디시리얼라이저가 필요하다 <br>


## 4.11 독립실행 컨슈머 - 컨슈머 그룹 없이 컨슈머를 사용해야 하는 이유와 방법
컨슈머 그룹은 컨슈머들에게 파티션을 자동으로 할당해주고 해당 그룹에 컨슈머가 추가되거나 제거될 경우 자동으로 리밸런싱을 해준다 <br>

consumer 가 특정 파티션을 읽어야 하는지 정확히 알고 있을 경우 토픽을 구독할 필요 없이 그냥 파티션을 스스로 할당받으면 된다 <br>
consuemr 는 토픽을 구독하거나 스스로 할당할 수 있지만, 두 가지를 동시에 할 수는 없다 <br>

