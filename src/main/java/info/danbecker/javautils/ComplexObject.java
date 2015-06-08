package info.danbecker.javautils;

import java.lang.reflect.Method;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class creates, sets, and gets objects from large object models.
 * The object model should consist of plain old Java objects (POJOs)
 * that have properly names instance fields and getters. For example:
 * <code>
 * String foo;
 * String getFoo() {...}
 * </code>
 *
 * @author <a href="mailto:dan@danbecker.info">Dan Becker</a>
 */
public class ComplexObject {
	/** Delimiter use in get path tree, for example: ClassA.ClassB.ClassC */
	public static final String PATH_DELIM = ".";
	
    /** This is the logger that we use to talk to Quetzalcoatl. */
    private static Logger logger = LoggerFactory.getLogger(ComplexObject.class);

	/** Uses introspection to get a value from a complex object tree.
	 * Returns null if getter method or field is not available.
	 * <p>
	 * For example getValue( foo, "Bar.Baz" ) takes object foo,
	 * performs a getBar() and getBaz() and returns the value.
	 * 
	 * @param start
	 * @param path
	 * @return object at end of path
	 */
	public static Object getValue( Object start, String path ) {
		if ( null == start ) return null;
		Object currObject = start;
		StringTokenizer st = new StringTokenizer( path, PATH_DELIM );
		while ( st.hasMoreTokens() ) {
			String fieldName = st.nextToken();
			Class<? extends Object> currClass = currObject.getClass();
			try {
				// Get the next simple getter method. will return exception if not found
			    Method method = currClass.getDeclaredMethod( "get" + fieldName, new Class [] {} );
			    Object currValue = method.invoke( currObject, new Object[] {} );
			    if ( null != currValue ) {
				   if ( st.hasMoreTokens() ) {
					   currObject = currValue;
				   } else {
					   return currValue;
				   }
			    } else {
				   return null;
			    }
			} catch ( Exception e ) {
				 // Can catch NoSuchMEthodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
				logger.debug( "No value found in object " + start.getClass().getSimpleName() + " for path \"" + path + "\". Exception=" + e );
				return null;
			}
		}
		logger.debug( "No path found in object " + start.getClass().getSimpleName() + " for path \"" + path + "\"." );
		return null;
	}

	/** Uses introspection to set a value on an object. 
	 * Save you some setter verbosity.
	 * <p>
	 * For example setValue( foo, "setValue", 42  ) }
	 * <p>
	 * If the optional setterName and value are provided, the value is pushed into object with the named setter.
	 * 
	 * @param setterName name of setter for example "setStart" 
	 * @param value value of setter call "setStart( foo )"
	 * @return method return value
	 */
    public static Object setValue( Object object, String setterName, Object value ) {
    	if ( null == object )
    		return null;
		if ( null != setterName ) {
			Class<?> currClass = object.getClass();
			// Get the next simple setter method. will return exception if not found
			Method method = null;
			try { 
				if ( null != value ) {
					method = currClass.getDeclaredMethod( setterName, new Class [] { value.getClass() } );
					return method.invoke( object, new Object[] { value } );
				} else {
					method = currClass.getDeclaredMethod( setterName, new Class [] { } );
					return method.invoke( object, new Object[] { } );
				}
			} catch (Exception e) {
				logger.debug( "Exception with class name " + object.getClass().getSimpleName() + ", setter=" + setterName + ", exception=" + e );
				return null;		
			}
		}
    	return null;
    }

    
	/** Uses introspection to create a hierarcy of objects. 
	 * Saves you some setter verbosity.
	 * <p>
	 * For example setValue( new Foo(), String [] { "setBar", "setBaz", "setName" }, new Object{} { new Bar( "Hello" ), new Baz( "World" ), "Beer" } );
	 * object Foo which can getBar, getBaz which is value 42.
	 * 
	 * @param start add objects from this object (null is allowed) 
	 * @param value setterNames use these setter names
	 * @param object array of objects to set
	 * @return object which is top level object 
	 */
	public static Object setValues( Object start, String [] setterNames,  Object [] objects ) {
		if ( null == start )
			return null;
		if ( null == setterNames || null == objects )
			return null;

		Object parent = start;		
		for ( int i = 0; i < setterNames.length; i++ ) {
			Object current = objects[ i ];
			try {
				// Get the next simple setter method. will return exception if not found
				Method method = parent.getClass().getDeclaredMethod( setterNames[ i ], new Class [] { current.getClass() } );

				// Push object into parent class
				method.invoke( parent, new Object[] { current } );
				parent = current;
			} catch ( Exception e ) {
				logger.debug( "Parent object " + parent.getClass().getSimpleName() + ", setter=" + setterNames[ i ] + ", object type=" + current.getClass().getSimpleName() + ", exception=" + e );
				return null;		
			}
		}
		return start;
	}
	
 }