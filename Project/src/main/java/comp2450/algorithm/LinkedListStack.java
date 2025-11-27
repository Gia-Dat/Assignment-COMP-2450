package comp2450.algorithm;

import com.google.common.base.Preconditions;

/**
 * Linked-list stack (no JDK LinkedList). Internal Node is private.
 * Applies DbC (preconditions/postconditions/invariant).
 */
public class LinkedListStack<T> implements Stack<T> {

    private static final class Node<T> {
        final T value;
        Node<T> next;
        Node(T v, Node<T> n) { value = v; next = n; }
    }

    private Node<T> head;
    private int count;

    public LinkedListStack() {
        head = null;
        count = 0;
        checkInvariant();
    }

    private void checkInvariant() {
        Preconditions.checkState(count >= 0, "Invariant: non-negative size");
        if (count == 0) Preconditions.checkState(head == null, "Invariant: head null when empty");
    }

    @Override
    public void push(T item) {
        Preconditions.checkNotNull(item, "Pre: item not null");
        int old = count;
        head = new Node<>(item, head);
        count++;
        Preconditions.checkState(count == old + 1, "Post: size increased");
        checkInvariant();
    }

    @Override
    public T pop() {
        Preconditions.checkState(count > 0, "Pre: cannot pop empty stack");
        int old = count;
        T v = head.value;
        head = head.next;
        count--;
        Preconditions.checkState(count == old - 1, "Post: size decreased");
        checkInvariant();
        return v;
    }

    @Override
    public T peek() {
        Preconditions.checkState(count > 0, "Pre: cannot peek empty stack");
        checkInvariant();
        return head.value;
    }

    @Override
    public int size() {
        checkInvariant();
        return count;
    }

    @Override
    public boolean isEmpty() {
        checkInvariant();
        return count == 0;
    }
}

