package com.interpreter.model;

import java.util.List;

public class Expression {
    private Instruction instruction;
    private List<String> arguments;

    public Expression(Instruction instruction, List<String> arguments) {
        this.instruction = instruction;
        this.arguments = arguments;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    public List<String> getArguments() {
        return arguments;
    }
}
