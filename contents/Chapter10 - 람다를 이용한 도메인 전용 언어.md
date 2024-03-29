# 람다를 이용한 도메인 전용 언어
* [도메인 전용 언어 , DSL](#101-도메인-전용-언어--dsl--domain-specific-language-)
* [최신 자바 API의 작은 DSL](#102-최신-자바-api의-작은-dsl)
* [자바로 DSL을 만드는 패턴과 기법](#103-자바로-dsl을-만드는-패턴과-기법)
* [실생활의 자바8 DSL](#104-실생활의-자바8-dsl)


## 10.1 도메인 전용 언어 , DSL(domain-specific language)
* 특정 비즈니스 도메인의 문제를 해결하려고 만든 언어
<sup>Maven의 경우 빌드 과정을 표현하는 DSL로 간주 할수 있다. </sup> 
* 특정 비스니스 도메인을 인터페이스로 만든 API
* 도메인을 표현할 수 있는 클래스와 메서드 집합이 필요하다

>💡 예시) "메뉴에서 400칼로리 이하의 모든 요리를 찾으시오"
> 
> ~~~java
> //자바에서
> while(block !=null){
>  read(block, buffer){
>       for (every record in buffer){
>       if(record.caloie < 400) System.out.print(record.name);
>       }      
>  }
> }
> ~~~
> 위처럼 구현한 코드는 로깅, I/O 디스크 할당 등과 같은 지식이 필요하다. 또한 애클리케이션 수준이 아닌 시스템 수준의 개념을 다루어야한다.
> 이것을 "SELECT name FROM menu WHERE calorie <400" 으로 표현하는것은  
> 자바가 아닌 DSL을 이용해 데이터 베이스를 조작하는 의미로 통한다. 이런것을 **외부적**이라한다. 
>
> ~~~java
> // 스트림 API활용
>  menu.stream()
>       .filter(d -> d.getCalories() < 400)
>       .map(Dish::getName)
>       .forEach(System.out::println)
> ~~~
> 스트림 API특성인 메서드 체인을 보통 자바의 루프 복잡한 제어와 비교해 유창함을 의미하는  
> 플루언트 스타일(fluent style) 이라고부른다. 위 예제에서 DSL은 **내부적**이다.

***도메인 전용언어***  
DSL은 범용 프로그래밍 언어가 아니다. DSL에서 동작과 용어는 특정 도메인에 국한 되므로 다른 문제는 걱정할 필요가 없고  
오직 자신앞에 놓인 문제를 어떻게 해결할지에만 집중할 수 있다. 
DSL을 개발할때는 아래 두가지 필요성을 생각해야한다.
* 의사소통의 왕: 우리의 코드의 의도가 명확히 전달되어야 하며 프로그래머가 아닌 사람도 이해할 수 있어야한다. 이런 방식으로 코드가 비즈니스 요구사항에 부합하는지 확인 할수 있다.
* 한번 코드를 구현하지만 여러번 읽는다: 가독성은 유지 보수의 핵심이다. 즉 항상 동료가 쉽게 이해할 수 있도록 코드를 구현해야한다. 

#### DSL의 장점
* 간결함 : API는 비즈니스 로직을 간편하게 캡슐화하므로 반복을 피할 수 있고 코드를 간결하게 만들 수 있다.
* 가독성 : 도메인 영역의 용어를 사용하므로 비 도메인 전문가도 코드를 쉽게 이해할 수 있다. 다양한 조직 구성원 간에 코드와 도메인 영역이 공유될 수 있다.
* 유지보수 : 잘 설계된 DSL로 구현한 코드는 쉽게 유지보수하고 바꿀 수 있다.
* 높은 수준의 추상화 : DSL은 도메인과 같은 추상화 수준에서 동작하므로 도메인의 문제와 직접적으로 관련되지 않은 세부 사항을 숨긴다.
* 집중 : 비즈니스 도메인의 규칙을 표현할 목적으로 설계된 언어이므로 프로그래머가 특정 코드에 집중할 수 있다.
관심사분리(SoC) : 지정된 언어로 비즈니스 로직을 표현함으로 애플리케이션의 인프라구조와 관련된 문제와 독립적으로 비즈니스 관련된 코드에서 집중하기가 용이하다.
#### DSL의 단점
* DSL 설계의 어려움 : 간결하게 제한적인 언어에 도메인 지식을 담는 것이 쉬운 작업은 아니다.
* 개발 비용 : 코드에 DSL을 추가하는 작업은 초기 프로젝트에 많은 비용과 시간이 소모된다. 또한 DSL 유지보수와 변경은 프로젝트에 부담을 주는 요소다.
* 추가 우회 계층 : DSL은 추가적인 계층으로 도메인 모델을 감싸며 이 때 계층을 최대한 작게 만들어 성능 문제를 회피한다.
* 새로 배워야 하는 언어 : DSL을 프로젝트에 추가하면서 팀이 배워야 하는 언어가 한 개 더 늘어난다는 부담이 있다.
* 호스팅 언어 한계 : 일부 자바 같은 범용 프로그래밍 언어는 장황하고 엄격한 문법을 가졌다. 이런 언어로는 사용자 친화적 DSL을 만들기가 힘들다.

### JVM에서 이용할 수 있는 다른 DSL 해결책
내부 DSL (순수 자바로 구현한 DSL)  
* 기존 자바 언어를 이용해 외부 DSL에 비해 새로운 패턴과 기술을 배워 DSL을 구현하는 노력이 현저히 줄어든다.
* 순수 자바로 DSL을 구현하면 나머지 코드와 함께 DSL을 컴파일 할 수 있다. 따라서 다른 언어의 컴파일러를 이용하거나 외부 DSL을 만드는 도구를 사용할 필요가 없으므로 추가 비용이 발생하지 않는다.
* 개발 팀이 새로운 언어를 배우거나 또는 익숙하지 않고 복잡한 외부 도구를 배울 필요가 없다.
* DSL 사용자는 기존의 자바 IDE를 이용해 자동 완성, 리팩터링과 같은 기능을 사용할 수 있다.
* 한개의 언어로 하나 또는 여러 도메인을 대응하지 못해 추가 DSL을 개발해야 하는 상황에서 자바를 이용하여 추가 DSL을 쉽게 합칠수 있다.  

다중 DSL  
* 새로운 프로그래밍 언어를 배우거나 또는 팀의 누군가가 리딩 할 수 있어야 한다.
* 두 개 이상의 언어가 혼재하므로 여러 컴파일러로 소스를 빌드 하도록 빌드 과정의 개선이 필요하다.
* JVM에서 실행되는 거의 모든 언어가 자바와 호환된다고 하지만 모든 것이 호환되지 않을 때가 있다.  

외부 DSL  
* 자신만의 문법과 구문으로 새로운 언어를 설계해야 한다.
* 외부 DSL을 개발하는 가장 큰 장점은 외부 DSL이 제공하는 무한한 유연성 때문이다.

## 10.2 최신 자바 API의 작은 DSL
자바의 새로운 기능의 장점을 적용한 첫 API는 네이티브 자바 API 자신이다.  
사람들을 가지고 있는 리스트에서 나이순으로 객체를 정렬하는 예제를 보자.
~~~java
Collections.sort(persons, new Comparator<Person>() {
  public int compare() ...
})
~~~
Java8 이전에는 위와 같이 익명 클래스를 활용하여 구현해야 했다.
내부 클래스를 간단한 람다 표현식으로 바꿀수 있다  
~~~java
Collections.sort(persons, (p1, p2) -> p1.getAge() - p2.getAge())

//람다사용
// Comparator.comparing
Collections.sort(persons, comparing(p -> p.getAge()))
~~~
위와 같은 구현은 코드를 간결하게 만들었지만 정적 유틸리티 메서드를 활용하여 좀 더 가독성 있게 개선할 수 있다

> 아래 예시는 컬렉션 정렬 도메인의 최소 DSL이다. 작은 영역에 국한된 예제지만 이미 람다와 메서드 참조를 이용한 DSL이 코드의 가독성, 재사용성, 결합성을 높일수 있는지 보여준다.
> ~~~java
> // Comparator.comparing
> Collections.sort(persons, comparing(Person::getAge))
> Collections.sort(persons, comparing(Person::getAge).reverse()) // 역순 정렬
> Collections.sort(persons, comparing(Person::getAge).thenComparing(Person::getName)) // 나이 정렬 후 이름 정렬
> // List 에 추가된 sort 메서드 이용하여 마지막 리팩토링
> persons.sort(comparing(Person::getAge).thenComparing(Person::getName)) 
> ~~~

#### 스트림 API는 컬렉션을 조작하는 DSL
Stream 인터페이스는 네이티브 자바 API에서 작은 내부 DSL을 적용한 좋은 예다.  
사실 Stream은 컬렉션의 항목을 필터, 정렬, 변환, 그룹화 조작하는 작지만 강력한 DSL로 볼수 있다. 
> **예제코드** : <a href="https://github.com/day0ung/ModernJavaInAction/blob/main/java_code/modern_java/src/chapter10/SourceCode102.java">SourceCode102</a>

#### 데이터를 수집하는 DSL인 Collectors
Stream 인터페이스를 데이터 리스트를 조작하는 DSL 로 간주할 수 있음을 확인했다.  
마찬가지로 Collector인터페이스는 데이터 수집을 수행하는 DSL로 간주 할수 있다. 
> 예시) groupingBy 팩터리 메서드에 작업을 위임하는 GroupingBuilder를 만드는 작업, 
> 유연한 방식으로 여러 그룹화 작업을 만든다.   
> **예제코드** : <a href="https://github.com/day0ung/ModernJavaInAction/blob/main/java_code/modern_java/src/chapter10/SourceCode102_2.java">SourceCode102_2</a>

## 10.3 자바로 DSL을 만드는 패턴과 기법
### 메서드 체인
메서드 체인은 DSL 에서 가장 흔한 방식 중 하나이다.
* 장점
  * 사용자가 지정된 절차에 따라 플루언트 API의 메서드 호출을 강제
  * 파라미터가 빌더 내부로 국한 됨
  * 메서드 이름이 인수의 이름을 대신하여 가독성 개선
  * 문법적 잡음이 최소화
  * 선택형 파라미터와 잘 동작
  * 정적 메서드 사용을 최소화하거나 없앨 수 있음
* 단점
  * 빌더를 구현해야 함
  * 상위 수준의 빌더를 하위 수준의 빌더와 연결할 많은 접착 코드가 필요
  * 도멘인 객체의 중첩 구조와 일치하게 들여쓰기를 강제할 수 없음

### 중첩된 함수 이용
중첩된 함수 DSL 패턴은 다른 함수 안에 함수를 이용해 도메인 모델을 만든다.
* 장점
  * 중첩 방식이 도메인 객체 계층 구조에 그대로 반영
  * 구현의 장황함을 줄일 수 있음
* 단점
  * 결과 DSL 에 더 많은 괄호를 사용
  * 정적 메서드 사용이 빈번
  * 인수 목록을 정적 메서드에 넘겨줘야 함
  * 인수의 의미가 이름이 아니라 위치에 의해 정의 됨
  * 도메인에 선택 사항 필드가 있으면 인수를 생략할 수 있으므로 메서드 오버로딩 필요

### 람다 표현식을 이용한 함수 시퀀싱
람다 표현식으로 정의한 함수 시퀀스를 사용한다.
* 장점
  * 메서드 체인 패턴처럼 플루언트 방식으로 정의가능하다
  * 중첩 함수 형식처럼 람다 표현식의 중첩 수준과 비슷하게 도메인 객체의 계층 구조를 유지한다
  * 플루언트 방식으로 도메인 객체 정의 가능
  * 선택형 파라미터와 잘 동작
  * 정적 메서드를 최소화하거나 없앨 수 있음
  * 빌더의 접착 코드가 없음
* 단점
  * 많은 설정 코드가 필요하며, 람다 표현식 문법에 의한 잡음의 영향을 받는다.

### 조합하기
중첩된 함수 패턴과 람다 기법을 혼용하면 다음과 같이 사용할 수 있다.
* 장점
  * 가독성 향상
* 단점
  * 여러 기법을 혼용했기 때문에 사용자가 DSL을 배우는 시간이 오래 걸림

> 💡 예제  
> 도메인 모델은 세가지로 구성된다.
> 1. 주어진 시장에서 주식가격을 모델링하는 순수 자바 빈즈, (Stock.class)
> 2. 주어진 가격에서 주어진 양의 주식을 사거나 파는 거래, (Trade.class)
> 3. 고객이 요청한 한개 이상의 거래의 주문, (Order.class)  
>
> **자바로 DSL을 만드는 패턴과 기법 예제코드** : <a href="https://github.com/day0ung/ModernJavaInAction/blob/main/java_code/modern_java/src/chapter10/SourceCode103_DSL.java">SourceCode103_DSL</a>  
> **Builder를 정의한 DSL package** : <a href= "https://github.com/day0ung/ModernJavaInAction/tree/main/java_code/modern_java/src/chapter10/dsl">DSL </a>

## 10.4 실생활의 자바8 DSL
DSL을 개발하는데 사용하는 유용한 패턴에 대해 알아봤으니
이 패턴들이 얼마나 사용되고 있는지 살펴본다.
* jOOQ : jOOQ는 SQL을 구현하는 내부적 DSL로 자바에 직접 내장된 형식 안전 언어다.
  jOOQ DSL 구현하는 데에는 메서드 체인 패턴을 사용했다
* 큐컴버 : 동작 주도 개발(BDD)은 테스트 주도 개발의 확장으로 다양한 비즈니스 시나리오를 구조적으로 서술하는 도메인 전용 스크립팅 언어를 사용한다.  
큐컴버(cucumber)는 BDD 프레임워크로 명령문을 실행할 수 있는 테스트 케이스로 변환하며, 비즈니스 시나리오를 평문으로 구현할 수 있도록 도와준다.  
* 스프링 통합 : 스프링 통합은 유명한 엔터프라이즈 통합 패턴을 지원할 수 있도록 의존성 주입에 기반한 스프링 프로그래밍 모델을 확장한다.
  복잡한 통합 솔루션 모델을 제공하고 비동기, 메시지 주도 아키텍처를 쉽게 적용할 수 있도록 돕는 것이 스프링 통합의 핵심 목표다.
  스프링 통합 DSL 에서도 메서드 체인 패턴이 가장 널리 사용되고 있다.