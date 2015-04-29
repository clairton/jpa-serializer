package br.eti.clairton.jpa.serializer;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.Type;
import javax.validation.constraints.NotNull;

import net.vidageek.mirror.dsl.Mirror;
import net.vidageek.mirror.invoke.dsl.InvocationHandler;
import net.vidageek.mirror.set.dsl.FieldSetter;
import net.vidageek.mirror.set.dsl.SetterHandler;

import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * Deserializa um JSON para um entidade JPA usando o Gson.
 * 
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 *
 * @param <T>
 *            tipo da entidade
 */
public abstract class JpaDeserializer<T> extends AbstractSerializator<T>
		implements JsonDeserializer<T> {
	private final EntityManager entityManager;

	/**
	 * Construtor Padrão.
	 * 
	 * @param entityManager
	 *            instancia de {@link EntityManager}
	 * @param mirror
	 *            instancia de {@link Mirror}
	 * @param logger
	 *            instancia de {@link Logger}
	 */
	public JpaDeserializer(final @NotNull EntityManager entityManager,
			@NotNull final Mirror mirror, @NotNull final Logger logger) {
		super(mirror, logger);
		this.entityManager = entityManager;
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public T deserialize(final JsonElement json, final java.lang.reflect.Type type, final JsonDeserializationContext context) throws JsonParseException {
		final T model = getInstance(type);
		final JsonObject jsonObject = (JsonObject) json;
		for (final Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			final Field field = getField(model.getClass(), entry.getKey());
			final Object value = getValue(context, entry.getValue(), field);
			logger.debug("Valor extraido {}#{}={}", type, field, value);
			setValue(model, field, value);
		}
		return model;
	}

	public T getInstance(java.lang.reflect.Type type) {
		final Class<T> klazz = getClass(type);
		final InvocationHandler<T> invoke = mirror.on(klazz).invoke();
		final T model = klazz.cast(invoke.constructor().withoutArgs());
		return model;
	}

	public Object getValue(final JsonDeserializationContext context, final JsonElement element, final Field field) {
		final Object value;
		if (isToMany(field)) {
			value = toMany(context, field, element);
		} else if (isToOne(field)) {
			value = toOne(context, field, element);
		} else {
			final java.lang.reflect.Type t = field.getType();
			value = context.deserialize(element, t);
		}
		return value;
	}

	public <W> W toOne(final JsonDeserializationContext context,
			final Field field, JsonElement element) {
		if (JsonNull.class.isInstance(element)) {
			return null;
		} else {
			final W value;
			try {
				@SuppressWarnings("unchecked")
				final W v = (W) field.getType().newInstance();
				value = v;
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
			final Metamodel metamodel = entityManager.getMetamodel();
			final EntityType<?> entity = metamodel.entity(value.getClass());
			final Type<?> idType = entity.getIdType();
			final Class<?> t = idType.getJavaType();
			final Attribute<?, ?> attribute = entity.getId(t);
			final String fieldIdName = attribute.getName();
			final SetterHandler handler = mirror.on(value).set();
			final FieldSetter fieldSetter = handler.field(fieldIdName);
			fieldSetter.withValue(element.getAsLong());
			return value;
		}
	}

	public <W> Collection<W> toMany(final JsonDeserializationContext context,
			final Field field, final JsonElement element) {
		final java.lang.reflect.Type fielType = field.getGenericType();
		final ParameterizedType pType = (ParameterizedType) fielType;
		final java.lang.reflect.Type[] arr = pType.getActualTypeArguments();
		final Class<?> elementType = (Class<?>) arr[0];
		final Collection<W> collection = getInstance(field.getType());
		final JsonArray array = element.getAsJsonArray();
		for (final JsonElement jsonElement : array) {
			final W object;
			try {
				@SuppressWarnings("unchecked")
				final W o = (W) elementType.newInstance();
				object = o;
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
			final SetterHandler handler = mirror.on(object).set();
			final Metamodel metamodel = entityManager.getMetamodel();
			final EntityType<?> entity = metamodel.entity(elementType);
			final Type<?> idType = entity.getIdType();
			final Class<?> t = idType.getJavaType();
			final Attribute<?, ?> attribute = entity.getId(t);
			final String fieldIdName = attribute.getName();
			final FieldSetter fieldSetter = handler.field(fieldIdName);
			fieldSetter.withValue(jsonElement.getAsLong());
			collection.add(object);
		}
		return collection;
	}

	@SuppressWarnings("unchecked")
	public <Z, W> Z getInstance(final Class<?> type) {
		Z z = null;
		if (type.isAssignableFrom(List.class)) {
			z = (Z) new ArrayList<W>();
		} else if (type.isAssignableFrom(Set.class)) {
			z = (Z) new HashSet<W>();
		}
		return z;
	}

	@Override
	public Boolean isToMany(final Field field) {
		return field.isAnnotationPresent(OneToMany.class)
				|| field.isAnnotationPresent(ManyToMany.class);
	}

	@Override
	public Boolean isToOne(final Field field) {
		return field.isAnnotationPresent(ManyToOne.class)
				|| field.isAnnotationPresent(OneToOne.class);
	}
}
