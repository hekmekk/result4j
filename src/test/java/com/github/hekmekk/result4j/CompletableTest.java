package com.github.hekmekk.result4j;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import com.github.hekmekk.result4j.Completable.Failure;
import com.github.hekmekk.result4j.Completable.Success;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class CompletableTest {

  @Test
  void of() {
    assertThat(Completable.of(() -> {}), instanceOf(Completable.Success.class));
    assertThat(
        Completable.of(
                () -> {
                  throw new Exception("FAILURE");
                })
            .unsafeGetError(),
        instanceOf(Exception.class));

    assertThat(
        Completable.of(
                () -> {
                  throw new RuntimeException("FAILURE");
                })
            .unsafeGetError(),
        instanceOf(RuntimeException.class));
  }

  @Test
  void success() {
    assertThat(Completable.success(), instanceOf(Success.class));
  }

  @Test
  @SuppressWarnings("ResultOfMethodCallIgnored")
  void failure() {
    assertThrows(NullPointerException.class, () -> Completable.failure(null));
    assertThat(Completable.failure(1), instanceOf(Failure.class));
  }

  @Test
  void verifyEqualsAndHashcodeContract() {
    EqualsVerifier.forClass(Success.class).verify();
    EqualsVerifier.forClass(Failure.class).verify();
  }

  @Test
  void toStringShouldMatch() {
    assertThat(Completable.success().toString(), is("Success[]"));
    assertThat(Completable.failure(1).toString(), is("Failure[error=1]"));
  }

  @Test
  void recoverWith() {
    final Completable<String> success = Completable.success();
    final Completable<String> failure = Completable.failure("FAILURE");
    assertThrows(
        NullPointerException.class, () -> failure.recoverWith(null, str -> Completable.success()));
    assertThrows(NullPointerException.class, () -> failure.recoverWith(null));
    assertThrows(NullPointerException.class, () -> failure.recoverWith(String.class, null));
    assertThat(success.recoverWith(null, str -> Completable.success()), is(success));
    assertThat(success.recoverWith(null), is(success));
    assertThat(success.recoverWith(String.class, null), is(success));
    assertThat(success.recoverWith(String.class, str -> Completable.success()), is(success));

    assertThat(
        Completable.failure("FAILURE").recoverWith(err -> Completable.success()),
        is(Completable.success()));

    final Completable<Throwable> recoverException =
        Completable.<Throwable>failure(new IOException("FAILURE"))
            .recoverWith(IOException.class, Completable::failure)
            .recoverWith(Throwable.class, t -> Completable.success());

    assertThat(recoverException, is(Completable.success()));
  }

  @Test
  void unsafeGetError() {
    assertThat(Completable.failure(1).unsafeGetError(), is(1));
    assertThrows(NoSuchElementException.class, () -> Completable.success().unsafeGetError());
  }

  @Test
  void onSuccessShouldThrowNPE() {
    assertThrows(NullPointerException.class, () -> Completable.success().onSuccess(null));
  }

  @Test
  void onSuccessShouldRun() {
    @SuppressWarnings("unchecked")
    final Runnable runnable = mock(Runnable.class);
    final Completable<Integer> success = Completable.success();
    assertThat(success.onSuccess(runnable), is(success));
    verify(runnable, times(1)).run();
  }

  @Test
  void onSuccessShouldNotRun() {
    @SuppressWarnings("unchecked")
    final Runnable runnable = mock(Runnable.class);
    final Completable<Integer> failure = Completable.failure(1);
    assertThat(failure.onSuccess(runnable), is(failure));
    verifyZeroInteractions(runnable);
  }

  @Test
  void onFailureShouldThrowNPE() {
    assertThrows(NullPointerException.class, () -> Completable.failure(1).onFailure(null));
  }

  @Test
  void onFailureShouldRun() {
    @SuppressWarnings("unchecked")
    final Consumer<Integer> consumer = mock(Consumer.class);
    final Completable<Integer> failure = Completable.failure(1);
    assertThat(failure.onFailure(consumer), is(failure));
    verify(consumer, times(1)).accept(1);

    Completable.success().onSuccess(() -> System.out.println("foo"));
  }

  @Test
  void onFailureShouldNotRun() {
    @SuppressWarnings("unchecked")
    final Consumer<Integer> consumer = mock(Consumer.class);
    final Completable<Integer> success = Completable.success();
    assertThat(success.onFailure(consumer), is(success));
    verifyZeroInteractions(consumer);
  }

  @Test
  void fold() {
    assertThrows(
        NullPointerException.class, () -> Completable.success().fold(null, err -> "failure"));
    assertThrows(
        NullPointerException.class, () -> Completable.failure(1).fold(() -> "success", null));
    assertThat(Completable.success().fold(() -> "success", err -> "failure"), is("success"));
    assertThat(Completable.failure(1).fold(() -> "success", err -> "failure"), is("failure"));
  }

  @Test
  void transform() {
    assertThrows(NullPointerException.class, () -> Completable.success().transform(null));
    assertThrows(NullPointerException.class, () -> Completable.failure(1).transform(null));
    assertThat(Completable.failure(1).transform(Completable::unsafeGetError), is(1));
  }
}
