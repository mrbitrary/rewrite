/*
 * Copyright 2020 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openrewrite.java.cleanup;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.SourceFile;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.style.Checkstyle;
import org.openrewrite.java.style.EqualsAvoidsNullStyle;
import org.openrewrite.java.tree.JavaSourceFile;

import java.time.Duration;
import java.util.Collections;
import java.util.Set;

public class EqualsAvoidsNull extends Recipe {

    @Override
    public String getDisplayName() {
        return "Equals avoids null";
    }

    @Override
    public String getDescription() {
        return "Checks that any combination of String literals is on the left side of an `equals()` comparison. Also checks for String literals assigned to some field (such as `someString.equals(anotherString = \"text\"))`.";
    }

    @Override
    public Set<String> getTags() {
        return Collections.singleton("RSPEC-1132");
    }

    @Override
    public Duration getEstimatedEffortPerOccurrence() {
        return Duration.ofMinutes(10);
    }

    @Override
    protected TreeVisitor<?, ExecutionContext> getVisitor() {
        return new EqualsAvoidsNullFromCompilationUnitStyle();
    }

    private static class EqualsAvoidsNullFromCompilationUnitStyle extends JavaIsoVisitor<ExecutionContext> {
        @Override
        public JavaSourceFile visitJavaSourceFile(JavaSourceFile cu, ExecutionContext executionContext) {
            EqualsAvoidsNullStyle style = ((SourceFile) cu).getStyle(EqualsAvoidsNullStyle.class);
            if (style == null) {
                style = Checkstyle.equalsAvoidsNull();
            }
            doAfterVisit(new EqualsAvoidsNullVisitor<>(style));
            return cu;
        }
    }
}
