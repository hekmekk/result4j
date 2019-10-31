package com.github.hekmekk.result4j;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A container type with two possible cases: A {@link Result} is either a {@link Success} with a
 * value or a {@link Failure} with an error.
 *
 * @param <V> the type of success value
 * @param <E> the type of the failure error
 */
public interface Result<V, E> extends Iterable<V>, Serializable {

  static <V> Result<V, Throwable> of(CheckedSupplier<V> s) {
    try {
      return success(s.get());
    } catch (Throwable t) {
      return failure(t);
    }
  }

  static <V, E> Result<V, E> success(V value) {
    return new Success<>(value);
  }

  static <V, E> Result<V, E> failure(E error) {
    return new Failure<>(error);
  }

  <U> Result<U, E> flatMap(Function<? super V, ? extends Result<? extends U, E>> f);

  <U> Result<U, E> map(Function<? super V, ? extends U> f);

  <F extends E> Result<V, E> recoverWith(Function<F, ? extends Result<? extends V, E>> f);

  <F extends E> Result<V, E> recoverWith(
      Class<F> errorType, Function<F, ? extends Result<? extends V, E>> f);

  <F extends E> Result<V, E> recover(Function<F, ? extends V> f);

  <F extends E> Result<V, E> recover(Class<F> errorType, Function<F, ? extends V> f);

  <U> U fold(Function<? super V, ? extends U> f, Function<? super E, ? extends U> g);

  default <U> U transform(Function<? super Result<V, E>, ? extends U> f) {
    Objects.requireNonNull(f, "f must not be null");
    return f.apply(this);
  }

  V orElse(V other);

  V orElse(Supplier<V> s);

  V orElse(Function<E, V> f);

  V unsafeGet();

  E unsafeGetError();

  Result<V, E> onSuccess(final Consumer<V> c);

  Result<V, E> onFailure(final Consumer<E> c);

  final class Success<V, E> implements Result<V, E> {

    private static final long serialVersionUID = 1L;

    private final V value;

    private Success(final V value) {
      Objects.requireNonNull(value, "value must not be null");
      this.value = value;
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(value);
    }

    @Override
    public boolean equals(final Object obj) {
      return (obj == this)
          || (obj instanceof Success) && Objects.equals(value, ((Success) obj).value);
    }

    @Override
    public String toString() {
      return "Success[value=" + value + "]";
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> Result<U, E> flatMap(final Function<? super V, ? extends Result<? extends U, E>> f) {
      Objects.requireNonNull(f, "f must not be null");
      return (Result<U, E>) f.apply(value);
    }

    @Override
    public <U> Result<U, E> map(final Function<? super V, ? extends U> f) {
      return flatMap(v -> Result.success(f.apply(v)));
    }

    @Override
    public <F extends E> Result<V, E> recoverWith(
        final Function<F, ? extends Result<? extends V, E>> f) {
      return this;
    }

    @Override
    public <F extends E> Result<V, E> recoverWith(
        final Class<F> errorType, final Function<F, ? extends Result<? extends V, E>> f) {
      return this;
    }

    @Override
    public <F extends E> Result<V, E> recover(final Function<F, ? extends V> f) {
      return this;
    }

    @Override
    public <F extends E> Result<V, E> recover(
        final Class<F> errorType, final Function<F, ? extends V> f) {
      return this;
    }

    @Override
    public <U> U fold(
        final Function<? super V, ? extends U> f, final Function<? super E, ? extends U> g) {
      Objects.requireNonNull(f, "f must not be null");
      return f.apply(value);
    }

    @Override
    public V orElse(final V other) {
      return value;
    }

    @Override
    public V orElse(final Supplier<V> s) {
      return value;
    }

    @Override
    public V orElse(final Function<E, V> f) {
      return value;
    }

    @Override
    public V unsafeGet() {
      return value;
    }

    @Override
    public E unsafeGetError() {
      throw new NoSuchElementException("unsafeGetError() on Success");
    }

    @Override
    public Result<V, E> onSuccess(final Consumer<V> c) {
      Objects.requireNonNull(c, "c must not be null");
      c.accept(value);
      return this;
    }

    @Override
    public Result<V, E> onFailure(final Consumer<E> c) {
      return this;
    }

    @Override
    public Iterator<V> iterator() {
      return new Iterator<V>() {
        private boolean more = true;

        @Override
        public boolean hasNext() {
          return more;
        }

        @Override
        public V next() {
          more = false;
          return value;
        }
      };
    }
  }

  final class Failure<V, E> implements Result<V, E> {

    private static final long serialVersionUID = 1L;

    private final E error;

    private Failure(final E error) {
      Objects.requireNonNull(error, "error must not be null");
      this.error = error;
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(error);
    }

    @Override
    public boolean equals(final Object obj) {
      return (obj == this)
          || (obj instanceof Failure) && Objects.equals(error, ((Failure) obj).error);
    }

    @Override
    public String toString() {
      return "Failure[error=" + error + "]";
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> Result<U, E> flatMap(final Function<? super V, ? extends Result<? extends U, E>> f) {
      return (Result<U, E>) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> Result<U, E> map(final Function<? super V, ? extends U> f) {
      return (Result<U, E>) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <F extends E> Result<V, E> recoverWith(
        final Function<F, ? extends Result<? extends V, E>> f) {
      Objects.requireNonNull(f, "f must not be null");
      return (Result<V, E>) f.apply((F) error);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <F extends E> Result<V, E> recoverWith(
        final Class<F> errorType, final Function<F, ? extends Result<? extends V, E>> f) {
      Objects.requireNonNull(errorType, "errorClazz must not be null");
      Objects.requireNonNull(f, "f must not be null");
      if (errorType.isAssignableFrom(error.getClass())) {
        return (Result<V, E>) f.apply((F) error);
      }

      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <F extends E> Result<V, E> recover(final Function<F, ? extends V> f) {
      return recoverWith(e -> Result.success(f.apply((F) e)));
    }

    @Override
    public <F extends E> Result<V, E> recover(
        final Class<F> errorType, final Function<F, ? extends V> f) {
      return recoverWith(errorType, e -> Result.success(f.apply(e)));
    }

    @Override
    public <U> U fold(
        final Function<? super V, ? extends U> f, final Function<? super E, ? extends U> g) {
      Objects.requireNonNull(g, "g must not be null");
      return g.apply(error);
    }

    @Override
    public V orElse(final V other) {
      Objects.requireNonNull(other, "other must not be null");
      return other;
    }

    @Override
    public V orElse(final Supplier<V> s) {
      Objects.requireNonNull(s, "s must not be null");
      return s.get();
    }

    @Override
    public V orElse(final Function<E, V> f) {
      Objects.requireNonNull(f, "s must not be null");
      return f.apply(error);
    }

    @Override
    public V unsafeGet() {
      throw new NoSuchElementException("unsafeGet() on Failure");
    }

    @Override
    public E unsafeGetError() {
      return error;
    }

    @Override
    public Result<V, E> onSuccess(final Consumer<V> c) {
      return this;
    }

    @Override
    public Result<V, E> onFailure(final Consumer<E> c) {
      Objects.requireNonNull(c, "c must not be null");
      c.accept(error);
      return this;
    }

    @Override
    public Iterator<V> iterator() {
      return new Iterator<V>() {

        @Override
        public boolean hasNext() {
          return false;
        }

        @Override
        public V next() {
          return null;
        }
      };
    }
  }
}
