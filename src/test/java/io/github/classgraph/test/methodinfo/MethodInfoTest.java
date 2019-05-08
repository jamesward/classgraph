/*
 * This file is part of ClassGraph.
 *
 * Author: Luke Hutchison
 *
 * Hosted at: https://github.com/classgraph/classgraph
 *
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Luke Hutchison
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.github.classgraph.test.methodinfo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.MethodInfoList.MethodInfoFilter;
import io.github.classgraph.ScanResult;
import io.github.classgraph.test.external.ExternalAnnotation;

/**
 * MethodInfoTest.
 */
public class MethodInfoTest {
    /**
     * Public method with args.
     *
     * @param str
     *            the str
     * @param c
     *            the c
     * @param j
     *            the j
     * @param f
     *            the f
     * @param b
     *            the b
     * @param l
     *            the l
     * @param varargs
     *            the varargs
     * @return the int
     */
    @ExternalAnnotation
    public final int publicMethodWithArgs(final String str, final char c, final long j, final float[] f,
            final byte[][] b, final List<Float> l, final int[]... varargs) {
        return 0;
    }

    /**
     * Private method.
     *
     * @return the string[]
     */
    @SuppressWarnings("unused")
    private static String[] privateMethod() {
        return null;
    }

    /**
     * Method info not enabled.
     */
    @Test(expected = IllegalArgumentException.class)
    public void methodInfoNotEnabled() {
        // .enableSaveMethodInfo() not called
        try (ScanResult scanResult = new ClassGraph().whitelistPackages(MethodInfoTest.class.getPackage().getName())
                .scan()) {
            scanResult.getClassInfo(MethodInfoTest.class.getName()).getMethodInfo();
        }
    }

    /**
     * Get method info.
     */
    @Test
    public void testGetMethodInfo() {
        try (ScanResult scanResult = new ClassGraph().whitelistPackages(MethodInfoTest.class.getPackage().getName())
                .enableClassInfo().enableMethodInfo().enableAnnotationInfo().scan()) {
            assertThat(scanResult.getClassInfo(MethodInfoTest.class.getName()).getMethodInfo()
                    .filter(new MethodInfoFilter() {
                        @Override
                        public boolean accept(final MethodInfo methodInfo) {
                            // JDK 10 fix
                            return !methodInfo.getName().equals("$closeResource") && !methodInfo.getName().equals("lambda$0") && !methodInfo.isSynthetic();
                        }
                    }).getAsStrings()).containsExactlyInAnyOrder( //
                            "@" + ExternalAnnotation.class.getName() //
                                    + " public final int publicMethodWithArgs"
                                    + "(java.lang.String, char, long, float[], byte[][], "
                                    + "java.util.List<java.lang.Float>, int[]...)",
                            "@" + Test.class.getName()
                                    + "(expected=class java.lang.IllegalArgumentException, timeout=0) "
                                    + "public void methodInfoNotEnabled()",
                            "@" + Test.class.getName() + "(expected=class org.junit.Test$None, timeout=0) "
                                    + "public void testGetMethodInfo()",
                            "@" + Test.class.getName() + "(expected=class org.junit.Test$None, timeout=0) "
                                    + "public void testGetConstructorInfo()",
                            "@" + Test.class.getName() + "(expected=class org.junit.Test$None, timeout=0) "
                                    + "public void testGetMethodInfoIgnoringVisibility()",
                            "@" + Test.class.getName() + "(expected=class org.junit.Test$None, timeout=0) "
                                    + "public void testMethodInfoLoadMethodForArrayArg()"
                            );
        }
    }

    /**
     * Get constructor info.
     */
    @Test
    public void testGetConstructorInfo() {
        try (ScanResult scanResult = new ClassGraph().whitelistPackages(MethodInfoTest.class.getPackage().getName())
                .enableMethodInfo().scan()) {
            assertThat(scanResult.getClassInfo(MethodInfoTest.class.getName()).getConstructorInfo().getAsStrings())
                    .containsExactlyInAnyOrder("public <init>()");
        }
    }

    /**
     * Get method info ignoring visibility.
     */
    @Test
    public void testGetMethodInfoIgnoringVisibility() {
        try (ScanResult scanResult = new ClassGraph().whitelistPackages(MethodInfoTest.class.getPackage().getName())
                .enableClassInfo().enableMethodInfo().enableAnnotationInfo().ignoreMethodVisibility().scan()) {
            assertThat(scanResult.getClassInfo(MethodInfoTest.class.getName()).getMethodInfo()
                    .filter(new MethodInfoFilter() {
                        @Override
                        public boolean accept(final MethodInfo methodInfo) {
                            // JDK 10 fix
                            return !methodInfo.getName().equals("$closeResource") && !methodInfo.getName().equals("lambda$0") && !methodInfo.isSynthetic();
                        }
                    }).getAsStrings()).containsExactlyInAnyOrder( //
                            "@" + ExternalAnnotation.class.getName() //
                                    + " public final int publicMethodWithArgs"
                                    + "(java.lang.String, char, long, float[], byte[][], "
                                    + "java.util.List<java.lang.Float>, int[]...)",
                            "private static java.lang.String[] privateMethod()",
                            "@" + Test.class.getName()
                                    + "(expected=class java.lang.IllegalArgumentException, timeout=0) "
                                    + "public void methodInfoNotEnabled()",
                            "@" + Test.class.getName() + "(expected=class org.junit.Test$None, timeout=0) "
                                    + "public void testGetMethodInfo()",
                            "@" + Test.class.getName() + "(expected=class org.junit.Test$None, timeout=0) "
                                    + "public void testGetConstructorInfo()",
                            "@" + Test.class.getName() + "(expected=class org.junit.Test$None, timeout=0) "
                                    + "public void testGetMethodInfoIgnoringVisibility()",
                            "@" + Test.class.getName() + "(expected=class org.junit.Test$None, timeout=0) "
                                    + "public void testMethodInfoLoadMethodForArrayArg()"
                            );
        }
    }
    
    /**
     * MethodInfo.loadClassAndGetMethod for arrays argument
     */
    @Test
    public void testMethodInfoLoadMethodForArrayArg() {
    	try (ScanResult scanResult = new ClassGraph().whitelistPackages(MethodInfoTest.class.getPackage().getName())
                .enableClassInfo().enableMethodInfo().enableAnnotationInfo().scan()) {
    		MethodInfo mi = scanResult.getClassInfo(MethodInfoTest.class.getName()).getMethodInfo()
    				.getSingleMethod("publicMethodWithArgs");
    		assertThat(mi).isNotNull();
    		assertThatCode(() -> {
    			mi.loadClassAndGetMethod();
    			}).doesNotThrowAnyException();
    		assertThat(mi.loadClassAndGetMethod()).isNotNull(); 
        }
    }
}
