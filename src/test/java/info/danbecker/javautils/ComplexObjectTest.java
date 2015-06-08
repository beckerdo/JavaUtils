package info.danbecker.javautils;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests to validate this class.
 * 
 * @author <a href="mailto:dabecker@paypal.com">Dan Becker</a>
 */
public class ComplexObjectTest {
	@Before
	public void setup() {
	}

	@Test
	public void testGetValue() {
		new ComplexObject(); // instantiation code coverage
		
		Object something = ComplexObject.getValue( null, "Forrest.Gump");
		assertEquals("null test", null, something );

		Object somethingelse = ComplexObject.getValue( new Object(), "" );
		assertEquals("path test", null, somethingelse );

        assertNull(ComplexObject.getValue( new Object(), "GetDown"));
        assertNull(ComplexObject.getValue( new Object(), "Hullabaloo"));
		
		Foo foo = new Foo();
		Foo.Bar bar = foo.new Bar();
		Foo.Bar.Baz baz = bar.new Baz();
		baz.name = "Beer";
		baz.amount = 100;
		bar.bazContainer = baz;

		assertEquals( "null container", null, ComplexObject.getValue( foo, "BarContainer.BazContainer.Name"));

		foo.barContainer = bar;

		String name = (String) ComplexObject.getValue( foo, "BarContainer.BazContainer.Name");
		assertEquals("deep value", "Beer", name );

		int amt = (int) ComplexObject.getValue( foo, "BarContainer.BazContainer.Amount");
		assertEquals("deep value", 100, amt );
		
		assertEquals( "Non-existent field", null, ComplexObject.getValue( foo, "BarContainer.BazContainer.NonExistent"));
	}
	
	@Test
	public void testSetValue() {
	    assertNull( "null", ComplexObject.setValue( null, null, null ));

	    Integer myInt = new Integer( 42 );
	    assertNull( "null method", ComplexObject.setValue( myInt, null, null ));
	    assertNull( "bad method", ComplexObject.setValue( myInt, "skanks", null ));

	    assertEquals( "null value", "42", ComplexObject.setValue( myInt, "toString", null ));
	    assertEquals( "value", 84, ComplexObject.setValue( myInt, "parseInt", "84" ) );
	}

	@Test
	public void testSetValues() {
			
		Foo foo = new Foo();
		Foo.Bar bar = foo.new Bar();
		Foo.Bar.Baz baz = bar.new Baz();

	    assertNull( "null method", ComplexObject.setValues( null, null, null ));
	    assertNull( "null method", ComplexObject.setValues( foo, null, null ));
	    assertNull( "null method", ComplexObject.setValues( foo, new String [] { "hello" }, null ));

		Foo testFoo = (Foo) ComplexObject.setValues( foo, new String[] { "setBarContainer", "setBazContainer" }, new Object [] { bar, baz } );
		assertEquals( "deep value", bar, testFoo.getBarContainer() );
		assertEquals( "deep value", baz, testFoo.getBarContainer().getBazContainer() );
		
		testFoo = (Foo) ComplexObject.setValues( foo, new String[] { "setBarContainer", "setBazContainer", "setName" }, new Object [] { bar, baz, "Beer" } );
		assertEquals( "deep value", bar, testFoo.getBarContainer() );
		assertEquals( "deep value", "Beer", testFoo.getBarContainer().getBazContainer().getName() );	

		assertNull( "null", ComplexObject.setValues( foo, new String[] { "setBarContainer", "setBazContainer", "NotAMethod" }, new Object [] { bar, baz, "Beer" } ));
	}
	

}

class Foo {
	class Bar {
		class Baz {
			protected int amount;
			protected String name;

			public int getAmount() {
				return amount;
			}
			public void setAmount(int amount) {
				this.amount = amount;
			}
			public String getName() {
				return name;
			}
			public void setName(String name) {
				this.name = name;
			}
		}
		
		protected Baz bazContainer;
		public Baz getBazContainer() {
			return bazContainer;
		}
		public void setBazContainer(Baz bazContainer) {
			this.bazContainer = bazContainer;
		}	}
	
	protected Bar barContainer;
	public Bar getBarContainer() {
		return barContainer;
	}
	public void setBarContainer(Bar barContainer) {
		this.barContainer = barContainer;
	}
}