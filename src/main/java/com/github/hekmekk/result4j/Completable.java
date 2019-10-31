package com.github.hekmekk.result4j;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A container type with two possible cases: A {@link Completable} is either a {@link Success}
 * without a value or a {@link Failure} with an error.
 *
 * @param <E> the type of the failure error
 */
public interface Completable<E> extends Serializable {

  static Completable<Throwable> of(CheckedRunnable r) {
    try {
      r.run();
      return success();
    } catch (Throwable t) {
      return failure(t);
    }
  }

  static <E> Completable<E> success() {
    return new Success<>();
  }

  static <E> Completable<E> failure(E error) {
    return new Failure<>(error);
  }

  <U> U fold(Supplier<? extends U> f, Function<? super E, ? extends U> g);

  default <U> U transform(Function<? super Completable<E>, ? extends U> f) {
    Objects.requireNonNull(f, "f must not be null");
    return f.apply(this);
  }

  <F extends E> Completable<E> recoverWith(final Function<F, ? extends Completable<E>> f);

  <F extends E> Completable<E> recoverWith(
      Class<F> errorType, Function<F, ? extends Completable<E>> f);

  E unsafeGetError();

  Completable<E> onSuccess(final Runnable c);

  Completable<E> onFailure(final Consumer<E> c);

  final class Success<E> implements Completable<E> {

    private static final long serialVersionUID = 1L;

    private Success() {}

    @Override
    public int hashCode() {
      return Objects.hashCode("Success[]");
    }

    @Override
    public boolean equals(final Object obj) {
      return (obj == this) || (obj instanceof Success);
    }

    @Override
    public String toString() {
      return "Success[]";
    }

    @Override
    public <F extends E> Completable<E> recoverWith(final Function<F, ? extends Completable<E>> f) {
      return this;
    }

    @Override
    public <F extends E> Completable<E> recoverWith(
        final Class<F> errorType, final Function<F, ? extends Completable<E>> f) {
      return this;
    }

    @Override
    public <U> U fold(final Supplier<? extends U> f, final Function<? super E, ? extends U> g) {
      Objects.requireNonNull(f, "f must not be null");
      return f.get();
    }

    @Override
    public E unsafeGetError() {
      throw new NoSuchElementException("unsafeGetError() on Success");
    }

    @Override
    public Completable<E> onSuccess(final Runnable r) {
      Objects.requireNonNull(r, "r must not be null");
      r.run();
      return this;
    }

    @Override
    public Completable<E> onFailure(final Consumer<E> c) {
      return this;
    }
  }

  final class Failure<E> implements Completable<E> {

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
    public <F extends E> Completable<E> recoverWith(final Function<F, ? extends Completable<E>> f) {
      Objects.requireNonNull(f, "f must not be null");
      return f.apply((F) error);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <F extends E> Completable<E> recoverWith(
        final Class<F> errorType, final Function<F, ? extends Completable<E>> f) {
      Objects.requireNonNull(errorType, "errorClazz must not be null");
      Objects.requireNonNull(f, "f must not be null");
      if (errorType.isAssignableFrom(error.getClass())) {
        return f.apply((F) error);
      }

      return this;
    }

    @Override
    public <U> U fold(final Supplier<? extends U> f, final Function<? super E, ? extends U> g) {
      Objects.requireNonNull(g, "g must not be null");
      return g.apply(error);
    }

    @Override
    public E unsafeGetError() {
      return error;
    }

    @Override
    public Completable<E> onSuccess(final Runnable r) {
      return this;
    }

    @Override
    public Completable<E> onFailure(final Consumer<E> c) {
      Objects.requireNonNull(c, "c must not be null");
      c.accept(error);
      return this;
    }
  }
}
