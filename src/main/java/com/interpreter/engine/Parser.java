package com.interpreter.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.interpreter.model.Expression;
import com.interpreter.model.Instruction;

public class Parser {

    /**
     * Resolves all labels in the given list of expressions.
     * Maps each label to its corresponding line number (PC).
     */
    public static HashMap<String, Integer> resolveLabels(List<Expression> expressions) {

        HashMap<String, Integer> labels = new HashMap<>();

        // if label exists maps the label to the address of the instruction
        for (int i = 0; i < expressions.size(); i++) {
            Expression exp = expressions.get(i);
            
            // Check if the current expression has a valid label
            if (exp.getLabel() != null && !exp.getLabel().trim().isEmpty()) {
                String labelName = exp.getLabel();
                int address = i;
                
                // Put the resolved label and its address into the map
                labels.put(labelName, address);
            }
        }

        return labels;
    }

    /**
     * Assigns the program counter (PC) for each expression.
     * Replaces jump instruction labels with integer addresses.
     */
    public static HashMap<Integer, Expression> assignPC(List<Expression> expressions) {

        HashMap<String, Integer> labelMap = resolveLabels(expressions);
        HashMap<Integer, Expression> pc = new HashMap<>();
        
        // iterate over all parsed expressions to assign correct PC addresses
        for (int i = 0; i < expressions.size(); i++) {
            Expression currentExpression = expressions.get(i);
            Instruction inst = currentExpression.getInstruction();
            
            // if the instruction is a jump instruction, replace the label with the address
            if (isJumpInstruction(inst)) {
                
                // Get the target label from the arguments
                List<String> args = currentExpression.getArguments();
                String label = args.get(0);
                
                // Validate that the label exists in our resolved map
                if (!labelMap.containsKey(label)) {
                    throw new IllegalArgumentException("Error: Label '" + label + "' was not found in the assembly code.");
                }
                
                // Fetch the integer target PC
                int targetPC = labelMap.get(label);
                
                // Build a new expression with the resolved integer address
                Expression newExp = createResolvedJumpExpression(currentExpression, inst, targetPC);
                
                pc.put(i, newExp);
            }
            // if not a jump instruction, just put the expression in the pc
            else {
                pc.put(i, currentExpression);
            }
        }
        
        return pc;
    }

    /**
     * Helper method to determine if an instruction is a branching/jump instruction.
     * Extracts logic to make the main loop more readable.
     */
    private static boolean isJumpInstruction(Instruction inst) {
        boolean isUnconditionalJump = (inst == Instruction.JMP);
        boolean isJumpIfZero = (inst == Instruction.JZ);
        boolean isJumpIfNotZero = (inst == Instruction.JNZ);
        boolean isJumpIfNegative = (inst == Instruction.JN);

        return isUnconditionalJump || isJumpIfZero || isJumpIfNotZero || isJumpIfNegative;
    }

    /**
     * Helper method to create a new Expression object where the label argument
     * is replaced by the actual integer PC address.
     */
    private static Expression createResolvedJumpExpression(Expression originalExp, Instruction inst, int targetPC) {
        
        // Convert the target PC integer to a String
        String pcString = String.valueOf(targetPC);
        
        // Create a new arguments list containing the integer address
        List<String> newArgs = new ArrayList<>();
        newArgs.add(pcString);
        
        // Construct and return the new Expression
        String originalLabel = originalExp.getLabel();
        
        return new Expression(originalLabel, inst, newArgs);
    }
}
