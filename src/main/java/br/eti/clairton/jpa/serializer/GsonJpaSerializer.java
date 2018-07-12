package br.eti.clairton.jpa.serializer;

import static br.eti.clairton.jpa.serializer.Operation.DESERIALIZE;
import static br.eti.clairton.jpa.serializer.Operation.SERIALIZE;
import static java.lang.Character.toLowerCase;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;
import static java.util.logging.Logger.getLogger;

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
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.validation.constraints.NotNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

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
	private final Logger logger = getLogger(GsonJpaSerializer.class.getSimpleName());
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
	 * @param nodes
	 *            instancia de {@link Nodes}
	 */
	public GsonJpaSerializer(final Nodes nodes, final EntityManager entityManager) {
		super(nodes);
		this.entityManager = entityManager;
	}

	/**
	 * Construtor Padrão.
	 *
	 * @param entityManager
	 *            instancia de {@link EntityManager}
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
				final String tag = getTag(field);
				if (isIgnore(src, tag, SERIALIZE)) {
					logger.log(FINE, "Ignorando field {1}", tag);
					continue;
				}
				final JsonElement element = serialize(src, field, type, context);
				json.add(tag, element);
			}
			return json;
		} catch (final Exception e) {
			logger.log(SEVERE, "Erro ao serializar " + src, e);
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
				logger.log(WARNING, "Field {1} não encontrado em {2}", new Object[]{entry.getKey(), model.getClass().getSimpleName()});
				continue;
			}
			final Object value = getValue(context, entry.getValue(), model, field);
			logger.log(FINE,"Valor extraido {1}#{2}={3}", new Object[]{type, field.getName(), value});
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

	protected String getTag(final Field field) {
		return field.getName();
	}

	protected Object getValue(final JsonSerializationContext context, final Object src, final Field field) {
		final Object value;
		final String klazz = src.getClass().getSimpleName();
		final String tag = field.getName();
		logger.log(FINE, "Serializando {1}#{2}", new Object[]{klazz, tag});
		if (isToOne(src, field, SERIALIZE)) {
			final Collection<Object> ids = new ArrayList<Object>();
			final Object v = getValue(src, field);
			final Collection<?> models = Collection.class.cast(v);
			if(isIdPolymorphic(src, field.getName(), SERIALIZE)){
				for (final Object model : models) {
					final Map<String, Object> object = new HashMap<String, Object>();
					object.put(getPolymorphicTagName(model), getPolymorphicTagValue(model));
					object.put("id", getId(model));
					ids.add(object);
				}				
			} else {				
				for (final Object model : models) {
					ids.add(getId(model));
				}
			}
			value = ids;
		} else if (isToMany(src, field, SERIALIZE)) {
			final Object v = getValue(src, field);
			if (v == null) {
				value = null;
			} else {
				value = getId(v);
			}
		} else {
			value = getValue(src, field);
		}
		logger.log(FINE, "Valor extraido {1}#{2}={3}", new Object[]{klazz, tag, value});
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
		} else if (isToOne(target, field, DESERIALIZE)) {
			value = toMany(context, field, element);
		} else if (isToMany(target, field, DESERIALIZE)) {
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
		} else if (isReload(null, field, DESERIALIZE)) {
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
		return getClass(arguments[0]);
	}

	protected <W> Collection<W> getValueCollection(final JsonDeserializationContext context, final JsonArray array, final Object target, final Field field) {
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
			return klazz.getDeclaredConstructor().newInstance();
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected <W> Collection<W> toMany(final JsonDeserializationContext context, final Field field, final JsonElement element) {
		final java.lang.reflect.Type fielType = field.getGenericType();
		final ParameterizedType pType = (ParameterizedType) fielType;
		final java.lang.reflect.Type[] arr = pType.getActualTypeArguments();
		final Class<?> elementType = getClass(arr[0]);
		final Collection<W> collection = getInstance(field.getType());
		final JsonArray array = element.getAsJsonArray();
		final Metamodel metamodel = entityManager.getMetamodel();
		final EntityType<?> entity = metamodel.entity(elementType);
		final javax.persistence.metamodel.Type<?> idType = entity.getIdType();
		final Class<?> t = idType.getJavaType();
		final Attribute<?, ?> attribute = entity.getId(t);
		final String fieldIdName = attribute.getName();
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
	
	protected String getPolymorphicTagName(final Object model){
		return "type";
	}
	
	protected String getPolymorphicTagValue(final Object model){
		final String name = model.getClass().getSimpleName();
		return toLowerCase(name.charAt(0)) + name.substring(1);
	}
}