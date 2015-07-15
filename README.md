# jpa-serializer[![Build Status](https://drone.io/github.com/clairton/jpa-serializer/status.png)](https://drone.io/github.com/clairton/jpa-serializer/latest)
Serialize JPA entities for JSON ActiveModelSerializer Style with GSON.
To Use example:
```java
public class FooDeserializer extends JpaDeserializer<Foo> {
	public FooDeserializer(final EntityManager entityManager) {
		super(entityManager);
	}
}
public class FooSerializer extends JpaSerializer<Foo> {}
```	
For  embedded field configure:
```java
public class FooSerializer extends JpaSerializer<Foo> {
	public FooSerializer(){
		nodes().put("fieldNameString", Mode.RECORD);
	}
}	

```

The Mode should be:
*ID: serialize/deserialize only id. aplicacao:{recursos[1,2,3]}

*RELOAD: deserialize reload item with entityManager by id. aplicacao:{recursos[1,2,3]} -> aplicao.getRecursos().get(0).getNome()

*RECORD: serialize/deserialize embedded item. aplicacao:{recursos[{id:1, nome: "save"}]}

*IGNORE: serialize/deserialize ignore this item

Download throught maven, dependency:
```xml
<dependency>
	<groupId>br.eti.clairton</groupId>
	<artifactId>jpa-serializer</artifactId>
	<version>0.1.0-SNAPSHOT</version>
</dependency>
```
Add the repositories:
```xml
<repository>
	<id>mvn-repo-releases</id>
	<url>https://raw.github.com/clairton/mvn-repo/releases</url>
</repository>
<repository>
	<id>mvn-repo-snaphots</id>
	<url>https://raw.github.com/clairton/mvn-repo/snapshots</url>
</repository>
```
