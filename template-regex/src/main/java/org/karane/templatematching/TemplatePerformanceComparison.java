package org.karane.templatematching;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.RunnerException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 1, warmups = 1)
@Warmup(iterations = 5)
@State(Scope.Benchmark)
public class TemplatePerformanceComparison {

    private static final Pattern PRE_COMPILED_PATTERN = Pattern.compile("\"\\$([^\"]+)\"");
    private static final Matcher MATCHER = PRE_COMPILED_PATTERN.matcher(StringUtils.EMPTY);
    private static final ArrayList<HashMap<String, String>> variablesMaps = new ArrayList<>();
    private static String templateStandard;
    private static String templateStringSubstitutor;

    public static void main(String[] args) throws IOException, RunnerException {
        org.openjdk.jmh.Main.main(args);
    }

    @Setup()
    public void setUp() {

        templateStandard = "{\"var1\": \"$var1\", \"var2\": \"$var2\"}";
        templateStringSubstitutor = "{\"var1\": \"${var1}\", \"var2\": \"${var2}\"}";

        for(int i=1; i<= 100_000; i++) {
            HashMap<String, String> variablesMap = new HashMap<>();
            variablesMap.put("var1", "(" + RandomStringUtils.randomAlphabetic(5, 100) + ")");
            variablesMap.put("var2", "(" + RandomStringUtils.randomAlphabetic(5, 100) + ")");
            variablesMap.put("var3", "(" + RandomStringUtils.randomAlphabetic(5, 100) + ")");
            variablesMaps.add(variablesMap);
        }

    }



    @Benchmark
    public void templateMatcherNewPattern(Blackhole bh) {
        for (HashMap variablesMap : variablesMaps) {
            bh.consume(replaceFields(templateStandard, variablesMap));
        }
    }

    @Benchmark
    public void templateMatcherPreCompiledPattern(Blackhole bh) {
       for (HashMap variablesMap : variablesMaps) {
            bh.consume(replaceFieldsPreCompiledPattern(templateStandard, variablesMap));
        }
    }

    @Benchmark
    public void templateMatcherPreExistingMatcher(Blackhole bh) {
        for (HashMap variablesMap : variablesMaps) {
            bh.consume(replaceFieldsPreExistingMatcher(templateStandard, variablesMap));
        }
    }

    @Benchmark
    public void templateMatcherStringSubstitutor(Blackhole bh) {
        for (HashMap variablesMap : variablesMaps) {
            bh.consume(replaceFieldsStringSubstitutor(templateStringSubstitutor, variablesMap));
        }
    }

    @Benchmark
    public void templateMatcherTemplateMapper(Blackhole bh) {

        TemplateApplier templateMapper = new TemplateApplier(templateStandard);
        for (HashMap variablesMap : variablesMaps) {
            bh.consume(replaceFieldsTemplateMapper(templateMapper, variablesMap));
        }
    }

    private static boolean replaceFields(String template, HashMap<String, String> valuesMap) {
        Pattern pattern = Pattern.compile("\"\\$([^\"]+)\"");
        Matcher match = pattern.matcher(template);
        StringBuffer sb = new StringBuffer(template.length());
        while (match.find()) {
            String fieldName = match.group(1);
            match.appendReplacement(sb, Matcher.quoteReplacement("\"" + valuesMap.getOrDefault(fieldName, "<NONE>") + "\""));
        }
        match.appendTail(sb);
        return true;
    }

    private static boolean replaceFieldsPreCompiledPattern(String template, HashMap<String, String> valuesMap) {
        Matcher match = PRE_COMPILED_PATTERN.matcher(template);
        StringBuffer sb = new StringBuffer(template.length());
        while (match.find()) {
            String fieldName = match.group(1);
            match.appendReplacement(sb, Matcher.quoteReplacement("\"" + valuesMap.getOrDefault(fieldName, "<NONE>") + "\""));
        }
        match.appendTail(sb);
        return true;
    }

    private static boolean replaceFieldsPreExistingMatcher(String template, HashMap<String, String> valuesMap) {
        MATCHER.reset(template).matches();

        StringBuffer sb = new StringBuffer(template.length());
        while (MATCHER.find()) {
            String fieldName = MATCHER.group(1);
            MATCHER.appendReplacement(sb, Matcher.quoteReplacement("\"" + valuesMap.getOrDefault(fieldName, "<NONE>") + "\""));
        }
        MATCHER.appendTail(sb);
        return true;
    }

    public static boolean replaceFieldsStringSubstitutor(String template, HashMap<String, String> valuesMap) {
        // Build StringSubstitutor
        StringSubstitutor sub = new StringSubstitutor(valuesMap);

        // Replace
        String resolvedString = sub.replace(template);
        resolvedString.length();
        return true;
    }

    public static boolean replaceFieldsTemplateMapper(TemplateApplier templateApplier, HashMap<String, String> valuesMap) {

        templateApplier.apply(valuesMap);
        return true;
    }

}
