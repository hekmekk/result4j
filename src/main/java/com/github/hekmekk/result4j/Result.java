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
 * @param <T> the type of success value
 * @param <E> the type of the failure error
 */
public interface Result<T, E> extends Iterable<T>, Serializable {

  static <T, E> Result<T, E> success(T value) {
    return new Success<>(value);
  }

  static <T, E> Result<T, E> failure(E error) {
    return new Failure<>(error);
  }

  <U> Result<U, E> flatMap(Function<? super T, ? extends Result<? extends U, E>> f);

  <U> Result<U, E> map(Function<? super T, ? extends U> f);

  <F extends E> Result<T, E> recoverWith(Function<F, ? extends Result<? extends T, E>> f);

  <F extends E> Result<T, E> recoverWith(
      Class<F> errorType, Function<F, ? extends Result<? extends T, E>> f);

  <F extends E> Result<T, E> recover(Function<F, ? extends T> f);

  <F extends E> Result<T, E> recover(Class<F> errorType, Function<F, ? extends T> f);

  <U> U fold(Function<? super T, ? extends U> f, Function<? super E, ? extends U> g);

  default <U> U transform(Function<? super Result<T, E>, ? extends U> f) {
    Objects.requireNonNull(f, "f must not be null");
    return f.apply(this);
  }

  T orElse(T other);

  T orElse(Supplier<T> s);

  T unsafeGet();

  E unsafeGetError();

  Result<T, E> onSuccess(final Consumer<T> c);

  Result<T, E> onFailure(final Consumer<E> c);

  final class Success<T, E> implements Result<T, E> {

    private static final long serialVersionUID = 1L;

    private final T value;

    private Success(final T value) {
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
    public <U> Result<U, E> flatMap(final Function<? super T, ? extends Result<? extends U, E>> f) {
      Objects.requireNonNull(f, "f must not be null");
      return (Result<U, E>) f.apply(value);
    }

    @Override
    public <U> Result<U, E> map(final Function<? super T, ? extends U> f) {
      return flatMap(v -> Result.success(f.apply(v)));
    }

    @Override
    public <F extends E> Result<T, E> recoverWith(
        final Function<F, ? extends Result<? extends T, E>> f) {
      return this;
    }

    @Override
    public <F extends E> Result<T, E> recoverWith(
        final Class<F> errorType, final Function<F, ? extends Result<? extends T, E>> f) {
      return this;
    }

    @Override
    public <F extends E> Result<T, E> recover(final Function<F, ? extends T> f) {
      return this;
    }

    @Override
    public <F extends E> Result<T, E> recover(
        final Class<F> errorType, final Function<F, ? extends T> f) {
      return this;
    }

    @Override
    public <U> U fold(
        final Function<? super T, ? extends U> f, final Function<? super E, ? extends U> g) {
      Objects.requireNonNull(f, "f must not be null");
      return f.apply(value);
    }

    @Override
    public T orElse(final T other) {
      return value;
    }

    @Override
    public T orElse(final Supplier<T> s) {
      return value;
    }

    @Override
    public T unsafeGet() {
      return value;
    }

    @Override
    public E unsafeGetError() {
      throw new NoSuchElementException("unsafeGetError() on Success");
    }

    @Override
    public Result<T, E> onSuccess(final Consumer<T> c) {
      Objects.requireNonNull(c, "c must not be null");
      c.accept(value);
      return this;
    }

    @Override
    public Result<T, E> onFailure(final Consumer<E> c) {
      return this;
    }

    @Override
    public Iterator<T> iterator() {
      return new Iterator<T>() {
        private boolean more = true;

        @Override
        public boolean hasNext() {
          return more;
        }

        @Override
        public T next() {
          more = false;
          return value;
        }
      };
    }
  }

  final class Failure<T, E> implements Result<T, E> {

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
    public <U> Result<U, E> flatMap(final Function<? super T, ? extends Result<? extends U, E>> f) {
      return (Result<U, E>) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> Result<U, E> map(final Function<? super T, ? extends U> f) {
      return (Result<U, E>) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <F extends E> Result<T, E> recoverWith(
        final Function<F, ? extends Result<? extends T, E>> f) {
      Objects.requireNonNull(f, "f must not be null");
      return (Result<T, E>) f.apply((F) error);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <F extends E> Result<T, E> recoverWith(
        final Class<F> errorType, final Function<F, ? extends Result<? extends T, E>> f) {
      Objects.requireNonNull(errorType, "errorClazz must not be null");
      Objects.requireNonNull(f, "f must not be null");
      if (errorType.isAssignableFrom(error.getClass())) {
        return (Result<T, E>) f.apply((F) error);
      }

      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <F extends E> Result<T, E> recover(final Function<F, ? extends T> f) {
      return recoverWith(e -> Result.success(f.apply((F) e)));
    }

    @Override
    public <F extends E> Result<T, E> recover(
        final Class<F> errorType, final Function<F, ? extends T> f) {
      return recoverWith(errorType, e -> Result.success(f.apply(e)));
    }

    @Override
    public <U> U fold(
        final Function<? super T, ? extends U> f, final Function<? super E, ? extends U> g) {
      Objects.requireNonNull(g, "g must not be null");
      return g.apply(error);
    }

    @Override
    public T orElse(final T other) {
      Objects.requireNonNull(other, "other must not be null");
      return other;
    }

    @Override
    public T orElse(final Supplier<T> s) {
      Objects.requireNonNull(s, "s must not be null");
      return s.get();
    }

    @Override
    public T unsafeGet() {
      throw new NoSuchElementException("unsafeGet() on Failure");
    }

    @Override
    public E unsafeGetError() {
      return error;
    }

    @Override
    public Result<T, E> onSuccess(final Consumer<T> c) {
      return this;
    }

    @Override
    public Result<T, E> onFailure(final Consumer<E> c) {
      Objects.requireNonNull(c, "c must not be null");
      c.accept(error);
      return this;
    }

    @Override
    public Iterator<T> iterator() {
      return new Iterator<T>() {

        @Override
        public boolean hasNext() {
          return false;
        }

        @Override
        public T next() {
          return null;
        }
      };
    }
  }
}
