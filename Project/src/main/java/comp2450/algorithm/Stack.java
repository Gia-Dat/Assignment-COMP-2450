package comp2450.algorithm;

/**
 * COMP 2450-required Stack ADT (generic).
 * Methods must enforce DbC (preconditions/postconditions).
 */
public interface Stack<T> {
    // push: add item to top
    void push(T item);

    // pop: remove and return top
    T pop();

    // peek: return top without removing
    T peek();

    // size: number of elements
    int size();

    // isEmpty: convenience
    boolean isEmpty();
}

