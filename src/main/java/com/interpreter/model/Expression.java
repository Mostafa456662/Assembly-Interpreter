package com.interpreter.model;

import java.util.List;

public class Expression {
    private String label;
    private Instruction instruction;
    private List<String> arguments;

    public Expression(String label, Instruction instruction, List<String> arguments) {
        this.label = label;
        this.instruction = instruction;
        this.arguments = arguments;
    }

    public String getLabel() {
        return label;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    public List<String> getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        return "[\"" + label + "\", \"" + instruction + "\", \"" + String.join(", ", arguments) + "\"]";
    }
}
