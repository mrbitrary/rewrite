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
package org.openrewrite.java;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Option;
import org.openrewrite.Recipe;
import org.openrewrite.internal.lang.Nullable;
import org.openrewrite.java.search.UsesMethod;
import org.openrewrite.java.tree.*;
import org.openrewrite.marker.Markers;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.openrewrite.Tree.randomId;

@Value
@EqualsAndHashCode(callSuper = true)
public class ChangeMethodTargetToStatic extends Recipe {

    /**
     * See {@link  MethodMatcher} for details on the expression's syntax.
     */
    @Option(displayName = "Method pattern",
            description = "A method pattern that is used to find matching method invocations. " +
                    "The original method call may or may not be a static method invocation.",
            example = "com.google.common.collect.ImmutableSet of(..)")
    String methodPattern;

    @Option(displayName = "Fully-qualified target type name",
            description = "A fully-qualified class name of the type upon which the static method is defined.",
            example = "java.util.Set")
    String fullyQualifiedTargetTypeName;

    @Option(displayName = "Return type after change",
            description = "Sometimes changing the target type also changes the return type. In the Guava example, changing from `ImmutableSet#of(..)` to `Set#of(..)` widens the return type from Guava's `ImmutableSet` to just `java.util.Set`.",
            example = "java.util.Set",
            required = false)
    @Nullable
    String returnType;

    @Option(displayName = "Match on overrides",
            description = "When enabled, find methods that are overrides of the method pattern.",
            required = false)
    @Nullable
    Boolean matchOverrides;

    @Override
    public String getDisplayName() {
        return "Change method target to static";
    }

    @Override
    public String getDescription() {
        return "Change method invocations to static method calls.";
    }

    @Override
    protected JavaVisitor<ExecutionContext> getSingleSourceApplicableTest() {
        return new UsesMethod<>(methodPattern, matchOverrides);
    }

    @Override
    public JavaVisitor<ExecutionContext> getVisitor() {
        return new ChangeMethodTargetToStaticVisitor(new MethodMatcher(methodPattern, matchOverrides));
    }

    private class ChangeMethodTargetToStaticVisitor extends JavaIsoVisitor<ExecutionContext> {
        private final MethodMatcher methodMatcher;
        private final JavaType.FullyQualified classType = JavaType.Class.build(fullyQualifiedTargetTypeName);

        public ChangeMethodTargetToStaticVisitor(MethodMatcher methodMatcher) {
            this.methodMatcher = methodMatcher;
        }

        @Override
        public J.MethodInvocation visitMethodInvocation(J.MethodInvocation method, ExecutionContext ctx) {
            J.MethodInvocation m = super.visitMethodInvocation(method, ctx);
            boolean isStatic = method.getMethodType() != null && method.getMethodType().hasFlags(Flag.Static);
            boolean isSameReceiverType = method.getSelect() != null &&
                    TypeUtils.isOfClassType(method.getSelect().getType(), fullyQualifiedTargetTypeName);

            if ((!isStatic || !isSameReceiverType) && methodMatcher.matches(method)) {
                JavaType.Method transformedType = null;
                if (method.getMethodType() != null) {
                    maybeRemoveImport(method.getMethodType().getDeclaringType());
                    transformedType = method.getMethodType().withDeclaringType(classType);
                    if (!method.getMethodType().hasFlags(Flag.Static)) {
                        Set<Flag> flags = new LinkedHashSet<>(method.getMethodType().getFlags());
                        flags.add(Flag.Static);
                        transformedType = transformedType.withFlags(flags);
                    }
                    if (returnType != null) {
                        JavaType returnTypeType = JavaType.Class.build(returnType);
                        transformedType = transformedType.withReturnType(returnTypeType);
                    }
                }
                if (m.getSelect() == null) {
                    maybeAddImport(fullyQualifiedTargetTypeName, m.getSimpleName());
                } else {
                    maybeAddImport(fullyQualifiedTargetTypeName);
                    m = method.withSelect(
                            new J.Identifier(randomId(),
                                    method.getSelect() == null ?
                                            Space.EMPTY :
                                            method.getSelect().getPrefix(),
                                    Markers.EMPTY,
                                    classType.getClassName(),
                                    classType,
                                    null
                            )
                    );
                }
                m = m.withMethodType(transformedType);
            }
            return m;
        }
    }
}
