package br.eti.clairton.jpa.serializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.validation.constraints.NotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.vidageek.mirror.dsl.Mirror;
import net.vidageek.mirror.invoke.dsl.InvocationHandler;
import net.vidageek.mirror.set.dsl.FieldSetter;
import net.vidageek.mirror.set.dsl.SetterHandler;

/**
 * Serializa um entidade JPA para JSON usando o Gson.
 *
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 *
 * @param <T>
 *            tipo da entidade
 */
public class GsonJpaSerializer<T> extends JpaSerializer<T> implements JsonSerializer<T>, JsonDeserializer<T> {
	private static final long serialVersionUID = 1L;
	private final Logger logger = LogManager.getLogger(GsonJpaSerializer.class);
	private final EntityManager entityManager;
	private static final Map<Class<?>, Method> annotations = new HashMap<Class<?>, Method>() {
		{
			try {
				put(OneToMany.class, OneToMany.class.getMethod("mappedBy"));
				put(ManyToMany.class, ManyToMany.class.getMethod("mappedBy"));
				put(OneToOne.class, OneToOne.class.getMethod("mappedBy"));
			} catch (final Exception e) {
				throw new NoSuchElementException();
			}
		}

		private static final long serialVersionUID = 1L;
	};

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
	public GsonJpaSerializer(final @NotNull EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public JsonElement serialize(final T src, final Type type, final JsonSerializationContext context) {
		try {
			final JsonObject json = new JsonObject();
			final Class<T> klazz = getClass(type);
			final List<Field> fields = getFields(klazz);
			for (final Field field : fields) {
				final String tag = field.getName();
				if (nodes().isIgnore(tag, Operation.SERIALIZE)) {
					logger.debug("Ignorando field {}", tag);
					continue;
				}
				final JsonElement element = serialize(src, field, type, context);
				json.add(tag, element);
			}
			return json;
		} catch (final Exception e) {
			logger.error("Erro ao serializar " + src, e);
			throw new JsonParseException(e);
		}
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
			if (field == null) {
				logger.warn("Field {} não encontrado em {}", entry.getKey(), model.getClass().getSimpleName());
				continue;
			}
			final Object value = getValue(context, entry.getValue(), model, field);
			logger.debug("Valor extraido {}#{}={}", type, field.getName(), value);
			setValue(model, field, value);
		}
		return model;
	}

	protected JsonElement serialize(final Object src, final Field field, final Type type, final JsonSerializationContext context) {
		final Object value = getValue(context, src, field);
		if (value == null) {
			return context.serialize(value);
		} else {
			return context.serialize(value, value.getClass());
		}
	}

	protected List<Field> getFields(final Class<T> klazz) {
		return mirror.on(klazz).reflectAll().fields();
	}

	protected Object getValue(final JsonSerializationContext context, final Object src, final Field field) {
		final Object value;
		final String klazz = src.getClass().getSimpleName();
		final String tag = field.getName();
		logger.debug("Serializando {}#{}", klazz, tag);
		if (isToOne(field, Operation.SERIALIZE)) {
			final Collection<Object> ids = new ArrayList<Object>();
			final Object v = getValue(src, field);
			final Collection<?> models = Collection.class.cast(v);
			for (final Object model : models) {
				ids.add(getId(model));
			}
			value = ids;
		} else if (isToMany(field, Operation.SERIALIZE)) {
			final Object v = getValue(src, field);
			if (v == null) {
				value = null;
			} else {
				value = getId(v);
			}
		} else {
			value = getValue(src, field);
		}
		logger.debug("Valor extraido {}#{}={}", klazz, tag, value);
		return value;
	}

	protected T getInstance(final java.lang.reflect.Type type) {
		final Class<T> klazz = getClass(type);
		final InvocationHandler<T> invoke = mirror.on(klazz).invoke();
		return klazz.cast(invoke.constructor().withoutArgs());
	}

	protected Object getValue(final JsonDeserializationContext context, final JsonElement element, final Object target, final Field field) {
		final Object value;
		if (field == null) {
			value = null;
		} else if (isToOne(field, Operation.DESERIALIZE)) {
			value = toMany(context, field, element);
		} else if (isToMany(field, Operation.DESERIALIZE)) {
			value = toOne(context, field, element);
		} else {
			if (JsonArray.class.isInstance(element)) {
				value = getValueCollection(context, element.getAsJsonArray(), target, field);
			} else {
				value = getValue(context, field.getName(), field.getType(), element);
			}
		}
		return value;
	}

	protected <W> W getValue(final JsonDeserializationContext context, final String field, final Class<?> type, final JsonElement element) {
		final W value;
		if (JsonNull.class.isInstance(element)) {
			value = null;
		} else if (nodes().isReload(field, Operation.DESERIALIZE)) {
			@SuppressWarnings("unchecked")
			final W w = (W) entityManager.find(type, unwrapId(type, element));
			value = w;
			if (value == null) {
				throw new EntityNotFoundException();
			}
		} else {
			value = context.deserialize(element, type);
		}
		return value;
	}

	protected Object unwrapId(final Class<?> type, final JsonElement element) {
		if (JsonNull.class.isInstance(element)) {
			return null;
		}
		final Metamodel metamodel = entityManager.getMetamodel();
		final EntityType<?> entity = metamodel.entity(type);
		final javax.persistence.metamodel.Type<?> entityType = entity.getIdType();
		final Class<?> idType = entityType.getJavaType();
		if (Long.class.equals(idType)) {
			return element.getAsLong();
		} else if (Integer.class.equals(idType)) {
			return element.getAsInt();
		} else {
			return element.getAsString();
		}
	}

	protected <X extends Annotation> String getMappedBy(final Field field) {
		for (final Entry<Class<?>, Method> entry : annotations.entrySet()) {
			@SuppressWarnings("unchecked")
			final Class<X> type = (Class<X>) entry.getKey();
			final X x = field.getAnnotation(type);
			if (x == null) {
				continue;
			}
			final Method mappedBy = entry.getValue();
			try {
				return (String) mappedBy.invoke(x);
			} catch (final Exception e) {
				throw new NoSuchElementException();
			}
		}
		return null;
	}

	protected Class<?> getRawType(final Field field) {
		final ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
		final java.lang.reflect.Type[] arguments = parameterizedType.getActualTypeArguments();
		try {
			return Class.forName(arguments[0].toString().replace("class ", ""));
		} catch (final ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	protected <W> Collection<W> getValueCollection(final JsonDeserializationContext context, final JsonArray array,
			final Object target, final Field field) {
		final Collection<W> collection = getInstance(field.getType());
		final Class<?> type = getRawType(field);
		final String mappedBy = getMappedBy(field);
		for (final JsonElement a : array) {
			final W object = getValue(context, field.getName(), type, a);
			collection.add(object);
		}
		if (mappedBy != null && !mappedBy.isEmpty()) {
			for (final W object : collection) {
				mirror.on(object).set().field(mappedBy).withValue(target);
			}
		}
		return collection;
	}

	protected <W> W toOne(final JsonDeserializationContext context, final Field field, JsonElement element) {
		if (JsonNull.class.isInstance(element)) {
			return null;
		} else {
			final W value;
			final Metamodel metamodel = entityManager.getMetamodel();
			final EntityType<?> entity = metamodel.entity(field.getType());
			final javax.persistence.metamodel.Type<?> entityType = entity.getIdType();
			final Class<?> idType = entityType.getJavaType();
			try {
				@SuppressWarnings("unchecked")
				final W v = (W) newInstance(field.getType());
				value = v;
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
			final Attribute<?, ?> attribute = entity.getId(idType);
			final String fieldIdName = attribute.getName();
			final SetterHandler handler = mirror.on(value).set();
			final FieldSetter fieldSetter = handler.field(fieldIdName);
			fieldSetter.withValue(element.getAsLong());
			return value;
		}
	}

	protected <X> X newInstance(final Class<X> klazz) {
		try {
			return klazz.newInstance();
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected <W> Collection<W> toMany(final JsonDeserializationContext context, final Field field,
			final JsonElement element) {
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
				final W o = (W) newInstance(elementType);
				object = o;
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
			final SetterHandler handler = mirror.on(object).set();
			final Metamodel metamodel = entityManager.getMetamodel();
			final EntityType<?> entity = metamodel.entity(elementType);
			final javax.persistence.metamodel.Type<?> idType = entity.getIdType();
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
	protected <Z, W> Z getInstance(final Class<?> type) {
		Z z = null;
		if (type.isAssignableFrom(List.class)) {
			z = (Z) new ArrayList<W>();
		} else if (type.isAssignableFrom(Set.class)) {
			z = (Z) new HashSet<W>();
		}
		return z;
	}
}