/*
 * $Id: EqualityTest.java,v 1.1.2.3 2008/11/08 22:53:36 fran Exp $
 * 
 * Copyright (c) 2005, 2006, 2007, 2008 Fran Lattanzio
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.teneighty.heap;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.Test;


/**
 * Test the equality methods, pairwise with all other heap implementations.
 * <p>
 * For now, this is an extremely dumb test, because none of the heaps actually
 * implements custom <code>Equals()</code> methods. However, as I start writing
 * additional heap implementations, there may be cases where we CAN write
 * <code>Equals()</code> overrides that don't use the (generic and
 * <code>O(n<sup>2</sup>)</code> in supplied in <code>AbstractHeap</code>.
 * 
 * @author Fran Lattanzio
 * @version $Revision: 1.1.2.3 $ $Date: 2008/11/08 22:53:36 $
 */
public final class EqualityTest
{

	/**
	 * Size of heaps used for comparison.
	 */
	private static final int HEAP_SIZE = 100;

	/**
	 * List of heap types to check.
	 */
	private static final Class[] heapTypes = new Class[] { BinaryHeap.class,
			BinomialHeap.class, FibonacciHeap.class, LeftistHeap.class,
			PairingHeap.class };

	/**
	 * Reflectively create a heap for the specified type.
	 * <p>
	 * This method fails (i.e. it JUnit fails) if it cannot create an instance
	 * of the specified class or the specified class does not implement the
	 * <code>Heap</code> interface.
	 * 
	 * @param heapClazz the Class of heap to create.
	 * @return a new heap of the specified type.
	 */
	@SuppressWarnings("unchecked")
	private Heap<Integer, Integer> reflectivelyCreateHeap(final Class heapClazz)
	{
		try
		{
			// yay for erased cast warning. Say what you will about M$, but C#
			// retains
			// the generic types at runtime... this also the reason for the
			// stupid,
			// insane
			// warning suppression above.
			return ((Heap<Integer, Integer>) heapClazz.newInstance());
		}
		catch (final IllegalAccessException iae)
		{
			fail("Unable to create a heap of type " + heapClazz.getName());
			return (null);
		}
		catch (final InstantiationException ie)
		{
			fail("Unable to create a heap of type " + heapClazz.getName());
			return (null);
		}
		catch (final ClassCastException cce)
		{
			fail(heapClazz.getName() + " does not implement the Heap interface");
			return (null);
		}
	}

	/**
	 * Test the heaps with equal entry bags actually correct implement semantic
	 * equality.
	 * <p>
	 * We test this for all different heaps types, pairwise.
	 */
	@Test
	public void testHeapsAreEqual()
	{
		for (int index = 0; index < heapTypes.length; index++)
		{
			for (int jindex = index; jindex < heapTypes.length; jindex++)
			{
				// create the two heap types.
				Heap<Integer, Integer> firstHeap = this
						.reflectivelyCreateHeap(heapTypes[index]);
				Heap<Integer, Integer> secondHeap = this
						.reflectivelyCreateHeap(heapTypes[jindex]);

				// perpare to equal heaps.
				this.prepareTwoEqualHeaps(firstHeap, secondHeap);

				// check with themselves (this is should be O(1) because any
				// sane
				// implementation of
				// equals(Object) should do the cheap check for reference
				// equality).
				// In either case, this will should not fail...
				assertTrue("Heap not equal to itself", firstHeap
						.equals(firstHeap));
				assertTrue("Heap not equal to itself", secondHeap
						.equals(secondHeap));

				// check them against each other.
				assertTrue("Heaps with the same entry set are not equal",
						firstHeap.equals(secondHeap));
				assertTrue("Heaps with the same entry set are not equal",
						secondHeap.equals(firstHeap));
			}
		}
	}

	/**
	 * Test that heaps with different keys (but the same value set) are not
	 * equal.
	 * <p>
	 * We compare all heap types, pairwise.
	 */
	@Test
	public void testHeapsAreNotEqualWithDifferentKeys()
	{
		for (int index = 0; index < heapTypes.length; index++)
		{
			for (int jindex = index; jindex < heapTypes.length; jindex++)
			{
				// create the two heap types.
				Heap<Integer, Integer> firstHeap = this
						.reflectivelyCreateHeap(heapTypes[index]);
				Heap<Integer, Integer> secondHeap = this
						.reflectivelyCreateHeap(heapTypes[jindex]);

				// get heaps with the different key sets but equal value sets.
				this.prepareHeapsWithDifferentKeys(firstHeap, secondHeap);

				// check with themselves.
				assertTrue("Heap not equal to itself", firstHeap
						.equals(firstHeap));
				assertTrue("Heap not equal to itself", secondHeap
						.equals(secondHeap));

				// check them against each other.
				assertFalse("Heaps with different key sets are equal",
						firstHeap.equals(secondHeap));
				assertFalse("Heaps with different key sets are equal",
						secondHeap.equals(firstHeap));
			}
		}
	}

	/**
	 * Test that heaps with different value sets (but equal key sets) are not
	 * equal.
	 * <p>
	 * Test for all types, pairwise.
	 */
	@Test
	public void testHeapsAreNotEqualWithDifferentValues()
	{
		for (int index = 0; index < heapTypes.length; index++)
		{
			for (int jindex = index; jindex < heapTypes.length; jindex++)
			{
				// create the two heap types.
				Heap<Integer, Integer> firstHeap = this
						.reflectivelyCreateHeap(heapTypes[index]);
				Heap<Integer, Integer> secondHeap = this
						.reflectivelyCreateHeap(heapTypes[jindex]);

				// get heaps with the different key sets but equal value sets.
				this.prepareHeapsWithDifferentValues(firstHeap, secondHeap);

				// check with themselves.
				assertTrue("Heap not equal to itself", firstHeap
						.equals(firstHeap));
				assertTrue("Heap not equal to itself", secondHeap
						.equals(secondHeap));

				// check them against each other.
				assertFalse("Heaps with different key sets are equal",
						firstHeap.equals(secondHeap));
				assertFalse("Heaps with different key sets are equal",
						secondHeap.equals(firstHeap));
			}
		}
	}

	/**
	 * Test that heaps with different entry sets are not equal.
	 * <p>
	 * We test all heap types, pairwise.
	 */
	@Test
	public void testHeapsAreNotEqualWithDifferentEntrySets()
	{
		for (int index = 0; index < heapTypes.length; index++)
		{
			for (int jindex = index; jindex < heapTypes.length; jindex++)
			{
				// create the two heap types.
				Heap<Integer, Integer> firstHeap = this
						.reflectivelyCreateHeap(heapTypes[index]);
				Heap<Integer, Integer> secondHeap = this
						.reflectivelyCreateHeap(heapTypes[jindex]);

				// get heaps with the different key sets but equal value sets.
				this.prepareHeapsWithDifferentEntrySets(firstHeap, secondHeap);

				// check with themselves.
				assertTrue("Heap not equal to itself", firstHeap
						.equals(firstHeap));
				assertTrue("Heap not equal to itself", secondHeap
						.equals(secondHeap));

				// check them against each other.
				assertFalse("Heaps with different key sets are equal",
						firstHeap.equals(secondHeap));
				assertFalse("Heaps with different key sets are equal",
						secondHeap.equals(firstHeap));
			}
		}
	}

	/**
	 * Test that heaps with equal entry sets but different entry bags are not
	 * equal.
	 * <p>
	 * We test all heap types, pairwise.
	 */
	@Test
	public void testHeapsAreNotEqualWithDifferentEntryBags()
	{
		for (int index = 0; index < heapTypes.length; index++)
		{
			for (int jindex = index; jindex < heapTypes.length; jindex++)
			{
				// create the two heap types.
				Heap<Integer, Integer> firstHeap = this
						.reflectivelyCreateHeap(heapTypes[index]);
				Heap<Integer, Integer> secondHeap = this
						.reflectivelyCreateHeap(heapTypes[jindex]);

				// get heaps with the different key sets but equal value sets.
				this.prepareHeapsWithDifferentEntryBags(firstHeap, secondHeap);

				// check with themselves.
				assertTrue("Heap not equal to itself", firstHeap
						.equals(firstHeap));
				assertTrue("Heap not equal to itself", secondHeap
						.equals(secondHeap));

				// check them against each other.
				assertFalse("Heaps with different key sets are equal",
						firstHeap.equals(secondHeap));
				assertFalse("Heaps with different key sets are equal",
						secondHeap.equals(firstHeap));
			}
		}
	}

	/**
	 * Test the heaps with equal entry bags actually correct implement semantic
	 * equality.
	 * <p>
	 * We test this for all different heaps types, pairwise.
	 */
	@Test
	public void testKeyCollectionsAreEqual()
	{
		for (int index = 0; index < heapTypes.length; index++)
		{
			for (int jindex = index; jindex < heapTypes.length; jindex++)
			{
				// create the two heap types.
				Heap<Integer, Integer> firstHeap = this
						.reflectivelyCreateHeap(heapTypes[index]);
				Heap<Integer, Integer> secondHeap = this
						.reflectivelyCreateHeap(heapTypes[jindex]);

				// perpare to equal heaps.
				this.prepareTwoEqualHeaps(firstHeap, secondHeap);

				// check with themselves (this is should be O(1) because any
				// sane
				// implementation of
				// equals(Object) should do the cheap check for reference
				// equality).
				// In either case, this will should not fail...
				assertTrue("Heap not equal to itself", firstHeap.getKeys()
						.equals(firstHeap.getKeys()));
				assertTrue("Heap not equal to itself", secondHeap.getKeys()
						.equals(secondHeap.getKeys()));

				// check them against each other.
				assertTrue("Heaps with the same entry set are not equal",
						firstHeap.getKeys().equals(secondHeap.getKeys()));
				assertTrue("Heaps with the same entry set are not equal",
						secondHeap.getKeys().equals(firstHeap.getKeys()));
			}
		}
	}

	/**
	 * Test that heaps with different keys (but the same value set) are not
	 * equal.
	 * <p>
	 * We compare all heap types, pairwise.
	 */
	@Test
	public void testKeyCollectionsAreNotEqualWithDifferentKeys()
	{
		for (int index = 0; index < heapTypes.length; index++)
		{
			for (int jindex = index; jindex < heapTypes.length; jindex++)
			{
				// create the two heap types.
				Heap<Integer, Integer> firstHeap = this
						.reflectivelyCreateHeap(heapTypes[index]);
				Heap<Integer, Integer> secondHeap = this
						.reflectivelyCreateHeap(heapTypes[jindex]);

				// get heaps with the different key sets but equal value sets.
				this.prepareHeapsWithDifferentKeys(firstHeap, secondHeap);

				// check with themselves.
				assertTrue("Heap not equal to itself", firstHeap.getKeys()
						.equals(firstHeap.getKeys()));
				assertTrue("Heap not equal to itself", secondHeap.getKeys()
						.equals(secondHeap.getKeys()));

				// check them against each other.
				assertFalse("Heaps with different key sets are equal",
						firstHeap.getKeys().equals(secondHeap.getKeys()));
				assertFalse("Heaps with different key sets are equal",
						secondHeap.getKeys().equals(firstHeap.getKeys()));
			}
		}
	}

	/**
	 * Test that heaps with different value sets (but equal key sets) are not
	 * equal.
	 * <p>
	 * Test for all types, pairwise.
	 */
	@Test
	public void testKeyCollectionsAreEqualWithDifferentValues()
	{
		for (int index = 0; index < heapTypes.length; index++)
		{
			for (int jindex = index; jindex < heapTypes.length; jindex++)
			{
				// create the two heap types.
				Heap<Integer, Integer> firstHeap = this
						.reflectivelyCreateHeap(heapTypes[index]);
				Heap<Integer, Integer> secondHeap = this
						.reflectivelyCreateHeap(heapTypes[jindex]);

				// get heaps with the different key sets but equal value sets.
				this.prepareHeapsWithDifferentValues(firstHeap, secondHeap);

				// check with themselves.
				assertTrue("Heap not equal to itself", firstHeap.getKeys()
						.equals(firstHeap.getKeys()));
				assertTrue("Heap not equal to itself", secondHeap.getKeys()
						.equals(secondHeap.getKeys()));

				// check them against each other.
				assertTrue(
						"Heaps with different value collections are not equal",
						firstHeap.getKeys().equals(secondHeap.getKeys()));
				assertTrue(
						"Heaps with different value collections are not equal",
						secondHeap.getKeys().equals(firstHeap.getKeys()));
			}
		}
	}

	/**
	 * Test that heaps with different entry sets are not equal.
	 * <p>
	 * We test all heap types, pairwise.
	 */
	@Test
	public void testKeyCollectionsAreNotEqualWithDifferentEntrySets()
	{
		for (int index = 0; index < heapTypes.length; index++)
		{
			for (int jindex = index; jindex < heapTypes.length; jindex++)
			{
				// create the two heap types.
				Heap<Integer, Integer> firstHeap = this
						.reflectivelyCreateHeap(heapTypes[index]);
				Heap<Integer, Integer> secondHeap = this
						.reflectivelyCreateHeap(heapTypes[jindex]);

				// get heaps with the different key sets but equal value sets.
				this.prepareHeapsWithDifferentEntrySets(firstHeap, secondHeap);

				// check with themselves.
				assertTrue("Heap not equal to itself", firstHeap.getKeys()
						.equals(firstHeap.getKeys()));
				assertTrue("Heap not equal to itself", secondHeap.getKeys()
						.equals(secondHeap.getKeys()));

				// check them against each other.
				assertFalse("Heaps with different key sets are equal",
						firstHeap.getKeys().equals(secondHeap.getKeys()));
				assertFalse("Heaps with different key sets are equal",
						secondHeap.getKeys().equals(firstHeap.getKeys()));
			}
		}
	}

	/**
	 * Test that heaps with equal entry sets but different entry bags are not
	 * equal.
	 * <p>
	 * We test all heap types, pairwise.
	 */
	@Test
	public void testKeyCollectionsAreNotEqualWithDifferentEntryBags()
	{
		for (int index = 0; index < heapTypes.length; index++)
		{
			for (int jindex = index; jindex < heapTypes.length; jindex++)
			{
				// create the two heap types.
				Heap<Integer, Integer> firstHeap = this
						.reflectivelyCreateHeap(heapTypes[index]);
				Heap<Integer, Integer> secondHeap = this
						.reflectivelyCreateHeap(heapTypes[jindex]);

				// get heaps with the different key sets but equal value sets.
				this.prepareHeapsWithDifferentEntryBags(firstHeap, secondHeap);

				// check with themselves.
				assertTrue("Heap not equal to itself", firstHeap.getKeys()
						.equals(firstHeap.getKeys()));
				assertTrue("Heap not equal to itself", secondHeap.getKeys()
						.equals(secondHeap.getKeys()));

				// check them against each other.
				assertFalse("Heaps with different key sets are equal",
						firstHeap.getKeys().equals(secondHeap.getKeys()));
				assertFalse("Heaps with different key sets are equal",
						secondHeap.getKeys().equals(firstHeap.getKeys()));
			}
		}
	}

	/**
	 * Test the heaps with equal entry bags actually correct implement semantic
	 * equality.
	 * <p>
	 * We test this for all different heaps types, pairwise.
	 */
	@Test
	public void testValueCollectionsAreEqual()
	{
		for (int index = 0; index < heapTypes.length; index++)
		{
			for (int jindex = index; jindex < heapTypes.length; jindex++)
			{
				// create the two heap types.
				Heap<Integer, Integer> firstHeap = this
						.reflectivelyCreateHeap(heapTypes[index]);
				Heap<Integer, Integer> secondHeap = this
						.reflectivelyCreateHeap(heapTypes[jindex]);

				// perpare to equal heaps.
				this.prepareTwoEqualHeaps(firstHeap, secondHeap);

				// check with themselves (this is should be O(1) because any
				// sane
				// implementation of
				// equals(Object) should do the cheap check for reference
				// equality).
				// In either case, this will should not fail...
				assertTrue("Heap not equal to itself", firstHeap.getValues()
						.equals(firstHeap.getValues()));
				assertTrue("Heap not equal to itself", secondHeap.getValues()
						.equals(secondHeap.getValues()));

				// check them against each other.
				assertTrue("Heaps with the same entry set are not equal",
						firstHeap.getValues().equals(secondHeap.getValues()));
				assertTrue("Heaps with the same entry set are not equal",
						secondHeap.getValues().equals(firstHeap.getValues()));
			}
		}
	}

	/**
	 * Test that heaps with different keys (but the same value set) are not
	 * equal.
	 * <p>
	 * We compare all heap types, pairwise.
	 */
	@Test
	public void testValueCollectionsAreEqualWithDifferentKeys()
	{
		for (int index = 0; index < heapTypes.length; index++)
		{
			for (int jindex = index; jindex < heapTypes.length; jindex++)
			{
				// create the two heap types.
				Heap<Integer, Integer> firstHeap = this
						.reflectivelyCreateHeap(heapTypes[index]);
				Heap<Integer, Integer> secondHeap = this
						.reflectivelyCreateHeap(heapTypes[jindex]);

				// get heaps with the different key sets but equal value sets.
				this.prepareHeapsWithDifferentKeys(firstHeap, secondHeap);

				// check with themselves.
				assertTrue("Heap not equal to itself", firstHeap.getValues()
						.equals(firstHeap.getValues()));
				assertTrue("Heap not equal to itself", secondHeap.getValues()
						.equals(secondHeap.getValues()));

				// check them against each other.
				assertTrue("Heaps with different key sets are equal", firstHeap
						.getValues().equals(secondHeap.getValues()));
				assertTrue("Heaps with different key sets are equal",
						secondHeap.getValues().equals(firstHeap.getValues()));
			}
		}
	}

	/**
	 * Test that heaps with different value sets (but equal key sets) are not
	 * equal.
	 * <p>
	 * Test for all types, pairwise.
	 */
	@Test
	public void testValueCollectionsAreNotEqualWithDifferentValues()
	{
		for (int index = 0; index < heapTypes.length; index++)
		{
			for (int jindex = index; jindex < heapTypes.length; jindex++)
			{
				// create the two heap types.
				Heap<Integer, Integer> firstHeap = this
						.reflectivelyCreateHeap(heapTypes[index]);
				Heap<Integer, Integer> secondHeap = this
						.reflectivelyCreateHeap(heapTypes[jindex]);

				// get heaps with the different key sets but equal value sets.
				this.prepareHeapsWithDifferentValues(firstHeap, secondHeap);

				// check with themselves.
				assertTrue("Heap not equal to itself", firstHeap.getValues()
						.equals(firstHeap.getValues()));
				assertTrue("Heap not equal to itself", secondHeap.getValues()
						.equals(secondHeap.getValues()));

				// check them against each other.
				assertFalse(
						"Heaps with different value collections are not equal",
						firstHeap.getValues().equals(secondHeap.getValues()));
				assertFalse(
						"Heaps with different value collections are not equal",
						secondHeap.getValues().equals(firstHeap.getValues()));
			}
		}
	}

	/**
	 * Test that heaps with different entry sets are not equal.
	 * <p>
	 * We test all heap types, pairwise.
	 */
	@Test
	public void testValueCollectionsAreNotEqualWithDifferentEntrySets()
	{
		for (int index = 0; index < heapTypes.length; index++)
		{
			for (int jindex = index; jindex < heapTypes.length; jindex++)
			{
				// create the two heap types.
				Heap<Integer, Integer> firstHeap = this
						.reflectivelyCreateHeap(heapTypes[index]);
				Heap<Integer, Integer> secondHeap = this
						.reflectivelyCreateHeap(heapTypes[jindex]);

				// get heaps with the different key sets but equal value sets.
				this.prepareHeapsWithDifferentEntrySets(firstHeap, secondHeap);

				// check with themselves.
				assertTrue("Heap not equal to itself", firstHeap.getValues()
						.equals(firstHeap.getValues()));
				assertTrue("Heap not equal to itself", secondHeap.getValues()
						.equals(secondHeap.getValues()));

				// check them against each other.
				assertFalse("Heaps with different key sets are equal",
						firstHeap.getValues().equals(secondHeap.getValues()));
				assertFalse("Heaps with different key sets are equal",
						secondHeap.getValues().equals(firstHeap.getValues()));
			}
		}
	}

	/**
	 * Test that heaps with equal entry sets but different entry bags are not
	 * equal.
	 * <p>
	 * We test all heap types, pairwise.
	 */
	@Test
	public void testValueCollectionsAreNotEqualWithDifferentEntryBags()
	{
		for (int index = 0; index < heapTypes.length; index++)
		{
			for (int jindex = index; jindex < heapTypes.length; jindex++)
			{
				// create the two heap types.
				Heap<Integer, Integer> firstHeap = this
						.reflectivelyCreateHeap(heapTypes[index]);
				Heap<Integer, Integer> secondHeap = this
						.reflectivelyCreateHeap(heapTypes[jindex]);

				// get heaps with the different key sets but equal value sets.
				this.prepareHeapsWithDifferentEntryBags(firstHeap, secondHeap);

				// check with themselves.
				assertTrue("Value collection not equal to itself", firstHeap
						.getValues().equals(firstHeap.getValues()));
				assertTrue("Value collection equal to itself", secondHeap
						.getValues().equals(secondHeap.getValues()));

				// check them against each other.
				assertFalse(
						"Value collection with different entry collections are equal",
						firstHeap.getValues().equals(secondHeap.getValues()));
				assertFalse(
						"Value collection with different entry collections are equal",
						secondHeap.getValues().equals(firstHeap.getValues()));
			}
		}
	}

	/**
	 * Test the heaps with equal entry bags actually correct implement semantic
	 * equality.
	 * <p>
	 * We test this for all different heaps types, pairwise.
	 */
	@Test
	public void testEntryCollectionsAreEqual()
	{
		for (int index = 0; index < heapTypes.length; index++)
		{
			for (int jindex = index; jindex < heapTypes.length; jindex++)
			{
				// create the two heap types.
				Heap<Integer, Integer> firstHeap = this
						.reflectivelyCreateHeap(heapTypes[index]);
				Heap<Integer, Integer> secondHeap = this
						.reflectivelyCreateHeap(heapTypes[jindex]);

				// perpare to equal heaps.
				this.prepareTwoEqualHeaps(firstHeap, secondHeap);

				// check with themselves (this is should be O(1) because any
				// sane
				// implementation of
				// equals(Object) should do the cheap check for reference
				// equality).
				// In either case, this will should not fail...
				assertTrue("Heap not equal to itself", firstHeap.getEntries()
						.equals(firstHeap.getEntries()));
				assertTrue("Heap not equal to itself", secondHeap.getEntries()
						.equals(secondHeap.getEntries()));

				// check them against each other.
				assertTrue("Heaps with the same entry set are not equal",
						firstHeap.getEntries().equals(secondHeap.getEntries()));
				assertTrue("Heaps with the same entry set are not equal",
						secondHeap.getEntries().equals(firstHeap.getEntries()));
			}
		}
	}

	/**
	 * Test that heaps with different keys (but the same value set) are not
	 * equal.
	 * <p>
	 * We compare all heap types, parwise.
	 */
	@Test
	public void testEntryCollectionsAreNotEqualWithDifferentKeys()
	{
		for (int index = 0; index < heapTypes.length; index++)
		{
			for (int jindex = index; jindex < heapTypes.length; jindex++)
			{
				// create the two heap types.
				Heap<Integer, Integer> firstHeap = this
						.reflectivelyCreateHeap(heapTypes[index]);
				Heap<Integer, Integer> secondHeap = this
						.reflectivelyCreateHeap(heapTypes[jindex]);

				// get heaps with the different key sets but equal value sets.
				this.prepareHeapsWithDifferentKeys(firstHeap, secondHeap);

				// check with themselves.
				assertTrue("Heap not equal to itself", firstHeap.getEntries()
						.equals(firstHeap.getEntries()));
				assertTrue("Heap not equal to itself", secondHeap.getEntries()
						.equals(secondHeap.getEntries()));

				// check them against each other.
				assertFalse("Heaps with different key sets are equal",
						firstHeap.getEntries().equals(secondHeap.getEntries()));
				assertFalse("Heaps with different key sets are equal",
						secondHeap.getEntries().equals(firstHeap.getEntries()));
			}
		}
	}

	/**
	 * Test that heaps with different value sets (but equal key sets) are not
	 * equal.
	 * <p>
	 * Test for all types, pairwise.
	 */
	@Test
	public void testEntryCollectionsAreNotEqualWithDifferentValues()
	{
		for (int index = 0; index < heapTypes.length; index++)
		{
			for (int jindex = index; jindex < heapTypes.length; jindex++)
			{
				// create the two heap types.
				Heap<Integer, Integer> firstHeap = this
						.reflectivelyCreateHeap(heapTypes[index]);
				Heap<Integer, Integer> secondHeap = this
						.reflectivelyCreateHeap(heapTypes[jindex]);

				// get heaps with the different key sets but equal value sets.
				this.prepareHeapsWithDifferentValues(firstHeap, secondHeap);

				// check with themselves.
				assertTrue("Heap not equal to itself", firstHeap.getEntries()
						.equals(firstHeap.getEntries()));
				assertTrue("Heap not equal to itself", secondHeap.getEntries()
						.equals(secondHeap.getEntries()));

				// check them against each other.
				assertFalse(
						"Heaps with different value collections are not equal",
						firstHeap.getEntries().equals(secondHeap.getEntries()));
				assertFalse(
						"Heaps with different value collections are not equal",
						secondHeap.getEntries().equals(firstHeap.getEntries()));
			}
		}
	}

	/**
	 * Test that heaps with different entry sets are not equal.
	 * <p>
	 * We test all heap types, pairwise.
	 */
	@Test
	public void testEntryCollectionsAreNotEqualWithDifferentEntrySets()
	{
		for (int index = 0; index < heapTypes.length; index++)
		{
			for (int jindex = index; jindex < heapTypes.length; jindex++)
			{
				// create the two heap types.
				Heap<Integer, Integer> firstHeap = this
						.reflectivelyCreateHeap(heapTypes[index]);
				Heap<Integer, Integer> secondHeap = this
						.reflectivelyCreateHeap(heapTypes[jindex]);

				// get heaps with the different key sets but equal value sets.
				this.prepareHeapsWithDifferentEntrySets(firstHeap, secondHeap);

				// check with themselves.
				assertTrue("Heap not equal to itself", firstHeap.getEntries()
						.equals(firstHeap.getEntries()));
				assertTrue("Heap not equal to itself", secondHeap.getEntries()
						.equals(secondHeap.getEntries()));

				// check them against each other.
				assertFalse("Heaps with different key sets are equal",
						firstHeap.getEntries().equals(secondHeap.getEntries()));
				assertFalse("Heaps with different key sets are equal",
						secondHeap.getEntries().equals(firstHeap.getEntries()));
			}
		}
	}

	/**
	 * Test that heaps with equal entry sets but different entry bags are not
	 * equal.
	 * <p>
	 * We test all heap types, pairwise.
	 */
	@Test
	public void testEntryCollectionsAreNotEqualWithDifferentEntryBags()
	{
		for (int index = 0; index < heapTypes.length; index++)
		{
			for (int jindex = index; jindex < heapTypes.length; jindex++)
			{
				// create the two heap types.
				Heap<Integer, Integer> firstHeap = this
						.reflectivelyCreateHeap(heapTypes[index]);
				Heap<Integer, Integer> secondHeap = this
						.reflectivelyCreateHeap(heapTypes[jindex]);

				// get heaps with the different key sets but equal value sets.
				this.prepareHeapsWithDifferentEntryBags(firstHeap, secondHeap);

				// check with themselves.
				assertTrue("Heap not equal to itself", firstHeap.getEntries()
						.equals(firstHeap.getEntries()));
				assertTrue("Heap not equal to itself", secondHeap.getEntries()
						.equals(secondHeap.getEntries()));

				// check them against each other.
				assertFalse("Heaps with different key sets are equal",
						firstHeap.getEntries().equals(secondHeap.getEntries()));
				assertFalse("Heaps with different key sets are equal",
						secondHeap.getEntries().equals(firstHeap.getEntries()));
			}
		}
	}

	/**
	 * Prepare two heaps with the same entry sets.
	 * 
	 * @param firstHeap the first heap to fill.
	 * @param secondHeap the second heap to fill.
	 */
	private void prepareTwoEqualHeaps(final Heap<Integer, Integer> firstHeap,
			final Heap<Integer, Integer> secondHeap)
	{
		for (int index = 0; index < HEAP_SIZE; index++)
		{
			firstHeap.insert(index, index);
			secondHeap.insert(index, index);
		}
	}

	/**
	 * Take the specified heaps and seed them with different key/value pair
	 * (i.e.
	 * different entry sets).
	 * 
	 * @param firstHeap the first heap.
	 * @param secondHeap the second heap.
	 */
	private void prepareHeapsWithDifferentKeys(
			final Heap<Integer, Integer> firstHeap,
			final Heap<Integer, Integer> secondHeap)
	{
		for (int index = 0; index < HEAP_SIZE; index++)
		{
			firstHeap.insert(index, index);
			secondHeap.insert((index * HEAP_SIZE), index);
		}
	}

	/**
	 * Take the specified heaps and seed them with different key/value pair
	 * (i.e.
	 * different entry sets).
	 * 
	 * @param firstHeap the first heap.
	 * @param secondHeap the second heap.
	 */
	private void prepareHeapsWithDifferentValues(
			final Heap<Integer, Integer> firstHeap,
			final Heap<Integer, Integer> secondHeap)
	{
		for (int index = 0; index < HEAP_SIZE; index++)
		{
			firstHeap.insert(index, index);
			secondHeap.insert(index, (index * HEAP_SIZE));
		}
	}

	/**
	 * Take the specified heaps and seed them with different key/value pair
	 * (i.e.
	 * different entry sets).
	 * 
	 * @param firstHeap the first heap.
	 * @param secondHeap the second heap.
	 */
	private void prepareHeapsWithDifferentEntrySets(
			final Heap<Integer, Integer> firstHeap,
			final Heap<Integer, Integer> secondHeap)
	{
		for (int index = 0; index < HEAP_SIZE; index++)
		{
			firstHeap.insert((index * HEAP_SIZE), index);
			secondHeap.insert(index, (index * HEAP_SIZE));
		}
	}

	/**
	 * Prepare heaps that have the same number of elements, the same set of
	 * entries, but different <i>bags</i> of entries.
	 * 
	 * @param firstHeap the first heap.
	 * @param secondHeap the second heap.
	 */
	private void prepareHeapsWithDifferentEntryBags(
			final Heap<Integer, Integer> firstHeap,
			final Heap<Integer, Integer> secondHeap)
	{
		for (int index = 0; index < (HEAP_SIZE / 2); index++)
		{
			firstHeap.insert(1, 1);
			secondHeap.insert(2, 2);
		}

		for (int index = 0; index < HEAP_SIZE; index++)
		{
			firstHeap.insert(2, 2);
			secondHeap.insert(1, 1);
		}
	}

}
