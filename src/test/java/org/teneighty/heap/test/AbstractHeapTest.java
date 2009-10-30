/*
 * $Id$
 * 
 * Copyright (c) 2005-2009 Fran Lattanzio
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

package org.teneighty.heap.test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Assert;
import org.junit.Test;

import org.teneighty.heap.Heap;
import org.teneighty.heap.NaturalOrderComparator;


/**
 * Abstract base test case from which all other heap-related tests should
 * probably extend.
 * 
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public abstract class AbstractHeapTest
{


	/**
	 * Default heap size.
	 */
	private static final int DEFAULT_HEAP_SIZE = 1000;

	/**
	 * Default deletion percentage.
	 */
	private static final double DEFAULT_DELETION_PERCENTAGE = 0.5d;

	/**
	 * Default decrease key percentage.
	 */
	private static final double DEFAULT_DECREASEKEY_PERCENTAGE = 0.5d;

	/**
	 * Default percentage by which to decrease keys.
	 */
	private static final int DEFAULT_DECREASEKEY_AMOUNT = DEFAULT_HEAP_SIZE;

	/**
	 * Default use-random-data/operations flag.
	 */
	private static final boolean DEFAULT_USE_RANDOM = true;

	/**
	 * Triple comparator (for triples with <code>Integer</code> keys).
	 */
	private static final Comparator<KeyValueTriple<Integer, ?, ?>> tripleComparator;


	/**
	 * Class constructor.
	 */
	static
	{
		// create comparator for ALL instances to share. OK because instances of
		// this class
		// are completely stateless.
		NaturalOrderComparator<Integer> integerComparator = new NaturalOrderComparator<Integer>();
		tripleComparator = new KeyValueTripleComparator<Integer>( integerComparator );
	}


	/**
	 * Generically "clone" the specified object, via serialization.
	 * <p>
	 * This method will fail, with an Assert.assertion exception if the
	 * serializaton or deserialization fail.
	 * 
	 * @param <T> the type of the object to clone.
	 * @param objectToClone the object to clone
	 * @return a serial clone of the specified object.
	 */
	@SuppressWarnings( "unchecked" )
	protected static <T> T cloneViaSerialization( final T objectToClone )
	{
		try
		{
			// Write it out.
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream( baos );
			oos.writeObject( objectToClone );

			ObjectInputStream ois = new ObjectInputStream( new ByteArrayInputStream( baos.toByteArray() ) );

			// erased cast.
			T clone = (T)ois.readObject();

			return ( clone );
		}
		catch( final IOException ioe )
		{
			ioe.printStackTrace();
			Assert.fail( "Clone via serialization failed: " + ioe );
			return ( null );
		}
		catch( final ClassNotFoundException cnfe )
		{
			Assert.fail( "Should not happen: " + cnfe.getMessage() );
			return ( null );
		}
	}


	/**
	 * Check the specified heaps are equal to each other.
	 * <p>
	 * In addition to checking that the heaps are <code>equals()</code> equal to
	 * each other, we verify that their key collections, value collections, and
	 * entry collections are also equal.
	 * 
	 * @param heapOne the first heap.
	 * @param heapTwo the second heap.
	 */
	protected static void assertHeapsAreEqual(
			final Heap<Integer, Integer> heapOne, final Heap<Integer, Integer> heapTwo )
	{
		// do some simple, but dumb, checks.

		Assert.assertEquals( "Heaps don't contain the same number of elements", heapOne.getSize(), heapTwo.getSize() );
		Assert.assertEquals( "Heaps don't have same isEmpty value", heapOne.isEmpty(), heapTwo.isEmpty() );

		// make sure heaps are semantically equal.
		Assert.assertTrue( "First heap not equal to second", heapOne.equals( heapTwo ) );
		Assert.assertTrue( "Second heap not equal to first", heapTwo.equals( heapOne ) );

		// now check the key, value, and entry collections.
		Assert.assertTrue( "First heap key collection not equal to second key collection", heapOne.getKeys().equals( heapTwo.getKeys() ) );
		Assert.assertTrue( "Second heap key collection not equal to first key collection", heapTwo.getKeys().equals( heapOne.getKeys() ) );
		Assert.assertTrue( "First heap value collection not equal to second value collection", heapOne.getValues().equals( heapTwo.getValues() ) );
		Assert.assertTrue( "Second heap value collection not equal to first value collection", heapTwo.getValues().equals( heapOne.getValues() ) );
		Assert.assertTrue( "First heap entry collection not equal to second entry collection", heapOne.getEntries().equals( heapTwo.getEntries() ) );
		Assert.assertTrue( "Second heap entry collection not equal to first entry collection", heapTwo.getEntries().equals( heapOne.getEntries() ) );

		// well, they're probably equal...
	}


	/**
	 * Check the specified heap contains the keys, values, and heap entries in the
	 * specified array of triples.
	 * <p>
	 * This will fail with an Assert.assertion exception (effectively) if any of
	 * the following are true:
	 * <ul>
	 * <li>The specified heap doesn't contain the same number of elements as the
	 * array <code>keysAndValues</code>.</li>
	 * <li>The order of the keys, values, and heap entries extract from the heap
	 * is not the same as the order in <code>keysAndValues</code>.</li>
	 * <li>The specified heap is not empty, and has a size of zero, by the time
	 * we've extracted all elements from it.</li>
	 * </ul>
	 * 
	 * @param heap the heap to check.
	 * @param keysAndValues the set of keys/value/Heap.entry triples.
	 */
	protected static void assertHeapIsEqualTo(
			final Heap<Integer, Integer> heap,
			final KeyValueTriple<Integer, Integer, Heap.Entry<Integer, Integer>>[] keysAndValues )
	{
		assertHeapIsEqualTo( heap, keysAndValues, true );
	}


	/**
	 * Check the specified heap contains the keys, values, and heap entries in the
	 * specified array of triples.
	 * <p>
	 * This will fail with an Assert.assertion exception (effectively) if any of
	 * the following are true:
	 * <ul>
	 * <li>The specified heap doesn't contain the same number of elements as the
	 * array <code>keysAndValues</code>.</li>
	 * <li>The order of the keys, values, and heap entries extract from the heap
	 * is not the same as the order in <code>keysAndValues</code>.</li>
	 * <li>The specified heap is not empty, and has a size of zero, by the time
	 * we've extracted all elements from it.</li>
	 * </ul>
	 * 
	 * @param heap the heap to check.
	 * @param keysAndValues the set of keys/value/Heap.entry triples.
	 * @param checkEntryReferentialEquality whther to check/enforce referential
	 *        equality for heap entries. Note that we also check for semantic
	 *        equality.
	 */
	protected static void assertHeapIsEqualTo(
			final Heap<Integer, Integer> heap,
			final KeyValueTriple<Integer, Integer, Heap.Entry<Integer, Integer>>[] keysAndValues,
			final boolean checkEntryReferentialEquality )
	{
		// sort the set of triples. This sorts by key, and we know/assume that the
		// keys are
		// unique...
		Arrays.sort( keysAndValues, tripleComparator );

		Assert.assertEquals( "Incorrect size", keysAndValues.length, heap.getSize() );

		Heap.Entry<Integer, Integer> minimum;
		Heap.Entry<Integer, Integer> extractedMinimum;

		for( int index = 0; index < keysAndValues.length; index++ )
		{
			// grab the minimum (but don't extract it yet).
			minimum = heap.getMinimum();

			// make sure the key and value are equal the specified index in the key
			// triple.
			Assert.assertEquals( "Incorrect key at " + index, minimum.getKey(), keysAndValues[ index ].getKey() );
			Assert.assertEquals( "Incorrect value at " + index, minimum.getValue(), keysAndValues[ index ].getFirstValue() );

			// make sure the entries are equal.
			if( checkEntryReferentialEquality == true )
			{
				Assert.assertTrue( "Heap entries not semantically equal at " + index, minimum == keysAndValues[ index ].getSecondValue() );
			}

			// check that they're semantically equal.
			Assert.assertTrue( "Heap entries not semantically equal at " + index, minimum.equals( keysAndValues[ index ].getSecondValue() ) );

			// make sure the heap both holds and contains the minimum.
			Assert.assertTrue( "Heap doesn't hold minimum", heap.holdsEntry( minimum ) );
			Assert.assertTrue( "Heap doesn't contain minimum", heap.containsEntry( minimum ) );

			// now, extract the minimum
			extractedMinimum = heap.extractMinimum();

			// make sure the extracted minimum is equal to the previously gotten
			// minimum.
			Assert.assertTrue( "Extract min not referentially equal to minimum at "
					+ index, minimum == extractedMinimum );
			Assert.assertTrue( "Extract min not semantically equal to minimum at "
					+ index, minimum.equals( extractedMinimum ) );

			// make sure the heap nither holds nor contains neither the minimum nor
			// the extracted minimum.
			Assert.assertFalse( "Heap still holds minimum at " + index, heap.holdsEntry( minimum ) );
			Assert.assertFalse( "Heap still contains minimum at " + index, heap.containsEntry( minimum ) );
			Assert.assertFalse( "Heap still holds extracted minimun at " + index, heap.holdsEntry( extractedMinimum ) );
			Assert.assertFalse( "Heap still contains extracted minimum at " + index, heap.containsEntry( extractedMinimum ) );
		}

		// now, make sure that the specified heap is EMPTY!
		Assert.assertEquals( "Heap size is not zero", 0, heap.getSize() );
		Assert.assertTrue( "Heap not empty", heap.isEmpty() );
	}


	/**
	 * Remove the nulls from the specified arrays; return (possibly shortened)
	 * array.
	 * 
	 * @param <K> the key type.
	 * @param <V> the first value type.
	 * @param <W> the second value type.
	 * @param inputArray the input array.
	 * @return a shorted array, with nulls removed.
	 */
	@SuppressWarnings( "unchecked" )
	protected static <K, V, W> KeyValueTriple<K, V, W>[] removeNullElements(
			final KeyValueTriple<K, V, W>[] inputArray )
	{
		// count the number of non-null entries.
		int count = 0;
		for( int index = 0; index < inputArray.length; index++ )
		{
			if( inputArray[ index ] != null )
			{
				count++;
			}
		}

		// alloc new array; this is an erased cast.
		KeyValueTriple<K, V, W>[] arrayWithNoNulls = new KeyValueTriple[ count ];

		count = 0;
		for( int index = 0; index < inputArray.length; index++ )
		{
			if( inputArray[ index ] != null )
			{
				arrayWithNoNulls[ ( count++ ) ] = inputArray[ index ];
			}
		}

		return ( arrayWithNoNulls );
	}


	/**
	 * Append/merge the specified arrays - we just return a new array that
	 * contains all the elements in the arrays <code>firstArray</code> and
	 * <code>secondArray</code>.
	 * 
	 * @param <K> the key type.
	 * @param <V> the first value type.
	 * @param <W> the second value type.
	 * @param firstArray the first array.
	 * @param secondArray the second array.
	 * @return the merged/appended array.
	 */
	@SuppressWarnings( "unchecked" )
	protected static <K, V, W> KeyValueTriple<K, V, W>[] appendArrays(
			final KeyValueTriple<K, V, W>[] firstArray,
			final KeyValueTriple<K, V, W>[] secondArray )
	{
		int totalLength = firstArray.length + secondArray.length;

		// alloc new array; this is an erased cast.
		KeyValueTriple<K, V, W>[] newArray = new KeyValueTriple[ totalLength ];

		for( int index = 0; index < newArray.length; index++ )
		{
			if( index < firstArray.length )
			{
				newArray[ index ] = firstArray[ index ];
			}
			else
			{
				newArray[ index ] = secondArray[ ( index - firstArray.length ) ];
			}
		}

		return ( newArray );
	}


	/**
	 * A source of randomness for all to use!
	 */
	protected final Random random;


	/**
	 * Constructor.
	 */
	protected AbstractHeapTest()
	{
		// create our source 'o' randomness.
		this.random = new Random( System.currentTimeMillis() );
	}


	/**
	 * Test for inserting stuff into a heap, then just making sure that it
	 * contains the specified values that we inserted.
	 * <p>
	 * We do many, many more checks to ensure the integrity of the heap along the
	 * way.
	 */
	@Test
	public final void testInsert()
	{
		// get a new heap.
		Heap<Integer, Integer> heap = this.newHeap();

		// load it!
		KeyValueTriple<Integer, Integer, Heap.Entry<Integer, Integer>>[] loadedValues = this.loadHeap( heap, this.getInsertSize() );

		// make sure that everything is equal.
		assertHeapIsEqualTo( heap, loadedValues );
	}


	/**
	 * Test the serialization mechanism.
	 * <p>
	 * Basically, we just make a new heap, load it up with a whole pile of values,
	 * "clone" it via serialization, make sure the "cloned" heap and the original
	 * heap are equal, and that both the original and cloned heap contain the same
	 * set of entries.
	 */
	@Test
	public final void testSerialization()
	{
		// create a new heap and stuff.
		Heap<Integer, Integer> heap = this.newHeap();

		// load the heap with a bunch of garbage.
		KeyValueTriple<Integer, Integer, Heap.Entry<Integer, Integer>>[] triples = this.loadHeap( heap, this.getSerializationSize() );

		// clone the specified heap, via serialization.
		Heap<Integer, Integer> serialClone = cloneViaSerialization( heap );

		// make sure the heaps are equal.
		assertHeapsAreEqual( heap, serialClone );

		// finally, put both heaps through the equality method.
		assertHeapIsEqualTo( heap, triples, true );
		assertHeapIsEqualTo( serialClone, triples, false );
	}


	/**
	 * Test the delete method.
	 */
	@Test
	public final void testDelete()
	{
		// first, create a new heap.
		Heap<Integer, Integer> heap = this.newHeap();

		// now, insert a huge pile of entries into it.
		KeyValueTriple<Integer, Integer, Heap.Entry<Integer, Integer>>[] triples = this.loadHeap( heap, this.getDeleteSize() );

		// we now remove a certain percentage of them...
		double percentage = this.getDeletionPercentage();

		// make sure percentage is valid.
		Assert.assertTrue( "Invalid deletion percentage", ( percentage > 0 )
				&& ( percentage < 1 ) );

		// tmp variable.
		Heap.Entry<Integer, Integer> entryToDelete;

		// the count to remove in the deterministic case.
		int countToRemove = (int)( percentage * this.getDeleteSize() );
		int oldSize;
		for( int index = 0; index < triples.length; index++ )
		{

			if( this.getRandomFlag() == true )
			{

				// randomly decide if we should remove. Note that nextDouble() returns a
				// double uniformly distributed between 0 and 1...
				if( this.random.nextDouble() < percentage )
				{
					entryToDelete = triples[ index ].getSecondValue();
				}
				else
				{
					// not deleting this one...
					continue;
				}
			}
			else
			{
				// deterministically remove first couple 'o' entries.
				if( index < countToRemove )
				{
					entryToDelete = triples[ index ].getSecondValue();
				}
				else
				{
					// no need to delete this node - in fact, we can break out of this
					// entire stupid loop.
					break;
				}
			}

			// we'll make sure size goes down by exactly one.
			oldSize = heap.getSize();

			// make sure we both hold and contain the specified entry.
			Assert.assertTrue( "Heap doesn't hold entry to delete", heap.holdsEntry( entryToDelete ) );
			Assert.assertTrue( "Heap doesn't contain entry to delete", heap.containsEntry( entryToDelete ) );

			// actually remove from the heap.
			heap.delete( entryToDelete );

			// ensure size decrease of 1.
			Assert.assertEquals( "Heap size didn't decrease by 1", ( oldSize - 1 ), heap.getSize() );

			// make sure we no longer hold or contain, of course.
			Assert.assertFalse( "Heap still holds entry after delete", heap.holdsEntry( entryToDelete ) );
			Assert.assertFalse( "Heap still contains entry after delete", heap.containsEntry( entryToDelete ) );

			// mark as removed.
			triples[ index ] = null;
		}

		// finally, re-jigger the triples array so that it contains only non-null
		// entries (we've marked all the deleted entries as null). Then, we make
		// sure the remaining elements of the heap are equal to the entries in the
		// specified triple array.
		triples = removeNullElements( triples );
		assertHeapIsEqualTo( heap, triples, true );
	}


	/**
	 * Test the heap in a for-each construct.
	 * <p>
	 * Doubles as a test of the <code>iterator()</code> method, as well as the
	 * <code>containsEntry</code> and <code>holdsEntry</code> methods.
	 */
	@Test( )
	public final void testForeach()
	{
		// create a new heap and load it with some junk.
		Heap<Integer, Integer> newHeap = this.newHeap();
		this.loadHeap( newHeap, this.getForeachSize() );

		for( Heap.Entry<Integer, Integer> entry : newHeap )
		{
			// make sure heap both hold and contains the specified entry.
			Assert.assertTrue( "Heap doesn't hold element returned from foreach", newHeap.holdsEntry( entry ) );
			Assert.assertTrue( "Heap doesn't contain element returned from foreach", newHeap.containsEntry( entry ) );

			// now, check the entry collection view contains the specified entry.
			Assert.assertTrue( "Heap entry view doesn't contain elements", newHeap.getEntries().contains( entry ) );
		}
	}


	/**
	 * Test the decrease key method.
	 */
	@Test( )
	public final void testDecreaseKey()
	{
		// get a new heap and load it with some data.
		Heap<Integer, Integer> heap = this.newHeap();
		KeyValueTriple<Integer, Integer, Heap.Entry<Integer, Integer>>[] triples = this.loadHeap( heap, this.getDecreaseKeySize() );

		// get and check percentage to decrease.
		double percentage = this.getDecreaseKeyPercentage();
		Assert.assertTrue( "Invalid decrease key percentage", ( percentage > 0 )
				&& ( percentage < 1 ) );

		// the entry whose key we will decrease.
		Heap.Entry<Integer, Integer> entryToDecrease;

		// we enforce that decrease keys doesn't change the heap size.
		final int heapSize = heap.getSize();
		final int entriesToDecrease = (int)( percentage * heapSize );

		// some stupid temp vars.
		int newKey;
		int oldKey;
		int decreaseAmount = this.getDecreaseKeyAmount();


		// now it's time for some key decreasin'!
		for( int index = ( triples.length - 1 ); index >= 0; index-- )
		{
			if( this.getRandomFlag() == true )
			{

				// randomly decide if we should decrease key.
				if( this.random.nextDouble() < percentage )
				{
					entryToDecrease = triples[ index ].getSecondValue();
				}
				else
				{
					continue;
				}

			}
			else
			{
				// deterministically decrease the largest entry (we assume that we also
				// inserted deterministic data into this heap).
				if( ( triples.length - index ) < entriesToDecrease )
				{
					entryToDecrease = triples[ index ].getSecondValue();
				}
				else
				{
					break;
				}

			}

			// make sure we still hold and contain and the specified key.
			Assert.assertTrue( "Heap doesn't contain entry to decrease", heap.holdsEntry( entryToDecrease ) );
			Assert.assertTrue( "Heap doesn't contain entry to decrease", heap.containsEntry( entryToDecrease ) );

			// compute new key and actually decrease the value.
			oldKey = entryToDecrease.getKey();

			if( this.getRandomFlag() == true )
			{
				newKey = oldKey - ( this.random.nextInt( decreaseAmount ) + 1 );
			}
			else
			{
				newKey = oldKey - decreaseAmount;
			}

			// finally, decrease the key...
			heap.decreaseKey( entryToDecrease, newKey );

			// ... and make new triple entry.
			triples[ index ] = new KeyValueTriple<Integer, Integer, Heap.Entry<Integer, Integer>>( newKey, entryToDecrease.getValue(), entryToDecrease );

			// make sure the key value gets propogated through to entry.
			Assert.assertEquals( "Decrease key didn't propogate value through to entry", entryToDecrease.getKey().intValue(), newKey );

			// make sure the heap size hasn't changed.
			Assert.assertEquals( "Heap size changed as a result of decrease key", heapSize, heap.getSize() );

			// make sure we still hold and contain and the specified key.
			Assert.assertTrue( "Heap no longer holds after decrease key", heap.holdsEntry( entryToDecrease ) );
			Assert.assertTrue( "Heap no longer contains after decrease key", heap.containsEntry( entryToDecrease ) );
		}

		// ensure the heap is equal to the specified set of triples.
		assertHeapIsEqualTo( heap, triples );
	}


	/**
	 * Test the union method.
	 */
	@Test( )
	public final void testUnion()
	{
		// create two heaps.
		Heap<Integer, Integer> firstHeap = this.newHeap();
		Heap<Integer, Integer> secondHeap = this.newHeap();

		// fill them with DETERMINISTIC data, so that we know we don't have
		// intersecting
		// key sets.
		KeyValueTriple<Integer, Integer, Heap.Entry<Integer, Integer>>[] firstEntries = this.loadHeap( firstHeap, this.getUnionSize(), false, 0 );
		KeyValueTriple<Integer, Integer, Heap.Entry<Integer, Integer>>[] secondEntries = this.loadHeap( secondHeap, this.getUnionSize(), false, this.getUnionSize() );

		// compute target size.
		final int targetSize = firstEntries.length + secondEntries.length;

		// union the first heap into the second heap.
		firstHeap.union( secondHeap );

		// make sure that the second heap is empty.
		Assert.assertTrue( "Unioned heap not empty", secondHeap.isEmpty() );
		Assert.assertEquals( "Unioned heap has non-zero size", 0, secondHeap.getSize() );

		// make sure that the second heap does not hold or contain any entries.
		for( int index = 0; index < secondEntries.length; index++ )
		{
			Assert.assertFalse( "Unioned heap still holds entries", secondHeap.holdsEntry( secondEntries[ index ].getSecondValue() ) );
			Assert.assertFalse( "Unioned heap still contains entries", secondHeap.containsEntry( secondEntries[ index ].getSecondValue() ) );
		}

		// make sure the first heap has the right size and all that jazz.
		Assert.assertEquals( "Target heap has incorrect size", targetSize, firstHeap.getSize() );

		// now, make sure it both holds and contains all the appropiate entries.
		// we just assume that two heaps were the same size.
		for( int index = 0; index < firstEntries.length; index++ )
		{
			Assert.assertTrue( "Tatget heap doesn't hold entry", firstHeap.holdsEntry( firstEntries[ index ].getSecondValue() ) );
			Assert.assertTrue( "Tatget heap doesn't hold entry", firstHeap.holdsEntry( secondEntries[ index ].getSecondValue() ) );

			// check containment as well.
			Assert.assertTrue( "Tatget heap doesn't contain entry", firstHeap.containsEntry( firstEntries[ index ].getSecondValue() ) );
			Assert.assertTrue( "Tatget heap doesn't contain entry", firstHeap.containsEntry( secondEntries[ index ].getSecondValue() ) );
		}

		// merge the entry sets.
		KeyValueTriple<Integer, Integer, Heap.Entry<Integer, Integer>>[] merged = appendArrays( firstEntries, secondEntries );

		// make sure the second heap is equal.
		assertHeapIsEqualTo( firstHeap, merged );
	}


	/**
	 * Test the insert all method.
	 */
	@Test( )
	public final void testInsertAll()
	{
		// create two heaps.
		Heap<Integer, Integer> firstHeap = this.newHeap();
		Heap<Integer, Integer> secondHeap = this.newHeap();

		// fill them with DETERMINISTIC data, so that we know we don't have
		// intersecting
		// key sets.
		KeyValueTriple<Integer, Integer, Heap.Entry<Integer, Integer>>[] firstEntries = this.loadHeap( firstHeap, this.getUnionSize(), false, 0 );
		KeyValueTriple<Integer, Integer, Heap.Entry<Integer, Integer>>[] secondEntries = this.loadHeap( secondHeap, this.getUnionSize(), false, this.getUnionSize() );

		// compute target size.
		final int targetSize = firstEntries.length + secondEntries.length;

		// union the first heap into the second heap.
		firstHeap.insertAll( secondHeap );

		// make sure that the second heap has the same size.
		Assert.assertEquals( "Inserting all changed source heap", secondEntries.length, secondHeap.getSize() );

		// make sure that the second heap still holds and contains all entries.
		for( int index = 0; index < secondEntries.length; index++ )
		{
			Assert.assertTrue( "Inserted from heap doesn't still holds entries", secondHeap.holdsEntry( secondEntries[ index ].getSecondValue() ) );
			Assert.assertTrue( "Inserted from heap doesn't still contains entries", secondHeap.containsEntry( secondEntries[ index ].getSecondValue() ) );
		}

		// make sure the first heap has the right size and all that jazz.
		Assert.assertEquals( "Target heap has incorrect size", targetSize, firstHeap.getSize() );

		// we have to contain all entries, but hold only those from the original...
		for( int index = 0; index < firstEntries.length; index++ )
		{
			Assert.assertTrue( "Target heap doesn't hold entry", firstHeap.holdsEntry( firstEntries[ index ].getSecondValue() ) );
			Assert.assertFalse( "Target heap incorrectly holds entry from inserted heap", firstHeap.holdsEntry( secondEntries[ index ].getSecondValue() ) );

			// check containment as well.
			Assert.assertTrue( "Tatget heap doesn't contain entry", firstHeap.containsEntry( firstEntries[ index ].getSecondValue() ) );
			Assert.assertTrue( "Tatget heap doesn't contain entry", firstHeap.containsEntry( secondEntries[ index ].getSecondValue() ) );
		}

		// merge the entry sets.
		KeyValueTriple<Integer, Integer, Heap.Entry<Integer, Integer>>[] merged = appendArrays( firstEntries, secondEntries );

		// make sure the second heap is equal, without referential equality for
		// entries (this is general contract of insertAll).
		assertHeapIsEqualTo( firstHeap, merged, false );
	}


	/**
	 * Get the default heap size; applies to all test, unless you override the
	 * per-test size getter methods.
	 * 
	 * @return the default heap size.
	 */
	protected int getDefaultHeapSize()
	{
		return ( DEFAULT_HEAP_SIZE );
	}


	/**
	 * Get the size to use for the insert test.
	 * 
	 * @return the insert test size.
	 */
	protected int getInsertSize()
	{
		return ( this.getDefaultHeapSize() );
	}


	/**
	 * Get the size of the heap we should use for serialization.
	 * 
	 * @return the serialization test size.
	 */
	protected int getSerializationSize()
	{
		return ( this.getDefaultHeapSize() );
	}


	/**
	 * Get the size of the heap to use for the delete test.
	 * 
	 * @return the delete test size.
	 */
	protected int getDeleteSize()
	{
		return ( this.getDefaultHeapSize() );
	}


	/**
	 * Get the size of the heap to use for foreach/iteration test.
	 * 
	 * @return the foreach heap size.
	 */
	protected int getForeachSize()
	{
		return ( this.getDefaultHeapSize() );
	}


	/**
	 * Get the size of the heap to use for the decrease key tests.
	 * 
	 * @return the heap size.
	 */
	protected int getDecreaseKeySize()
	{
		return ( this.getDefaultHeapSize() );
	}


	/**
	 * Get the size of the heaps to use for the union test.
	 * 
	 * @return int the heap size.
	 */
	protected int getUnionSize()
	{
		return ( this.getDefaultHeapSize() );
	}


	/**
	 * Get the deletion percentage.
	 * <p>
	 * The returned number must be in <code>(0,1)</code>.
	 * 
	 * @return the deletion percentage.
	 */
	protected double getDeletionPercentage()
	{
		return ( DEFAULT_DELETION_PERCENTAGE );
	}


	/**
	 * Get the decrease key percentage.
	 * <p>
	 * The returned number must be in <code>(0,1)</code>.
	 * 
	 * @return the percentage of entries on which to call <code>decreaseKey</code>.
	 */
	protected double getDecreaseKeyPercentage()
	{
		return ( DEFAULT_DECREASEKEY_PERCENTAGE );
	}


	/**
	 * Get the decrease key amount.
	 * 
	 * @return the amount by which to decrease key.
	 */
	protected int getDecreaseKeyAmount()
	{
		return ( DEFAULT_DECREASEKEY_AMOUNT );
	}


	/**
	 * Get the default use random flag.
	 * 
	 * @return the use random flag.
	 */
	protected boolean getRandomFlag()
	{
		return ( DEFAULT_USE_RANDOM );
	}


	/**
	 * Load the specified heap with the specified number of entries.
	 * 
	 * @param heapToLoad the heap to load.
	 * @param size the number of elements to load into the heap.
	 * @return Array of triples.
	 */
	protected final KeyValueTriple<Integer, Integer, Heap.Entry<Integer, Integer>>[] loadHeap(
			final Heap<Integer, Integer> heapToLoad, final int size )
	{
		return ( this.loadHeap( heapToLoad, size, this.getRandomFlag() ) );
	}


	/**
	 * Load the specified heap with the specified number of entries.
	 * 
	 * @param heapToLoad the heap to load.
	 * @param size the number of elements to load into the heap.
	 * @param useRandom whether to use random keys and values.
	 * @return Array of triples.
	 */
	protected final KeyValueTriple<Integer, Integer, Heap.Entry<Integer, Integer>>[] loadHeap(
			final Heap<Integer, Integer> heapToLoad, final int size,
			final boolean useRandom )
	{
		return ( this.loadHeap( heapToLoad, size, useRandom, 0 ) );
	}


	/**
	 * Load the specified heap with the specified number of entries.
	 * 
	 * @param heapToLoad the heap to load.
	 * @param size the number of elements to load into the heap.
	 * @param useRandom whether to use random keys and values.
	 * @param keyOffset offset/minimum for key values; applies only if not using
	 *        random keys and values.
	 * @return Array of triples.
	 * @throws IllegalArgumentException If <code>keyStart</code> is negative.
	 */
	@SuppressWarnings( "unchecked" )
	protected final KeyValueTriple<Integer, Integer, Heap.Entry<Integer, Integer>>[] loadHeap(
			final Heap<Integer, Integer> heapToLoad, final int size,
			final boolean useRandom, int keyOffset )
		throws IllegalArgumentException
	{
		if( keyOffset < 0 )
		{
			throw new IllegalArgumentException();
		}

		// make stupid array of triples - note that Java won't let you actually
		// allocate
		// arrays of generic types... hence the unchecked suppression up top.
		KeyValueTriple<Integer, Integer, Heap.Entry<Integer, Integer>>[] triples = new KeyValueTriple[ size ];

		Integer key;
		Integer value;
		Heap.Entry<Integer, Integer> newEntry;
		Map<Integer, Object> keysAlreadyUsed = new HashMap<Integer, Object>();

		for( int index = 0; index < size; index++ )
		{
			if( useRandom == true )
			{
				// generate a random, but unique, key.
				do
				{
					key = this.random.nextInt();
				}
				while( keysAlreadyUsed.containsKey( key ) );

				// record the key we generated.
				keysAlreadyUsed.put( key, null );

				// finally, generate a random value.
				value = this.random.nextInt();
			}
			else
			{
				key = index + keyOffset;
				value = index;
			}

			// actually insert into the heap.
			newEntry = heapToLoad.insert( key, value );

			// now, store in stupid array.
			triples[ index ] = new KeyValueTriple<Integer, Integer, Heap.Entry<Integer, Integer>>( key, value, newEntry );
		}

		// clear stupid map, perhaps to aid the garbage collector.
		keysAlreadyUsed.clear();

		return ( triples );
	}


	/**
	 * Get a new, empty heap.
	 * <p>
	 * This method delegates to <code>getHeapCore()</code> and will fail, with
	 * an asseration exception, if the heap returned from
	 * <code>getHeapCore()</code> isn't empty and doesn't have a
	 * <code>null</code> comparator.
	 * 
	 * @return a new, empty heap.
	 */
	private Heap<Integer, Integer> newHeap()
	{
		// get heap impl. from subclass.
		Heap<Integer, Integer> heap = this.newHeapCore();

		// make sure that we're using the natural ordering (for our purposes
		// that heap is null).
		Assert.assertTrue( "Not using natural ordering", heap.getComparator() == null );

		// make sure the guy is empty.
		Assert.assertTrue( "Heap isn't empty", heap.isEmpty() );

		return ( heap );
	}


	/**
	 * Create a new, empty heap, with both integer keys and values, that uses the
	 * keys' (i.e. <code>Integer</code>'s) <i>natural ordering</i>.
	 * 
	 * @return a new, empty heap.
	 */
	protected abstract Heap<Integer, Integer> newHeapCore();


}
