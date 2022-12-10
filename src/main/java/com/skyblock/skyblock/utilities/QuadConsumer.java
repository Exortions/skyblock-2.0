package com.skyblock.skyblock.utilities;

import java.util.Objects;

/**
 * Copyright Screaming Sandals 2022
 * Found on GitHub at ScreamingSandals/ScreamingLib
 * @author ScreamingSandals
 */

public interface QuadConsumer<T, U, V, W> {

   void accept(T t, U u, V v, W w);

   default QuadConsumer<T, U, V, W> andThen(QuadConsumer<? super T, ? super U, ? super V, ? super W> after) {
      Objects.requireNonNull(after);

      return (l, r, v, w) -> {
         accept(l, r, v, w);
         after.accept(l, r, v, w);
      };
   }

}
