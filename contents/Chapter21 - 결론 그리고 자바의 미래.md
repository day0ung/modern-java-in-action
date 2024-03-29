# 결론 그리고 자바의 미래
자바8은 자바 역사상 가장 큰 변화가 일어난 버전이라고 한다.  
실무에서 주먹구구식으로 알아봤던 다양한 용어들을 책을 통해 상세하게 알아보았다.  
<sup>2019년도에 나온책인데, 2022년 10월 부터 읽기시작해서 늦은감이 있지만,, 완독했다!</sup> 

## 자바 8의 기능 리뷰
* 동작 파라미터화(람다와 메서드참조)
  * 람다와 메서드 참조
  * 메서드로 전달되는 값(파라미터로 넘기는 동작)은 Function, Predicate, BiFunction등의 형식을 갖는다.
  * 메서드를 수신한 코드에서는 apply, test 등의 메서드를 동해 전달받은 코드를 실행할 수 있다.
* 스트림
  * 기존 컬렉션에 filter, map 등의 메서드를 추가하지 않고 스트림을 만든 이유?
    1. 파이프라인으로 연결하기 불편함
    2. 스트림은 스트림생성-중간연산-최종연산 연결하기 좋음
  * 쉬운 병렬처리(parallel(), 병렬스트림)
  * 함수형 개념의 핵심 : 외부 반복 대신 내부 반복 지원, 부작용 없는 연산(final 같은 변수만 사용), 메서드 파라미터화
* CompletableFuture 클래스
  * 자바 5 부터 생긴 Future 인터페이스
  * 생성된 Future가 완료될 때 까지 기다릴 수 있다.
  * CompletableFuture와 Future의 관계는 스트림과 컬렉션의 관계와 같다.
  * thenCompose, thenCombine, allOf등을 제공한다.
  * 공통 디자인 패턴을 위 메서드들을 통해 함수형 프로그래밍으로 간결하게 표현할 수 있다.
  * 이와 같은 연산 형식은 Optional에도 적용된다.
* Optional 클래스
  * 값이 없을 때 에러를 발생시킬 수 있는 null 대신 정해진 데이터 형식을 제공할 수 있다.
  * map, filter, ifPresent 등 상황에 맞게 빈 값을 처리하기 위한 메서드를 제공한다.
* Flow API
  * 자바 9에서 리액티브 스트림, 리액티브 당김 기반 역압력 프로토콜을 표준화했다.
  * Publisher, Subscriber, Subscription, Processor를 포함한다.
* 디폴트 메서드
  * 자바 8 이전에는 인터페이스에서 메서드 시그니처만 정의할 수 있었다.
  * 디폴트 메서드는 인터페이스에서 메서드의 기본 구현을 제공한다.
