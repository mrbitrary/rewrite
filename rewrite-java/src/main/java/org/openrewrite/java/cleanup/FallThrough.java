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
import org.openrewrite.java.style.FallThroughStyle;
import org.openrewrite.java.tree.JavaSourceFile;

import java.time.Duration;
import java.util.Collections;
import java.util.Set;

public class FallThrough extends Recipe {
    @Override
    public String getDisplayName() {
        return "Fall through";
    }

    @Override
    public String getDescription() {
        return "Checks for fall-through in switch statements, adding `break` statements in locations where a case contains Java code but does not have a `break`, `return`, `throw`, or `continue` statement.";
    }

    @Override
    public Set<String> getTags() {
        return Collections.singleton("RSPEC-128");
    }

    @Override
    public Duration getEstimatedEffortPerOccurrence() {
        return Duration.ofMinutes(10);
    }

    @Override
    protected TreeVisitor<?, ExecutionContext> getVisitor() {
        return new FallThroughFromCompilationUnitStyle();
    }

    private static class FallThroughFromCompilationUnitStyle extends JavaIsoVisitor<ExecutionContext> {
        @Override
        public JavaSourceFile visitJavaSourceFile(JavaSourceFile cu, ExecutionContext executionContext) {
            FallThroughStyle style = ((SourceFile)cu).getStyle(FallThroughStyle.class);
            if (style == null) {
                style = Checkstyle.fallThrough();
            }
            doAfterVisit(new FallThroughVisitor<>(style));
            return cu;
        }
    }
}
