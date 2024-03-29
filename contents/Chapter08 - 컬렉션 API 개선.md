# 컬렉션 API 개선

* [컬렉션 팩토리](#81-컬렉션-팩토리)
* [리스트와 집합 처리](#82-리스트와-집합-처리)
* [맵 처리](#83-맵-처리)
* [개선된 ConcurrentHashMap](#84-개선된-concurrenthashmap)

## 8.1 컬렉션 팩토리
자바 9에서는 작은 컬렉션 객체를 쉽게 만들수 있는 몇 가지 방법을 제공한다.  
<code>Arrays.asList("a", "b", "c")</code>는 고정 크기의 리스트를 만들어 요소를 갱신할수 있지만,  
새 요소를 추가하거나 요소를 삭제할 순 없다. 추가하려면 Unsupported OperationException이 발생한다.

***Unsupported OperationException 예외 발생***
내부적으로 고정된 크기의 변환할 수 있는 배열로 구현되었기때문에 이와같은 일이 일어난다.  
리스트는 이렇게 팩토리 메서드라고 존재했지만 집합의 경우 리스트를 인수로 받는 HashSet 생성자를 사용하거나 스트림 API를 사용하는 방법이 존재했다.  
~~~java
Set<String> friends = new HashSet<>(Arrays.asList("e1","e2","e3"));

Set<String> friends = Stream.of("e1","e2","e3").collect(toSet());
~~~
두 방법 모두 매끄럽지 못하며 내부적으로 불필요한 객체 할당을 필요로 한다.그리고 결과는 변환할 수 있는 집합이다  
자바 9에서 작은 리스트, 집합, 맵을 쉽게 만들 수 있도록 팩토리 메서드를 제공한다.

> 📌컬렉션 리터럴
> 파이썬, 그루비 등을 포함한 일부 언어는 컬렉션 리터럴 [42, 1, 5] 같은 특별한 문법을 이용해 컬렉션을 만들 수있는 기능을 지원한다.
> 자바에서는 너무 큰 언어 변화와 관련된 비용이 든다는 이유로 기능을 지원하지 못했다. 자바 9에서는 대신 컬렉션 API를 개선했다.

### 자바 9에서 제공되는 팩토리 메서드
* List.of : 변경할 수 없는 불변 리스트를 만든다.
* Set.of : 변경할 수 없는 불변 집합을 만든다. 중복된 요소를 제공해 집합 생성 시 IllegalArgumentException이 발생한다.
* Map.of : 키와 값을 번갈아 제공하는 방법으로 맵을 만들 수 있다.
* Map.ofEntries : Map.Entry<K, V> 객체를 인수로 받아 맵을 만들 수 있다. 엔트리 생성은 Map.entry 팩터리 메서드를 이용해서 전달하자

## 8.2 리스트와 집합 처리
자바 8에서는 List, Set 인터페이스에 다음과 같은 메서드를 추가했다.
* removeIf : 프레디케이트를 만족하는 요소를 제거한다. List나 Set을 구현하거나 그 구현을 상속받은 모든 클래스에서 이용
* replaceAll : 리스트에서 이용할 수 있는 기능으로 UnaryOperator함수를 이용해 요소를 바꾼다.
* sort : List 인터페이스에서 제공하는 기능으로 리스트를 정렬한다. 

## 8.3 맵 처리
자바 8에서는 Map 인터페이스에 몇가지 디폴트 메서드를 추가했다.
### forEach 메서드 
맵에서 키와 값을 반복할 수 있으며, BiConsumer를 인수를 받는 메서드를 지원한다
  * ~~~java
    for(map.Entry<String, Integer> entry :  ageOfFriends.entrySet()){
        String friend = entry.getKey();
        Integer age = entry.getValue();
        System.out.print(friend + " is" + age + " years old");
    }
    //BiConsumer(키와 값을 인수로 받음)를 인수로 받는 forEach
    ageOfFriends.forEach((friend, age) ->  System.out.print(friend + " is" + age + " years old"));
    ~~~ 
### 정렬 메서드 
맵의 항목을 값 또는 키를 기준으로 정렬할 수 있다. 스트림 API의 sorted()내부에 정렬 메서드를 인자로 전달한다.
* Entry.comparingByValue
* Entry.comparingByKey 
* ~~~java
  Map<String, String> movies = Map.ofEntries(entry("Rapheal","Star Wars"), entry("Cristina", "Matrix"));
  
  //사람의 이름을 알파벳순으로 스트림요소를 처리
  movies.entrySet()
        .stream()
        .sorted(Entry.comparingByKey())
        .forEachOrdered(System.out::print);
  
  ~~~ 
### getOrDefault 메서드
찾으려는 키가 존재하지 않으면 null이 반환되므로 NullPointException 방지를 위해 요청 결과가 널인지 확인하는 로직이 필요하다.  
getOrDefault 메서드로 이 문제를 해결할 수 있다. 단, 키가 존재하더라도 값이 널은 상황은 getOrDefault가 널을 반환할 수 있다.
~~~java
Map<String, String> movies = Map.ofEntries(entry("Rapheal","Star Wars"), entry("Olivia", "James Bond"));
System.out.print(movies.getOrDefault("Olivia", "Matrix")); //Jame Bond 출력
System.out.print(movies.getOrDefault("Thibaut", "Matrix")); // Matrix 출력
~~~
### 계산패턴
맵에 키가 존재 하는지 여부에 따라 어떤 동작을 실행하고 결과를 저장해야 하는 상황이 필요한 때에 사용.  
예를 들어 키를 이용해 동작을 실행해서 얻은 결과를 캐시하려 한다. 
* computeIfAbsent :  제공된 키에 해당하는 값이 없으면(empty or null), 키를 이용해 새 값을 계산하고 맵에 추가한다.
* computeIfPresent : 제공된 키가 존재하면 새값을 계산하고 맵에 추가
* compute :  제공된 키로 새값을 계산하고 맵에 저장한다. 

> 정보를 캐시할때 computeIfAbsent를 활용할 수 있다. 파일 집합의 각 행을 파싱해 SHA-256을 계산한다고 가정할때
> ~~~java 
> Map <String, byte[]> dataToHash = new HashMap<>();
> MessageDigest md = MessageDisgest.getInstance("SHA-256");
> lines.forEach(line -> dataToHash.computeIfAbsent( line, // line은 맵에서 찾은 키
>                                   this::calcuageDigest)); // 키가 존재하지 않으면 동작실행

### 삭제패턴
제공된 키에 해당하는 맵 항목을 제거하는 remove 메서드와 더불어, 키가 특정한 값에 연관되어 있을 때만 항목을 제거하면 오버로드 버전 메서드를 제공한다.
~~~java
default boolean remove(Object key, Object value)
~~~
### 교체패턴
맵의 항목을 바꾸는데 사용할 수 있는 메서드이다
* replaceAll : BiFunction을 적용한 결과로 각 항목의 값을 교체한다. List의 replaceAll과 비슷한 동작을 수행한다.
* replace : 키가 존재하면 맵의 값을 바꾼다. 키가 특정 값으로 매핑되었을 때만 값을 교체하는 오버로드 버전도 존재한다.

### 합침
두 개의 맵을 합칠 때 putAll 메서드를 사용했는데, 이때 중복된 키가 있다면 원하는 동작이 이루어지지 못할 수 있다. 새로 제공되는 merge 메서드는 중복된 키에 대한 동작(BiFunction)을 정의해줄 수있다.

>  📌 **계산, 삭제, 교체 패턴 및 합침 예제코드**:  <a href="https://github.com/day0ung/ModernJavaInAction/blob/main/java_code/modern_java/src/chapter08/SourceCode083.java">SourceCode083</a>

## 8.4 개선된 ConcurrentHashMap 
ConcurrentHashMap 클래스는 동시성 친화적이며 최신 기술을 반영한 HashMap 버전이다. 

***리듀스와 검색***
* forEach : 각 (키, 값) 쌍에 주어진 액션을 수행
* reduce : 모든 (키, 값) 쌍을 제공된 리듀스 함수를 이용해 결과로 합침
* search : 널이 아닌 값을 반환할 때까지 각 (키, 값) 쌍에 함수를 적용
또한 연산에 병렬성 기준값(threshold)를 정해야 한다. 맵의 크기가 기준값보다 작으면 순차적으로 연산을 진행한다. 기준값을 1로 지정하면 공통 스레드 풀을 이용해 병렬성을 극대화할 수 있다.

***계수***  
맵의 매핑 개수를 반환하는 mappingCount 메서드를 제공한다. 기존에 제공되던 size 함수는 int형으로 반환하지만 long 형으로 반환하는 mappingCount를 사용할 때 매핑의 개수가 int의 범위를 넘어서는 상황에 대하여 대처할 수 있을 것이다.

***집합뷰***  
ConcurrentHashMap을 집합 뷰로 반환하는 keySet 메서드를 제공한다. 맵을 바꾸면 집합도 바뀌고 반대로 집합을 바꾸면 맵도 영향을 받는다. newKeySet이라는 메서드를 통해 ConcurrentHashMap으로 유지되는 집합을 만들 수도 있다.