package com.github.hekmekk.result4j;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import com.github.hekmekk.result4j.Result.Failure;
import com.github.hekmekk.result4j.Result.Success;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class ResultTest {

  @Test
  @SuppressWarnings("ResultOfMethodCallIgnored")
  void success() {
    assertThrows(NullPointerException.class, () -> Result.success(null));
    assertThat(Result.success(1), instanceOf(Result.Success.class));
  }

  @Test
  @SuppressWarnings("ResultOfMethodCallIgnored")
  void failure() {
    assertThrows(NullPointerException.class, () -> Result.failure(null));
    assertThat(Result.failure(1), instanceOf(Result.Failure.class));
  }

  @Test
  void verifyEqualsAndHashcodeContract() {
    EqualsVerifier.forClass(Success.class).verify();
    EqualsVerifier.forClass(Failure.class).verify();
  }

  @Test
  void toStringShouldMatch() {
    assertThat(Result.success(1).toString(), is("Success[value=1]"));
    assertThat(Result.failure(1).toString(), is("Failure[error=1]"));
  }

  @Test
  void iteratorShouldRun() {
    @SuppressWarnings("unchecked")
    final Consumer<Integer> consumer = mock(Consumer.class);
    final Result<Integer, Integer> success = Result.success(1);
    success.forEach(consumer);
    verify(consumer, times(1)).accept(1);
  }

  @Test
  void iteratorShouldNotRun() {
    @SuppressWarnings("unchecked")
    final Consumer<Integer> consumer = mock(Consumer.class);
    final Result<Integer, Integer> failure = Result.failure(1);
    failure.forEach(consumer);
    verifyZeroInteractions(consumer);
  }

  @Test
  void map() {
    assertThrows(NullPointerException.class, () -> Result.success(1).map(null));
    assertThat(Result.success(1).map(Function.identity()), is(Result.success(1)));
    assertThat(Result.success(1).map(String::valueOf), is(Result.success("1")));
    assertThat(Result.failure(1).map(Function.identity()), is(Result.failure(1)));
    assertThat(Result.failure(1).map(String::valueOf), is(Result.failure(1)));
  }

  @Test
  void mapError() {
    assertThrows(NullPointerException.class, () -> Result.failure(1).mapError(null));
    assertThat(Result.failure(1).mapError(Function.identity()), is(Result.failure(1)));
    assertThat(Result.failure(1).mapError(String::valueOf), is(Result.failure("1")));
    assertThat(Result.success(1).mapError(Function.identity()), is(Result.success(1)));
    assertThat(Result.success(1).mapError(String::valueOf), is(Result.success(1)));
  }

  @Test
  void recover() {
    final Result<Integer, String> success = Result.success(1);
    final Result<Integer, String> failure = Result.failure("FAILURE");
    assertThrows(NullPointerException.class, () -> failure.recover(null, String::length));
    assertThrows(NullPointerException.class, () -> failure.recover(String.class, null));
    assertThat(success.recover(null, String::length), is(success));
    assertThat(success.recover(String.class, null), is(success));
    assertThat(success.recover(String.class, String::length), is(success));

    final Result<Integer, Throwable> recoverException =
        Result.<Integer, Throwable>failure(new IOException("FAILURE"))
            .recover(IOException.class, e -> 23)
            .recover(Throwable.class, t -> 42);

    assertThat(recoverException, is(Result.success(23)));
  }

  @Test
  void recoverWith() {
    final Result<Integer, String> success = Result.success(1);
    final Result<Integer, String> failure = Result.failure("FAILURE");
    assertThrows(
        NullPointerException.class,
        () -> failure.recoverWith(null, str -> Result.success(str.length())));
    assertThrows(NullPointerException.class, () -> failure.recoverWith(String.class, null));
    assertThat(success.recoverWith(null, str -> Result.success(str.length())), is(success));
    assertThat(success.recoverWith(String.class, null), is(success));
    assertThat(success.recoverWith(String.class, str -> Result.success(str.length())), is(success));

    final Result<Integer, Throwable> recoverException =
        Result.<Integer, Throwable>failure(new IOException("FAILURE"))
            .recoverWith(IOException.class, Result::failure)
            .recoverWith(Throwable.class, t -> Result.success(23));

    assertThat(recoverException, is(Result.success(23)));
  }

  @Test
  void flatMap() {
    assertThrows(NullPointerException.class, () -> Result.success(1).flatMap(null));
    final Result<Integer, Integer> success = Result.success(1);
    final Result<Integer, Integer> failure = Result.failure(1);
    assertThat(success.flatMap(n -> Result.success(23)), is(Result.success(23)));
    assertThat(success.flatMap(n -> Result.failure(23)), is(Result.failure(23)));
    assertThat(failure.flatMap(n -> Result.success(23)), is(failure));
    assertThat(failure.flatMap(n -> Result.failure(23)), is(failure));
  }

  @Test
  void flatMapError() {
    assertThrows(NullPointerException.class, () -> Result.failure(1).flatMapError(null));
    final Result<Integer, Integer> success = Result.success(1);
    final Result<Integer, Integer> failure = Result.failure(1);
    assertThat(success.flatMapError(n -> Result.success(23)), is(success));
    assertThat(success.flatMapError(n -> Result.failure(23)), is(success));
    assertThat(failure.flatMapError(n -> Result.success(23)), is(Result.success(23)));
    assertThat(failure.flatMapError(n -> Result.failure(23)), is(Result.failure(23)));
  }

  @Test
  void orElse() {
    assertThrows(
        NullPointerException.class,
        () -> Result.<Integer, Integer>failure(1).orElse((Integer) null));
    assertThrows(
        NullPointerException.class,
        () -> Result.<Integer, Integer>failure(1).orElse((Supplier<Integer>) null));
    assertThat(Result.success(1).orElse(23), is(1));
    assertThat(Result.failure(1).orElse(23), is(23));
    assertThat(Result.success(1).orElse(() -> 23), is(1));
    assertThat(Result.failure(1).orElse(() -> 23), is(23));
  }

  @Test
  void unsafeGet() {
    assertThat(Result.success(1).unsafeGet(), is(1));
    assertThrows(NoSuchElementException.class, () -> Result.failure(1).unsafeGet());
  }

  @Test
  void unsafeGetError() {
    assertThat(Result.failure(1).unsafeGetError(), is(1));
    assertThrows(NoSuchElementException.class, () -> Result.success(1).unsafeGetError());
  }

  @Test
  void onSuccessShouldThrowNPE() {
    assertThrows(NullPointerException.class, () -> Result.success(1).onSuccess(null));
  }

  @Test
  void onSuccessShouldRun() {
    @SuppressWarnings("unchecked")
    final Consumer<Integer> consumer = mock(Consumer.class);
    final Result<Integer, Object> success = Result.success(1);
    assertThat(success.onSuccess(consumer), is(success));
    verify(consumer, times(1)).accept(1);
  }

  @Test
  void onSuccessShouldNotRun() {
    @SuppressWarnings("unchecked")
    final Consumer<Integer> consumer = mock(Consumer.class);
    final Result<Integer, Integer> failure = Result.failure(1);
    assertThat(failure.onSuccess(consumer), is(failure));
    verifyZeroInteractions(consumer);
  }

  @Test
  void onFailureShouldThrowNPE() {
    assertThrows(NullPointerException.class, () -> Result.failure(1).onFailure(null));
  }

  @Test
  void onFailureShouldRun() {
    @SuppressWarnings("unchecked")
    final Consumer<Integer> consumer = mock(Consumer.class);
    final Result<Object, Integer> failure = Result.failure(1);
    assertThat(failure.onFailure(consumer), is(failure));
    verify(consumer, times(1)).accept(1);
  }

  @Test
  void onFailureShouldNotRun() {
    @SuppressWarnings("unchecked")
    final Consumer<Integer> consumer = mock(Consumer.class);
    final Result<Integer, Integer> success = Result.success(1);
    assertThat(success.onFailure(consumer), is(success));
    verifyZeroInteractions(consumer);
  }

  @Test
  void fold() {
    assertThrows(NullPointerException.class, () -> Result.success(1).fold(null, err -> "failure"));
    assertThrows(
        NullPointerException.class, () -> Result.failure(1).fold(success -> "success", null));
    assertThat(Result.success(1).fold(success -> "success", err -> "failure"), is("success"));
    assertThat(Result.failure(1).fold(success -> "success", err -> "failure"), is("failure"));
  }

  @Test
  void transform() {
    assertThrows(NullPointerException.class, () -> Result.success(1).transform(null));
    assertThrows(NullPointerException.class, () -> Result.failure(1).transform(null));
    assertThat(Result.success(1).transform(Result::unsafeGet), is(1));
    assertThat(Result.failure(1).transform(Result::unsafeGetError), is(1));
  }
}
