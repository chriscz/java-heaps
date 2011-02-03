/*
 * $Id: KeyValueTriple.java,v 1.1.2.2 2008/02/18 02:45:02 fran Exp $
 * 
 * Copyright (c) 2005-2011 Fran Lattanzio
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

/**
 * Rancid, hideous triple class.
 * <p>
 * Instances of this class are immutable.
 * 
 * @param <K> the key type.
 * @param <V> the first value type.
 * @param <W> the second value type.
 * @author Fran Lattanzio
 * @version $Revision: 1.1.2.2 $ $Date: 2008/02/18 02:45:02 $
 */
final class KeyValueTriple<K, V, W>
	extends Object
{

	/**
	 * Compare the specified objects for equality, including checks for
	 * <code>null</code> values.
	 * 
	 * @param x the first objet to compare.
	 * @param y the second object to compare.
	 * @return <code>true</code> if both are <code>null</code> or
	 *         <code>x.equals(y)</code>; <code>false</code> otherwise.
	 */
	private static boolean objectEquals(final Object x, final Object y)
	{
		return (x == null && y == null) || (x != null && x.equals(y));
	}

	/**
	 * Get the hashcode for the specified object, returning 0 if
	 * <code>object</code> is <code>null</code>.
	 * 
	 * @param object the object for which we want get a hashcode.
	 * @return the hashcde.
	 */
	private static int objectHashcode(final Object object)
	{
		return (object == null ? 0 : object.hashCode());
	}

	/**
	 * The key.
	 */
	private final K key;

	/**
	 * The first value.
	 */
	private final V firstValue;

	/**
	 * The second value.
	 */
	private final W secondValue;

	/**
	 * Constructor.
	 * 
	 * @param key the key.
	 * @param firstValue the first value.
	 * @param secondValue the second value.
	 */
	KeyValueTriple(K key, V firstValue, W secondValue)
	{
		super();

		// store all stupid fields.
		this.key = key;
		this.firstValue = firstValue;
		this.secondValue = secondValue;
	}

	/**
	 * Get the key
	 * 
	 * @return the key.
	 */
	K getKey()
	{
		return (this.key);
	}

	/**
	 * Get the first value.
	 * 
	 * @return the first value.
	 */
	V getFirstValue()
	{
		return (this.firstValue);
	}

	/**
	 * Get the second value.
	 * 
	 * @return the second value.
	 */
	W getSecondValue()
	{
		return (this.secondValue);
	}

	/**
	 * Compare this objet to the specified object for equality.
	 * 
	 * @param other the object to which to compare.
	 * @return <code>true</code> if equal, as per above definition;
	 *         <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(final Object other)
	{
		if (other == null) { return (false); }

		if (this == other) { return (true); }

		if (other.getClass().equals(KeyValueTriple.class) == false) { return (false); }

		KeyValueTriple that = (KeyValueTriple) other;

		return (objectEquals(this.key, that.key)
				&& objectEquals(this.firstValue, that.firstValue) && objectEquals(
				this.secondValue, that.secondValue));
	}

	/**
	 * Get a hashcode which is consistent with equals.
	 * 
	 * @return the hashcode.
	 */
	@Override
	public int hashCode()
	{
		return (objectHashcode(this.key) ^ objectHashcode(this.firstValue) ^ objectHashcode(this.secondValue));
	}

}
