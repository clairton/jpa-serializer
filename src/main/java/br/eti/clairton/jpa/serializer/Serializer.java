package br.eti.clairton.jpa.serializer;

import java.lang.reflect.Field;

import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonParseException;

import net.vidageek.mirror.dsl.AccessorsController;
import net.vidageek.mirror.dsl.ClassController;
import net.vidageek.mirror.dsl.Mirror;

abstract class Serializer<T> extends Tagable<T> {
	protected final Mirror mirror = new Mirror();
	private final Logger logger = LogManager.getLogger(Serializer.class);
	private final Nodes nodes = new Nodes() {
		private static final long serialVersionUID = 1L;

		{
			put("serialVersionUID", Mode.IGNORE);
			put("MIRROR", Mode.IGNORE);
			put("logger", Mode.IGNORE);
			put("STYLE", Mode.IGNORE);
		}
	};

	protected void record(final String field) {
		config(field, Mode.RECORD);
	}

	protected void record(final String field, final Operation operation) {
		config(field, Mode.RECORD, operation);
	}

	protected void id(final String field) {
		config(field, Mode.ID);
	}

	protected void id(final String field, final Operation operation) {
		config(field, Mode.ID, operation);
	}

	protected void reload(final String field) {
		config(field, Mode.RELOAD);
	}

	protected void reload(final String field, final Operation operation) {
		config(field, Mode.RELOAD, operation);
	}

	protected void ignore(final String field) {
		config(field, Mode.IGNORE);
	}

	protected void ignore(final String field, final Operation operation) {
		config(field, Mode.IGNORE, operation);
	}

	protected void config(final String field, final Mode mode) {
		nodes().put(field, mode);
	}

	protected void config(final String field, final Mode mode, final Operation operation) {
		nodes().put(field, mode, operation);
	}

	protected Field getField(final Class<?> type, final String field) {
		final ClassController<?> controller = mirror.on(type);
		return controller.reflect().field(field);
	}

	protected Object getId(final Object target) {
		return getValue(target, "id");
	}

	protected Object getValue(final Object target, final String field) {
		final AccessorsController controller = mirror.on(target);
		return controller.get().field(field);
	}

	protected Object getValue(final Object target, final Field field) {
		final AccessorsController controller = mirror.on(target);
		return controller.get().field(field);
	}

	protected void setValue(final Object target, final Field field, final Object value) {
		final AccessorsController accessor = mirror.on(target);
		accessor.set().field(field).withValue(value);
	}

	protected Class<T> getClass(final java.lang.reflect.Type type) {
		final String name = type.toString().replaceAll("class ", "");
		try {
			@SuppressWarnings("unchecked")
			final Class<T> t = (Class<T>) Class.forName(name);
			logger.debug("Deserializando tipo {}", t.getSimpleName());
			return t;
		} catch (final Exception e) {
			logger.error("Erro ao instanciar tipo {}, detalhe: {}", type, e.getMessage());
			logger.debug("Erro ao instanciar", e);
			throw new JsonParseException(e);
		}
	}

	public Nodes nodes() {
		return nodes;
	}

	protected Boolean isToMany(final Field field) {
		return (field.isAnnotationPresent(ManyToOne.class) || 
					field.isAnnotationPresent(OneToOne.class)) && 
						nodes().isId(field.getName());
	}

	protected Boolean isToOne(final Field field) {
		return (field.isAnnotationPresent(OneToMany.class) || 
					field.isAnnotationPresent(ManyToMany.class)) && 
						nodes().isId(field.getName());
	}
}