/*
 * $Id$
 * 
 * Copyright (c) 2005, 2006 Fran Lattanzio
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

import junit.framework.TestCase;

import org.teneighty.heap.BinaryHeap;
import org.teneighty.heap.BinomialHeap;
import org.teneighty.heap.FibonacciHeap;
import org.teneighty.heap.Heap;
import org.teneighty.heap.LeftistHeap;
import org.teneighty.heap.PairingHeap;

import java.util.Random;
import java.util.Arrays;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**
 * The main class for testing heaps. This class test all the major public
 * methods of the heap class, as well as checking the validity of their
 * collection view objects, equals and hashcode implementation, and
 * serialization mechanisms.
 * <p>
 * It does <i>not</i> examine heap performance characteristics; see
 * <code>PerformanceTest</code>.
 * 
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
@SuppressWarnings( "unchecked" )
public class HeapTest
	extends TestCase
{


	/**
	 * Size to test.
	 */
	private static final int SIZE = 500;

	/**
	 * Equals size to test.
	 */
	private static final int EQUAL_SIZE = 150;

	/**
	 * Times to repeat tests.
	 */
	private static final int TIMES = 25;

	/**
	 * Use random numbers?
	 */
	private static final boolean USE_RANDOM = true;


	/**
	 * Constructor.
	 * 
	 * @param name the name.
	 */
	public HeapTest( final String name )
	{
		super( name );

		// Use even test size.
		assertTrue( "Use an even test size...", ( SIZE % 2 == 0 ) );
	}


	/**
	 * Test Fibonacci heap.
	 */
	public void testFibonacci()
	{
		doAll( new FibonacciHeap<Integer, Integer>() );
	}


	/**
	 * Test binary heap.
	 */
	public void testBinary()
	{
		doAll( new BinaryHeap<Integer, Integer>() );
	}


	/**
	 * Test the leftist heap.
	 */
	public void testLeftist()
	{
		doAll( new LeftistHeap<Integer, Integer>() );
	}


	/**
	 * Test the binomial heap.
	 */
	public void testBinomial()
	{
		doAll( new BinomialHeap<Integer, Integer>() );
	}


	/**
	 * Test the pairing heap.
	 * <p>
	 * Test both the multi pass and two pass strategies...
	 */
	public void testPairing()
	{
		doAll( new PairingHeap<Integer, Integer>( PairingHeap.MergeStrategy.MULTI ) );
		doAll( new PairingHeap<Integer, Integer>( PairingHeap.MergeStrategy.TWO ) );
	}


	/**
	 * Test equals.
	 */
	public void testEquals()
	{
		doEquals();
	}


	/**
	 * Do equal.
	 */
	private void doEquals()
	{
		for( int kindex = 0; kindex < TIMES; kindex++ )
		{
			final int count = 5;
			Heap<Integer, Integer> h1 = null;
			Heap<Integer, Integer> h2 = null;

			for( int index = 0; index < count; index++ )
			{
				for( int jindex = 0; jindex < count; jindex++ )
				{
					h1 = createHeapType( index );
					h2 = createHeapType( jindex );

					doEqual( h1, h2 );
				}
			}
		}
	}


	/**
	 * Create heap for type.
	 * 
	 * @param type the "type"
	 * @return Heap{@literal <Integer, Integer>} a brand spanking new heap.
	 */
	private Heap<Integer, Integer> createHeapType( final int type )
	{
		switch( type )
		{
		case ( 0 ):
			return ( new BinaryHeap<Integer, Integer>() );
		case ( 1 ):
			return ( new FibonacciHeap<Integer, Integer>() );
		case ( 2 ):
			return ( new LeftistHeap<Integer, Integer>() );
		case ( 3 ):
			return ( new BinomialHeap<Integer, Integer>() );
		case ( 4 ):
			// Merge stategy obviously doesn't affect this...
			return ( new PairingHeap<Integer, Integer>() );
		default:
			assertTrue( "Unknown type " + type, false );
			return ( null );
		}
	}


	/**
	 * Run a bunch of tests for the specified heaps.
	 * 
	 * @param heap the heap to test.
	 */
	private void doAll( final Heap<Integer, Integer> heap )
	{
		for( int index = 0; index < TIMES; index++ )
		{
			// Check insert.
			doInsert( heap );

			// Check decrease key.
			doDecreaseKey( heap );

			// Check delete.
			doDelete( heap );

			// Do serial test.
			doSerial( heap );
			heap.clear();

			// Union test.
			doUnion( heap );

			heap.clear();
		}
	}


	/**
	 * Union test.
	 * <p>
	 * We also inadvertantly test the serialization mechanisms for the specified
	 * heap.
	 * 
	 * @param heap the heap to test.
	 */
	private void doUnion( final Heap<Integer, Integer> heap )
	{
		Integer[] array = makeArray( EQUAL_SIZE );
		loadHeap( heap, array );

		// Create "clone" via serialization...
		Heap<Integer, Integer> clone = serialClone( heap );

		Integer[] stuff = new Integer[ ( EQUAL_SIZE * 2 ) ];
		for( int index = 0, jindex = 0; index < array.length; index++ )
		{
			stuff[ ( jindex++ ) ] = array[ index ];
			stuff[ ( jindex++ ) ] = array[ index ];
		}

		heap.union( clone );
		checkEqual( heap, stuff );
	}


	/**
	 * Serial clone.
	 * 
	 * @param heap the heap to clone via serialization trickeration.
	 * @return Heap{@literal <Integer,Integer>} a clone of the specified heap.
	 */
	private Heap<Integer, Integer> serialClone( final Heap<Integer, Integer> heap )
	{
		try
		{
			// Write it out.
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream( baos );
			oos.writeObject( heap );

			ObjectInputStream ois = new ObjectInputStream( new ByteArrayInputStream( baos.toByteArray() ) );
			Heap<Integer, Integer> clone = (Heap<Integer, Integer>)ois.readObject();

			return ( clone );
		}
		catch( final IOException ioe )
		{
			ioe.printStackTrace();
			assertTrue( "Serial test failed: " + ioe.getMessage(), false );
			return ( null );
		}
		catch( final ClassNotFoundException cnfe )
		{
			assertTrue( "Should not happen", false );
			return ( null );
		}
	}


	/**
	 * Do serialization test.
	 * 
	 * @param heap the heap to test.
	 */
	private void doSerial( final Heap<Integer, Integer> heap )
	{
		Integer[] array = makeArray( EQUAL_SIZE );
		loadHeap( heap, array );

		Heap<Integer, Integer> clone = serialClone( heap );

		// Do some checks...
		assertTrue( "Not equal", clone.equals( heap ) );
		assertTrue( "Not eqaul", heap.equals( clone ) );
		assertTrue( "Bad hashcode", clone.hashCode() == heap.hashCode() );

		// Pretty much has to pass.
		checkEqual( clone, array );
	}


	/**
	 * Check delete.
	 * 
	 * @param heap the heap to test.
	 */
	private void doDelete( final Heap<Integer, Integer> heap )
	{
		// Load the damn heap.
		Integer[] keys = makeArray();
		Heap.Entry[] entries = loadHeap( heap, keys );

		// Delete half the entries.
		Integer[] remaining = new Integer[ ( keys.length / 2 ) ];
		for( int index = 0; index < keys.length; index += 2 )
		{
			heap.delete( entries[ index ] );
			remaining[ ( index / 2 ) ] = keys[ ( index + 1 ) ];
		}

		checkEqual( heap, remaining );
	}


	/**
	 * Do decrease key test.
	 * 
	 * @param heap the heap.
	 */
	private void doDecreaseKey( final Heap<Integer, Integer> heap )
	{
		Integer[] keys = makeArray();
		Heap.Entry[] entries = loadHeap( heap, keys );

		// Create a random number.
		Random random = new Random( System.currentTimeMillis() );

		// Load the heap.
		int reduc = 0;
		for( int index = 0; index < keys.length; index += 2 )
		{
			reduc = Math.abs( random.nextInt( SIZE ) );
			heap.decreaseKey( entries[ index ], ( keys[ index ] - reduc ) );
			keys[ index ] = keys[ index ] - reduc;
		}

		checkEqual( heap, keys );
	}


	/**
	 * Do equality for the specified heap.
	 * 
	 * @param h1 the first heap.
	 * @param h2 the second heap.
	 */
	private void doEqual( final Heap<Integer, Integer> h1,
			final Heap<Integer, Integer> h2 )
	{
		h1.clear();
		h2.clear();

		// Make sure they're empty.
		assertTrue( h1.isEmpty() );
		assertTrue( h2.isEmpty() );

		// Load the heap.
		Integer[] keys = makeArray( EQUAL_SIZE );
		loadHeap( h1, keys );
		loadHeap( h2, keys );

		assertTrue( h1.getClass().getName() + " and " + h2.getClass().getName(), h1.getKeys().equals( h2.getKeys() ) );
		assertTrue( h1.getClass().getName() + " and " + h2.getClass().getName(), h1.getValues().equals( h2.getValues() ) );
		assertTrue( h1.getClass().getName() + " and " + h2.getClass().getName(), h1.getEntries().equals( h2.getEntries() ) );
	}


	/**
	 * Create a random array of integers of size <code>SIZE</code>.
	 * 
	 * @return Integer[] the array.
	 */
	private Integer[] makeArray()
	{
		return ( makeArray( SIZE ) );
	}


	/**
	 * Create a random array of integers of the specified size.
	 * 
	 * @param size the size.
	 * @return Integer[] the array.
	 */
	private Integer[] makeArray( final int size )
	{
		Random random = new Random( System.currentTimeMillis() );
		Integer[] keys = new Integer[ size ];

		for( int index = 0; index < keys.length; index++ )
		{
			if( USE_RANDOM )
			{
				keys[ index ] = Math.abs( random.nextInt( ( size * 2 ) ) );
			}
			else
			{
				keys[ index ] = index;
			}
		}

		return ( keys );
	}


	/**
	 * Load the specified heap.
	 * 
	 * @param heap
	 * @return Integer[] the keys loaded.
	 */
	private Integer[] loadHeap( final Heap<Integer, Integer> heap )
	{
		Integer[] keys = makeArray();
		loadHeap( heap, keys );
		return ( keys );
	}


	/**
	 * Load the specified heap with a random set of entries, those returned in the
	 * array, actually.
	 * 
	 * @param heap the heap to load.
	 * @param keys the keys to load.
	 * @return Heap.Entry[] the entries created.
	 */
	private Heap.Entry[] loadHeap( final Heap<Integer, Integer> heap,
			final Integer[] keys )
	{
		Heap.Entry[] rets = new Heap.Entry[ keys.length ];

		for( int index = 0; index < keys.length; index++ )
		{
			rets[ index ] = heap.insert( keys[ index ], index );
		}

		return ( rets );
	}


	/**
	 * Test the specified for insert.
	 * 
	 * @param heap the heap to test.
	 */
	private void doInsert( final Heap<Integer, Integer> heap )
	{
		Integer[] keys = loadHeap( heap );

		// Check sizes.
		assertTrue( "Heap still empty.", heap.isEmpty() == false );
		assertTrue( "Heap short on entities.", heap.getSize() == keys.length );

		// Check equal.
		checkEqual( heap, keys );
	}


	/**
	 * Assert that the specified heap and array contain the same elements.
	 * 
	 * @param heap the heap.
	 * @param keys the keys to the heap.
	 */
	private void checkEqual( final Heap<Integer, Integer> heap,
			final Integer[] keys )
	{
		Arrays.sort( keys );

		assertTrue( heap.getSize() == keys.length );

		// Order should be the same.
		Heap.Entry<Integer, Integer> min;
		for( int index = 0; index < keys.length; index++ )
		{
			assertTrue( "Heap is empty", heap.isEmpty() == false );

			// Get min.
			min = heap.getMinimum();

			// Check values.
			assertTrue( "Heap didn't return the minimum " + index + ": "
					+ min.getKey() + " <-> " + keys[ index ], min.getKey().equals( keys[ index ] ) );

			// Check hold values.
			assertTrue( "Heap doesn't hold minimum (" + index + ")", heap.holdsEntry( min ) == true );

			// Actually remove the minimum.
			heap.extractMinimum();
			assertTrue( "Heap still holds minimum", heap.holdsEntry( min ) == false );
		}

		assertTrue( "Heap had extra elements", heap.getSize() == 0 );
		assertTrue( "Heap not empty", heap.isEmpty() );
	}


}
