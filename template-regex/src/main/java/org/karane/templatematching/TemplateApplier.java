package org.karane.templatematching;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateApplier {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\"(\\$([^\"]+))\"");
    private static final int DOLAR_GROUP_INDEX = 1;
    private static final int VARIABLE_GROUP_INDEX = 2;

    private ArrayList<TemplatePiece> templatePieces;
    private String template;
    private int initialCapacity = 16;

    public TemplateApplier(String template) {
        this.template = template;
        buildTemplatePieces(this.template);
    }

    static public void main(String [] args) {
        TemplateApplier templateApplier = new TemplateApplier("{\"var1\": \"$var1\", \"var2\": \"$var2\"}");
        HashMap<String, String> contextMap = new HashMap<>();
        contextMap.put("var1", "(1)");
        contextMap.put("var2", "(2)");
        String output = templateApplier.apply(contextMap);
        System.out.println("Output: " + output);

        contextMap.put("var2", "(3)");
        output = templateApplier.apply(contextMap);

        System.out.println("Output: " + output);
    }

    private void buildTemplatePieces(String template){
        Matcher match = VARIABLE_PATTERN.matcher(template);
        int previous = 0;
        this.templatePieces = new ArrayList<>();
        while (match.find()) {
            int currentMatchIndex = match.start(DOLAR_GROUP_INDEX) ;
            String substring = template.substring(previous, currentMatchIndex);
            templatePieces.add(new TemplateString(substring));

            String variableName = match.group(VARIABLE_GROUP_INDEX);
            templatePieces.add(new TemplateVariable(variableName));

            previous = match.end(DOLAR_GROUP_INDEX);
        }

        if(previous!= 0) {
            String tailString = template.substring(previous, this.template.length());
            templatePieces.add(new TemplateString(tailString));
        }

//        System.out.println(templatePieces);
    }

    public String apply(HashMap<String, String> contextMap) {
        StringBuilder sb = new StringBuilder(initialCapacity);
        for(TemplatePiece piece: templatePieces) {
            sb.append(piece.getValue(contextMap));
        }
        return sb.toString();
    }

    public void setInitialCapacity(int initialCapacity) {
        this.initialCapacity = initialCapacity;
    }

    interface TemplatePiece {
        String getValue(HashMap<String, String> contextMap);
    }

    class TemplateString implements TemplatePiece {
        private String content;

        public TemplateString(String content) {
            this.content = content;
        }

        public String getValue(HashMap<String, String> contextMap) {
            return content;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("TemplateString{");
            sb.append("content='").append(content).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

    class TemplateVariable implements TemplatePiece {
        private String variableName;

        public TemplateVariable(String variableName) {
            this.variableName = variableName;
        }

        public String getVariableName() {
            return variableName;
        }

        public void setVariableName(String variableName) {
            this.variableName = variableName;
        }

        public String getValue(HashMap<String, String> contextMap){
            return contextMap.getOrDefault(variableName, StringUtils.EMPTY);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("TemplateVariable{");
            sb.append("variableName='").append(variableName).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}
