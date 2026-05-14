package com.interpreter.engine;

import com.interpreter.model.Expression;
import com.interpreter.model.Instruction;

import java.util.HashMap;
import java.util.List;

public class Executor {
    private int[] memory;
    private int[] registers;
    private boolean zeroFlag;
    private boolean negativeFlag;
    private int pc;

    private HashMap<Integer, Expression> expressions;
    private HashMap<String, Integer> resolveLabels;

    public Executor(int[] memory, int[] registers, HashMap<Integer, Expression> expressions,
            HashMap<String, Integer> resolveLabels) {
        this.memory = memory;
        this.registers = registers;
        this.zeroFlag = false;
        this.negativeFlag = false;
        this.expressions = expressions;
        this.resolveLabels = resolveLabels;
        this.pc = 0;
    }

    // This is the one that calls the needed method
    public void execute() {
        Expression currentExpression = expressions.get(pc);
        Instruction currentInstruction = currentExpression.getInstruction();
        List<String> arguments = currentExpression.getArguments();
        switch (currentInstruction) {
            case MOV:
                mov(arguments);
                break;
            case ADD:
                add(arguments);
                break;
            case SUB:
                sub(arguments);
                break;
            case MUL:
                mul(arguments);
                break;
            case DIV:
                div(arguments);
                break;
            case AND:
                and(arguments);
                break;
            case OR:
                or(arguments);
                break;
            case XOR:
                xor(arguments);
                break;
            case NOT:
                not(arguments);
                break;
            case SHL:
                shl(arguments);
                break;
            case SHR:
                shr(arguments);
                break;
            case LOAD:
                load(arguments);
                break;
            case STORE:
                store(arguments);
                break;
            case CMP:
                cmp(arguments);
                break;
            case JMP:
                jmp(arguments);
                break;
            case JZ:
                jz(arguments);
                break;
            case JN:
                jn(arguments);
                break;
            case JNZ:
                jnz(arguments);
                break;
            case PRINT:
                print(arguments);
                break;
            case HALT:
                halt();
                break;
            default:
                throw new IllegalArgumentException("Unknown instruction: " + currentInstruction);
        }
    }

    private void mov(List<String> arguments) {

    }

    private void add(List<String> arguments) {

    }

    private void sub(List<String> arguments) {

    }

    private void mul(List<String> arguments) {

    }

    private void div(List<String> arguments) {

    }

    private void and(List<String> arguments) {

    }

    private void or(List<String> arguments) {

    }

    private void xor(List<String> arguments) {

    }

    private void not(List<String> arguments) {

    }

    private void shl(List<String> arguments) {

    }

    private void shr(List<String> arguments) {
        

    }

    private void load(List<String> arguments) {
        int R = Integer.parseInt(arguments.get(0).substring(1));
        int address = Integer.parseInt(arguments.get(1));
        registers[R] = memory[address];

    }

    private void store(List<String> arguments) {
        int R1 = Integer.parseInt(arguments.get(0).substring(1));
        int address = Integer.parseInt(arguments.get(1));
        memory[address] = registers[R1];
    }

    private void cmp(List<String> arguments) {
        int R1 = Integer.parseInt(arguments.get(0).substring(1));
        int R2 = Integer.parseInt(arguments.get(1).substring(1));
        int val1 = registers[R1];
        int val2 = registers[R2];
        if (val1 == val2) {
            zeroFlag = true;
            negativeFlag = false;
        } else if (val1 < val2) {
            zeroFlag = false;
            negativeFlag = true;
        } else {
            zeroFlag = false;
            negativeFlag = false;
        }
        zeroFlag = (val1 == val2);
        negativeFlag = (val1 < val2);

    }

    private void jmp(List<String> arguments) {
        String label = arguments.get(0);
        int targetAddress = resolveLabels.get(label);
        pc = targetAddress;
    }

    private void jz(List<String> arguments) {
        if (zeroFlag) {
            int targetAddress = Integer.parseInt(arguments.get(0));
            pc = targetAddress;
        }
    }

    private void jn(List<String> arguments) {
        if (negativeFlag) {
            int targetAddress = Integer.parseInt(arguments.get(0));
            pc = targetAddress;
        }
    }

    private void jnz(List<String> arguments) {
        if (!zeroFlag) {
            int targetAddress = Integer.parseInt(arguments.get(0));
            pc = targetAddress;
        }
    }

    private void print(List<String> arguments) {
        String register = arguments.get(0);
        int registerIndex = Integer.parseInt(register.substring(1));
        System.out.println(registers[registerIndex]);

    }

    private void halt() {
        System.exit(0);
    }
}
