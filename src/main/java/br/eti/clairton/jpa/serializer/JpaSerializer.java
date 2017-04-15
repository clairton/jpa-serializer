package br.eti.clairton.jpa.serializer;

import static br.eti.clairton.jpa.serializer.Mode.ID;
import static br.eti.clairton.jpa.serializer.Mode.ID_POLYMORPHIC;
import static br.eti.clairton.jpa.serializer.Mode.IGNORE;
import static br.eti.clairton.jpa.serializer.Mode.RECORD;
import static br.eti.clairton.jpa.serializer.Mode.RELOAD;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Logger.getLogger;

import java.lang.reflect.Field;
import java.util.logging.Logger;

import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.google.gson.JsonParseException;

import net.vidageek.mirror.dsl.AccessorsController;
import net.vidageek.mirror.dsl.ClassController;
import net.vidageek.mirror.dsl.Mirror;

public abstract class JpaSerializer<T> extends Tagable<T> {
	private static final long serialVersionUID = 1L;
	private final Logger logger = getLogger(JpaSerializer.class.getSimpleName());
	private final Nodes nodes;
	protected final Mirror mirror = new Mirror();

	public JpaSerializer() {
		this(new NodesProgramatic());
	}

	public JpaSerializer(final Nodes nodes) {
		super();
		this.nodes = nodes;
	}

	protected void record(final String field) {
		config(field, RECORD);
	}

	protected void record(final String field, final Operation operation) {
		config(field, RECORD, operation);
	}

	protected void id(final String field) {
		config(field, ID);
	}

	protected void idPolymorphic(final String field) {
		config(field, ID_POLYMORPHIC);
	}

	protected void id(final String field, final Operation operation) {
		config(field, ID, operation);
	}

	protected void idPolymorphic(final String field, final Operation operation) {
		config(field, ID_POLYMORPHIC, operation);
	}

	protected void reload(final String field) {
		config(field, RELOAD);
	}

	protected void reload(final String field, final Operation operation) {
		config(field, RELOAD, operation);
	}

	protected void ignore(final String field) {
		config(field, IGNORE);
	}

	protected void ignore(final String field, final Operation operation) {
		config(field, IGNORE, operation);
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
		final String name = type.toString().replaceAll("class ", "").replaceAll("<\\?.*", "");
		try {
			@SuppressWarnings("unchecked")
			final Class<T> t = (Class<T>) Class.forName(name);
			logger.log(FINE, "Deserializando tipo {}", t.getSimpleName());
			return t;
		} catch (final Exception e) {
			logger.log(SEVERE, "Erro ao instanciar tipo {}, detalhe: {}", new Object[]{type, e.getMessage()});
			logger.log(FINE, "Erro ao instanciar", e);
			throw new JsonParseException(e);
		}
	}

	public Nodes nodes() {
		return nodes;
	}

	@Deprecated
	protected Boolean isToMany(final Field field, final Operation operation) {
		return isToMany(null, field, operation);
	}

	@Deprecated
	protected Boolean isToOne(final Field field, final Operation operation) {
		return isToOne(null, field, operation);
	}

	@Deprecated
	protected Boolean isIgnore(final String key, final Operation operation) {
		return isIgnore(null, key, operation);
	}

	@Deprecated
	protected Boolean isIgnore(final String key) {
		return isIgnore(null, key);
	}

	@Deprecated
	protected Boolean isId(final String key, final Operation operation) {
		return isId(null, key, operation);
	}

	@Deprecated
	protected Boolean isId(final String key) {
		return isId(null, key);
	}

	@Deprecated
	protected Boolean isIdPolymorphic(final String key) {
		return isIdPolymorphic(null, key);
	}

	@Deprecated
	protected Boolean isIdPolymorphic(final String key, final Operation operation) {
		return isIdPolymorphic(null, key, operation);
	}

	@Deprecated
	protected Boolean isReload(final String key, final Operation operation) {
		return isReload(null, key, operation);
	}

	@Deprecated
	protected Boolean isReload(final String key) {
		return isReload(null, key);
	}

	@Deprecated
	protected Boolean isRecord(final String key, final Operation operation) {
		return isRecord(null, key, operation);
	}

	@Deprecated
	protected Boolean isRecord(final String key) {
		return isRecord(null, key);
	}

	protected Boolean isToMany(final Object source, final Field field, final Operation operation) {
		return (field.isAnnotationPresent(ManyToOne.class) || field.isAnnotationPresent(OneToOne.class))
				&& !isRecord(source, field.getName(), operation) && !isReload(source, field.getName(), operation);
	}

	protected Boolean isToOne(final Object source, final Field field, final Operation operation) {
		return (field.isAnnotationPresent(OneToMany.class) || field.isAnnotationPresent(ManyToMany.class))
				&& !isRecord(source, field.getName(), operation) && !isReload(source, field.getName(), operation);
	}

	protected Boolean isIgnore(final Object source, final String key, final Operation operation) {
		return nodes(source).isIgnore(key, operation);
	}

	protected Boolean isIgnore(final Object source, final String key) {
		return nodes(source).isIgnore(key);
	}

	protected Boolean isId(final Object source, final String key, final Operation operation) {
		return nodes(source).isId(key, operation);
	}

	protected Boolean isId(final Object source, final String key) {
		return nodes(source).isId(key);
	}

	protected Boolean isIdPolymorphic(final Object source, final String key) {
		return nodes(source).isIdPolymorphic(key);
	}

	protected Boolean isIdPolymorphic(final Object source, final String key, final Operation operation) {
		return nodes(source).isIdPolymorphic(key, operation);
	}

	protected Boolean isReload(final Object source, final String key, final Operation operation) {
		return nodes(source).isReload(key, operation);
	}

	protected Boolean isReload(final Object source, final String key) {
		return nodes(source).isReload(key);
	}

	protected Boolean isRecord(final Object source, final String key, final Operation operation) {
		return nodes(source).isRecord(key, operation);
	}

	protected Boolean isRecord(final Object source, final String key) {
		return nodes(source).isRecord(key);
	}

	public Nodes nodes(final Object source) {
		return nodes;
	}
}