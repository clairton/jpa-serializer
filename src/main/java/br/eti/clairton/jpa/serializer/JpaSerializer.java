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

import net.vidageek.mirror.dsl.AccessorsController;
import net.vidageek.mirror.dsl.Mirror;

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
public abstract class JpaSerializer<T> implements JsonSerializer<T> {
	private final Mirror mirror;
	private final Logger logger;

	private final List<String> ignored = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;

		{
			add("serialVersionUID");
			add("MIRROR");
		}
	};

	/**
	 * Construtor Padrão.
	 * 
	 * @param mirror
	 *            instancia de {@link Mirror}
	 * @param logger
	 *            instancia de {@link Logger}
	 */
	public JpaSerializer(final @NotNull Mirror mirror,
			final @NotNull Logger logger) {
		super();
		this.mirror = mirror;
		this.logger = logger;
	}

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
			final String name = type.toString().replaceAll("class ", "");
			logger.debug("Serializando tipo {}", name);
			final List<Field> fields = mirror.on(name).reflectAll().fields();
			final AccessorsController controller = mirror.on(src);
			for (final Field field : fields) {
				final String tag = field.getName();
				if (ignored.contains(tag)) {
					logger.debug("Ignorando field {}", tag);
					continue;
				}
				logger.debug("Serializando {}#{}", name, tag);
				final Object value;
				if (field.isAnnotationPresent(OneToMany.class)
						|| field.isAnnotationPresent(ManyToMany.class)) {
					final Collection<Object> ids = new ArrayList<Object>();
					final Object v = controller.get().field(tag);
					final Collection<?> models = Collection.class.cast(v);
					for (Object model : models) {
						ids.add(mirror.on(model).get().field("id"));
					}
					value = ids;
				} else if (field.isAnnotationPresent(ManyToOne.class)
						|| field.isAnnotationPresent(OneToOne.class)) {
					final Object v = controller.get().field(tag);
					value = mirror.on(v).get().field("id");
				} else {
					value = mirror.on(src).get().field(tag);
				}
				logger.debug("Valor extraido {}#{}=", name, tag, value);
				final JsonElement element;
				if (value == null) {
					element = context.serialize(value);
				} else {
					element = context.serialize(value, value.getClass());
				}
				json.add(tag, element);
			}
			return json;
		} catch (final Exception e) {
			logger.error("Erro ao serializar " + src, e);
			throw new JsonParseException(e);
		}
	}
}