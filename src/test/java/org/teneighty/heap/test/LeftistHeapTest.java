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

import org.teneighty.heap.LeftistHeap;
import org.teneighty.heap.Heap;


/**
 * Test harness for the <code>LeftistHeap</code> implementation.
 * 
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public final class LeftistHeapTest
	extends AbstractHeapTest
{


	/**
	 * Create a new, empty heap, with both integer keys and values, and that uses
	 * the keys' (i.e. <code>Integer</code>'s <i>natural ordering</i>).
	 * 
	 * @return a new, empty heap.
	 */
	@Override
	protected Heap<Integer, Integer> newHeapCore()
	{
		return ( new LeftistHeap<Integer, Integer>() );
	}


}
