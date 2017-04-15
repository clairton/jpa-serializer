# jpa-serializer[![Build Status](https://travis-ci.org/clairton/jpa-serializer.svg?branch=master)](https://travis-ci.org/clairton/jpa-serializer)

Serialize JPA entities for JSON ActiveModelSerializer Style with GSON.

To Use example:
```java
public class FooSerializer extends GsonJpaSerializer<Foo> {
	public FooDeserializer(final EntityManager entityManager) {
		super(entityManager);
	}
}
```	
For  embedded field configure:
```java
public class FooSerializer extends GsonJpaSerializer<Foo> {
	public FooDeserializer(final EntityManager entityManager) {
		super(entityManager);
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
	<version>latest</version>
</dependency>
```
