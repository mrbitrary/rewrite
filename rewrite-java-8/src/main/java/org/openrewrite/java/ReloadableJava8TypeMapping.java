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
package org.openrewrite.java;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.code.*;
import com.sun.tools.javac.tree.JCTree;
import lombok.RequiredArgsConstructor;
import org.openrewrite.internal.lang.Nullable;
import org.openrewrite.java.internal.JavaTypeCache;
import org.openrewrite.java.tree.Flag;
import org.openrewrite.java.tree.JavaType;
import org.openrewrite.java.tree.TypeUtils;

import javax.lang.model.type.NullType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.openrewrite.java.tree.JavaType.GenericTypeVariable.Variance.*;

@RequiredArgsConstructor
class ReloadableJava8TypeMapping implements JavaTypeMapping<Tree> {
    private static final int KIND_BITMASK_INTERFACE = 1 << 9;
    private static final int KIND_BITMASK_ANNOTATION = 1 << 13;
    private static final int KIND_BITMASK_ENUM = 1 << 14;

    private final ReloadableJava8TypeSignatureBuilder signatureBuilder = new ReloadableJava8TypeSignatureBuilder();

    private final JavaTypeCache typeCache;

    public JavaType type(@Nullable com.sun.tools.javac.code.Type type) {
        if (type == null || type instanceof Type.ErrorType || type instanceof Type.PackageType || type instanceof Type.UnknownType ||
                type instanceof NullType) {
            return JavaType.Class.Unknown.getInstance();
        }

        String signature = signatureBuilder.signature(type);
        JavaType existing = typeCache.get(signature);
        if (existing != null) {
            return existing;
        }

        if (type instanceof Type.ClassType) {
            return classType((Type.ClassType) type, signature);
        } else if (type instanceof Type.TypeVar) {
            return generic((Type.TypeVar) type, signature);
        } else if (type instanceof Type.JCPrimitiveType) {
            return primitive(type.getTag());
        } else if (type instanceof Type.JCVoidType) {
            return JavaType.Primitive.Void;
        } else if (type instanceof Type.ArrayType) {
            return array(type, signature);
        } else if (type instanceof Type.WildcardType) {
            return generic((Type.WildcardType) type, signature);
        } else if (type instanceof Type.AnnotatedType) {
            return type(type.unannotatedType());
        } else if (type instanceof Type.JCNoType) {
            return JavaType.Class.Unknown.getInstance();
        }

        throw new UnsupportedOperationException("Unknown type " + type.getClass().getName());
    }

    private JavaType array(Type type, String signature) {
        JavaType.Array arr = new JavaType.Array(null, null);
        typeCache.put(signature, arr);
        arr.unsafeSet(type(((Type.ArrayType) type).elemtype));
        return arr;
    }

    private JavaType.GenericTypeVariable generic(Type.WildcardType wildcard, String signature) {
        JavaType.GenericTypeVariable gtv = new JavaType.GenericTypeVariable(null, "?", INVARIANT, null);
        typeCache.put(signature, gtv);

        JavaType.GenericTypeVariable.Variance variance;
        List<JavaType> bounds;

        switch (wildcard.kind) {
            case SUPER:
                variance = CONTRAVARIANT;
                bounds = singletonList(type(wildcard.getSuperBound()));
                break;
            case EXTENDS:
                variance = COVARIANT;
                bounds = singletonList(type(wildcard.getExtendsBound()));
                break;
            case UNBOUND:
            default:
                variance = INVARIANT;
                bounds = null;
                break;
        }

        if (bounds != null && bounds.get(0) instanceof JavaType.FullyQualified && ((JavaType.FullyQualified) bounds.get(0))
                .getFullyQualifiedName().equals("java.lang.Object")) {
            bounds = null;
        }

        gtv.unsafeSet(variance, bounds);
        return gtv;
    }

    private JavaType generic(Type.TypeVar type, String signature) {
        String name = type.tsym.name.toString();
        JavaType.GenericTypeVariable gtv = new JavaType.GenericTypeVariable(null,
                name, INVARIANT, null);
        typeCache.put(signature, gtv);

        List<JavaType> bounds = null;
        if (type.getUpperBound() instanceof Type.IntersectionClassType) {
            Type.IntersectionClassType intersectionBound = (Type.IntersectionClassType) type.getUpperBound();
            if (intersectionBound.interfaces_field.length() > 0) {
                bounds = new ArrayList<>(intersectionBound.interfaces_field.length());
                for (Type bound : intersectionBound.interfaces_field) {
                    bounds.add(type(bound));
                }
            } else if (intersectionBound.supertype_field != null) {
                JavaType mappedBound = type(intersectionBound.supertype_field);
                if (!(mappedBound instanceof JavaType.FullyQualified) || !((JavaType.FullyQualified) mappedBound).getFullyQualifiedName().equals("java.lang.Object")) {
                    bounds = singletonList(mappedBound);
                }
            }
        } else if (type.getUpperBound() != null) {
            JavaType mappedBound = type(type.getUpperBound());
            if (!(mappedBound instanceof JavaType.FullyQualified) || !((JavaType.FullyQualified) mappedBound).getFullyQualifiedName().equals("java.lang.Object")) {
                bounds = singletonList(mappedBound);
            }
        }

        gtv.unsafeSet(bounds == null ? INVARIANT : COVARIANT, bounds);
        return gtv;
    }

    private JavaType.FullyQualified classType(Type.ClassType classType, String signature) {
        Symbol.ClassSymbol sym = (Symbol.ClassSymbol) classType.tsym;
        Type.ClassType symType = (Type.ClassType) sym.type;

        JavaType.FullyQualified fq = typeCache.get(sym.flatName().toString());
        JavaType.Class clazz = (JavaType.Class) (fq instanceof JavaType.Parameterized ? ((JavaType.Parameterized) fq).getType() : fq);
        if (clazz == null) {
            completeClassSymbol(sym);
            clazz = new JavaType.Class(
                    null,
                    sym.flags_field,
                    sym.flatName().toString(),
                    getKind(sym),
                    null, null, null, null, null, null
            );

            typeCache.put(sym.flatName().toString(), clazz);

            JavaType.FullyQualified supertype = TypeUtils.asFullyQualified(type(classType.supertype_field == null ? symType.supertype_field :
                    classType.supertype_field));

            JavaType.FullyQualified owner = null;
            if (sym.owner instanceof Symbol.ClassSymbol) {
                owner = TypeUtils.asFullyQualified(type(sym.owner.type));
            }

            List<JavaType.FullyQualified> interfaces = null;
            if (symType.interfaces_field != null) {
                interfaces = new ArrayList<>(symType.interfaces_field.length());
                for (com.sun.tools.javac.code.Type iParam : symType.interfaces_field) {
                    JavaType.FullyQualified javaType = TypeUtils.asFullyQualified(type(iParam));
                    if (javaType != null) {
                        interfaces.add(javaType);
                    }
                }
            }

            List<JavaType.Variable> fields = null;
            List<JavaType.Method> methods = null;

            if (sym.members_field != null) {
                for (Symbol elem : sym.members_field.getElements()) {
                    if (elem instanceof Symbol.VarSymbol &&
                            (elem.flags_field & (Flags.SYNTHETIC | Flags.BRIDGE | Flags.HYPOTHETICAL |
                                    Flags.GENERATEDCONSTR | Flags.ANONCONSTR)) == 0) {
                        if (sym.flatName().toString().equals("java.lang.String") && sym.name.toString().equals("serialPersistentFields")) {
                            // there is a "serialPersistentFields" member within the String class which is used in normal Java
                            // serialization to customize how the String field is serialized. This field is tripping up Jackson
                            // serialization and is intentionally filtered to prevent errors.
                            continue;
                        }

                        if (fields == null) {
                            fields = new ArrayList<>();
                        }
                        fields.add(variableType(elem, clazz));
                    } else if (elem instanceof Symbol.MethodSymbol &&
                            (elem.flags_field & (Flags.SYNTHETIC | Flags.BRIDGE | Flags.HYPOTHETICAL |
                                    Flags.ANONCONSTR)) == 0) {
                        if (methods == null) {
                            methods = new ArrayList<>();
                        }
                        Symbol.MethodSymbol methodSymbol = (Symbol.MethodSymbol) elem;
                        if (!methodSymbol.isStaticOrInstanceInit()) {
                            methods.add(methodDeclarationType(methodSymbol, clazz));
                        }
                    }
                }
            }

            clazz.unsafeSet(supertype, owner, listAnnotations(sym), interfaces, fields, methods);
        }

        if (classType.typarams_field != null && classType.typarams_field.length() > 0) {
            // NOTE because of completion that happens when building the base type,
            // the signature may shift from when it was first calculated.
            JavaType.Parameterized pt = typeCache.get(signatureBuilder.signature(classType));
            if (pt == null) {
                pt = new JavaType.Parameterized(null, null, null);
                typeCache.put(signature, pt);

                List<JavaType> typeParameters = new ArrayList<>(classType.typarams_field.length());
                for (Type tParam : classType.typarams_field) {
                    typeParameters.add(type(tParam));
                }

                pt.unsafeSet(clazz, typeParameters);
            }
            return pt;
        }

        return clazz;
    }

    private JavaType.Class.Kind getKind(Symbol.ClassSymbol sym) {
        JavaType.Class.Kind kind;
        if ((sym.flags_field & KIND_BITMASK_ENUM) != 0) {
            kind = JavaType.Class.Kind.Enum;
        } else if ((sym.flags_field & KIND_BITMASK_ANNOTATION) != 0) {
            kind = JavaType.Class.Kind.Annotation;
        } else if ((sym.flags_field & KIND_BITMASK_INTERFACE) != 0) {
            kind = JavaType.Class.Kind.Interface;
        } else {
            kind = JavaType.Class.Kind.Class;
        }
        return kind;
    }

    @SuppressWarnings("ConstantConditions")
    public JavaType type(@Nullable Tree tree) {
        if (tree == null) {
            return null;
        }

        Symbol symbol = null;
        if (tree instanceof JCTree.JCIdent) {
            symbol = ((JCTree.JCIdent) tree).sym;
        } else if (tree instanceof JCTree.JCMethodDecl) {
            symbol = ((JCTree.JCMethodDecl) tree).sym;
        } else if (tree instanceof JCTree.JCVariableDecl) {
            return variableType(((JCTree.JCVariableDecl) tree).sym);
        }

        return type(((JCTree) tree).type, symbol);
    }

    @Nullable
    private JavaType type(Type type, Symbol symbol) {
        if (type instanceof Type.MethodType) {
            return methodInvocationType(type, symbol);
        }
        return type(type);
    }

    public JavaType.Primitive primitive(TypeTag tag) {
        switch (tag) {
            case BOOLEAN:
                return JavaType.Primitive.Boolean;
            case BYTE:
                return JavaType.Primitive.Byte;
            case CHAR:
                return JavaType.Primitive.Char;
            case DOUBLE:
                return JavaType.Primitive.Double;
            case FLOAT:
                return JavaType.Primitive.Float;
            case INT:
                return JavaType.Primitive.Int;
            case LONG:
                return JavaType.Primitive.Long;
            case SHORT:
                return JavaType.Primitive.Short;
            case VOID:
                return JavaType.Primitive.Void;
            case NONE:
                return JavaType.Primitive.None;
            case CLASS:
                return JavaType.Primitive.String;
            case BOT:
                return JavaType.Primitive.Null;
            default:
                throw new IllegalArgumentException("Unknown type tag " + tag);
        }
    }

    @Nullable
    public JavaType.Variable variableType(@Nullable Symbol symbol) {
        return variableType(symbol, null);
    }

    @Nullable
    private JavaType.Variable variableType(@Nullable Symbol symbol,
                                           @Nullable JavaType.FullyQualified owner) {
        if (!(symbol instanceof Symbol.VarSymbol)) {
            return null;
        }

        String signature = signatureBuilder.variableSignature(symbol);
        JavaType.Variable existing = typeCache.get(signature);
        if (existing != null) {
            return existing;
        }

        JavaType.Variable variable = new JavaType.Variable(
                null,
                symbol.flags_field,
                symbol.name.toString(),
                null, null, null);

        typeCache.put(signature, variable);

        JavaType resolvedOwner = owner;
        if (owner == null) {
            Type type = symbol.owner.type;
            Symbol sym = symbol.owner;

            if (sym.type instanceof Type.ForAll) {
                type = ((Type.ForAll) type).qtype;
            }

            resolvedOwner = type instanceof Type.MethodType ?
                    methodInvocationType(type, sym) :
                    type(type);
            assert resolvedOwner != null;
        }

        variable.unsafeSet(resolvedOwner, type(symbol.type), listAnnotations(symbol));
        return variable;
    }

    /**
     * Method type of a method invocation. Parameters and return type represent resolved types when they are generic
     * in the method declaration.
     *
     * @param selectType The method type.
     * @param symbol     The method symbol.
     * @return Method type attribution.
     */
    @Nullable
    public JavaType.Method methodInvocationType(@Nullable com.sun.tools.javac.code.Type selectType, @Nullable Symbol symbol) {
        if (selectType == null || selectType instanceof Type.ErrorType || symbol == null || symbol.kind == Kinds.ERR) {
            return null;
        }

        Symbol.MethodSymbol methodSymbol = (Symbol.MethodSymbol) symbol;

        if (selectType instanceof Type.ForAll) {
            Type.ForAll fa = (Type.ForAll) selectType;
            return methodInvocationType(fa.qtype, methodSymbol);
        }

        String signature = signatureBuilder.methodSignature(selectType, methodSymbol);
        JavaType.Method existing = typeCache.get(signature);
        if (existing != null) {
            return existing;
        }

        List<String> paramNames = null;
        if (!methodSymbol.params().isEmpty()) {
            paramNames = new ArrayList<>(methodSymbol.params().size());
            for (Symbol.VarSymbol p : methodSymbol.params()) {
                String s = p.name.toString();
                paramNames.add(s);
            }
        }

        JavaType.Method method = new JavaType.Method(
                null,
                methodSymbol.flags_field,
                null,
                methodSymbol.isConstructor() ? "<constructor>" : methodSymbol.getSimpleName().toString(),
                null,
                paramNames,
                null, null, null
        );
        typeCache.put(signature, method);

        JavaType returnType = null;
        List<JavaType> parameterTypes = null;
        List<JavaType.FullyQualified> exceptionTypes = null;

        if (selectType instanceof Type.MethodType) {
            Type.MethodType methodType = (Type.MethodType) selectType;

            if (!methodType.argtypes.isEmpty()) {
                parameterTypes = new ArrayList<>(methodType.argtypes.size());
                for (com.sun.tools.javac.code.Type argtype : methodType.argtypes) {
                    if (argtype != null) {
                        JavaType javaType = type(argtype);
                        parameterTypes.add(javaType);
                    }
                }
            }

            returnType = type(methodType.restype);

            if (!methodType.thrown.isEmpty()) {
                exceptionTypes = new ArrayList<>(methodType.thrown.size());
                for (Type exceptionType : methodType.thrown) {
                    JavaType.FullyQualified javaType = TypeUtils.asFullyQualified(type(exceptionType));
                    if (javaType == null) {
                        // if the type cannot be resolved to a class (it might not be on the classpath, or it might have
                        // been mapped to cyclic)
                        if (exceptionType instanceof Type.ClassType) {
                            Symbol.ClassSymbol sym = (Symbol.ClassSymbol) exceptionType.tsym;
                            javaType = new JavaType.Class(null, Flag.Public.getBitMask(), sym.flatName().toString(), JavaType.Class.Kind.Class,
                                    null, null, null, null, null, null);
                        }
                    }
                    if (javaType != null) {
                        // if the exception type is not resolved, it is not added to the list of exceptions
                        exceptionTypes.add(javaType);
                    }
                }
            }
        } else if (selectType instanceof Type.UnknownType) {
            returnType = JavaType.Unknown.getInstance();
        }

        JavaType.FullyQualified resolvedDeclaringType = TypeUtils.asFullyQualified(type(methodSymbol.owner.type));
        if (resolvedDeclaringType == null) {
            return null;
        }

        assert returnType != null;

        method.unsafeSet(resolvedDeclaringType,
                methodSymbol.isConstructor() ? resolvedDeclaringType : returnType,
                parameterTypes, exceptionTypes, listAnnotations(methodSymbol));
        return method;
    }

    /**
     * Method type of a method declaration. Parameters and return type represent generic signatures when applicable.
     *
     * @param symbol        The method symbol.
     * @param declaringType The method's declaring type.
     * @return Method type attribution.
     */
    @Nullable
    public JavaType.Method methodDeclarationType(@Nullable Symbol symbol, @Nullable JavaType.FullyQualified declaringType) {
        // if the symbol is not a method symbol, there is a parser error in play
        Symbol.MethodSymbol methodSymbol = symbol instanceof Symbol.MethodSymbol ? (Symbol.MethodSymbol) symbol : null;

        if (methodSymbol != null) {

            String signature = signatureBuilder.methodSignature(methodSymbol);
            JavaType.Method existing = typeCache.get(signature);
            if (existing != null) {
                return existing;
            }

            List<String> paramNames = null;
            if (!methodSymbol.params().isEmpty()) {
                paramNames = new ArrayList<>(methodSymbol.params().size());
                for (Symbol.VarSymbol p : methodSymbol.params()) {
                    String s = p.name.toString();
                    paramNames.add(s);
                }
            }

            JavaType.Method method = new JavaType.Method(
                    null,
                    methodSymbol.flags_field,
                    null,
                    methodSymbol.isConstructor() ? "<constructor>" : methodSymbol.getSimpleName().toString(),
                    null,
                    paramNames,
                    null, null, null
            );
            typeCache.put(signature, method);

            Type signatureType = methodSymbol.type instanceof Type.ForAll ?
                    ((Type.ForAll) methodSymbol.type).qtype :
                    methodSymbol.type;

            List<JavaType.FullyQualified> exceptionTypes = null;

            Type selectType = methodSymbol.type;
            if (selectType instanceof Type.ForAll) {
                selectType = ((Type.ForAll) selectType).qtype;
            }

            if (selectType instanceof Type.MethodType) {
                Type.MethodType methodType = (Type.MethodType) selectType;
                if (!methodType.thrown.isEmpty()) {
                    exceptionTypes = new ArrayList<>(methodType.thrown.size());
                    for (Type exceptionType : methodType.thrown) {
                        JavaType.FullyQualified javaType = TypeUtils.asFullyQualified(type(exceptionType));
                        if (javaType == null) {
                            // if the type cannot be resolved to a class (it might not be on the classpath, or it might have
                            // been mapped to cyclic)
                            if (exceptionType instanceof Type.ClassType) {
                                Symbol.ClassSymbol sym = (Symbol.ClassSymbol) exceptionType.tsym;
                                javaType = new JavaType.Class(null, Flag.Public.getBitMask(), sym.flatName().toString(), JavaType.Class.Kind.Class,
                                        null, null, null, null, null, null);
                            }
                        }
                        if (javaType != null) {
                            // if the exception type is not resolved, it is not added to the list of exceptions
                            exceptionTypes.add(javaType);
                        }
                    }
                }
            }

            JavaType.FullyQualified resolvedDeclaringType = declaringType;
            if (declaringType == null) {
                if (methodSymbol.owner instanceof Symbol.ClassSymbol || methodSymbol.owner instanceof Symbol.TypeVariableSymbol) {
                    resolvedDeclaringType = TypeUtils.asFullyQualified(type(methodSymbol.owner.type));
                }
            }

            if (resolvedDeclaringType == null) {
                return null;
            }

            JavaType returnType;
            List<JavaType> parameterTypes = null;

            if (signatureType instanceof Type.ForAll) {
                signatureType = ((Type.ForAll) signatureType).qtype;
            }
            if (signatureType instanceof Type.MethodType) {
                Type.MethodType mt = (Type.MethodType) signatureType;

                if (!mt.argtypes.isEmpty()) {
                    parameterTypes = new ArrayList<>(mt.argtypes.size());
                    for (com.sun.tools.javac.code.Type argtype : mt.argtypes) {
                        if (argtype != null) {
                            JavaType javaType = type(argtype);
                            parameterTypes.add(javaType);
                        }
                    }
                }

                returnType = type(mt.restype);
            } else {
                throw new UnsupportedOperationException("Unexpected method signature type" + signatureType.getClass().getName());
            }

            method.unsafeSet(resolvedDeclaringType,
                    methodSymbol.isConstructor() ? resolvedDeclaringType : returnType,
                    parameterTypes, exceptionTypes, listAnnotations(methodSymbol));
            return method;
        }

        return null;
    }

    private void completeClassSymbol(Symbol.ClassSymbol classSymbol) {
        try {
            classSymbol.complete();
        } catch (Symbol.CompletionFailure ignore) {
        }
    }

    @Nullable
    private List<JavaType.FullyQualified> listAnnotations(Symbol symb) {
        List<JavaType.FullyQualified> annotations = null;
        if (!symb.getDeclarationAttributes().isEmpty()) {
            annotations = new ArrayList<>(symb.getDeclarationAttributes().size());
            for (Attribute.Compound a : symb.getDeclarationAttributes()) {
                JavaType.FullyQualified annotType = TypeUtils.asFullyQualified(type(a.type));
                if (annotType == null) {
                    continue;
                }
                Retention retention = a.getAnnotationType().asElement().getAnnotation(Retention.class);
                if(retention != null && retention.value() == RetentionPolicy.SOURCE) {
                    continue;
                }
                annotations.add(annotType);
            }
        }
        return annotations;
    }
}
