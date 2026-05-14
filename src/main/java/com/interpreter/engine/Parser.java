package com.interpreter.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.interpreter.model.Expression;
import com.interpreter.model.Instruction;

public class Parser {

    public static HashMap<String, Expression> resolveLabels(List<Expression> expressions) {
        HashMap<String, Expression> labels = new HashMap<>();
        for (Expression exp : expressions) {
            if (exp.getLabel() != null) {
                labels.put(exp.getLabel(), exp);
            }
        }
        return labels;
    }

    public static HashMap<Integer, Expression> assignPC(List<Expression> expressions) {

        HashMap<String, Expression> labelMap = resolveLabels(expressions);
        HashMap<Integer, Expression> pc = new HashMap<>();

        for (int i = 0; i < expressions.size(); i++) {
            Instruction inst = expressions.get(i).getInstruction();
            if (inst == Instruction.JMP || inst == Instruction.JNZ || inst == Instruction.JZ
                    || inst == Instruction.JN) {
                String label = expressions.get(i).getArguments().get(0);
                Expression targetExp = labelMap.get(label);
                int targetPC = expressions.indexOf(targetExp);
                List<String> newArgs = new ArrayList<>();
                newArgs.add(String.valueOf(targetPC));
                Expression newExp = new Expression(expressions.get(i).getLabel(), inst, newArgs);
                pc.put(i, newExp);
            }

            else {
                pc.put(i, expressions.get(i));
            }

        }
        return pc;
    }
}
