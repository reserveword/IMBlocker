package io.github.reserveword.imblocker.common;

import java.util.AbstractSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Predicate;

public class IdentityHashSet<E> extends AbstractSet<E> {
	
	private static final Object PRESENT = new Object();
	
	private final IdentityHashMap<E, Object> map = new IdentityHashMap<>();
	
	@Override
	public boolean add(E e) {
		return map.put(e, PRESENT) == null;
	}
	
	@Override
	public boolean remove(Object o) {
		return map.remove(o) == PRESENT;
	}
	
	@Override
	public boolean contains(Object o) {
		return map.containsKey(o);
	}
	
	public boolean contains(Predicate<E> predicate) {
		return stream().anyMatch(predicate);
	}
	
	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public int size() {
		return map.size();
	}
	
	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return map.keySet().iterator();
	}
	
	@Override
	public Spliterator<E> spliterator() {
		return map.keySet().spliterator();
	}
}
