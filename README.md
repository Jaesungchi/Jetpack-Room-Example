# Jetpack-Room-Example
![imge](https://img.shields.io/badge/ProjectType-SingleStudy-green) ![imge](https://img.shields.io/badge/Language-Kotlin-yellow) ![imge](https://img.shields.io/badge/Tools-AndroidStudio-blue)

안드로이드의 내부 DB를 편리하게 사용하기 위한 JetPack의 Room 이용 예제입니다.

간단하게 한줄씩 글을 적는 게시판을 만들며 Room을 익혀보겠습니다.

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

## 2. ROOM 구성요소 생성

### (1) Database 생성

*AppDatabase.kt

```kotlin
@Database(entities = [WriteDataEntity::class],version = 1,exportSchema = false)
abstract class AppDatabase : RoomDatabase(){
    abstract fun WriteDao()
    companion object{
        private var INSTANCE : AppDatabase? = null
        fun getInstance(context : Context): AppDatabase?{
            if(INSTANCE == null){
                synchronized(AppDatabase::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java,"room_example.db").build()
                }
            }
            return INSTANCE
        }
    }
}
```

Database는 싱글톤을 통해 한가지 정보만 갖고 있도록 합니다. 또한 사용하는 Entity에 관해서는 위에 entities에 명시하도록 합니다.

### (2) Entity 생성

*writeDataEntity.kt

```kotlin
@Entity(tableName = "writeData")
data class WriteDataEntity(@PrimaryKey(autoGenerate = true) val id:Long,
                           var title: String,
                           var content: String,
                           var date : String)
```

글의 제목과 내용 그리고 날짜를 저장할 Entity를 생성합니다.

### (3) DAO 생성

DAO 부분은 Interface로 작성해야합니다. 저는 Entity가 1개라 DAO도 하나면 되지만 BaseDAO를 만들어 상속받아 처리하게 하였습니다.

*BaseDao.kt

```kotlin
interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(obj : T)
    
    @Delete
    fun delete(obj : T)
    
    @Update(onConflict = OnConflictStrategy.ABORT)
    fun update(obj : T)
}
```

*WriteDateDao.kt

```kotlin
@Dao
interface WriteDataDao : BaseDao<WriteDataEntity>{
    @Query("SELECT * FROM writeData WHERE id = :id")
    fun selectById(id : Int) : Maybe<WriteDataEntity>

    @Query("SELECT * FROM writeData")
    fun selectAll() : Maybe<WriteDataEntity>

    @Query("SELECT * FROM writeData WHERE date = :date")
    fun selectByDate(date : String ) : WriteDataEntity

    @Query("DELETE FROM writeData WHERE date = :date")
    fun deleteByDate(date : String)
}
```

위를 보면 Maybe 나 Single 반환 flowable 반환형식이 존재합니다.

- Maybe 타입은 행이 1개 or 0개가 오거나 Update시 success -> onComplete를 탑니다.
- Single은 1개가 오면 success 안오면 Error를 탑니다.
- flowable은 어떤 행도 존재하지 않을 경우 onNext나 onError를 반환하지 않습니다.

[출처](https://medium.com/androiddevelopers/room-rxjava-acb0cd4f3757)