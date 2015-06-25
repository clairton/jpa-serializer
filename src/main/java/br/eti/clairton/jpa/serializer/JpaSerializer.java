package br.eti.clairton.jpa.serializer;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Serializa um entidade JPA para JSON usando o Gson.
 *
 * @author Clairton Rodrigo Heinzen<clairton.rodrigo@gmail.com>
 *
 * @param <T>
 *            tipo da entidade
 */
public class JpaSerializer<T> extends AbstractSerializator<T> implements
		JsonSerializer<T> {
	private final Logger logger = LogManager.getLogger(JpaSerializer.class);
	private final List<String> ignored = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;

		{
			add("serialVersionUID");
			add("MIRROR");
			add("logger");
		}
	};

	public void addIgnoredField(@NotNull final String field) {
		ignored.add(field);
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public JsonElement serialize(final T src, final Type type,
			final JsonSerializationContext context) {
		try {
			final JsonObject json = new JsonObject();
			final Class<T> klazz = getClass(type);
			final List<Field> fields = getFields(klazz);
			for (final Field field : fields) {
				final String tag = field.getName();
				if (ignored.contains(tag)) {
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

	public JsonElement serialize(final Object src, final Field field,
			final Type type, final JsonSerializationContext context) {
		final Object value = getValue(context, src, field);
		if (value == null) {
			return context.serialize(value);
		} else {
			return context.serialize(value, value.getClass());
		}
	}

	public List<Field> getFields(final Class<T> klazz) {
		return mirror.on(klazz).reflectAll().fields();
	}

	public Object getValue(final JsonSerializationContext context,
			final Object src, final Field field) {
		final Object value;
		final String klazz = src.getClass().getSimpleName();
		final String tag = field.getName();
		logger.debug("Serializando {}#{}", klazz, tag);
		if (isToOne(field)) {
			final Collection<Object> ids = new ArrayList<Object>();
			final Object v = getValue(src, field);
			final Collection<?> models = Collection.class.cast(v);
			for (final Object model : models) {
				ids.add(getId(model));
			}
			value = ids;
		} else if (isToMany(field)) {
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

	@Override
	public Boolean isToMany(final Field field) {
		return (field.isAnnotationPresent(ManyToOne.class) || field
				.isAnnotationPresent(OneToOne.class))
				&& nodes().get(field.getName()).equals(Mode.ID);
	}

	@Override
	public Boolean isToOne(final Field field) {
		return (field.isAnnotationPresent(OneToMany.class) || field
				.isAnnotationPresent(ManyToMany.class))
				&& nodes().get(field.getName()).equals(Mode.ID);
	}
}