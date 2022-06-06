/*
 * Copyright 2022 Pavel Kastornyy.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pk.ansi4j.core.api;

import java.io.Closeable;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * Stream parser is created for one input stream. After using parser must be closed.
 *
 * @author Pavel Kastornyy
 */
@NotThreadSafe
public interface StreamParser extends Parser, Closeable {

    /**
     * {@inheritDoc}
     *
     * This method will return null when input stream returns -1 and there is no more buffered data. However,
     * if input stream will provide data again the same instance of StreamParser can be used. Also
     * see https://stackoverflow.com/questions/611760/java-inputstream-blocking-read
     *
     * @return
     */
    @Override
    public Fragment parse();
}
