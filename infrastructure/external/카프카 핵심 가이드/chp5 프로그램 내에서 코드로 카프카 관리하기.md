# 5장 프로그램 내에서 코드로 카프카 관리하기
kafka 0.11부터 프로그램적인 관리 기능 API 를 제공하기 위한 목적으로 AdminClient 가 추가되었다 <br>
위 AdminClient 에서는 토픽 목록, 생성, 삭제, 클러스터 상세 정보 확인, ACL 관리 등등 설정 변경이 가능하다 <br>

## 5.1 AdminClient 개요
AdminClient 가 어떻게 설계되었고, 언제 사용되어야 하는지를 알아야 각각의 메소드가 좀 더 직관적으로 이해할 수 있다 

### 5.1.1) 비동기적이고 최종적 일관성을 가지는 API
카프카의 AdminClient 이해할 때 가장 중요한 것은 비동기적으로 작동한다는 것이다 <br>
각 메소드는 요청을 클러스터 컨트롤러로 전송한 뒤 바로 1개 이상의 Future 객체를 리턴한다 <br>

Future 객체는 비동기 작업의 결과를 가리키며, 비동기 작업으 결과를 확인하거나, 취소, 완료될때 까지 대기 또는 작업 완료시 실행할 함수 지정하는 메소드를 가진다 <br>

카프카 컨트롤러부터 브로커로의 메타데이터 전파가 비동기적으로 이루어지기 때문에, AdminClient API 가 리턴하는 Future 객체들은 컨트롤러의 상태가 완전히 업데이트된 시점에서 완료된 것으로 간주된다 <br>

하지만 알아둬야 할 점은 최종적으로는 모든 브로커가 토픽에 대한 정보를 알게되지만, 언제 알게될지는 모른다. <br>

### 5.1.2 옵션
AdminClient 는 각 메소드별로 특정한 XOptions 객체를 파라미터로 받는다 <br>
ex) ListTopicsOptions, DescribeClusterOptions <br>

위 옵션들은 브로커가 요청을 어떻게 처리할지에 대해 서로 다른 설정을 담는다 <br>


### 5.1.3 수평구조
모든 어드민 작업은 KafkaAdminClient 에 구현된 아파치 카프카 프로토콜을 사용해서 이루어진다 <br>

주키퍼에 메타데이터를 직접 수정하는 방식은 강력히 권장되지 않는다 <br>

### 5.2 AdminClient 사용법: 생성,설정, 닫기
AdminClient 를 사용하기 위해 가장 먼저 해야 할 일은 AdminClient 객체를 생성하는 것이다 <br>
```java
AdminClient admin = AdminClient.create(props);
admin.close(Duration.ofSeconds(30));
```

AdminClient 를 생성하기 위해서는 Kafka Cluster URI 만 있으면 된다 <br>

Spring Boot 에서 설정하는 방법은 아래와 같다
```java
@Configuration
public class KafkaAdminConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Spring Kafka가 제공하는 KafkaAdmin Bean
     * 애플리케이션 시작 시 자동으로 토픽 생성 등의 작업 수행
     */
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // DNS 별칭을 사용할 경우, 2개 이상의 IP주소로 연결되는 하나의 DNS 항목을 사용할 경우 사용
        configs.put(AdminClientConfig.CLIENT_DNS_LOOKUP_CONFIG, "use_all_dns_ips");

        // 연결 안정성
        configs.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000); // 어플리케이션이 AdminClient 응답 기다릴 수 있는 최대 시간 정의

        // 재시도 설정
        configs.put(AdminClientConfig.RETRIES_CONFIG, 3);
        configs.put(AdminClientConfig.RETRY_BACKOFF_MS_CONFIG, 1000);

        // 클라이언트 ID
        configs.put(AdminClientConfig.CLIENT_ID_CONFIG, "payment-gateway-admin");

        return new KafkaAdmin(configs);
    }

    @Bean
    public AdminClient adminClient() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configs.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
        return AdminClient.create(configs);
    }
}

```

## 5.3 필수적인 토픽 관리 기능
AdminClient 이걸 가지고 무엇을 할 수 있을지 알아보자 <br>

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaAdminClientService {
    private final AdminClient adminClient;

    public void callAdminClientTopicList() {
        // 토픽 리스트 조회
        ListTopicsResult topicsResult = adminClient.listTopics();
    }

}
```

기본적으로 AdminClient 는 응답을 기다리지 않는 비동기 동작이라는 점은 꼭 기억해야 한다 <br>

## 5.4 설정 관리
설정관리는 ConfigResource 객체를 사용해서 할 수 있다. <br>
설정 가능한 자원에는 브로커, 브로커 로그, 토픽이 있다 <br>

## 5.5 컨슈머 그룹 관리
AdminClient 를 사용하여 프로그램적으로 컨슈머 그룹 및 커밋한 오프셋을 조회하고 수정하는 방법을 알아보자 <br>

### 5.5.1 컨슈머 그룹 조회
````java
    public void callConsumerGroupsList() throws Exception{
        adminClient.listConsumerGroups().valid().get().forEach(System.out::println);
    }
````

위 valid(), get() 메소드 호출시 에러가 없는 컨슈머 그룹만 조회가 된다 <br>

추가적으로 특정 그룹에 대한 상세한 정보를 보기 위해서는 `ConsumerGroupDescription` 객체를 사용하면 된다 <br>
위 정보는 트러블슈팅을 할 떄 유용하게 사용된다 <br>

### 5.5.2 컨슈머 그룹 수정
위 부분은 어플리케이션이 장애가 있을 때 복구를 하기 위한 임시방편으로 사용이 된다 <br>

## 5.6 클러스터 메타데이터
어플리케이션이 연결된 클러스터에 대한 정보를 명시적으로 읽어와야 하는 경우는 드물다 <br>
궁금하다면 아래 로직을 통해 알 수 있다 <br>
```java
    public void getMetadataByBroker() throws ExecutionException, InterruptedException {
        DescribeClusterResult cluster = adminClient.describeCluster();

        cluster.clusterId().get(); // 클러스터 ID 확인 
        cluster.controller().get(); // 컨트롤러 브로커 확인
        cluster.nodes().get();  // 노드 확인
    }
```

## 5.7 고급 어드민 작업
- 토픽에 파티션 추가
- 토픽에 레코드(메시지) 삭제하기
- 리더 선출
  - 선호 리더 선출
  - 언클린 리더 선출
- 레플리카 재할당
- 테스트하기.

