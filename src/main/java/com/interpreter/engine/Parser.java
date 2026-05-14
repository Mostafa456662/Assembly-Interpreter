package com.interpreter.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.interpreter.model.Expression;
import com.interpreter.model.Instruction;

public class Parser {

    public static HashMap<String, Integer> resolveLabels(List<Expression> expressions) {

        HashMap<String, Integer> labels = new HashMap<>();

        // if label exists maps the label to the address of the instruction
        for (int i = 0; i < expressions.size(); i++) {
            Expression exp = expressions.get(i);
            if (exp.getLabel() != null) {
                labels.put(exp.getLabel(), i);
            }
        }

        return labels;
    }

    public static HashMap<Integer, Expression> assignPC(List<Expression> expressions) {

        HashMap<String, Integer> labelMap = resolveLabels(expressions);
        HashMap<Integer, Expression> pc = new HashMap<>();
        // if the instruction is a jump instruction, replace the label with the
        // address of the label
        for (int i = 0; i < expressions.size(); i++) {
            Instruction inst = expressions.get(i).getInstruction();
            if (inst == Instruction.JMP || inst == Instruction.JNZ || inst == Instruction.JZ
                    || inst == Instruction.JN) {
                String label = expressions.get(i).getArguments().get(0);
                int targetPC = labelMap.get(label);
                List<String> newArgs = new ArrayList<>(List.of(String.valueOf(targetPC)));
                Expression newExp = new Expression(expressions.get(i).getLabel(), inst, newArgs);
                pc.put(i, newExp);
            }

            // if not a jump instruction, just put the expression in the pc
            else {
                pc.put(i, expressions.get(i));
            }
        }
        return pc;
    }
}
