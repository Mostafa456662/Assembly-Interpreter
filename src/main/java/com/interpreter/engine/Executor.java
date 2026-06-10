package com.interpreter.engine;

import com.interpreter.model.Expression;
import com.interpreter.model.Instruction;
import com.interpreter.exceptions.DivisionByZeroException;

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

    public void run() throws DivisionByZeroException, IllegalArgumentException {
        while (pc < expressions.size()) {
            execute();
        }
    }

    public void execute() throws DivisionByZeroException, IllegalArgumentException {
        Expression currentExpression = expressions.get(pc);
        // System.out.println(currentExpression);
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
        if (currentInstruction != Instruction.JMP && currentInstruction != Instruction.JZ
                && currentInstruction != Instruction.JN && currentInstruction != Instruction.JNZ) {
            pc++;
        }
    }

    private void mov(List<String> arguments) {
        String output = "[Line " + (pc + 1) + "] " + expressions.get(pc).getInstruction() + " "
                + expressions.get(pc).getArguments();
        for (int i = output.length(); i < 40; i++) {
            output += " ";
        }
        int targetRegisterIndex = Integer.parseInt(arguments.get(0).substring(1));
        int value = Integer.parseInt(arguments.get(1));
        registers[targetRegisterIndex] = value;
        output += "| R" + targetRegisterIndex + " = " + value;
        System.out.println(output);
    }

    private void add(List<String> arguments) {
        String output = "[Line " + (pc + 1) + "] " + expressions.get(pc).getInstruction() + " "
                + expressions.get(pc).getArguments();
        for (int i = output.length(); i < 40; i++) {
            output += " ";
        }
        int targetRegisterIndex = Integer.parseInt(arguments.get(0).substring(1));
        int value1RegisterIndex = Integer.parseInt(arguments.get(1).substring(1));
        String thirdArgument = arguments.get(2);
        int value2 = thirdArgument.startsWith("R")
                ? registers[Integer.parseInt(thirdArgument.substring(1))]
                : Integer.parseInt(thirdArgument);

        registers[targetRegisterIndex] = registers[value1RegisterIndex] + value2;
        if (registers[targetRegisterIndex] == 0) {
            zeroFlag = true;
        } else {
            zeroFlag = false;
        }
        if (registers[targetRegisterIndex] < 0) {
            negativeFlag = true;
        } else {
            negativeFlag = false;
        }
        output += "| R" + targetRegisterIndex + " = " + registers[targetRegisterIndex] + "  (Z=" + zeroFlag + ", N=" + negativeFlag + ")";
        System.out.println(output);
    }

    private void sub(List<String> arguments) {
        String output = "[Line " + (pc + 1) + "] " + expressions.get(pc).getInstruction() + " "
                + expressions.get(pc).getArguments();
        for (int i = output.length(); i < 40; i++) {
            output += " ";
        }
        int targetRegisterIndex = Integer.parseInt(arguments.get(0).substring(1));
        int value1RegisterIndex = Integer.parseInt(arguments.get(1).substring(1));
        String thirdArgument = arguments.get(2);
        int value2 = thirdArgument.startsWith("R")
                ? registers[Integer.parseInt(thirdArgument.substring(1))]
                : Integer.parseInt(thirdArgument);

        registers[targetRegisterIndex] = registers[value1RegisterIndex] - value2;
        if (registers[targetRegisterIndex] == 0) {
            zeroFlag = true;
        } else {
            zeroFlag = false;
        }
        if (registers[targetRegisterIndex] < 0) {
            negativeFlag = true;
        } else {
            negativeFlag = false;
        }
        output += "| R" + targetRegisterIndex + " = " + registers[targetRegisterIndex] + "  (Z=" + zeroFlag + ", N=" + negativeFlag + ")";
        System.out.println(output);
    }

    private void mul(List<String> arguments) {
        String output = "[Line " + (pc + 1) + "] " + expressions.get(pc).getInstruction() + " "
                + expressions.get(pc).getArguments();
        for (int i = output.length(); i < 40; i++) {
            output += " ";
        }
        int targetRegisterIndex = Integer.parseInt(arguments.get(0).substring(1));
        int value1RegisterIndex = Integer.parseInt(arguments.get(1).substring(1));
        String thirdArgument = arguments.get(2);
        int value2 = thirdArgument.startsWith("R")
                ? registers[Integer.parseInt(thirdArgument.substring(1))]
                : Integer.parseInt(thirdArgument);

        registers[targetRegisterIndex] = registers[value1RegisterIndex] * value2;
        if (registers[targetRegisterIndex] == 0) {
            zeroFlag = true;
        } else {
            zeroFlag = false;
        }
        if (registers[targetRegisterIndex] < 0) {
            negativeFlag = true;
        } else {
            negativeFlag = false;
        }
        output += "| R" + targetRegisterIndex + " = " + registers[targetRegisterIndex] + "  (Z=" + zeroFlag + ", N=" + negativeFlag + ")";
        System.out.println(output);
    }

    private void div(List<String> arguments) throws DivisionByZeroException {
        String output = "[Line " + (pc + 1) + "] " + expressions.get(pc).getInstruction() + " "
                + expressions.get(pc).getArguments();
        for (int i = output.length(); i < 40; i++) {
            output += " ";
        }
        int targetRegisterIndex = Integer.parseInt(arguments.get(0).substring(1));
        int value1RegisterIndex = Integer.parseInt(arguments.get(1).substring(1));
        String thirdArgument = arguments.get(2);
        int value2 = thirdArgument.startsWith("R")
                ? registers[Integer.parseInt(thirdArgument.substring(1))]
                : Integer.parseInt(thirdArgument);

        if (value2 == 0) {
            throw new DivisionByZeroException("ERROR: Division by zero");
        }

        registers[targetRegisterIndex] = registers[value1RegisterIndex] / value2;
        if (registers[targetRegisterIndex] == 0) {
            zeroFlag = true;
        } else {
            zeroFlag = false;
        }
        if (registers[targetRegisterIndex] < 0) {
            negativeFlag = true;
        } else {
            negativeFlag = false;
        }
        output += "| R" + targetRegisterIndex + " = " + registers[targetRegisterIndex] + "  (Z=" + zeroFlag + ", N=" + negativeFlag + ")";
        System.out.println(output);
    }

    private void and(List<String> arguments) {
        String output = "[Line " + (pc + 1) + "] " + expressions.get(pc).getInstruction() + " "
                + expressions.get(pc).getArguments();
        for (int i = output.length(); i < 40; i++) {
            output += " ";
        }
        int targetRegisterIndex = Integer.parseInt(arguments.get(0).substring(1));
        int value1RegisterIndex = Integer.parseInt(arguments.get(1).substring(1));
        int value2RegisterIndex = Integer.parseInt(arguments.get(2).substring(1));
        registers[targetRegisterIndex] = registers[value1RegisterIndex] & registers[value2RegisterIndex];
        output += "| R" + targetRegisterIndex + " = " + registers[targetRegisterIndex];
        System.out.println(output);
    }

    private void or(List<String> arguments) {
        String output = "[Line " + (pc + 1) + "] " + expressions.get(pc).getInstruction() + " "
                + expressions.get(pc).getArguments();
        for (int i = output.length(); i < 40; i++) {
            output += " ";
        }
        int targetRegisterIndex = Integer.parseInt(arguments.get(0).substring(1));
        int value1RegisterIndex = Integer.parseInt(arguments.get(1).substring(1));
        int value2RegisterIndex = Integer.parseInt(arguments.get(2).substring(1));
        registers[targetRegisterIndex] = registers[value1RegisterIndex] | registers[value2RegisterIndex];
        output += "| R" + targetRegisterIndex + " = " + registers[targetRegisterIndex];
        System.out.println(output);
    }

    private void xor(List<String> arguments) {
        String output = "[Line " + (pc + 1) + "] " + expressions.get(pc).getInstruction() + " "
                + expressions.get(pc).getArguments();
        for (int i = output.length(); i < 40; i++) {
            output += " ";
        }
        int targetRegisterIndex = Integer.parseInt(arguments.get(0).substring(1));
        int value1RegisterIndex = Integer.parseInt(arguments.get(1).substring(1));
        int value2RegisterIndex = Integer.parseInt(arguments.get(2).substring(1));
        registers[targetRegisterIndex] = registers[value1RegisterIndex] ^ registers[value2RegisterIndex];
        output += "| R" + targetRegisterIndex + " = " + registers[targetRegisterIndex];
        System.out.println(output);
    }

    private void not(List<String> arguments) {
        String output = "[Line " + (pc + 1) + "] " + expressions.get(pc).getInstruction() + " "
                + expressions.get(pc).getArguments();
        for (int i = output.length(); i < 40; i++) {
            output += " ";
        }
        int targetRegisterIndex = Integer.parseInt(arguments.get(0).substring(1));
        int value1RegisterIndex = Integer.parseInt(arguments.get(1).substring(1));
        registers[targetRegisterIndex] = ~registers[value1RegisterIndex];
        output += "| R" + targetRegisterIndex + " = " + registers[targetRegisterIndex];
        System.out.println(output);
    }

    private void shl(List<String> arguments) {
        String output = "[Line " + (pc + 1) + "] " + expressions.get(pc).getInstruction() + " "
                + expressions.get(pc).getArguments();
        for (int i = output.length(); i < 40; i++) {
            output += " ";
        }
        int R1 = Integer.parseInt(arguments.get(0).substring(1));
        int R2 = Integer.parseInt(arguments.get(1).substring(1));
        int n = Integer.parseInt(arguments.get(2));
        int val = registers[R2];
        val = val << n;
        registers[R1] = val;
        output += "| R" + R1 + " = " + val;
        System.out.println(output);
    }

    private void shr(List<String> arguments) {
        String output = "[Line " + (pc + 1) + "] " + expressions.get(pc).getInstruction() + " "
                + expressions.get(pc).getArguments();
        for (int i = output.length(); i < 40; i++) {
            output += " ";
        }
        int R1 = Integer.parseInt(arguments.get(0).substring(1));
        int R2 = Integer.parseInt(arguments.get(1).substring(1));
        int n = Integer.parseInt(arguments.get(2));
        int val = registers[R2];
        val = val >>> n;
        registers[R1] = val;
        output += "| R" + R1 + " = " + val;
        System.out.println(output);
    }

    private void load(List<String> arguments) {
        String output = "[Line " + (pc + 1) + "] " + expressions.get(pc).getInstruction() + " "
                + expressions.get(pc).getArguments();
        for (int i = output.length(); i < 40; i++) {
            output += " ";
        }
        int R = Integer.parseInt(arguments.get(0).substring(1));
        int address = Integer.parseInt(arguments.get(1));
        registers[R] = memory[address];
        output += "| R" + R + " = " + memory[address];
        System.out.println(output);

    }

    private void store(List<String> arguments) {
        String output = "[Line " + (pc + 1) + "] " + expressions.get(pc).getInstruction() + " "
                + expressions.get(pc).getArguments();
        for (int i = output.length(); i < 40; i++) {
            output += " ";
        }
        int R1 = Integer.parseInt(arguments.get(0).substring(1));
        int address = Integer.parseInt(arguments.get(1));
        if (address < 0 || address >= memory.length) {
            throw new IllegalArgumentException("ERROR: Memory address out of bounds");
        }
        memory[address] = registers[R1];
        output += "| Stored " + registers[R1] + " at address " + address;
        System.out.println(output);
    }

    private void cmp(List<String> arguments) {
        String output = "[Line " + (pc + 1) + "] " + expressions.get(pc).getInstruction() + " "
                + expressions.get(pc).getArguments();
        for (int i = output.length(); i < 40; i++) {
            output += " ";
        }
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
        output += "| Z =" + zeroFlag + ", N=" + negativeFlag;
        System.out.println(output);

    }

    private void jmp(List<String> arguments) {
        String output = "[Line " + (pc + 1) + "] " + expressions.get(pc).getInstruction() + " "
                + expressions.get(pc).getArguments();
        for (int i = output.length(); i < 40; i++) {
            output += " ";
        }
        int targetAddress = Integer.parseInt(arguments.get(0));
        pc = targetAddress;
        output += "| Jump taken -> Line " + (pc + 1);
        System.out.println(output);
    }

    private void jz(List<String> arguments) {
        String output = "[Line " + (pc + 1) + "] " + expressions.get(pc).getInstruction() + " "
                + expressions.get(pc).getArguments();
        for (int i = output.length(); i < 40; i++) {
            output += " ";
        }
        if (zeroFlag) {
            int targetAddress = Integer.parseInt(arguments.get(0));
            pc = targetAddress;
        } else {
            pc++;
        }
        output += "| Jump taken -> Line " + (pc + 1);
        System.out.println(output);
    }

    private void jn(List<String> arguments) {
        String output = "[Line " + (pc + 1) + "] " + expressions.get(pc).getInstruction() + " "
                + expressions.get(pc).getArguments();
        for (int i = output.length(); i < 40; i++) {
            output += " ";
        }
        if (negativeFlag) {
            int targetAddress = Integer.parseInt(arguments.get(0));
            pc = targetAddress;
        } else {
            pc++;
        }
        output += "| Jump taken -> Line " + (pc + 1);
        System.out.println(output);
    }

    private void jnz(List<String> arguments) {
        String output = "[Line " + (pc + 1) + "] " + expressions.get(pc).getInstruction() + " "
                + expressions.get(pc).getArguments();
        for (int i = output.length(); i < 40; i++) {
            output += " ";
        }
        if (!zeroFlag) {
            int targetAddress = Integer.parseInt(arguments.get(0));
            pc = targetAddress;
        } else {
            pc++;
        }
        for (int i = output.length(); i < 40; i++) {
            output += " ";
        }
        output += "| Jump taken -> Line " + (pc + 1);
        System.out.println(output);
    }

    private void print(List<String> arguments) {
        String register = arguments.get(0);
        int registerIndex = Integer.parseInt(register.substring(1));
        System.out.println("R" + registerIndex + "=" + registers[registerIndex]);

    }

    private void halt() {
        System.exit(0);
    }
}
