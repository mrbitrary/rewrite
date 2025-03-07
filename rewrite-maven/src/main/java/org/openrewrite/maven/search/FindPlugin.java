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
package org.openrewrite.maven.search;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.openrewrite.*;
import org.openrewrite.maven.MavenVisitor;
import org.openrewrite.maven.tree.Maven;
import org.openrewrite.xml.tree.Xml;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Value
@Incubating(since = "7.7.0")
public class FindPlugin extends Recipe {

    @Option(displayName = "Group",
            description = "The first part of a dependency coordinate 'org.openrewrite.maven:rewrite-maven-plugin:VERSION'.",
            example = "org.openrewrite.maven")
    String groupId;

    @Option(displayName = "Artifact",
            description = "The second part of a dependency coordinate 'org.openrewrite.maven:rewrite-maven-plugin:VERSION'.",
            example = "rewrite-maven-plugin")
    String artifactId;

    public static Set<Xml.Tag> find(Maven maven, String groupId, String artifactId) {
        Set<Xml.Tag> ds = new HashSet<>();
        new MavenVisitor() {
            @Override
            public Xml visitTag(Xml.Tag tag, ExecutionContext context) {
                if (isPluginTag(groupId, artifactId)) {
                    ds.add(tag);
                }
                return super.visitTag(tag, context);
            }
        }.visit(maven, new InMemoryExecutionContext());
        return ds;
    }

    @Override
    public String getDisplayName() {
        return "Find Maven plugin";
    }

    @Override
    public String getDescription() {
        return "Finds a Maven plugin within a pom.xml.";
    }

    @Override
    protected TreeVisitor<?, ExecutionContext> getVisitor() {
        return new MavenVisitor() {
            @Override
            public Xml visitTag(Xml.Tag tag, ExecutionContext context) {
                if (isPluginTag(groupId, artifactId)) {
                    return tag.withMarkers(tag.getMarkers().searchResult());
                }
                return super.visitTag(tag, context);
            }
        };
    }
}
