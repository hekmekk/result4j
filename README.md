# A Result type for Java

This project aims to refine a **Result** type for Java.

The `Result<T, E>` type is nothing new. It's [part of the rust standard library](https://doc.rust-lang.org/std/result/). It's also very similar in nature to the `Either<L, R>` type, which is [part of the scala standard library](https://www.scala-lang.org/api/current/scala/util/Either.html) and [available for the Java Programming Language most famously via the vavr library](https://www.vavr.io/vavr-docs/#_either). Either however is more generic than Result. It just means "either left or right" after all. Only _by convention_ do we denote `Left` as the failure and `Right` as the success case. Result on the other hand enforces this policy. It's a specialized kind of Either, with `Failure` and `Success` instead of `Left` and `Right`. It clearly communicates _to a human looking at the definition of an operation_ that it might fail or succeed.

## The current state

More of an experiment right now than a "solid library" (also, only available here on github as of now). These are the available types:

- `Result<T, E>`: A **Result** is either a _Success_ or a _Failure_, these are its two _cases_. It is meant to be used as a means to communicate that the respective operation may succeed or fail. Its monadic structure _strongly encourages_\* treating failure as a first class citizen.
- `Done`\*\*: This type is meant to be used in conjuction with e.g. **Result** to signal a successfully completed operation without a corresponding value.

\* The api currently provides `unsafeGet()` and `unsafeGetError()` for testing convenience, but they might very well be removed in future versions.

\*\* Based on [akka.Done](https://doc.akka.io/api/akka/current/akka/Done.html).

