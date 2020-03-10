# Jetpack-Room-Example
![imge](https://img.shields.io/badge/ProjectType-SingleStudy-green) ![imge](https://img.shields.io/badge/Language-Kotlin-yellow) ![imge](https://img.shields.io/badge/Tools-AndroidStudio-blue)

안드로이드의 내부 DB를 편리하게 사용하기 위한 JetPack의 Room 이용 예제입니다.

## 0. 시작

### Room 이란?

Room 이란 ORM(Object Relational Mapping) 라이브러리로 데이터베이스의 객체를 자바나 코틀린 객체로 맵핑해줍니다. SQLite의 추상레이어 위에 제공하며 SQLite의 모든기능을 제공합니다.

### Room과 SQLite의 차이점

1. SQLite는 쿼리에 대한 에러를 컴파일 중 확인하지 않지만 ROOM은 컴파일 도중 SQL에 대한 유효성 검사가 가능합니다.
2. Schema가 변경되면 SQLite는 쿼리를 수동으로 업데이트 해야하지만 ROOM은 쉽게 해줍니다.
3. ROOM은 LiveData와 RxJava를 위한 Observation으로 생성하여 동작할 수 있지만 SQLite는 되지 않습니다.

### Room의 3개 구성요소

![img](https://t1.daumcdn.net/cfile/tistory/99D89F395C1B02EC24)

- Database 
- Entities : Database내 테이블
- DAO : Database에 엑세스 시 사용되는 메소드를 갖는다.

## 1. Dependencies 설정

app 단 gradle에 아래 내용을 추가한다.

```kotlin
def room_version = "2.1.0-alpha03"
implementation "androidx.room:room-runtime:$room_version"
annotationProcessor "androidx.room:room-compiler:$room_version" // use kapt for Kotlin
// optional - RxJava support for Room
implementation "androidx.room:room-rxjava2:$room_version"
// optional - Guava support for Room, including Optional and ListenableFuture
implementation "androidx.room:room-guava:$room_version"
// optional - Coroutines support for Room
implementation "androidx.room:room-coroutines:$room_version"
// Test helpers
testImplementation "androidx.room:room-testing:$room_version"
```

