/*
 *  Copyright 2021 the original author or authors.
 *  <p>
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  <p>
 *  https://www.apache.org/licenses/LICENSE-2.0
 *  <p>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.openrewrite.polyglot;

import org.graalvm.polyglot.TypeLiteral;
import org.graalvm.polyglot.Value;
import org.openrewrite.Tree;
import org.openrewrite.TreeVisitor;
import org.openrewrite.config.OptionDescriptor;
import org.openrewrite.config.RecipeDescriptor;
import org.openrewrite.internal.lang.Nullable;
import org.openrewrite.marker.Marker;
import org.openrewrite.marker.Markers;

import java.lang.reflect.Constructor;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.openrewrite.polyglot.PolyglotUtils.invokeMemberOrElse;

public interface PolyglotValueMappings {

    TypeLiteral<String> STRING_TYPE = new TypeLiteral<String>() {
    };
    TypeLiteral<Boolean> BOOLEAN_TYPE = new TypeLiteral<Boolean>() {
    };
    TypeLiteral<Set<String>> STRING_SET_TYPE = new TypeLiteral<Set<String>>() {
    };
    TypeLiteral<List<String>> STRING_LIST_TYPE = new TypeLiteral<List<String>>() {
    };
    TypeLiteral<Duration> DURATION_TYPE = new TypeLiteral<Duration>() {
    };
    TypeLiteral<URI> URI_TYPE = new TypeLiteral<URI>() {
    };
    @SuppressWarnings("rawtypes")
    TypeLiteral<List> RAW_LIST_TYPE = new TypeLiteral<List>() {
    };
    TypeLiteral<List<OptionDescriptor>> OPTIONS_TYPE = new TypeLiteral<List<OptionDescriptor>>() {
    };
    TypeLiteral<List<Marker>> MARKERS = new TypeLiteral<List<Marker>>() {
    };

    static Predicate<Value> hasMeta() {
        return v -> v.getMetaObject() != null && v.getMetaObject().hasMember("meta");
    }

    static Predicate<Value> hasName() {
        return v -> v.canInvokeMember("getName") || v.hasMember("name");
    }

    static Predicate<Value> hasGetVisitor() {
        return v -> v.canInvokeMember("getVisitor");
    }

    static Predicate<Value> hasDisplayName() {
        return v -> v.canInvokeMember("getDisplayName") || v.hasMember("displayName");
    }

    interface FromValuePolyglotMapping<OUT> extends PolyglotMapping<Value, OUT> {
        @Override
        default Class<Value> inputType() {
            return Value.class;
        }
    }

    interface ToValuePolyglotMapping<IN> extends PolyglotMapping<IN, Value> {
        @Override
        default Class<Value> outputType() {
            return Value.class;
        }
    }

    class PolyglotRecipeMapping implements FromValuePolyglotMapping<PolyglotRecipe> {
        @Override
        public Class<PolyglotRecipe> outputType() {
            return PolyglotRecipe.class;
        }

        @Override
        public PolyglotRecipe apply(Value value) {
            Value meta = value.getMetaObject().getMember("meta");
            String name = meta.getMember("category") + "." + value.getMetaObject().getMember("member");
            return new PolyglotRecipe(name, value);
        }

        @Override
        public boolean test(Value value) {
            return hasMeta().and(hasGetVisitor()).test(value);
        }
    }

    class RecipeDescriptorMapping implements FromValuePolyglotMapping<RecipeDescriptor> {
        @Override
        public Class<RecipeDescriptor> outputType() {
            return RecipeDescriptor.class;
        }

        @Override
        public boolean test(Value value) {
            return hasMeta().test(value);
        }

        @Override
        public RecipeDescriptor apply(Value v) {
            Value meta = v.getMetaObject().getMember("meta");
            List<OptionDescriptor> options = new ArrayList<>();
            if (meta != null && meta.hasMember("options")) {
                Value opts = meta.getMember("options");
                for (String optKey : opts.getMemberKeys()) {
                    Value opt = opts.getMember(optKey);
                    opt.putMember("name", optKey);
                    options.add(opts.getMember(optKey).as(OptionDescriptor.class));
                }
            }
            return new ConstructorMappingBuilder<>(v, RecipeDescriptor.class)
                    .withGetterOrMetaProperty("getName", "name", STRING_TYPE)
                    .withGetterOrMetaProperty("getDisplayName", "displayName", STRING_TYPE)
                    .withGetterOrMetaProperty("getDescription", "description", STRING_TYPE)
                    .withGetterOrMetaProperty("getTags", "tags", STRING_SET_TYPE)
                    .withGetterOrMetaProperty("getEstimatedEffortPerOccurrence", "estimatedEffortPerOccurrence", Duration.ZERO, DURATION_TYPE)
                    .withRawValue(options, OPTIONS_TYPE)
                    .withGetterOrMetaProperty("getLanguages", "languages", STRING_LIST_TYPE)
                    .withGetterOrMetaProperty("getRecipeList", "recipeList", RAW_LIST_TYPE)
                    .withRawValue(v.getContext().getPolyglotBindings().getMember("sourceUri").as(URI.class), URI_TYPE)
                    .build();
        }
    }

    class OptionDescriptorMapping implements FromValuePolyglotMapping<OptionDescriptor> {
        @Override
        public Class<OptionDescriptor> outputType() {
            return OptionDescriptor.class;
        }

        @Override
        public OptionDescriptor apply(Value value) {
            return new ConstructorMappingBuilder<>(value, OptionDescriptor.class)
                    .withProperty("name", STRING_TYPE)
                    .withProperty("type", STRING_TYPE)
                    .withProperty("displayName", STRING_TYPE)
                    .withProperty("description", STRING_TYPE)
                    .withProperty("example", STRING_TYPE)
                    .withProperty("valid", STRING_LIST_TYPE)
                    .withProperty("required", BOOLEAN_TYPE)
                    .withProperty("value", STRING_TYPE)
                    .build();
        }

        @Override
        public boolean test(Value value) {
            return value.hasMember("displayName");
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    class TreeVisitorMapping implements FromValuePolyglotMapping<TreeVisitor> {
        @Override
        public Class<TreeVisitor> outputType() {
            return TreeVisitor.class;
        }

        @Override
        public TreeVisitor apply(Value value) {
            return new TreeVisitor() {
                @Override
                public Tree preVisit(Tree tree, Object o) {
                    return PolyglotUtils.<Tree, Object>invokeMemberOrElse(value, "preVisit", tree, o, super::preVisit);
                }

                @Override
                public Tree postVisit(Tree tree, Object o) {
                    return PolyglotUtils.<Tree, Object>invokeMemberOrElse(value, "postVisit", tree, o, super::postVisit);
                }

                @Override
                public Markers visitMarkers(Markers markers, Object o) {
                    return PolyglotUtils.<Markers, Object>invokeMemberOrElse(value, "visitMarkers", markers, o, super::visitMarkers);
                }

                @Override
                public Marker visitMarker(Marker marker, Object o) {
                    return invokeMemberOrElse(value, "visitMarker", marker, o, super::visitMarker);
                }

                @Override
                public Tree visit(@Nullable Tree tree, Object o) {
                    return invokeMemberOrElse(value, "visit", tree, o, super::visit);
                }
            };
        }

        @Override
        public boolean test(Value value) {
            return value.canInvokeMember("visit");
        }
    }

    class MarkersMapping implements FromValuePolyglotMapping<Markers> {
        @Override
        public Class<Markers> outputType() {
            return Markers.class;
        }

        @Override
        public Markers apply(Value value) {
            return Markers.build(value.invokeMember("entries").as(MARKERS));
        }

        @Override
        public boolean test(Value value) {
            return value.canInvokeMember("entries");
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    class PolyglotTreeMapping implements ToValuePolyglotMapping<Tree> {
        @Override
        public Class<Tree> inputType() {
            return Tree.class;
        }

        @Override
        public Value apply(Tree tree) {
            return Value.asValue(new PolyglotTree(tree));
        }

        @Override
        public boolean test(Tree tree) {
            return true;
        }
    }

    class ConstructorMappingBuilder<T> {
        private final Value parent;

        private final Constructor<?> ctor;
        private final Object[] constructorArgs;

        private int argIdx = 0;

        public ConstructorMappingBuilder(Value parent, Class<T> targetType) {
            this(parent, targetType, ctor -> true);
        }

        public ConstructorMappingBuilder(Value parent, Class<T> targetType, Predicate<Constructor<?>> ctorFn) {
            this.parent = parent;
            this.ctor = Stream.of(targetType.getConstructors())
                    .filter(ctorFn)
                    .findFirst()
                    .orElseThrow(RuntimeException::new);
            this.constructorArgs = new Object[ctor.getParameterCount()];
        }

        public <O> ConstructorMappingBuilder<T> withRawValue(O property,
                                                             TypeLiteral<O> targetType) {
            return withPropertyOrDefault(null, property, targetType);
        }

        public <O> ConstructorMappingBuilder<T> withProperty(String property,
                                                             TypeLiteral<O> targetType) {
            return withPropertyOrDefault(property, null, targetType);
        }

        public <O> ConstructorMappingBuilder<T> withGetterOrMetaProperty(String getter,
                                                                         @Nullable String property,
                                                                         TypeLiteral<O> targetType) {
            return withGetterOrMetaProperty(getter, property, null, targetType);
        }

        public <O> ConstructorMappingBuilder<T> withGetterOrMetaProperty(String getter,
                                                                         @Nullable String property,
                                                                         @Nullable O defaultValue,
                                                                         TypeLiteral<O> targetType) {
            int i = argIdx++;
            if (parent.hasMember(getter) && parent.getMember(getter).canExecute()) {
                constructorArgs[i] = parent.getMember(getter).execute().as(targetType);
            } else if (property != null && parent.getMetaObject().getMember("meta").hasMember(property)) {
                constructorArgs[i] = parent.getMetaObject().getMember("meta").getMember(property).as(targetType);
            } else {
                constructorArgs[i] = defaultValue;
            }
            return this;
        }

        public <O> ConstructorMappingBuilder<T> withPropertyOrDefault(@Nullable String property,
                                                                      @Nullable O defaultValue,
                                                                      TypeLiteral<O> targetType) {
            int i = argIdx++;
            constructorArgs[i] = property != null && parent.hasMember(property) ? parent.getMember(property).as(targetType) : defaultValue;
            return this;
        }

        @SuppressWarnings("unchecked")
        public T build() {
            try {
                return (T) ctor.newInstance(constructorArgs);
            } catch (Throwable t) {
                throw new IllegalStateException(t.getMessage(), t);
            }
        }
    }

}
