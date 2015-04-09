package patterns.creation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * A really really simple example of a configurable Factory
 * Reads from a Properties file on classpath of the form
 * beanName=fullClassName
 * e.g.:
 * renderer=patterns.creation.MyRenderer
 */
public class AbstractFactoryDemo {

	private static final String FACTORY_CONFIG_RESOURCE_NAME = 
				"/patterns/creation/factory.config";

	enum FactoryType { 
		JDBC,
		JPA,
		HIBERNATE
	}
	static FactoryType type;

	public static void main(String[] args) throws Exception {
		type = FactoryType.valueOf(props.getProperty("productgroup"));
		System.out.println(getConnectionFactory().getConnection());
	}

	public static ConnectionFactory getConnectionFactory() {
		switch(type) {
			case JDBC: return new JdbcConnectionFactory();
			case JPA: return new JpaConnectionFactory();	// actually returns EntityManager
			case HIBERNATE: return new HibernateConnectionFactory(); // returns HibernateSession
			default:
				throw new IllegalArgumentException("Unknown Factory Type");
		}
	}

	// Dummy definitions to make this compile and sort of work
	interface ConnectionFactory {
		Object getConnection();
	}

	static class JdbcConnectionFactory implements ConnectionFactory {
		public Object getConnection() {
			return "My Dummy JDBC Connection";
		}
	}
	static class HibernateConnectionFactory implements ConnectionFactory {
		public Object getConnection() {
			return "My Fake Hibernate Session";
		}
	}
	static class JpaConnectionFactory implements ConnectionFactory {
		public Object getConnection() {
			return "Trust me! This is a JPA EntityManager";
		}
	}
	
	// Stuff for loading properties
	static Properties props = new Properties();

	static {
		try {
			InputStream stream = 
				Factory.class.getResourceAsStream(FACTORY_CONFIG_RESOURCE_NAME);
			if (stream == null) {
				throw new ExceptionInInitializerError("Can't load properties file from classpath: " + FACTORY_CONFIG_RESOURCE_NAME);
			}
			props.load(stream);
		} catch (IOException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	/** Construct and return a bean whose declared class is clasz
	 * and whose implementing class is the value of "bean" in a props file.
	 * @param name - The name the Bean should have in the props/config
	 * @param clazz - the declared (often an interface) class
	 * @return The instantiated bean.
	 * @throws Exception
	 */
	public static <T> T getBean(String name, Class<T> clazz) throws Exception {
		final String clazzName = props.getProperty(name);
		@SuppressWarnings("unchecked")
		final Class<T> c = (Class<T>) Class.forName(clazzName);
		final T o = c.newInstance();
		return o;
	}

}
