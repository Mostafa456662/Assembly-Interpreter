package com.interpreter.engine;

import com.interpreter.model.Expression;
import java.util.HashMap;

public class Executor {
    private int[] memory;
    private int[] registers;
    private boolean zeroFlag;
    private boolean negativeFlag;

    private HashMap<Integer, Expression> instructions;
    private HashMap<String, Expression> labels;

    public Executor(int[] memory, int[] registers, HashMap<Integer, Expression> instructions,
            HashMap<String, Expression> labels) {
        this.memory = memory;
        this.registers = registers;
        this.zeroFlag = false;
        this.negativeFlag = false;
        this.instructions = instructions;
        this.labels = labels;
    }

    // This is the one that calls the needed method
    public void execute() {

    }

    private void mov() {

    }

    private void add() {

    }

    private void sub() {

    }

    private void mul() {

    }

    private void div() {

    }

    private void and() {

    }

    private void or() {

    }

    private void xor() {

    }

    private void not() {

    }

    private void shl() {

    }

    private void shr() {

    }

    private void load() {

    }

    private void store() {

    }

    private void cmp() {

    }

    private void jmp() {

    }

    private void jz() {

    }

    private void jn() {

    }

    private void jnz() {

    }

    private void print() {

    }

    private void halt() {

    }
}
