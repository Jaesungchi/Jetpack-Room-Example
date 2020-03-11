# Jetpack-Room-Example :notebook:
![imge](https://img.shields.io/badge/ProjectType-SingleStudy-green) ![imge](https://img.shields.io/badge/Language-Kotlin-yellow) ![imge](https://img.shields.io/badge/Tools-AndroidStudio-blue)

안드로이드의 내부 DB를 편리하게 사용하기 위한 JetPack의 Room 이용 예제입니다.

간단하게 한줄씩 글을 적는 게시판을 만들며 Room을 익혀보겠습니다.

- [0. 시작](https://github.com/Jaesungchi/Jetpack-Room-Example#0-시작)
- [1. Dependencies 설정](https://github.com/Jaesungchi/Jetpack-Room-Example#1-Dependencies-설정)
- [2. ROOM 구성요소 생성](https://github.com/Jaesungchi/Jetpack-Room-Example#2-room-구성요소-생성)
  - [(1) Database 생성](https://github.com/Jaesungchi/Jetpack-Room-Example#1-database-생성)
  - [(2) Entity 생성](https://github.com/Jaesungchi/Jetpack-Room-Example#2-entity-생성)
  - [(3) DAO 생성](https://github.com/Jaesungchi/Jetpack-Room-Example#3-dao-생성)
- [3. View 만들기](https://github.com/Jaesungchi/Jetpack-Room-Example#3-view-만들기)
- [4. 사용하기](https://github.com/Jaesungchi/Jetpack-Room-Example#4-사용하기)

## 0. 시작 :runner:

### Room 이란? :house:

Room 이란 ORM(Object Relational Mapping) 라이브러리로 데이터베이스의 객체를 자바나 코틀린 객체로 맵핑해줍니다. SQLite의 추상레이어 위에 제공하며 SQLite의 모든기능을 제공합니다.

### Room과 SQLite의 차이점 :left_right_arrow:

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
    fun selectById(id : Int) : Array<WriteDataEntity>

    @Query("SELECT * FROM writeData")
    fun selectAll() : Array<WriteDataEntity>

    @Query("SELECT * FROM writeData WHERE date = :date")
    fun selectByDate(date : String ) : WriteDataEntity

    @Query("DELETE FROM writeData WHERE date = :date")
    fun deleteByDate(date : String)
}
```

반환형태는 Maybe 나 Single 반환 flowable 반환형식이 존재합니다.

- Maybe 타입은 행이 1개 or 0개가 오거나 Update시 success -> onComplete를 탑니다.
- Single은 1개가 오면 success 안오면 Error를 탑니다.
- flowable은 어떤 행도 존재하지 않을 경우 onNext나 onError를 반환하지 않습니다.

[출처](https://medium.com/androiddevelopers/room-rxjava-acb0cd4f3757)

## 3. View 만들기

간단하게 mainActivity에서 제목과 글을 쓰고 추가하면 날짜와 함께 출력되는 뷰만 담는 xml 파일을 만든다.

*activity_main.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <EditText
        android:id="@+id/titleText"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:ems="10"
        android:hint="title"
        android:inputType="textPersonName"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.106"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_bias="0.023" />

    <EditText
        android:id="@+id/contentText"
        android:layout_width="377dp"
        android:layout_height="49dp"
        android:ems="10"
        android:hint="content"
        android:inputType="textPersonName"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.617"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/titleText"
        app:layout_constraintVertical_bias="0.028" />

    <LinearLayout
        android:id="@+id/listLayout"
        android:layout_width="416dp"
        android:layout_height="593dp"
        android:orientation="vertical"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/contentText"
        app:layout_constraintVertical_bias="1.0"></LinearLayout>

    <Button
        android:id="@+id/insertBtn"
        android:layout_width="109dp"
        android:layout_height="43dp"
        android:text="Insert"
        app:layout_constraintBottom_toBottomOf="@+id/titleText"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.544"
        app:layout_constraintStart_toEndOf="@+id/titleText"
        app:layout_constraintTop_toTopOf="@+id/titleText"
        app:layout_constraintVertical_bias="0.0" />
</androidx.constraintlayout.widget.ConstraintLayout>
```

## 4. 사용하기

ROOM을 이용할 때 쿼리의 호출은 MainThread에서 하면 에러가 발생합니다.

MainThread에서 많은 양의 쿼리를 사용하면 오랫동안 UI가 작동하지 않기 때문에 비동기로 처리를 해주어야 합니다. 따라서 이번에는 Coroutine을 이용해 비동기 처리를 하도록 하겠습니다.

### (0) Coroutine ?

코루틴은 루틴의 일종으로, 협동 루틴이라고 합니다. 보통의 루틴과 서브 루틴은 서로 비대칭적인 관계지만, 코루틴은 완전히 대칭으로, 서로가 서로를 호출하는 관계에 있습니다. 따라서 코루틴에서는 무엇이 서브루틴이고 루틴인지 구분이 불가능합니다. 그래서 코루틴은 A와 B가 있을때 A를 프로그래밍할때에는 B를 A의 서브루틴이라 생각하고, B를 할때에는 거꾸로 생각합니다.

코루틴을 쉽게 이용하는 방법은 launch와 async 함수를 이용하면 됩니다. 둘은 거의 동일한 기능이지만 launch는 Job을 return 하고 어떠한 값도 전달하지 않지만, async는 Deferred를 return 하면서 약속된 값을 제공하는 차이점이 있습니다. 만약 실행중인 코드가 예외적으로 종료되면 launch에서는 android 응용프로그램과 충돌하고, async는 Deferred에 저장되고 처리하지 않으면 자동적으로 삭제된다는 차이점이 있습니다. 

코루틴의 dispatcher는 두가지입니다.

- uiDispatcher : 안드로이드 UIThread에서 실행(Dispatchers.Main)
- bgDisPatcher : BGThread에서 실행합니다. (Dispatchers.IO)

### (1) Dependencies 추가

```kotlin
def coroutineVersion = "1.1.0"
implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutineVersion"
```

## (2) Code 추가

*MainActivity.kt

```kotlin
private fun insertData(){
    if(titleText.text.isNullOrEmpty() || contentText.text.isNullOrEmpty())
        return
    CoroutineScope(Dispatchers.IO).launch {
        AppDatabase.getInstance(this@MainActivity)?.WriteDao()?.insert(
            WriteDataEntity(0,titleText.text.toString(), contentText.text.toString(), LocalDate.now().toString())
        )
    }
}
```

이제 만들어진 뷰를 기반으로 데이터를 넣는 insertData() 메소드를 만듭니다.

Coroutine 사용시 Dispatcher.IO 를 이용해 비동기로 처리할 수 있도록 합니다.

*MainActivity.kt

```kotlin
fun updateView(){
    CoroutineScope(Dispatchers.IO).launch {
        var tmps = AppDatabase.getInstance(this@MainActivity)?.WriteDao()?.selectAll()
        if (tmps != null) {
            for(i in tmps)
                Log.d("test",i.toString())
        }
    }
}
```

이제 데이터를 받아오는 updateView() 메소드를 만들고 잘 데이터가 들어오는지 확인합니다.

## ※ISSUE

```
java.lang.RuntimeException: cannot find implementation for com.kotlin.jaesungchi.jetpack_room.AppDatabase. AppDatabase_Impl does not exist
```

이 이슈는 kapt를 쓰지않고 annotetaionProcessor를 이용하여 발생한 이슈로 kapt로 사용한 후, appLevel의 gradle에  apply plugin : 'kotlin-kapt'를 사용하면 해결할 수있다.

---

```
 java.lang.IllegalStateException: Cannot access database on the main thread since it may potentially lock the UI for a long period of time.
```

이 이슈는 Room 쿼리를 mainthread에서 진행했을때 발생하는 이슈로 비동기로 처리하면 처리할 수 있다.