/*
 * Copyright 2021 the original author or authors.
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
package org.openrewrite.groovy;

import org.codehaus.groovy.ast.GenericsType;
import org.openrewrite.InMemoryExecutionContext;
import org.openrewrite.Parser;
import org.openrewrite.internal.StringUtils;
import org.openrewrite.java.JavaTypeSignatureBuilderTest;

import java.io.ByteArrayInputStream;
import java.nio.file.Paths;

import static java.util.Collections.singletonList;

public class GroovyTypeSignatureBuilderTest implements JavaTypeSignatureBuilderTest {

    private static final String goat = StringUtils.readFully(
            GroovyTypeSignatureBuilderTest.class.getResourceAsStream("/GroovyTypeGoat.groovy"));

    private static final CompiledGroovySource cu = GroovyParser.builder()
            .logCompilationWarningsAndErrors(true)
            .build()
            .parseInputsToCompilerAst(
                    singletonList(new Parser.Input(Paths.get("GroovyTypeGoat.groovy"), () -> new ByteArrayInputStream(goat.getBytes()))),
                    null,
                    new InMemoryExecutionContext(Throwable::printStackTrace))
            .iterator()
            .next();

    @Override
    public String fieldSignature(String field) {
        return signatureBuilder().variableSignature(cu.getModule().getClasses().get(0).getDeclaredField(field));
    }

    @Override
    public String methodSignature(String methodName) {
        return signatureBuilder().methodSignature(cu.getModule().getClasses().get(0).getDeclaredMethods(methodName).get(0));
    }

    @Override
    public String constructorSignature() {
        return signatureBuilder().methodSignature(cu.getModule().getClasses().get(0).getDeclaredConstructors().get(0));
    }

    @Override
    public Object firstMethodParameter(String methodName) {
        return cu.getModule().getClasses().get(0).getDeclaredMethods(methodName).get(0).getParameters()[0].getType();
    }

    @Override
    public GenericsType lastClassTypeParameter() {
        return cu.getModule().getClasses().get(0).getGenericsTypes()[1];
    }

    @Override
    public GroovyAstTypeSignatureBuilder signatureBuilder() {
        return new GroovyAstTypeSignatureBuilder();
    }
}
