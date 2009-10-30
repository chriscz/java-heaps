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

import java.util.Comparator;


/**
 * Key value triple comparator.
 * <p>
 * Compares <code>KeyValueTriple</code> instances based on their keys, using
 * the supplied comparator to actually compare them.
 * 
 * @param <K> the key type.
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
final class KeyValueTripleComparator<K>
	extends Object
	implements Comparator<KeyValueTriple<K, ?, ?>>
{


	/**
	 * Comparator to use to compare keys.
	 */
	private Comparator<K> keyComparator;


	/**
	 * Constructor.
	 * 
	 * @param comparator the comparator to use to compare the keys.
	 * @throws NullPointerException If <code>comparator</code> is
	 *         <code>null</code>.
	 */
	KeyValueTripleComparator( final Comparator<K> comparator )
		throws NullPointerException
	{
		if( comparator == null )
		{
			throw new NullPointerException();
		}

		this.keyComparator = comparator;
	}


	/**
	 * Compare the keys of the specified triples.
	 * 
	 * @param kvt1 the first triple.
	 * @param kvt2 the second triple.
	 * @return {-1, 0, 1} if the key of the first triple is less than, equal to,
	 *         or greater than the key of second triple, respectively.
	 */
	public int compare( final KeyValueTriple<K, ?, ?> kvt1,
			final KeyValueTriple<K, ?, ?> kvt2 )
	{
		if( kvt1 == null || kvt2 == null )
		{
			throw new NullPointerException();
		}

		return ( this.keyComparator.compare( kvt1.getKey(), kvt2.getKey() ) );
	}


	/**
	 * Equals.
	 * 
	 * @param other the other object.
	 * @return boolean <code>true</code> if equal.
	 */
	@Override
	public boolean equals( final Object other )
	{
		if( other == null )
		{
			return ( false );
		}

		if( this == other )
		{
			return ( true );
		}

		return ( this.getClass().equals( other.getClass() ) == true );
	}


	/**
	 * Get the hashcode inline with equals.
	 * 
	 * @return int the hashcode.
	 */
	@Override
	public int hashCode()
	{
		return ( 1 );
	}


	/**
	 * To String.
	 * 
	 * @return String a rancid string.
	 */
	@Override
	public String toString()
	{
		return ( "KeyValueTripleComparator" );
	}


}
