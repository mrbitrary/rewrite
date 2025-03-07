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
package org.openrewrite.maven.tree;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.openrewrite.internal.PropertyPlaceholderHelper;
import org.openrewrite.internal.lang.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

@Value
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@ref")
public class Pom {

    private static final PropertyPlaceholderHelper placeholderHelper = new PropertyPlaceholderHelper("${", "}", null);

    private static final Map<Pom, List<Pom>> flyweights = new WeakHashMap<>();

    public static void clearCaches() {
        flyweights.clear();
    }

    @EqualsAndHashCode.Include
    @Nullable
    String groupId;

    /**
     * Cannot be inherited from a parent POM.
     */
    @Getter
    @EqualsAndHashCode.Include
    String artifactId;

    @EqualsAndHashCode.Include
    @Nullable
    String version;

    /**
     * The timestamp and build numbered version number (the latest snapshot at time dependencies were resolved).
     */
    @Getter
    @EqualsAndHashCode.Include
    @Nullable
    String datedSnapshotVersion;

    @Getter
    @Nullable
    String name;

    @Getter
    @Nullable
    String description;

    @With
    @Nullable
    String packaging;

    @Getter
    @EqualsAndHashCode.Include
    @Nullable
    String classifier;

    @Getter
    @Nullable
    Pom parent;

    @Getter
    List<Dependency> dependencies;

    @Getter
    DependencyManagement dependencyManagement;

    @Getter
    Collection<License> licenses;

    /**
     * A collection of all repositories declared within this pom. Does not include repositories from parent poms.
     */
    @Getter
    Collection<MavenRepository> repositories;

    /**
     * @return a collection of all repositories known to this Pom, as declared in the pom itself or in any of its parent poms.
     */
    public Collection<MavenRepository> getEffectiveRepositories() {
        List<MavenRepository> allRepositories = new ArrayList<>(repositories);
        Pom ancestor = parent;
        while (ancestor != null) {
            allRepositories.addAll(ancestor.getRepositories());
            ancestor = ancestor.getParent();
        }
        return allRepositories;
    }

    /**
     * The properties parsed directly from this POM. These may differ from the effective property values.
     */
    @Getter
    Map<String, String> properties;

    /**
     * A pom's property values can be overridden when the pom is a parent and the child defines a different value
     * for that property.
     */
    @EqualsAndHashCode.Include
    @Getter
    Map<String, String> propertyOverrides;

    public Set<Dependency> getDependencies(Scope scope) {
        Set<Dependency> dependenciesForScope = new TreeSet<>(Comparator.comparing(Dependency::getCoordinates));
        for (Dependency dependency : dependencies) {
            addDependenciesFromScope(scope, dependency, dependenciesForScope);
        }
        return dependenciesForScope;
    }

    /**
     * Fetch transitive dependencies of some dependency given a particular scope.
     *
     * @param dependency The dependency to determine the transitive closure of. The result will include this dependency.
     * @param scope      The scope to traverse.
     * @return A set of transitive dependencies including the provided dependency.
     */
    public Set<Dependency> getDependencies(Dependency dependency, Scope scope) {
        Set<Dependency> dependenciesForScope = new TreeSet<>(Comparator.comparing(Dependency::getCoordinates));
        addDependenciesFromScope(scope, dependency, dependenciesForScope);
        return dependenciesForScope;
    }

    private void addDependenciesFromScope(Scope scope, Dependency dep, Set<Dependency> found) {
        if (dep.getScope().isInClasspathOf(scope) || dep.getScope().equals(scope)) {
            found.add(dep);
            for (Dependency child : dep.getModel().getDependencies()) {
                addDependenciesFromScope(scope, child, found);
            }
        }
    }

    @Nullable
    public String getValue(@Nullable String value) {
        if (value == null) {
            return null;
        }
        return placeholderHelper.replacePlaceholders(value, this::getProperty);
    }

    @Nullable
    private String getProperty(@Nullable String property) {
        if (property == null) {
            return null;
        }
        switch (property) {
            case "groupId":
            case "project.groupId":
            case "pom.groupId":
                return getGroupId();
            case "project.parent.groupId":
                return parent != null ? parent.getGroupId() : null;
            case "artifactId":
            case "project.artifactId":
            case "pom.artifactId":
                return getArtifactId(); // cannot be inherited from parent
            case "project.parent.artifactId":
                return parent == null ? null : parent.getArtifactId();
            case "version":
            case "project.version":
            case "pom.version":
                return getVersion();
            case "project.parent.version":
                return parent != null ? parent.getVersion() : null;
        }

        String value = System.getProperty(property);
        if (value != null) {
            return value;
        }

        String key = property.replace("${", "").replace("}", "");
        value = propertyOverrides.get(key);

        if (value != null) {
            return value;
        }
        value = properties.get(key);
        if (value != null) {
            return value;
        }

        if (parent != null) {
            return parent.getProperty(key);
        } else {
            return null;
        }
    }

    public String getGroupId() {
        if (groupId == null) {
            if (parent == null) {
                throw new IllegalStateException("groupId must be defined");
            }
            return parent.getGroupId();
        }
        return groupId;
    }

    public DependencyManagement getEffectiveDependencyManagement() {
        if (parent == null) {
            return dependencyManagement;
        }
        return new DependencyManagement(Stream.concat(dependencyManagement.getDependencies().stream(),
                parent.getEffectiveDependencyManagement().getDependencies().stream()).collect(Collectors.toList())
        );
    }

    @Nullable
    public String getManagedVersion(String groupId, String artifactId) {
        return getEffectiveDependencyManagement().getManagedVersion(groupId, artifactId);
    }

    @Nullable
    public String getManagedScope(String groupId, String artifactId) {
        return getEffectiveDependencyManagement().getManagedScope(groupId, artifactId);
    }

    public Collection<Pom.Dependency> findDependencies(String groupId, String artifactId) {
        return dependencies.stream()
                .flatMap(d -> d.findDependencies(groupId, artifactId).stream())
                .collect(Collectors.toList());
    }

    public String getVersion() {
        if (version == null) {
            if (parent == null) {
                throw new IllegalStateException("version must be defined");
            }
            return parent.getVersion();
        }
        return version;
    }

    public String getCoordinates() {
        return getGroupId() + ":" + getArtifactId() + ":" + getVersion();
    }

    public String getPackaging() {
        return packaging == null ? "jar" : packaging;
    }

    public boolean deepEquals(@Nullable Pom other) {
        return this == other || (other != null &&
                Objects.equals(this.groupId, other.groupId) &&
                Objects.equals(this.artifactId, other.artifactId) &&
                Objects.equals(this.version, other.version) &&
                Objects.equals(this.datedSnapshotVersion, other.datedSnapshotVersion) &&
                Objects.equals(this.name, other.name) &&
                Objects.equals(this.description, other.description) &&
                Objects.equals(this.packaging, other.packaging) &&
                Objects.equals(this.propertyOverrides, other.propertyOverrides) &&
                Objects.equals(this.properties, other.properties) &&
                Objects.equals(this.repositories, other.repositories) &&
                Objects.equals(this.licenses, other.licenses) &&
                (this.dependencyManagement == other.dependencyManagement || (this.dependencyManagement.deepEquals(other.dependencyManagement))) &&
                (this.parent == other.parent || (this.parent != null && this.parent.deepEquals(other.parent))) &&
                Objects.equals(this.dependencies, other.dependencies)
        );
    }

    public Pom withVersion(String version) {
        if (Objects.equals(this.version, version)) {
            return this;
        }
        return Pom.build(
                this.groupId,
                this.artifactId,
                version,
                this.datedSnapshotVersion,
                this.name,
                this.description,
                this.packaging,
                this.classifier,
                this.parent,
                this.dependencies,
                this.dependencyManagement,
                this.licenses,
                this.repositories,
                this.properties,
                this.propertyOverrides,
                false
        );
    }

    public Pom withDependencies(List<Dependency> dependencies) {
        if (Objects.equals(this.dependencies, dependencies)) {
            return this;
        }
        return Pom.build(
                this.groupId,
                this.artifactId,
                this.version,
                this.datedSnapshotVersion,
                this.name,
                this.description,
                this.packaging,
                this.classifier,
                this.parent,
                dependencies,
                this.dependencyManagement,
                this.licenses,
                this.repositories,
                this.properties,
                this.propertyOverrides,
                false
        );
    }

    public Pom withDependencyManagement(DependencyManagement dependencyManagement) {
        if (Objects.equals(this.dependencyManagement, dependencyManagement)) {
            return this;
        }

        return Pom.build(
                this.groupId,
                this.artifactId,
                this.version,
                this.datedSnapshotVersion,
                this.name,
                this.description,
                this.packaging,
                this.classifier,
                this.parent,
                this.dependencies,
                dependencyManagement,
                this.licenses,
                this.repositories,
                this.properties,
                this.propertyOverrides,
                false
        );
    }

    public Pom withLicenses(List<License> licenses) {
        if (Objects.equals(this.licenses, licenses)) {
            return this;
        }

        return Pom.build(
                this.groupId,
                this.artifactId,
                this.version,
                this.datedSnapshotVersion,
                this.name,
                this.description,
                this.packaging,
                this.classifier,
                this.parent,
                this.dependencies,
                this.dependencyManagement,
                licenses,
                this.repositories,
                this.properties,
                this.propertyOverrides,
                false
        );
    }

    public static Pom build(
            @Nullable String groupId,
            String artifactId,
            @Nullable String version,
            @Nullable String datedSnapshotVersion) {

        return build(groupId, artifactId, version, datedSnapshotVersion, null, null, null, null,
                null, emptyList(), DependencyManagement.empty(), emptyList(), emptyList(), emptyMap(),
                emptyMap(), true);
    }

    @JsonCreator
    public static Pom build(
            @Nullable String groupId,
            String artifactId,
            @Nullable String version,
            @Nullable String datedSnapshotVersion,
            @Nullable String name,
            @Nullable String description,
            @Nullable String packaging,
            @Nullable String classifier,
            @Nullable Pom parent,
            List<Dependency> dependencies,
            DependencyManagement dependencyManagement,
            Collection<License> licenses,
            Collection<MavenRepository> repositories,
            Map<String, String> properties,
            Map<String, String> propertyOverrides,
            boolean relaxedMatching) {

        Pom candidate = new Pom(groupId, artifactId, version, datedSnapshotVersion, name, description, packaging, classifier,
                parent, dependencies, dependencyManagement, licenses, repositories, properties, propertyOverrides);

        List<Pom> variants = flyweights.get(candidate);
        if (relaxedMatching && variants != null && !variants.isEmpty()) {
            // no lock access to existing flyweight when relaxed class type matching is off
            return variants.iterator().next();
        }

        synchronized (flyweights) {
            variants = flyweights.computeIfAbsent(candidate, k -> new ArrayList<>());

            if (relaxedMatching) {
                if (variants.isEmpty()) {
                    variants.add(candidate);
                    return candidate;
                }
                return variants.iterator().next();
            } else {
                for (Pom v : variants) {
                    if (v.deepEquals(candidate)) {
                        return v;
                    }
                }
                variants.add(candidate);
                return candidate;
            }
        }
    }

    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    @Data
    public static class License {
        String name;
        LicenseType type;

        public static Pom.License fromName(@Nullable String license) {
            if (license == null) {
                return new Pom.License("", Pom.LicenseType.Unknown);
            }

            switch (license) {
                case "Apache License, Version 2.0":
                case "The Apache Software License, Version 2.0":
                    return new Pom.License(license, Pom.LicenseType.Apache2);
                case "GNU Lesser General Public License":
                case "GNU Library General Public License":
                    // example Lanterna
                    return new Pom.License(license, Pom.LicenseType.LGPL);
                case "Public Domain":
                    return new Pom.License(license, LicenseType.PublicDomain);
                default:
                    if (license.contains("LGPL")) {
                        // example Checkstyle
                        return new Pom.License(license, Pom.LicenseType.LGPL);
                    } else if (license.contains("GPL") || license.contains("GNU General Public License")) {
                        // example com.buschmais.jqassistant:jqassistant-maven-plugin
                        // example com.github.mtakaki:dropwizard-circuitbreaker
                        return new Pom.License(license, Pom.LicenseType.GPL);
                    } else if (license.contains("CDDL")) {
                        return new Pom.License(license, LicenseType.CDDL);
                    } else if (license.contains("Creative Commons") || license.contains("CC0")) {
                        return new Pom.License(license, LicenseType.CreativeCommons);
                    } else if (license.contains("BSD")) {
                        return new Pom.License(license, LicenseType.BSD);
                    } else if (license.contains("MIT")) {
                        return new Pom.License(license, LicenseType.MIT);
                    } else if (license.contains("Eclipse") || license.contains("EPL")) {
                        return new Pom.License(license, LicenseType.Eclipse);
                    } else if (license.contains("Apache") || license.contains("ASF")) {
                        return new Pom.License(license, LicenseType.Apache2);
                    } else if (license.contains("Mozilla")) {
                        return new Pom.License(license, LicenseType.Mozilla);
                    } else if (license.toLowerCase().contains("GNU Lesser General Public License".toLowerCase()) ||
                            license.contains("GNU Library General Public License")) {
                        return new Pom.License(license, LicenseType.LGPL);
                    }
                    return new Pom.License(license, Pom.LicenseType.Unknown);
            }
        }
    }

    public enum LicenseType {
        Apache2,
        BSD,
        CDDL,
        CreativeCommons,
        Eclipse,
        GPL,
        LGPL,
        MIT,
        Mozilla,
        PublicDomain,
        Unknown
    }

    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    @Data
    @With
    public static class Dependency implements DependencyDescriptor {
        MavenRepository repository;

        Scope scope;

        @Nullable
        String classifier;

        @Nullable
        String type;

        boolean optional;

        Pom model;

        @Nullable
        String requestedVersion;

        Set<GroupArtifact> exclusions;

        public String getGroupId() {
            return model.getGroupId();
        }

        public String getArtifactId() {
            return model.getArtifactId();
        }

        public String getVersion() {
            return model.getVersion();
        }

        public Dependency withVersion(String version) {
            return withModel(model
                    .withVersion(version)
                    .withDependencies(emptyList()));
        }

        @Nullable
        public String getDatedSnapshotVersion() {
            return model.getDatedSnapshotVersion();
        }

        public String getCoordinates() {
            return model.getGroupId() + ':' + model.getArtifactId() + ':' + model.getVersion() +
                    (classifier == null ? "" : ':' + classifier);
        }

        @Override
        public String toString() {
            return "Dependency {" + getCoordinates() +
                    (optional ? ", optional" : "") +
                    (!getVersion().equals(requestedVersion) ? ", requested=" + requestedVersion : "") +
                    '}';
        }

        /**
         * Finds transitive dependencies of this dependency that match the provided group and artifact ids.
         *
         * @param groupId    The groupId to match
         * @param artifactId The artifactId to match.
         * @return Transitive dependencies with any version matching the provided group and artifact id, if any.
         */
        public Collection<Pom.Dependency> findDependencies(String groupId, String artifactId) {
            return findDependencies(d -> d.getGroupId().equals(groupId) && d.getArtifactId().equals(artifactId));
        }

        /**
         * Finds transitive dependencies of this dependency that match the given predicate.
         *
         * @param matcher A dependency test.
         * @return Transitive dependencies with any version matching the given predicate.
         */
        public Collection<Pom.Dependency> findDependencies(Predicate<Dependency> matcher) {
            List<Pom.Dependency> matches = new ArrayList<>();
            if (matcher.test(this)) {
                matches.add(this);
            }
            for (Dependency d : model.getDependencies()) {
                matches.addAll(d.findDependencies(matcher));
            }
            return matches;
        }

        public boolean deepEquals(@Nullable Dependency other) {

            return this == other || (other != null &&
                    Objects.equals(this.repository, other.repository) &&
                    Objects.equals(this.scope, other.scope) &&
                    Objects.equals(this.classifier, other.classifier) &&
                    Objects.equals(this.type, other.type) &&
                    this.optional == other.optional &&
                    Objects.equals(this.requestedVersion, other.requestedVersion) &&
                    Objects.equals(this.exclusions, other.exclusions) &&
                    (this.model == other.model || (this.model != null && this.model.deepEquals(other.model))));
        }
    }

    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    @Data
    public static class DependencyManagement {

        private static final DependencyManagement EMPTY = new DependencyManagement(emptyList());

        public static DependencyManagement empty() {
            return EMPTY;
        }

        @With
        List<DependencyManagementDependency> dependencies;

        @Nullable
        public String getManagedVersion(String groupId, String artifactId) {
            for (DependencyManagementDependency dep : dependencies) {
                for (DependencyDescriptor dependencyDescriptor : dep.getDependencies()) {
                    if (groupId.equals(dependencyDescriptor.getGroupId()) && artifactId.equals(dependencyDescriptor.getArtifactId())) {
                        return dependencyDescriptor.getVersion();
                    }
                }
            }
            return null;
        }

        @Nullable
        public String getManagedScope(String groupId, String artifactId) {
            Scope scope = null;
            for (DependencyManagementDependency dep : dependencies) {
                for (DependencyDescriptor dependencyDescriptor : dep.getDependencies()) {
                    if (groupId.equals(dependencyDescriptor.getGroupId()) && artifactId.equals(dependencyDescriptor.getArtifactId())) {
                        scope = Scope.maxPrecedence(scope, dependencyDescriptor.getScope() == null ? Scope.Compile : dependencyDescriptor.getScope());
                    }
                }
            }
            return scope == null ? null : scope.name().toLowerCase();
        }

        public boolean deepEquals(@Nullable DependencyManagement other) {
            if (this == other) {
                return true;
            } else if (other == null) {
                return false;
            }

            ListIterator<DependencyManagementDependency> e1 = dependencies.listIterator();
            ListIterator<DependencyManagementDependency> e2 = other.dependencies.listIterator();
            while (e1.hasNext() && e2.hasNext()) {
                DependencyManagementDependency o1 = e1.next();
                DependencyManagementDependency o2 = e2.next();
                if (!((o1 == o2)
                        || (o1 instanceof DependencyManagementDependency.Defined && o1.equals(o2))
                        || (o1 instanceof DependencyManagementDependency.Imported && ((DependencyManagementDependency.Imported)o1).deepEquals(o2)))) {
                    return false;
                }
            }
            return !(e1.hasNext() || e2.hasNext());
        }

    }

    @Override
    public String toString() {
        return "Pom{" +
                groupId + ':' + artifactId + ':' + version +
                '}';
    }
}
