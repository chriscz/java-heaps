/*
 * $Id: PerformanceTest.java,v 1.1.2.3 2008/06/24 01:59:49 fran Exp $
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

import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;

import java.util.Random;

/**
 * Runs a performance test on various heap implementations and reports the lame
 * results to <code>stdout</code>.
 * <p>
 * This class basically works as follows:
 * <ol>
 * <li>Create an empty heap.</li>
 * <li>Insert <code>OPERATIONS</code> random elements.</li>
 * <li>Based on the method ratio, randomly choose a method of the heap to
 * execute. For some of these, we need to create new random elements or find an
 * existing one (but this isn't too hard).</li>
 * <li>Execute the specified method, measuring the time it take, in
 * milliseconds, to execute. We add this time to the total time taken by that
 * method, across all executions of it.</li>
 * <li>Repeat the above <code>TIMES</code> time.</li>
 * <li>Print out the results.</li>
 * </ol>
 * <p>
 * Below, you can change the total count of operations, the number of times to
 * execute, and the ratio of operations. The best thing to do is get a sense of
 * the size of your dataset (with an order of magnitude or two), and a rough
 * estimate of the operation ratios. Plug these into this class and execute!
 * This will probably tell which implementation is the <i>best for you
 * problem</i>. As noted in the package manifest, it's pretty easy to tweak this
 * class to make anyone implementation look the best.
 * 
 * @author Fran Lattanzio
 * @version $Revision: 1.1.2.3 $ $Date: 2008/06/24 01:59:49 $
 */
@SuppressWarnings("unchecked")
public final class PerformanceTest
{

	/**
	 * Seed.
	 */
	private static final long SEED = System.currentTimeMillis();

	/**
	 * Operation ratio: <br>
	 * <code>getMin:extractMin:decreaseKey:delete:insert:union</code>
	 */
	private static final int[] RATIO = { 100, 25, 50, 25, 100, 0 };

	/**
	 * Total operations.
	 */
	private static final int OPERATIONS = 100000;

	/**
	 * Times to repeat.
	 */
	private static final int TIMES = 20;

	/**
	 * Union size.
	 */
	private static final int UNION = 50;

	/**
	 * Random
	 */
	private Random random;

	/**
	 * Total.
	 */
	private int total;

	/**
	 * Partial sums.
	 */
	private int[] partial;

	/**
	 * Entry array.
	 */
	private Heap.Entry[] entries;

	/**
	 * Test initializer.
	 */
	@Before
	public void testInitialize()
	{
		// Create random.
		this.random = new Random(SEED);

		// Calc total.
		this.total = RATIO[0] + RATIO[1] + RATIO[2] + RATIO[3] + RATIO[4]
				+ RATIO[5];

		// Alloc dumb array
		this.entries = new Heap.Entry[OPERATIONS];

		this.partial = new int[RATIO.length];
		for (int index = 0; index < RATIO.length; index++)
		{
			if (index == 0)
			{
				this.partial[index] = RATIO[index];
			}
			else
			{
				this.partial[index] = RATIO[index] + this.partial[(index - 1)];
			}
		}
	}

	/**
	 * Run performance test.
	 */
	@Test
	public void testPerformance()
	{
		// I did this just to make sure the generic expression was correct.
		// It serves no real performance purpose...
		NaturalOrderComparator<Integer> comp = new NaturalOrderComparator<Integer>();

		doPerf(new BinaryHeap<Integer, Integer>(comp, OPERATIONS),
				new BinaryHeap<Integer, Integer>(comp, OPERATIONS));
		doPerf(new FibonacciHeap<Integer, Integer>(comp),
				new FibonacciHeap<Integer, Integer>(comp));
		doPerf(new LeftistHeap<Integer, Integer>(comp),
				new LeftistHeap<Integer, Integer>(comp));
		doPerf(new BinomialHeap<Integer, Integer>(comp),
				new BinomialHeap<Integer, Integer>(comp));
		doPerf(new PairingHeap<Integer, Integer>(comp,
				PairingHeap.MergeStrategy.TWO),
				new PairingHeap<Integer, Integer>(comp,
						PairingHeap.MergeStrategy.TWO),
				"PairingHeap (two-pass merge)");
		doPerf(new PairingHeap<Integer, Integer>(comp,
				PairingHeap.MergeStrategy.MULTI),
				new PairingHeap<Integer, Integer>(comp,
						PairingHeap.MergeStrategy.MULTI),
				"PairingHeap (multi-pass merge)");
		doPerf(new SkewHeap<Integer, Integer>(comp),
				new PairingHeap<Integer, Integer>(comp));
	}

	/**
	 * A rancid overload, because I am extremely lazy.
	 * 
	 * @param heap
	 *            the heap.
	 * @param unioner
	 *            the unioner.
	 */
	private void doPerf(final Heap<Integer, Integer> heap,
			final Heap<Integer, Integer> unioner)
	{
		this.doPerf(heap, unioner, null);
	}

	/**
	 * Do performance stuff.
	 * 
	 * @param heap
	 *            the heap.
	 * @param unioner
	 *            for doing happy union operations.
	 * @param name
	 *            the name...
	 */
	private void doPerf(final Heap<Integer, Integer> heap,
			final Heap<Integer, Integer> unioner, String name)
	{
		int which = 0;
		int index;
		int op;
		long then = System.currentTimeMillis();
		int getMin = 0, extractMin = 0, decreaseKey = 0, delete = 0, insert = 0, union = 0;
		Heap.Entry<Integer, Integer> entry;

		for (int times = 0; times < TIMES; times++)
		{
			for (index = 0; index < OPERATIONS; index++)
			{
				this.entries[index] = heap.insert(Math.abs(this.random
						.nextInt()), 1);
			}

			for (index = 0; index < OPERATIONS; index++)
			{
				op = this.random.nextInt(this.total);

				if (op < this.partial[0])
				{
					heap.getMinimum();
					getMin += 1;
				}
				else if (op < this.partial[1])
				{
					heap.extractMinimum();
					extractMin += 1;
				}
				else if (op < this.partial[2])
				{
					which = this.random.nextInt((this.entries.length));
					entry = (Heap.Entry<Integer, Integer>) this.entries[which];

					if (heap.holdsEntry(entry) == false)
					{
						continue;
					}

					int newkey = entry.getKey() - this.random.nextInt(100);
					heap.decreaseKey(entry, newkey);
					decreaseKey += 1;
				}
				else if (op < this.partial[3])
				{
					which = this.random.nextInt((this.entries.length));
					entry = (Heap.Entry<Integer, Integer>) this.entries[which];

					if (heap.holdsEntry(entry) == false)
					{
						continue;
					}

					heap.delete(entry);
					delete += 1;
				}
				else if (op < this.partial[4])
				{
					// Insert a new random entry.
					heap.insert(this.random.nextInt(), this.random.nextInt());
					insert += 1;
				}
				else if (op < this.partial[5])
				{
					union += 1;
					// Union!
					unioner.clear();

					// Fill unioner... A wee bit lame, but unioning clears other
					// heap, so
					// we need to make new one.
					for (int jindex = 0; jindex < UNION; jindex++)
					{
						unioner.insert(this.random.nextInt(), this.random
								.nextInt());
					}

					heap.union(unioner);
				}
				else
				{
					Assert.assertTrue("Bad operation", false);
				}

			}

			// Clear between runs.
			heap.clear();
		}

		long time = System.currentTimeMillis() - then;

		if (name == null)
		{
			name = heap.getClass().getName();
			index = name.lastIndexOf(".");
			index += 1;
			name = name.substring(index, name.length());
		}

		getMin /= TIMES;
		extractMin /= TIMES;
		decreaseKey /= TIMES;
		delete /= TIMES;
		insert /= TIMES;
		union /= TIMES;

		System.out.println(name + ": " + time + "/" + TIMES);
		System.out
				.println("getMin  extractMin  decreaseKey  delete  insert  union");
		System.out.printf("%6d  %10d  %11d  %6d  %6d  %5d", getMin, extractMin,
				decreaseKey, delete, insert, union);
		System.out.println();
		System.out.println();
	}

}
