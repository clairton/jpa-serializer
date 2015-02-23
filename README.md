# jpa-serializer
Serialize JPA entities for JSON ActiveModelSerializer Style with GSON.
To Use example:
```java
public class FooDeserializer extends JpaDeserializer<Foo> {

	public AplicacaoDeserializer(final @NotNull EntityManager entityManager,
			final @NotNull Mirror mirror, final @NotNull Logger logger) {
		super(entityManager, mirror, logger);
	}

}
```	
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