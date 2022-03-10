package org.karane.templatematching;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.RunnerException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Fork(1)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Measurement(iterations = 10)
@Warmup(iterations = 10)
@BenchmarkMode(Mode.Throughput)
public class StringFormatBenchmark {
    private String name = "UserName";
    private String lName = "LUserName";
    private String nick = "UserNick";

    public static void main(String[] args) throws IOException, RunnerException {
        org.openjdk.jmh.Main.main(args);
    }

    @Benchmark
    public void stringFormat(Blackhole blackhole) {
        final String result = String.format("Contact {name=%s, lastName=%s, nickName=%s}", name, lName, nick);
        blackhole.consume(result);
    }

    @Benchmark
    public void stringBuilder(Blackhole blackhole) {
        final StringBuffer sb = new StringBuffer("Contact {");
        sb.append(", name='").append(name)
                .append(", lastName='").append(lName)
                .append(", nickName='").append(nick)
                .append('}');
        final String result = sb.toString();
        blackhole.consume(result);
    }

    @Benchmark
    public void stringBuilder128(Blackhole blackhole) {
        final StringBuffer sb = new StringBuffer(128);
        sb.append("Contact {");
        sb.append(", name='").append(name)
                .append(", lastName='").append(lName)
                .append(", nickName='").append(nick)
                .append('}');
        final String result = sb.toString();
        blackhole.consume(result);
    }

    @Benchmark
    public void stringBuilder256(Blackhole blackhole) {
        final StringBuffer sb = new StringBuffer(256);
        sb.append("Contact {");
        sb.append(", name='").append(name)
                .append(", lastName='").append(lName)
                .append(", nickName='").append(nick)
                .append('}');
        final String result = sb.toString();
        blackhole.consume(result);
    }

    @Benchmark
    public void stringBuilderNonFinal(Blackhole blackhole) {
        StringBuffer sb = new StringBuffer("Contact {");
        sb.append(", name='").append(name)
                .append(", lastName='").append(lName)
                .append(", nickName='").append(nick)
                .append('}');
        String result = sb.toString();
        blackhole.consume(result);
    }
}
